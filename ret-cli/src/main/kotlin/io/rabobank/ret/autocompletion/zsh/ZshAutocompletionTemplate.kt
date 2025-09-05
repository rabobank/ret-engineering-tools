package io.rabobank.ret.autocompletion.zsh

import jakarta.enterprise.context.ApplicationScoped
import picocli.CommandLine
import picocli.CommandLine.Model.OptionSpec
import picocli.CommandLine.Model.PositionalParamSpec

private const val TAB = "    "
private const val AUTOCOMPLETION_FUNCTION_PREFIX = "function:"

@ApplicationScoped
class ZshAutocompletionTemplate {
    /**
     * Generates a ZSH function for a command containing either subcommands
     * or positional parameters and possibly options(/flags).
     */
    fun applyForCommand(
        functionName: String,
        subcommands: List<CommandLine>,
        positionalParameters: List<PositionalParamSpec>,
        options: List<OptionSpec>,
        isRootFunction: Boolean,
    ): String {
        val commandOrParameterSpecActionPairs =
            when {
                subcommands.isNotEmpty() -> generateSubcommandSpecActionPairs(subcommands, functionName)
                positionalParameters.isNotEmpty() -> generatePositionalParameterSpecActionPairs(positionalParameters)
                else -> emptyList()
            }

        val allSpecActionPairs = commandOrParameterSpecActionPairs + generateOptionSpecActionPairs(options)

        val specs = allSpecActionPairs.map { it.spec }
        val actions = allSpecActionPairs.mapNotNull { it.action }

        return """
function $functionName() {
    local context state state_descr line
    local curcontext="${'$'}curcontext"
    typeset -A opt_args
    _arguments -C \
        ${specs.joinToString(separator = " \\\n${TAB.repeat(2)}")}
    ${if (isRootFunction) "RET_COMBINED_OPT_ARGS=()" else ""}
    # merge args from earlier functions with this one
    for key in "${'$'}{(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [${'$'}key]=${'$'}{opt_args[${'$'}key]} ); done
    case "${'$'}state" in
        ${actions.joinToString(separator = "\n${TAB.repeat(2)}")}
    esac
}
            """.trim()
    }

    private fun generateSubcommandSpecActionPairs(
        subcommands: List<CommandLine>,
        parentFunctionName: String,
    ): List<ArgumentSpecActionPair> {
        val overviewValues =
            subcommands.joinToString(" ") {
                val description =
                    it.commandSpec
                        .usageMessage()
                        .description()
                        .firstOrNull() ?: ""
                "'${it.commandName}[$description]'"
            }

        val overviewTitle = parentFunctionName.replace("_", " ")
        val subcommandOverview =
            ArgumentSpecActionPair(
                "\"1:$overviewTitle subcommands:->subcommands_overview\"",
                "subcommands_overview) _values \"autocompletion candidates\" $overviewValues;;",
            )

        val subcommandActions =
            ArgumentSpecActionPair(
                "\"*::arg:->call_subcommand\"",
                "call_subcommand) function=\"${parentFunctionName}_${'$'}{line[1]}\"; " +
                    "_call_function_if_exists \"${'$'}function\";;",
            )

        return listOf(subcommandOverview, subcommandActions)
    }

    private fun generateOptionSpecActionPairs(options: List<OptionSpec>) =
        options.withIndex().map {
            val orderedOptionNames = it.value.names().sortedBy { name -> name.length }
            val namesDefinition =
                if (orderedOptionNames.size == 1) {
                    it.value.names()[0]
                } else {
                    val optionNamesSpaceSeparated = orderedOptionNames.joinToString(" ")
                    val optionNamesCommaSeparated = orderedOptionNames.joinToString(",")
                    "($optionNamesSpaceSeparated)'{$optionNamesCommaSeparated}'"
                }

            val description = it.value.description().joinToString("")
            val isFlagOption = it.value.type() == java.lang.Boolean.TYPE || it.value.type() == Boolean::class.java
            if (isFlagOption) {
                ArgumentSpecActionPair("'$namesDefinition[$description]'")
            } else {
                when (val action = generateAutocompleteAction(it.value.completionCandidates())) {
                    is NoAction -> ArgumentSpecActionPair("'$namesDefinition=[$description]:option:->'")
                    is StaticValuesAction ->
                        ArgumentSpecActionPair("'$namesDefinition=[$description]:option:(${action.staticValues})'")

                    is FunctionCallAction -> {
                        val specValue = "'$namesDefinition=[$description]:option:->option${it.index}'"
                        val actionValue = "option${it.index}) ${action.functionName};;"
                        ArgumentSpecActionPair(specValue, actionValue)
                    }
                }
            }
        }

    private fun generatePositionalParameterSpecActionPairs(positionalParameters: List<PositionalParamSpec>) =
        positionalParameters.withIndex().mapNotNull {
            val description =
                it.value
                    .description()
                    .joinToString("")
                    .ifEmpty { " " }
            val index = it.index + 1

            when (val action = generateAutocompleteAction(it.value.completionCandidates())) {
                is NoAction ->
                    if (it.value.required()) {
                        ArgumentSpecActionPair("\"$index:$description: \"")
                    } else {
                        // positional parameter(s) do not have autocompletion candidates and is optional,
                        // so it's not needed to generate candidates
                        null
                    }

                is StaticValuesAction -> {
                    val argumentsEntry = "\"$index:$description:->parameter${it.index}\""
                    val caseEntry =
                        "parameter${it.index}) _values \"autocompletion candidates\" ${action.staticValues};;"
                    ArgumentSpecActionPair(argumentsEntry, caseEntry)
                }

                is FunctionCallAction -> {
                    val argumentsEntry = "\"$index:$description:->parameter${it.index}\""
                    val caseEntry = "parameter${it.index}) ${action.functionName};;"
                    ArgumentSpecActionPair(argumentsEntry, caseEntry)
                }
            }
        }

    private fun generateAutocompleteAction(completionCandidates: Iterable<String>?): AutoCompleteAction {
        val candidates = completionCandidates?.toList() ?: listOf()
        return when {
            candidates.isEmpty() -> NoAction
            candidates.size == 1 && candidates[0].startsWith(AUTOCOMPLETION_FUNCTION_PREFIX) ->
                FunctionCallAction(candidates[0].removePrefix(AUTOCOMPLETION_FUNCTION_PREFIX))

            else -> StaticValuesAction(candidates.joinToString(separator = " ", prefix = "'", postfix = "'"))
        }
    }

    /**
     * This data class holds the spec which will be added to ZSH's _arguments method,
     * and the action describes what's done when a certain argument spec is matched.
     */
    data class ArgumentSpecActionPair(
        val spec: String,
        val action: String? = null,
    )

    sealed interface AutoCompleteAction

    data object NoAction : AutoCompleteAction

    data class FunctionCallAction(
        val functionName: String,
    ) : AutoCompleteAction

    data class StaticValuesAction(
        val staticValues: String,
    ) : AutoCompleteAction
}
