package io.rabobank.ret.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.rabobank.ret.config.Environment
import io.rabobank.ret.context.ExecutionContext
import io.rabobank.ret.plugin.Plugin
import io.rabobank.ret.plugin.PluginDefinition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import picocli.CommandLine
import picocli.CommandLine.Command
import java.nio.file.Path

class PluginLoaderTest {
    private val objectMapper = jacksonObjectMapper()
    private val commandLine = CommandLine(EmptyCommand())

    @Test
    fun `simple command with description`() {
        val plugin = loadPlugin("simplecommand.json")
        val loader = PluginLoader(listOf(plugin), objectMapper, ExecutionContext(), Environment.CLI)
        val command = loader.getPluginCommands(commandLine).first()

        assertThat(command.name).isEqualTo("test")
        assertThat(command.commandSpec.usageMessage().description()).containsExactly("description")
    }

    @Test
    fun `command with argument`() {
        val plugin = loadPlugin("argument.json")
        val loader = PluginLoader(listOf(plugin), objectMapper, ExecutionContext(), Environment.CLI)
        val command = loader.getPluginCommands(commandLine).first()
        val argument = command.commandSpec.positionalParameters()[0]

        assertThat(argument.paramLabel()).isEqualTo("argumentName")
        assertThat(argument.index().originalValue()).isEqualTo("0..3")
        assertThat(argument.arity().originalValue()).isEqualTo("0..*")
        assertThat(argument.completionCandidates()).containsExactly("completionCandidate")
    }

    @Test
    fun `command with option`() {
        val plugin = loadPlugin("option.json")
        val loader = PluginLoader(listOf(plugin), objectMapper, ExecutionContext(), Environment.CLI)
        val command = loader.getPluginCommands(commandLine).first()
        val option = command.commandSpec.options()[0]

        assertThat(option.names()).containsExactly("-o", "--option")
        assertThat(option.type()).isEqualTo(String::class.java)
        assertThat(option.completionCandidates()).containsExactly("completionCandidate")
    }

    @Test
    fun `command with subcommand`() {
        val plugin = loadPlugin("subcommand.json")
        val loader = PluginLoader(listOf(plugin), objectMapper, ExecutionContext(), Environment.CLI)
        val command = loader.getPluginCommands(commandLine).first()
        val subcommand = command.commandSpec.subcommands()["subcommand"]!!

        assertThat(subcommand.commandSpec.name()).isEqualTo("subcommand")
        assertThat(subcommand.commandSpec.usageMessage().hidden()).isTrue()
    }

    private fun loadPlugin(file: String): Plugin {
        val definition = this.javaClass.classLoader.getResourceAsStream("testdata/$file")!!
            .bufferedReader()
            .readText()

        return objectMapper.readValue(definition, PluginDefinition::class.java).let {
            Plugin(it, Path.of(it.libName))
        }
    }

    @Command
    class EmptyCommand
}
