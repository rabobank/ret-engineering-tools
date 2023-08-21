package io.rabobank.ret.command

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rabobank.ret.RetConsole
import io.rabobank.ret.autocompletion.zsh.ZshAutocompletionGenerator
import io.rabobank.ret.configuration.Answer
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import io.rabobank.ret.configuration.Question
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine.Model.CommandSpec
import java.nio.file.Path
import java.util.Properties

internal class ConfigureCommandTest {

    private lateinit var command: ConfigureCommand

    private val retConsole = mockk<RetConsole>(relaxed = true)
    private val zshAutocompletionGenerator = mockk<ZshAutocompletionGenerator>()

    @BeforeEach
    fun before() {
        command = ConfigureCommand(retConsole, zshAutocompletionGenerator, emptyList())
        command.commandSpec = CommandSpec.create()
    }

    @Test
    fun configureAutocompleteTest() {
        command.printInstallAutocompleteCommand("zsh")

        verify {
            retConsole.out(
                """
                To install RET autocompletion, run the following command
                    echo 'source <(ret configure autocomplete-zsh)' >>~/.zshrc && source ~/.zshrc
                """.trimIndent(),
            )
        }
    }

    @Test
    fun configureAutocompleteTestUnsupportedShell() {
        command.printInstallAutocompleteCommand("martinshell")
        verify { retConsole.out("Autocompletion for shell martinshell not supported.") }
    }

    @Test
    fun printZshAutocompletionScript() {
        every { zshAutocompletionGenerator.generate(any()) } returns "mocked zsh autocompletion file"
        command.printZshAutocompletionScript()
        verify { retConsole.out("mocked zsh autocompletion file") }
    }

    class TestConfig : Config {
        private val configProps = listOf(
            ConfigurationProperty("project", "Enter your Rabobank project"),
            ConfigurationProperty("organisation", "Enter your Rabobank organisation"),
        )
        private val properties = Properties()

        override fun get(key: String) = properties[key] as String?

        override fun set(key: String, value: String) {
            properties[key] = value
        }

        override fun configure(function: (ConfigurationProperty) -> Unit) {
            configProps.forEach(function)
        }

        override fun prompt(function: (Question) -> Answer): List<Answer> {
            TODO("Not yet implemented")
        }

        override fun configFile(): Path = Path.of("test-configuration")
    }
}
