package io.rabobank.ret.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.quarkus.logging.Log
import io.rabobank.ret.util.OsUtils
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.Dependent
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

/**
 * Extend from this base plugin class to help out with loading plugin specific configuration.
 * This class also implements [Configurable] with a default implementation of [properties] with an empty list.
 *
 * @see Configurable
 */
@Dependent
open class BasePluginConfig : Configurable {
    @ConfigProperty(name = "plugin.name", defaultValue = "ret")
    lateinit var pluginName: String

    @Inject
    lateinit var osUtils: OsUtils

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var retConfig: RetConfig

    val config by lazy { PluginConfig(pluginName, objectMapper, osUtils) }

    @PostConstruct
    fun migrateOldConfig() {
        val keysToMigrate = keysToMigrate()
        if (keysToMigrate.isNotEmpty()) {
            Log.info("Migrating old configuration to plugin specific configuration")
            keysToMigrate.forEach { (oldKey, newKey) ->
                val oldValue = retConfig[oldKey]
                if (oldValue != null) {
                    config[newKey] = oldValue

                    retConfig.remove(oldKey)
                }
            }

            config.save()
            retConfig.save()
        }
    }

    override fun properties() = emptyList<ConfigurationProperty>()

    /**
     * You can provide a map of keys which RET can automatically migrate to the new plugin configuration.
     *
     * The left should be the name of the old key and the right the name of the new key
     */
    open fun keysToMigrate() = emptyList<Pair<String, String>>()
}

class PluginConfig(pluginName: String, private val objectMapper: ObjectMapper, osUtils: OsUtils) {
    private val pluginFile = osUtils.getPluginConfig(pluginName).toFile()
    val config = runCatching { objectMapper.readValue<Map<String, Any?>>(pluginFile) }
        .getOrDefault(emptyMap())
        .toMutableMap()

    inline operator fun <reified T> get(key: String): T? {
        val value = config[key]
        return if (value is T?) value else error("The config value cannot be cast to ${T::class.java}")
    }

    operator fun set(key: String, value: Any?) {
        config[key] = value
    }

    fun save() {
        objectMapper.writeValue(pluginFile, config)
    }
}
