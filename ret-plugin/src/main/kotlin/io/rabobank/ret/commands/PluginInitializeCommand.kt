package io.rabobank.ret.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.plugin.Argument
import io.rabobank.ret.plugin.Option
import io.rabobank.ret.plugin.PluginCommand
import io.rabobank.ret.plugin.PluginDefinition
import io.rabobank.ret.util.OsUtils
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileWriter
import java.nio.file.Path

@Command(
    name = "initialize",
    hidden = true,
)
class PluginInitializeCommand(
    private val objectMapper: ObjectMapper,
    private val config: Config,
    private val retConsole: RetConsole,
    osUtils: OsUtils
) : Runnable {
    @Spec
    lateinit var commandSpec: CommandSpec

    @Parameters(
        arity = "1",
        paramLabel = "<plugin name>"
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
        val pluginDefinition = PluginDefinition(
            plugin.name,
            listOf(generateCommand(rootCommand)),
            loadCustomZshAutocompletion()
        )

        FileWriter(pluginDirectory.resolve("${plugin.nameWithoutExtension}.plugin").toFile(), false).use {
            it.write(objectMapper.writeValueAsString(pluginDefinition))
        }
    }

    private fun loadCustomZshAutocompletion(): String? =
        javaClass.getResourceAsStream("/autocompletion/zsh/completions.zsh")
            ?.bufferedReader()
            ?.readText()


    private fun generateCommand(commandSpec: CommandSpec): PluginCommand {
        val commandName = commandSpec.name()

        val arguments = commandSpec.positionalParameters().map {
            Argument(it.paramLabel(), 0, it.completionCandidates()?.toList() ?: emptyList())
        }
        val options = commandSpec.options()
            .map { Option(it.names().toList(), it.type().canonicalName, it.completionCandidates()?.toList() ?: emptyList()) }

        val subcommands = commandSpec.subcommands().values.filter { !it.commandSpec.usageMessage().hidden() }
            .map {
                generateCommand(it.commandSpec)
            }
        val description = commandSpec.usageMessage().description().joinToString(" | ")

        return PluginCommand(
            commandName,
            arguments,
            options,
            subcommands,
            description,
        )
    }

    private fun promptForOverride(message: String, key: String) {
        val currentValue = config[key]
        val input = retConsole.prompt(message, currentValue)
        config[key] = input.ifEmpty { currentValue.orEmpty() }
    }

}
