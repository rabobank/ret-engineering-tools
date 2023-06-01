package io.rabobank.ret.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.rabobank.ret.IntrospectionUtil
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.util.OsUtils
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileWriter
import java.nio.file.Path

/**
 * Plugin Initialize Command
 *
 * This is a mandatory sub command for any RET plugin that you create. RET will use it when executing `ret plugin initialize your-plugin`.
 * With this, RET will be able to run the plugin commands, automatically add autocompletion for the plugin, and prompt the user for any property that needs to be configured.
 */
@Command(
    name = "initialize",
    hidden = true,
)
class PluginInitializeCommand(
    private val objectMapper: ObjectMapper,
    private val config: Config,
    private val retConsole: RetConsole,
    osUtils: OsUtils,
) : Runnable {
    @Spec
    lateinit var commandSpec: CommandSpec

    @Parameters(
        arity = "1",
        paramLabel = "<plugin name>",
    )
    lateinit var pluginName: String

    private val pluginDirectory = Path.of(osUtils.getHomeDirectory(), ".ret", "plugins")

    override fun run() {
        val plugin = File(pluginName)
        createPluginInformationFile(plugin)
        configurePlugin(plugin.nameWithoutExtension)
    }

    private fun configurePlugin(pluginName: String) {
        retConsole.out("Hello! Let's start configuring the $pluginName plugin.")

        config.configure {
            promptForOverride("${it.prompt}:", it.key)
        }

        retConsole.out("Done! Feel free to run this command again to make changes.")
        retConsole.out("Wrote configuration to ${config.configFile()}")
    }

    private fun createPluginInformationFile(plugin: File) {
        val rootCommand = commandSpec.root()
        val pluginDefinition = IntrospectionUtil.introspect(rootCommand, plugin.name)

        FileWriter(pluginDirectory.resolve("${plugin.nameWithoutExtension}.plugin").toFile(), false).use {
            it.write(objectMapper.writeValueAsString(pluginDefinition))
        }
    }

    private fun promptForOverride(message: String, key: String) {
        val currentValue = config[key]
        val input = retConsole.prompt(message, currentValue)
        config[key] = input.ifEmpty { currentValue.orEmpty() }
    }
}
