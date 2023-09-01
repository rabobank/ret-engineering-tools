package io.rabobank.ret.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import io.rabobank.ret.RetConsole
import io.rabobank.ret.util.OsUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.nio.file.Path
import java.util.Properties

private const val RET_VERSION = "ret_config_version"

/**
 * Access the RET Configurations
 *
 * This class gives to access configurations set by the user.
 */
@ApplicationScoped
class RetConfig(
    private val osUtils: OsUtils,
    private val retConsole: RetConsole,
    private val objectMapper: ObjectMapper,
    private val configurables: Instance<Configurable>,
    @ConfigProperty(name = "quarkus.application.version") private val retVersion: String,
) : Config {
    private val oldConfigFile = osUtils.getRetHomeDirectory().resolve("ret.config").toFile()
    private val oldConfigFileBackup = osUtils.getRetHomeDirectory().resolve("ret.config.bak").toFile()
    private val configFile = osUtils.getRetHomeDirectory().resolve("ret.json").toFile()
    private var properties = mutableMapOf<String, Any?>()

    init {
        loadExistingProperties()
    }

    private fun loadExistingProperties() {
        if (oldConfigFile.exists() && !configFile.exists()) {
            migrateConfig()
        }

        if (configFile.exists()) {
            properties = objectMapper.readValue<MutableMap<String, Any?>>(configFile)
        }
    }

    private fun migrateConfig() {
        val oldProperties = Properties()
            .apply { load(oldConfigFile.inputStream()) }
        properties = objectMapper.convertValue<MutableMap<String, Any?>>(oldProperties)

        if (!oldConfigFile.renameTo(oldConfigFileBackup)) {
            retConsole.errorOut("Unable to rename $oldConfigFile to $oldConfigFileBackup")
        }
        save()
    }

    /**
     * Get the [key] property from the user configurations.
     *
     * @return The configured value for [key], or null if not configured.
     */
    override operator fun get(key: String) = properties[key]

    /**
     * Set the [value] to property [key] in the user configurations.
     * This is automatically called when initializing a plugin, so you normally do not call this yourself.
     */
    override operator fun set(key: String, value: Any?) {
        properties[key] = value
    }

    /**
     * Delete a property
     */
    fun remove(key: String) = properties.remove(key)

    /**
     * Configure all defined configuration properties, based on the provided function,
     * and saves to the configuration file.
     * This is automatically called when initializing a plugin, so you normally do not call this yourself.
     */
    override fun configure(function: (ConfigurationProperty) -> Unit) {
        configurables.flatMap(Configurable::properties).forEach(function)
        save()
    }

    fun save() {
        properties[RET_VERSION] = retVersion
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, properties)
    }

    override fun configFile(): Path = Path.of(configFile.toURI())

    override fun pluginConfigDirectory(): Path = osUtils.getRetPluginsDirectory()
}
