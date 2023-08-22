package io.rabobank.ret.configuration

/**
 * Implement this interface in an ApplicationScoped class if you need custom configurations in your plugin.
 * Upon initializing the plugin, the user will be prompted for all provided configuration properties,
 * and inputs are saved in the RET configuration file.
 */
fun interface Configurable {
    fun properties(): List<ConfigurationProperty>
}
