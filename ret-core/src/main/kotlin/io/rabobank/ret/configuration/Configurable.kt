package io.rabobank.ret.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import io.rabobank.ret.util.OsUtils
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

/**
 * Implement this class in an ApplicationScoped class if you need custom configurations in your plugin.
 * Upon configuring the plugin, the user will be prompted for all provided configuration properties,
 * and inputs are saved in the RET configuration file.
 *
 * This class will also take care of migrating old "generic" configuration to plugin specific configuration.
 */
open class Configurable {
    @ConfigProperty(name = "plugin.name", defaultValue = "ret")
    lateinit var pluginName: String

    @Inject
    lateinit var osUtils: OsUtils

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var retConfig: RetConfig

    @Inject
    lateinit var retConsole: RetConsole

    private val configLoader by lazy { PluginConfigLoader(pluginName, objectMapper, osUtils) }

    val pluginConfig by lazy { initialize() }

    private fun initialize(): PluginConfig {
        var configMigrated = false
        val configCopy = configLoader.load()
        val keysToMigrate = keysToMigrate()

        if (keysToMigrate.isNotEmpty()) {
            keysToMigrate.forEach { (oldKey, newKey) ->
                val oldValue = retConfig[oldKey]
                if (oldValue != null) {
                    configCopy[newKey] = oldValue

                    retConfig.remove(oldKey)
                    configMigrated = true
                }
            }
        }

        val oldConfigToMigrate = properties().map { it.key }
        if (oldConfigToMigrate.isNotEmpty()) {
            oldConfigToMigrate.forEach {
                val oldValue = retConfig[it]
                if (oldValue != null) {
                    configCopy[it] = oldValue

                    retConfig.remove(it)
                    configMigrated = true
                }
            }
        }

        return if (configMigrated) {
            Log.debug("Migrated old configuration to plugin specific configuration")
            retConfig.save()
            configLoader.saveAndReload(configCopy)
        } else {
            configCopy
        }
    }

    fun load() = pluginConfig

    /**
     * An optional list of configuration properties that will be used to prompt the user to answer questions.
     */
    open fun properties() = emptyList<ConfigurationProperty>()

    /**
     * You can provide a map of keys which RET can automatically migrate to the new plugin configuration.
     *
     * The left should be the name of the old key and the right the name of the new key
     */
    open fun keysToMigrate() = emptyList<Pair<String, String>>()

    /**
     * Helper method to convert a simple Map to type-safe plugin specifc configuration.
     */
    inline fun <reified T> convertTo() = objectMapper.convertValue<T>(pluginConfig.config)
}

class PluginConfig(
    val config: MutableMap<String, Any?>,
) {
    inline operator fun <reified T> get(key: String): T? {
        val value = config[key]
        return if (value is T?) {
            value
        } else {
            error(
                "The config value '$key' cannot be cast to ${T::class.java}, because it's of type ${value?.javaClass}",
            )
        }
    }

    operator fun set(
        key: String,
        value: Any?,
    ) {
        config[key] = value
    }
}

class PluginConfigLoader(
    pluginName: String,
    private val objectMapper: ObjectMapper,
    osUtils: OsUtils,
) {
    private val pluginFile = osUtils.getPluginConfig(pluginName).toFile()

    fun load() =
        PluginConfig(
            runCatching { objectMapper.readValue<Map<String, Any?>>(pluginFile) }
                .getOrDefault(emptyMap())
                .toMutableMap(),
        )

    fun saveAndReload(pluginConfig: PluginConfig): PluginConfig {
        if (!pluginFile.parentFile.exists() && !pluginFile.parentFile.mkdirs()) {
            error("Unable to create directory ${pluginFile.parentFile}")
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(pluginFile, pluginConfig.config)

        return load()
    }
}
