package io.rabobank.ret.commands

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.every
import io.mockk.mockk
import io.rabobank.ret.RetConsole
import io.rabobank.ret.configuration.Answer
import io.rabobank.ret.configuration.Config
import io.rabobank.ret.configuration.ConfigurationProperty
import io.rabobank.ret.configuration.Question
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

    private val objectMapper = jacksonObjectMapper()
    private val config: Config = TestConfig()
    private val pluginConfigFileName = "demo-plugin.json"
    private val retConsole = mockk<RetConsole>(relaxed = true)
    private val osUtils by lazy {
        mockk<OsUtils> {
            every { getHomeDirectory() } returns mockUserHomeDirectory.pathString
        }
    }
    private val pluginsPath by lazy {
        val retFolder = Files.createDirectory(mockUserHomeDirectory.resolve(".ret"))
        val pluginsPath = Files.createDirectory(retFolder.resolve("plugins"))
        val demoPlugin = "demo-plugin.dylib"
        Files.createFile(pluginsPath.resolve(demoPlugin))

        pluginsPath
    }

    @TempDir
    lateinit var mockUserHomeDirectory: Path

    @BeforeEach
    fun before() {
        command = PluginInitializeCommand(jacksonObjectMapper(), config, retConsole, osUtils)
        command.commandSpec = CommandLine.Model.CommandSpec.create()

        command.pluginName = "demo-plugin"

        pluginsPath.resolve(pluginConfigFileName).toFile().delete()
    }

    @Test
    fun `should create plugin information file`() {
        every { retConsole.prompt("Enter your Rabobank project", null) } returns "myProject"
        every { retConsole.prompt("Enter your Rabobank organisation", null) } returns "myOrganisation"
        every { retConsole.prompt("What's the answer to everything (required)", null) } returns "42"

        command.run()

        assertThat(mockUserHomeDirectory.resolve(".ret/plugins/demo-plugin.plugin")).exists()
        assertThat(mockUserHomeDirectory.resolve(".ret/plugins/demo-plugin.json")).exists()
    }

    @Test
    fun newProperty() {
        every { retConsole.prompt("Enter your Rabobank project", any()) } returns "myProject"
        every { retConsole.prompt("Enter your Rabobank organisation", any()) } returns "myOrganisation"
        every { retConsole.prompt("Plugin project", any()) } returns "myPluginProject"
        every { retConsole.prompt("Plugin organisation", any()) } returns "myPluginOrganisation"
        every { retConsole.prompt("What's the answer to everything (required)", any()) } returns "42"

        command.run()

        assertThat(config["project"]).isEqualTo("myProject")
        assertThat(config["organisation"]).isEqualTo("myOrganisation")

        val pluginConfig = objectMapper.readValue<Map<String, String>>(pluginsPath.resolve(pluginConfigFileName).toFile())
        assertThat(pluginConfig).isEqualTo(
            mapOf(
                "demo_project" to "myPluginProject",
                "demo_organisation" to "myPluginOrganisation",
                "demo_question" to "42",
            ),
        )
    }

    @Test
    fun overwriteExistingProperty() {
        config["project"] = "oldProject"
        config["organisation"] = "oldOrganisation"

        val demoConfig = mapOf(
            "demo_project" to "oldPluginProject",
            "demo_organisation" to "oldPluginOrganisation",
            "demo_question" to "420",
        )
        objectMapper.writeValue(pluginsPath.resolve(pluginConfigFileName).toFile(), demoConfig)

        every { retConsole.prompt("Enter your Rabobank project", "oldProject") } returns "newProject"
        every {
            retConsole.prompt("Enter your Rabobank organisation", "oldOrganisation")
        } returns "newOrganisation"
        every { retConsole.prompt("Plugin project", "oldPluginProject") } returns "newPluginProject"
        every { retConsole.prompt("Plugin organisation", "oldPluginOrganisation") } returns "newPluginOrganisation"
        every { retConsole.prompt("What's the answer to everything (required)", "420") } returns "42"

        command.run()

        assertThat(config["project"]).isEqualTo("newProject")
        assertThat(config["organisation"]).isEqualTo("newOrganisation")

        val pluginConfig = objectMapper.readValue<Map<String, String>>(pluginsPath.resolve(pluginConfigFileName).toFile())
        assertThat(pluginConfig).isEqualTo(
            mapOf(
                "demo_project" to "newPluginProject",
                "demo_organisation" to "newPluginOrganisation",
                "demo_question" to "42",
            ),
        )
    }

    @Test
    fun overwriteExistingPropertyWithDefaults() {
        config["project"] = "oldProject"
        config["organisation"] = "oldOrganisation"

        val demoConfig = mapOf(
            "demo_project" to "oldPluginProject",
            "demo_organisation" to "oldPluginOrganisation",
            "demo_question" to "420",
        )
        objectMapper.writeValue(pluginsPath.resolve(pluginConfigFileName).toFile(), demoConfig)

        every { retConsole.prompt("Enter your Rabobank project", "oldProject") } returns ""
        every { retConsole.prompt("Enter your Rabobank organisation", "oldOrganisation") } returns ""
        every { retConsole.prompt("Plugin project", "oldPluginProject") } returns ""
        every { retConsole.prompt("Plugin organisation", "oldPluginOrganisation") } returns ""
        every { retConsole.prompt("What's the answer to everything (required)", "420") } returns ""

        command.run()

        assertThat(config["project"]).isEqualTo("oldProject")
        assertThat(config["organisation"]).isEqualTo("oldOrganisation")

        val pluginConfig = objectMapper.readValue<Map<String, String>>(pluginsPath.resolve(pluginConfigFileName).toFile())
        assertThat(pluginConfig).isEqualTo(demoConfig)
    }

    class TestConfig : Config {
        private val configProps = listOf(
            ConfigurationProperty("project", "Enter your Rabobank project"),
            ConfigurationProperty("organisation", "Enter your Rabobank organisation"),
        )
        private val questions = listOf(
            Question("demo_project", "Plugin project"),
            Question("demo_organisation", "Plugin organisation"),
            Question("demo_question", "What's the answer to everything", required = true),
        )
        private val properties = Properties()

        override fun get(key: String) = properties[key] as String?

        override fun set(key: String, value: String) {
            properties[key] = value
        }

        override fun configure(function: (ConfigurationProperty) -> Unit) {
            configProps.forEach(function)
        }

        override fun prompt(function: (Question) -> Answer): List<Answer> = questions.map(function)

        override fun configFile(): Path = Path.of("test-configuration")
    }
}
