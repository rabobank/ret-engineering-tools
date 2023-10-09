package io.rabobank.ret.command

import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import io.rabobank.ret.plugins.RetPlugin
import io.rabobank.ret.util.Logged
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec

@Command(
    name = "initialize",
    description = ["Initialize plugin"],
)
@Logged
class PluginInitializeCommand(
    private val retConsole: RetConsole,
) : Runnable {
    @Parameters(
        arity = "1",
        paramLabel = "<plugin file>",
        description = ["Absolute path to plugin"],
    )
    lateinit var pluginFile: String

    @Spec
    lateinit var commandSpec: CommandSpec

    override fun run() {
        runCatching {
            System.load(pluginFile)
            val isolateThread = RetPlugin.createIsolate()
            RetPlugin.initialize(isolateThread, pluginFile)
        }.onFailure {
            retConsole.errorOut("Unable to load plugin file $pluginFile: ${it.message}")
            Log.error("Unable to load plugin file $pluginFile", it)
        }.getOrThrow()
    }
}
