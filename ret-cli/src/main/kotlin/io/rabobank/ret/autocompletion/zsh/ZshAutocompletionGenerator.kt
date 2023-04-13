package io.rabobank.ret.autocompletion.zsh

import picocli.CommandLine
import picocli.CommandLine.Model.CommandSpec
import javax.enterprise.context.ApplicationScoped

private const val SHEBANG = "#!/bin/zsh"

@ApplicationScoped
class ZshAutocompletionGenerator(private val template: ZshAutocompletionTemplate) {

    /**
     * This function generates the full ZSH autocompletion script for the command that's provided and all subcommands
     */
    fun generate(commandSpec: CommandSpec): String {
        val rootCommandLine = commandSpec.commandLine()
        val functions = generateFunctionForCommandsRecursively(rootCommandLine, "", true)

        return buildString {
            appendLine(SHEBANG)
            functions.forEach { appendLine(it) }
            loadLinesForResource("footer.sh").forEach { appendLine(it) }
        }
    }

    private fun loadLinesForResource(resourceFile: String) =
        ZshAutocompletionGenerator::class.java.getResourceAsStream("/autocompletion/zsh/$resourceFile")
            ?.bufferedReader()?.lines()
            ?: throw IllegalStateException("Cannot find $resourceFile for the autocompletion script")

    /**
     * This function generates a ZSH function for a command and calls this function recursively for all of its subcommands.
     * @return A list containing all generated functions, ordered by hierarchy top-down.
     */
    private fun generateFunctionForCommandsRecursively(commandLine: CommandLine, functionPrefix: String, isRootFunction: Boolean): List<String> {
        val functionName = "${functionPrefix}_${commandLine.commandName}"

        val subcommands = commandLine.subcommands.values
            .filterNot { it.commandSpec.usageMessage().hidden() }
            .sortedBy { it.commandName }

        val options = commandLine.commandSpec
            .options()
            .filterNot { it.hidden() }
            .sortedBy { it.shortestName() }

        val positionalParameters = commandLine.commandSpec.positionalParameters()

        val shouldGenerateFunction = (subcommands + positionalParameters + options).isNotEmpty()

        return if (shouldGenerateFunction) {
            val function = template.applyForCommand(functionName, subcommands, positionalParameters, options, isRootFunction)
            val subcommandFunctions = subcommands.flatMap { generateFunctionForCommandsRecursively(it, functionName, false) }
            listOf(function) + subcommandFunctions
        } else listOf()
    }
}
