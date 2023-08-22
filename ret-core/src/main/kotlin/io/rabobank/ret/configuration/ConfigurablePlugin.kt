package io.rabobank.ret.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.rabobank.ret.util.OsUtils
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
open class ConfigurablePlugin : Configurable {
    @ConfigProperty(name = "plugin.name", defaultValue = "ret")
    lateinit var pluginName: String

    @Inject
    lateinit var osUtils: OsUtils

    @Inject
    lateinit var objectMapper: ObjectMapper

    val config by lazy {
        runCatching { objectMapper.readValue<Map<String, String>>(osUtils.getPluginConfig(pluginName).toFile()) }
            .getOrDefault(emptyMap())
    }

    override fun properties() = emptyList<ConfigurationProperty>()
}
