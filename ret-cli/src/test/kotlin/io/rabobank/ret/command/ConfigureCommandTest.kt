package io.rabobank.ret.command

import io.rabobank.ret.RetConsole
import io.rabobank.ret.autocompletion.zsh.ZshAutocompletionGenerator
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import picocli.CommandLine.Model.CommandSpec
import java.nio.file.Path
import java.util.*

internal class ConfigureCommandTest {

    private lateinit var command: ConfigureCommand

    private lateinit var config: Config
    private lateinit var retConsole: RetConsole
    private lateinit var zshAutocompletionGenerator: ZshAutocompletionGenerator

    @BeforeEach
    fun before() {
        config = TestConfig()
        retConsole = mock()
        zshAutocompletionGenerator = mock()
        command = ConfigureCommand(retConsole, zshAutocompletionGenerator, emptyList())
        command.commandSpec = CommandSpec.create()
    }

    @Test
    fun configureAutocompleteTest() {
        command.printInstallAutocompleteCommand("zsh")

        verify(retConsole).out(
            """
            To install RET autocompletion, run the following command
                echo 'source <(ret configure autocomplete-zsh)' >>~/.zshrc && source ~/.zshrc
            """.trimIndent()
        )
    }

    @Test
    fun configureAutocompleteTestUnsupportedShell() {
        command.printInstallAutocompleteCommand("martinshell")
        verify(retConsole).out("Autocompletion for shell martinshell not supported.")
    }

    @Test
    fun printZshAutocompletionScript() {
        `when`(zshAutocompletionGenerator.generate(any())).thenReturn("mocked zsh autocompletion file")
        command.printZshAutocompletionScript()
        verify(retConsole).out("mocked zsh autocompletion file")
    }

    class TestConfig : Config {
        private val configProps = listOf(
            ConfigurationProperty("project", "Enter your Rabobank project"),
            ConfigurationProperty("organisation", "Enter your Rabobank organisation")
        )
        private val properties = Properties()

        override fun get(key: String) = properties[key] as String?

        override fun set(key: String, value: String) {
            properties[key] = value
        }

        override fun configure(function: (ConfigurationProperty) -> Unit) {
            configProps.forEach(function)
        }

        override fun configFile(): Path = Path.of("test-configuration")
    }
}
