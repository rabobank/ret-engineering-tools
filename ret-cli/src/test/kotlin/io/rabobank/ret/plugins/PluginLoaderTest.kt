package io.rabobank.ret.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.rabobank.ret.config.Environment
import io.rabobank.ret.context.ExecutionContext
import io.rabobank.ret.plugin.Plugin
import io.rabobank.ret.plugin.PluginDefinition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `command with subcommand arguments without positions`(argumentCount: Int) {
        val plugin = loadPlugin("missing-position.json")
        val loader = PluginLoader(listOf(plugin), objectMapper, ExecutionContext(), Environment.CLI)
        val command = loader.getPluginCommands(commandLine).first()
        val subcommand = command.commandSpec.subcommands()["open"]!!.commandSpec

        assertThat(subcommand.positionalParameters().map { it.index().toString() })
            .containsExactly("0", "1")
        assertThat(subcommand.options().first().type()).isEqualTo(Boolean::class.javaPrimitiveType)

        val arguments = listOf("pipeline-id", "run-id").take(argumentCount)
        val result =
            CommandLine(command.commandSpec)
                .parseArgs("open", "--enabled", *arguments.toTypedArray())
                .subcommand()

        assertThat(result.matchedPositionalValue<String>(0, null)).isEqualTo(arguments.getOrNull(0))
        assertThat(result.matchedPositionalValue<String>(1, null)).isEqualTo(arguments.getOrNull(1))
        assertThat(result.matchedOptionValue<Boolean>("--enabled", false)).isTrue()
    }

    private fun loadPlugin(file: String): Plugin {
        val definition =
            this.javaClass.classLoader
                .getResourceAsStream("testdata/$file")!!
                .bufferedReader()
                .readText()

        return objectMapper.readValue(definition, PluginDefinition::class.java).let {
            Plugin(it, Path.of(it.libName))
        }
    }

    @Command
    class EmptyCommand
}
