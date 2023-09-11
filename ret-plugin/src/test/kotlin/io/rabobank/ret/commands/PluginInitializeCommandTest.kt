package io.rabobank.ret.commands

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.rabobank.ret.RetConsole
import io.rabobank.ret.util.OsUtils
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import picocli.CommandLine
import picocli.CommandLine.Model.CommandSpec
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

class PluginInitializeCommandTest {
    private lateinit var command: PluginInitializeCommand

    private val pluginName = "demo"
    private val osUtils by lazy {
        spyk(OsUtils()) {
            every { getHomeDirectory() } returns mockUserHomeDirectory.pathString
        }
    }
    private val retConsole = mockk<RetConsole>(relaxed = true) {
        every { prompt(any(), any()) } answers { fail { "No prompt configured for args '$args'" } }
    }
    private val retFolder by lazy { Files.createDirectory(mockUserHomeDirectory.resolve(".ret")) }
    private val pluginsPath by lazy {
        val path = Files.createDirectory(retFolder.resolve("plugins"))
        val demoPlugin = "$pluginName.${osUtils.getPluginFileExtension()}"
        Files.createFile(path.resolve(demoPlugin))
        path
    }

    @TempDir
    lateinit var mockUserHomeDirectory: Path

    @BeforeEach
    fun before() {
        command = PluginInitializeCommand(retConsole, jacksonObjectMapper(), osUtils)
        val commandLine = CommandLine(
            CommandSpec.create()
                .parent(CommandLine(CommandSpec.create().name(pluginName)).commandSpec),
        )
        command.commandSpec = commandLine.commandSpec
        command.pluginPath = pluginsPath.resolve("$pluginName.${osUtils.getPluginFileExtension()}").nameWithoutExtension
    }

    @AfterEach
    fun tearDown() {
        FileUtils.deleteQuietly(mockUserHomeDirectory.toFile())
    }

    @Test
    fun `should create plugin information file`() {
        command.run()

        assertThat(
            mockUserHomeDirectory.resolve(
                ".ret/plugins/$pluginName.${osUtils.getPluginFileExtension()}",
            ),
        ).exists()
        assertThat(mockUserHomeDirectory.resolve(".ret/plugins/$pluginName.plugin")).exists()
    }

    @Test
    fun `should overwrite plugin information file if absolute path is passed and accepted`() {
        val diffPlugin = pluginsPath.resolve(Path.of("extra", "demo-plugin.${osUtils.getPluginFileExtension()}"))
        diffPlugin.parent.createDirectories()
        Files.createFile(diffPlugin)
        command.pluginPath = diffPlugin.toFile().path

        every { retConsole.prompt("The plugin is already installed, overwrite [Yn]?", "Y") } returns "Y"

        command.run()

        assertThat(
            mockUserHomeDirectory.resolve(
                ".ret/plugins/$pluginName.${osUtils.getPluginFileExtension()}",
            ),
        ).exists()
        assertThat(mockUserHomeDirectory.resolve(".ret/plugins/$pluginName.plugin")).exists()
    }
}
