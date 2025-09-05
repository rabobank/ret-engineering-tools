package io.rabobank.ret.util

import io.rabobank.ret.plugin.Argument
import io.rabobank.ret.plugin.Option
import io.rabobank.ret.plugin.PluginCommand
import io.rabobank.ret.plugin.PluginDefinition
import picocli.CommandLine.Model.CommandSpec

object IntrospectionUtil {
    fun introspect(
        commandSpec: CommandSpec,
        pluginName: String,
    ) = PluginDefinition(
        pluginName,
        listOf(generateCommand(commandSpec)),
        loadCustomZshAutocompletion(),
    )

    private fun loadCustomZshAutocompletion(): String? =
        javaClass
            .getResourceAsStream("/autocompletion/zsh/completions.zsh")
            ?.bufferedReader()
            ?.readText()

    private fun generateCommand(commandSpec: CommandSpec): PluginCommand {
        val commandName = commandSpec.name()

        val arguments =
            commandSpec.positionalParameters().map {
                Argument(
                    it.paramLabel(),
                    it.index().toString(),
                    it.completionCandidates()?.toList() ?: emptyList(),
                    it.arity().toString(),
                )
            }
        val options =
            commandSpec
                .options()
                .map {
                    Option(
                        it.names().toList(),
                        it.type().canonicalName,
                        it.completionCandidates()?.toList() ?: emptyList(),
                    )
                }

        val subcommands =
            commandSpec
                .subcommands()
                .values
                .map {
                    generateCommand(it.commandSpec)
                }

        val description = commandSpec.usageMessage().description().joinToString(" | ")

        val hidden = commandSpec.usageMessage().hidden()

        return PluginCommand(
            commandName,
            arguments,
            options,
            subcommands,
            description,
            hidden,
        )
    }
}
