package io.rabobank.ret.configuration

interface Configurable {
    fun properties(): List<ConfigurationProperty>
}
