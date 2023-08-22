package io.rabobank.ret.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec

/**
 * Plugin Configure Command
 *
 * This is a mandatory sub command for any RET plugin that you create. RET will use it when executing `ret plugin configure your-plugin`.
 * With this, RET will be able to run the plugin commands, automatically add autocompletion for the plugin, and prompt the user for any property that needs to be configured.
 */
@Command(
    name = "configure",
    hidden = true,
    subcommands = [
        ConfigureProjectCommand::class,
    ],
)
class PluginConfigureCommand(
    private val config: Config,
    private val retConsole: RetConsole,
    private val objectMapper: ObjectMapper,
) : Runnable {
    @Spec
    lateinit var commandSpec: CommandSpec

    override fun run() {
        val pluginName = commandSpec.parent().name()
        Log.debug("Configuring plugin '$pluginName'")

        retConsole.out("Hello! Let's start configuring the $pluginName plugin.")

        storePluginConfiguration(pluginName)

        retConsole.out("Done! Feel free to run this command again to make changes.")
        retConsole.out("Configuration saved to ${config.configFile()}")

        Log.debug("Plugin '$pluginName' is now configured")
    }

    private fun storePluginConfiguration(pluginName: String) {
        var hasPluginSpecificConfig = false
        val pluginConfigFile = config.pluginConfigDirectory().resolve("${pluginName}.json").toFile()
        val pluginConfig = if (pluginConfigFile.exists()) objectMapper.readValue<Map<String, String>>(pluginConfigFile) else emptyMap()

        val answers = mutableMapOf<String, Any>()

        config.configure {
            hasPluginSpecificConfig = true
            val message = it.toMessage()
            val currentValue = pluginConfig[it.key]
            var input = retConsole.prompt(message, currentValue)

            while (it.required && input.ifEmpty { currentValue.orEmpty() }.isEmpty()) {
                retConsole.out("Please fill in an answer")
                input = retConsole.prompt(message, currentValue)
            }

            answers[it.key] = input.ifEmpty { currentValue.orEmpty() }
        }

        if (hasPluginSpecificConfig) {
            objectMapper.writeValue(pluginConfigFile, answers)

            retConsole.out("Wrote configuration to $pluginConfigFile")
        }
    }

    private fun ConfigurationProperty.toMessage(): String {
        var message = prompt
        if (!message.endsWith("?") && !message.endsWith(":")) message += ":"
        return message
    }
}
