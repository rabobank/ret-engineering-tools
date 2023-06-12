package io.rabobank.ret.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import io.rabobank.ret.RetContext
import io.rabobank.ret.config.Environment
import io.rabobank.ret.context.ExecutionContext
import io.rabobank.ret.plugin.Plugin
import io.rabobank.ret.plugin.PluginCommand
import jakarta.enterprise.context.ApplicationScoped
import picocli.CommandLine
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Model.OptionSpec
import picocli.CommandLine.Model.PositionalParamSpec
import picocli.CommandLine.ParseResult
import kotlin.io.path.pathString

@ApplicationScoped
class PluginLoader(
    private val plugins: List<Plugin>,
    private val objectMapper: ObjectMapper,
    private val executionContext: ExecutionContext,
    private val environment: Environment,
) {

    fun getPluginCommands(commandLine: CommandLine): List<PluginCommandEntry> =
        plugins.flatMap { plugin ->
            plugin.pluginDefinition.commands.map {
                val spec = createCommandSpec(it, it.name, commandLine, plugin)
                PluginCommandEntry(it.name, spec)
            }
        }

    private fun createCommandSpec(
        command: PluginCommand,
        topCommand: String,
        commandLine: CommandLine,
        plugin: Plugin,
    ): CommandSpec {
        val commandSpec = CommandSpec.wrapWithoutInspection(
            Runnable {
                System.load(plugin.pluginLocation.pathString)
                val retContext = createRetContext(commandLine.parseResult, topCommand)
                val isolate = RetPlugin.createIsolate()
                RetPlugin.invoke(isolate, objectMapper.writeValueAsString(retContext))
            },
        )

        commandSpec.usageMessage().description(command.description)
        commandSpec.usageMessage().hidden(command.hidden)

        command.arguments.forEach {
            commandSpec.addPositional(
                PositionalParamSpec.builder()
                    .paramLabel(it.name)
                    .index(it.position)
                    .arity(it.arity)
                    .completionCandidates(it.completionCandidates)
                    .build(),
            )
        }

        command.options.forEach {
            commandSpec.addOption(
                OptionSpec.builder(it.names.toTypedArray())
                    .type(Class.forName(it.type))
                    .completionCandidates(it.completionCandidates)
                    .build(),
            )
        }

        command.subcommands.forEach {
            commandSpec.addSubcommand(it.name, createCommandSpec(it, topCommand, commandLine, plugin))
        }

        return commandSpec
    }

    private fun createRetContext(parseResult: ParseResult, topCommand: String): RetContext =
        RetContext(
            parseResult.originalArgs().filter { it != topCommand },
            environment.name,
            executionContext.repositoryName(),
            executionContext.branchName(),
        )
}
