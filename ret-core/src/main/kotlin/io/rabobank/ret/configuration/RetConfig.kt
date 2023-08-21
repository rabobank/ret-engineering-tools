package io.rabobank.ret.configuration

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
    osUtils: OsUtils,
    private val configurables: Instance<Configurable>,
    @ConfigProperty(name = "quarkus.application.version") private val retVersion: String,
) : Config {
    private val configFile = Path.of(osUtils.getHomeDirectory(), ".ret", "ret.config").toFile()
    private val properties = Properties()

    init {
        loadExistingProperties()
    }

    private fun loadExistingProperties() {
        if (configFile.exists()) {
            properties.load(configFile.inputStream())
        }
    }

    /**
     * Get the [key] property from the user configurations.
     *
     * @return The configured value for [key], or null if not configured.
     */
    override operator fun get(key: String) = properties[key] as String?

    /**
     * Set the [value] to property [key] in the user configurations.
     * This is automatically called when initializing a plugin, so you normally do not call this yourself.
     */
    override operator fun set(key: String, value: String) {
        properties[key] = value
    }

    /**
     * Configure all defined configuration properties, based on the provided function and saves to the configuration file.
     * This is automatically called when initializing a plugin, so you normally do not call this yourself.
     */
    override fun configure(function: (ConfigurationProperty) -> Unit) {
        configurables.flatMap(Configurable::properties).forEach(function)
        save()
    }

    override fun prompt(function: (Question) -> Answer): List<Answer> =
        configurables.flatMap(Configurable::prompts).map(function)

    private fun save() {
        properties[RET_VERSION] = retVersion
        properties.store(configFile.outputStream(), "")
    }

    override fun configFile(): Path = Path.of(configFile.toURI())
}
