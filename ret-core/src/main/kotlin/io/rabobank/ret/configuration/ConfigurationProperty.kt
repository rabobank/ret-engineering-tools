package io.rabobank.ret.configuration

/**
 * Configuration Property for user configs
 *
 * Used for user configurations that can be used by RET plugins. For example: user specific parts in a URL, or personal access tokens.
 * @property key the name of the config property.
 * @property prompt the message to the user, specifying what should be provided for this config property.
 */
data class ConfigurationProperty(val key: String, val prompt: String)
