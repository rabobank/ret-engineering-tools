package io.rabobank.ret.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import io.rabobank.ret.util.IntrospectionUtil
import io.rabobank.ret.util.Logged
import io.rabobank.ret.util.OsUtils
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createDirectories

/**
 * Plugin Initialize Command
 *
 * This is a mandatory sub command for any RET plugin that you create. RET will use it when executing `ret plugin initialize your-plugin`.
 * With this, RET will be able to run the plugin commands, automatically add autocompletion for the plugin, and prompt the user for any property that needs to be configured.
 */
@Command(
    name = "initialize",
    hidden = true,
)
@Logged
class PluginInitializeCommand(
    private val retConsole: RetConsole,
    private val objectMapper: ObjectMapper,
    private val osUtils: OsUtils,
) : Runnable {
    @Spec
    lateinit var commandSpec: CommandSpec

    @Parameters(
        arity = "0..1",
        paramLabel = "<path to plugin>",
    )
    lateinit var pluginPath: String

    private val pluginDirectory = osUtils.getRetPluginsDirectory()

    override fun run() {
        Log.debug("Creating directories")
        pluginDirectory.createDirectories()

        val parent = commandSpec.parent()
        val pluginName = parent.name()

        retConsole.out("Initializing plugin '$pluginName'")
        val pluginNameWithFallbackToParent = if (this::pluginPath.isInitialized) pluginPath else pluginName
        val plugin = File(pluginNameWithFallbackToParent)
        val pluginFile = pluginDirectory.resolve("$pluginName.${osUtils.getPluginFileExtension()}").toFile()

        if (plugin.isAbsolute) {
            if (pluginFile.exists() && plugin != pluginFile) {
                val overwrite = retConsole.prompt("The plugin is already installed, overwrite [Yn]?", "Y")
                if (overwrite.isBlank() || overwrite.lowercase() == "y") {
                    plugin.copyTo(pluginFile, true)
                    Log.info("Plugin file overwritten for '$pluginName'")
                }
            } else {
                plugin.copyTo(pluginFile, true)
                Log.info("Copied plugin file for '$pluginName'")
            }
        }

        if (!pluginFile.exists()) {
            throw FileNotFoundException("$pluginFile does not exist")
        }

        Log.info("Generating/updating plugin file for '$pluginName'")
        createPluginInformationFile(pluginName)

        if (parent.subcommands().containsKey("configure")) {
            parent.commandLine().execute("configure")
        } else {
            retConsole.out(
                "Since version 0.2 RET no longer initializes plugin specific configuration during 'initialize', " +
                    "this is now moved to 'configure'. " +
                    "Plugin '$pluginName' does not seem to have a subcommand 'configure' (PluginConfigureCommand).",
            )
        }
    }

    private fun createPluginInformationFile(pluginName: String) {
        val pluginDefinition = IntrospectionUtil.introspect(commandSpec.root(), pluginName)
        objectMapper.writeValue(pluginDirectory.resolve("$pluginName.plugin").toFile(), pluginDefinition)
    }
}
