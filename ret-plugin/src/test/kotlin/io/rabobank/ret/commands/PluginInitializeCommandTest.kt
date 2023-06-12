package io.rabobank.ret.commands

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import io.rabobank.ret.util.OsUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import picocli.CommandLine
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.pathString

class PluginInitializeCommandTest {
    private lateinit var command: PluginInitializeCommand
    private val config: Config = TestConfig()
    private val retConsole = mockk<RetConsole>(relaxed = true)
    private val osUtils by lazy {
        mockk<OsUtils> {
            every { getHomeDirectory() } returns mockUserHomeDirectory.pathString
        }
    }

    @TempDir
    lateinit var mockUserHomeDirectory: Path

    @BeforeEach
    fun before() {
        command = PluginInitializeCommand(jacksonObjectMapper(), config, retConsole, osUtils)
        command.commandSpec = CommandLine.Model.CommandSpec.create()

        val retFolder = Files.createDirectory(mockUserHomeDirectory.resolve(".ret"))
        val pluginsPath = Files.createDirectory(retFolder.resolve("plugins"))
        val demoPlugin = "demo-plugin.dylib"
        Files.createFile(pluginsPath.resolve(demoPlugin))

        command.pluginName = "demo-plugin.dylib"
    }

    @Test
    fun `should create plugin information file`() {
        every { retConsole.prompt("Enter your Rabobank project:", null) } returns "myProject"
        every { retConsole.prompt("Enter your Rabobank organisation:", null) } returns "myOrganisation"

        command.run()
        assertThat(mockUserHomeDirectory.resolve(".ret/plugins/demo-plugin.plugin")).exists()
    }

    @Test
    fun newProperty() {
        every { retConsole.prompt("Enter your Rabobank project:", null) } returns "myProject"
        every { retConsole.prompt("Enter your Rabobank organisation:", null) } returns "myOrganisation"

        command.run()

        assertThat(config["project"]).isEqualTo("myProject")
        assertThat(config["organisation"]).isEqualTo("myOrganisation")
    }

    @Test
    fun overwriteExistingProperty() {
        config["project"] = "oldProject"
        config["organisation"] = "oldOrganisation"

        every { retConsole.prompt("Enter your Rabobank project:", "oldProject") } returns "newProject"
        every {
            retConsole.prompt(
                "Enter your Rabobank organisation:",
                "oldOrganisation",
            )
        } returns "newOrganisation"

        command.run()

        assertThat(config["project"]).isEqualTo("newProject")
        assertThat(config["organisation"]).isEqualTo("newOrganisation")
    }

    @Test
    fun overwriteExistingPropertyWithDefaults() {
        config["project"] = "oldProject"
        config["organisation"] = "oldOrganisation"

        every { retConsole.prompt("Enter your Rabobank project:", "oldProject") } returns ""
        every { retConsole.prompt("Enter your Rabobank organisation:", "oldOrganisation") } returns ""

        command.run()

        assertThat(config["project"]).isEqualTo("oldProject")
        assertThat(config["organisation"]).isEqualTo("oldOrganisation")
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

        override fun configFile(): Path = Path.of("test-configuration")
    }
}
