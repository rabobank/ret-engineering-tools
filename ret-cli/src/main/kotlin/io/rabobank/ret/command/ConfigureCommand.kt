package io.rabobank.ret.command

import io.rabobank.ret.RetConsole
import io.rabobank.ret.autocompletion.zsh.ZshAutocompletionGenerator
import io.rabobank.ret.config.Logged
import io.rabobank.ret.plugin.Plugin
import picocli.CommandLine.Command
import picocli.CommandLine.Model
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec

@Command(
    name = "configure",
    description = ["Initialize or update RET configuration file"]
)
@Logged
class ConfigureCommand(
    private val retConsole: RetConsole,
    private val zshAutocompletionGenerator: ZshAutocompletionGenerator,
    private val plugins: List<Plugin>,
) {

    @Spec
    lateinit var commandSpec: Model.CommandSpec

    @Command(name = "autocomplete", description = ["Prints the command to install autocomplete. Supported shells are: zsh"])
    fun printInstallAutocompleteCommand(@Parameters(description = ["shells"]) shell: String) {
        when (shell.lowercase()) {
            "zsh" -> printConfigureZsh()
            else -> retConsole.out("Autocompletion for shell $shell not supported.")
        }
    }

    @Command(name = "autocomplete-zsh", description = ["Prints the autocompletion script for ZSH"])
    fun printZshAutocompletionScript() {
        val rootCommandSpec = commandSpec.root()
        val autoCompleteScript = zshAutocompletionGenerator.generate(rootCommandSpec)
        retConsole.out(autoCompleteScript)

        plugins.forEach { plugin ->
            plugin.pluginDefinition.customZshAutocompletion?.let {
                retConsole.out(it)
            }
        }
    }

    private fun printConfigureZsh() {
        retConsole.out(
            """
            To install RET autocompletion, run the following command
                echo 'source <(ret configure autocomplete-zsh)' >>~/.zshrc && source ~/.zshrc
            """.trimIndent()
        )
    }
}
