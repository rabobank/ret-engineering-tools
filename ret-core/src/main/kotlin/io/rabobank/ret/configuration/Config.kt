package io.rabobank.ret.configuration

import java.nio.file.Path

interface Config {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)
    fun load(): PluginConfig
    fun configure(function: (ConfigurationProperty) -> Unit)
    fun configFile(): Path
    fun pluginConfigDirectory(): Path
}
