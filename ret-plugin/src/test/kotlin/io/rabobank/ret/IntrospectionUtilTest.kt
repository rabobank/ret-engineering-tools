package io.rabobank.ret

import io.rabobank.ret.plugin.Argument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

class IntrospectionUtilTest {

    @Test
    fun `should include root command`() {
        val pluginName = "test-plugin"
        val commandLine = CommandLine(TestCommand())

        val definition = IntrospectionUtil.introspect(commandLine.commandSpec.root(), pluginName)
        assertThat(definition.libName).isEqualTo(pluginName)
        assertThat(definition.commands[0].name).isEqualTo("test")
        assertThat(definition.commands[0].description).isEqualTo("description")
        assertThat(definition.commands[0].arguments).isEmpty()
        assertThat(definition.commands[0].options).isEmpty()
        assertThat(definition.commands[0].subcommands).hasSize(2)
    }

    @Test
    fun `should include subcommand`() {
        val pluginName = "test-plugin"
        val commandLine = CommandLine(TestCommand())

        val definition = IntrospectionUtil.introspect(commandLine.commandSpec.root(), pluginName)
        val subcommands = definition.commands[0].subcommands

        assertThat(subcommands[1].name).isEqualTo("subcommand")
        assertThat(subcommands[1].arguments).containsExactly(Argument("<input>", "0..*", emptyList(), "1"))
        assertThat(subcommands[1].hidden).isFalse()
        assertThat(subcommands[1].options).isEmpty()
        assertThat(subcommands[1].description).isEmpty()
    }

    @Test
    fun `should include hidden subcommand`() {
        val pluginName = "test-plugin"
        val commandLine = CommandLine(TestCommand())

        val definition = IntrospectionUtil.introspect(commandLine.commandSpec.root(), pluginName)
        val subcommands = definition.commands[0].subcommands

        assertThat(subcommands[0].name).isEqualTo("hiddencommand")
        assertThat(subcommands[0].arguments).isEmpty()
        assertThat(subcommands[0].options).isEmpty()
        assertThat(subcommands[0].description).isEmpty()
        assertThat(subcommands[0].hidden).isTrue()
    }

    @Command(name = "test", description = ["description"])
    class TestCommand {

        @Suppress("unused")
        @Command(name = "subcommand")
        fun subcommand(@Parameters(arity = "1", index = "0..*", paramLabel = "<input>") input: String) {
            // No-op
        }

        @Command(name = "hiddencommand", hidden = true)
        fun hiddencommand() {
            // No-op
        }
    }
}
