package io.rabobank.ret.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import io.rabobank.ret.util.Logged
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec

/**
 * Plugin Configure Command
 *
 * This is a sub command for any RET plugin that you create.
 * RET will use it when executing `ret yourPlugin configure`.
 *
 * With this, RET will prompt the user for any property that needs to be configured.
 */
@Command(
    name = "configure",
    hidden = true,
    subcommands = [
        ConfigureProjectCommand::class,
    ],
)
@Logged
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
        val pluginConfigFile = config.pluginConfigDirectory().resolve("$pluginName.json").toFile()
        val pluginConfig = config.load()
        val answers = pluginConfig.config

        config.configure {
            hasPluginSpecificConfig = true
            val message = it.toMessage()
            val currentValue = (answers[it.key] as String?).orEmpty()
            var input = retConsole.prompt(message, currentValue)

            while (it.required && input.ifEmpty { currentValue }.isEmpty()) {
                retConsole.out("Please fill in an answer")
                input = retConsole.prompt(message, currentValue)
            }

            answers[it.key] = input.ifEmpty { currentValue }
        }

        if (hasPluginSpecificConfig) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(pluginConfigFile, answers)

            retConsole.out("Wrote configuration to $pluginConfigFile")
        }
    }

    private fun ConfigurationProperty.toMessage(): String {
        var message = prompt
        if (!message.endsWith("?") && !message.endsWith(":")) message += ":"
        return message
    }
}
