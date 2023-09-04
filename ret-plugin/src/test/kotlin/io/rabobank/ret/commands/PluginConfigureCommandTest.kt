package io.rabobank.ret.commands

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.every
import io.mockk.mockk
import io.rabobank.ret.RetConsole
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import picocli.CommandLine.Model.CommandSpec
import java.nio.file.Files
import java.nio.file.Path

class PluginConfigureCommandTest {
    private lateinit var command: PluginConfigureCommand

    private val objectMapper = jacksonObjectMapper()
    private val pluginName = "demo"
    private val retConsole = mockk<RetConsole>(relaxed = true) {
        every { prompt(any(), any()) } answers { fail { "No prompt configured for args '$args'" } }
    }
    private val pluginsPath by lazy {
        val retFolder = Files.createDirectory(mockUserHomeDirectory.resolve(".ret"))
        val pluginsPath = Files.createDirectory(retFolder.resolve("plugins"))
        val demoPlugin = "$pluginName-plugin.dylib"
        Files.createFile(pluginsPath.resolve(demoPlugin))

        pluginsPath
    }
    private val config by lazy { TestConfig(pluginsPath) }

    @TempDir
    lateinit var mockUserHomeDirectory: Path

    @BeforeEach
    fun before() {
        command = PluginConfigureCommand(config, retConsole, jacksonObjectMapper())
        command.commandSpec = CommandSpec.create()
            .parent(CommandSpec.create().name(pluginName))
    }

    @AfterEach
    fun tearDown() {
        FileUtils.deleteQuietly(mockUserHomeDirectory.toFile())
    }

    @Test
    fun newProperty() {
        every { retConsole.prompt("Enter your Rabobank project:", any()) } returns "myProject"
        every { retConsole.prompt("Enter your Rabobank organisation:", any()) } returns "myOrganisation"

        command.run()

        val pluginConfig = readConfig()
        assertThat(pluginConfig).isEqualTo(
            mapOf(
                "project" to "myProject",
                "organisation" to "myOrganisation",
            ),
        )
    }

    @Test
    fun overwriteExistingProperty() {
        val demoConfig = mapOf(
            "project" to "oldProject",
            "organisation" to "oldOrganisation",
        )
        storeConfig(demoConfig)

        every { retConsole.prompt("Enter your Rabobank project:", "oldProject") } returns "newProject"
        every {
            retConsole.prompt("Enter your Rabobank organisation:", "oldOrganisation")
        } returns "newOrganisation"

        command.run()

        val pluginConfig = readConfig()
        assertThat(pluginConfig).isEqualTo(
            mapOf(
                "project" to "newProject",
                "organisation" to "newOrganisation",
            ),
        )
    }

    @Test
    fun overwriteExistingPropertyWithDefaults() {
        val demoConfig = mapOf(
            "project" to "oldProject",
            "organisation" to "oldOrganisation",
        )
        storeConfig(demoConfig)

        every { retConsole.prompt("Enter your Rabobank project:", "oldProject") } returns ""
        every { retConsole.prompt("Enter your Rabobank organisation:", "oldOrganisation") } returns ""

        command.run()

        val pluginConfig = readConfig()
        assertThat(pluginConfig).isEqualTo(demoConfig)
    }

    private fun storeConfig(demoConfig: Map<String, String>) {
        demoConfig.forEach { (k, v) -> config[k] = v }
    }

    private fun readConfig() =
        objectMapper.readValue<Map<String, String>>(pluginsPath.resolve("$pluginName.json").toFile())
}
