package io.rabobank.ret.commands

import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import io.rabobank.ret.configuration.PluginConfig
import java.nio.file.Path
import java.util.Properties

internal class TestConfig(private val pluginsPath: Path) : Config {
    private val configProps = listOf(
        ConfigurationProperty("project", "Enter your Rabobank project", required = true),
        ConfigurationProperty("organisation", "Enter your Rabobank organisation"),
    )
    private val properties = Properties()

    override fun get(key: String) = properties[key]

    override fun set(key: String, value: Any?) {
        properties[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun load(): PluginConfig = PluginConfig(properties as MutableMap<String, Any?>)

    override fun configure(function: (ConfigurationProperty) -> Unit) {
        configProps.forEach(function)
    }

    override fun configFile(): Path = pluginsPath.parent.resolve("ret.json")

    override fun pluginConfigDirectory(): Path = pluginsPath
}
