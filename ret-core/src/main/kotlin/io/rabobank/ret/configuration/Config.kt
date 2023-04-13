package io.rabobank.ret.configuration

import java.nio.file.Path

interface Config {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
    fun configure(function: (ConfigurationProperty) -> Unit)
    fun configFile(): Path
}
