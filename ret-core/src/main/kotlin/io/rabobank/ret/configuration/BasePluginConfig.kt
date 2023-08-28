package io.rabobank.ret.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

    val config by lazy {
        runCatching { objectMapper.readValue<Map<String, Any?>>(osUtils.getPluginConfig(pluginName).toFile()) }
            .getOrDefault(emptyMap())
            .toMutableMap()
    }

    @PostConstruct
    fun migrateOldConfig() {
        val keysToMigrate = keysToMigrate()
        if (keysToMigrate.isNotEmpty()) {
            keysToMigrate.forEach { (oldKey, newKey) ->
                val oldValue = retConfig[oldKey]
                if (oldValue != null) {
                    config[newKey] = oldValue

                    retConfig.remove(oldKey)
                }
            }

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
