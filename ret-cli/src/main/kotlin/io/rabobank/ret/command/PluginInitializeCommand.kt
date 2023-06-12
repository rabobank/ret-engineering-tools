package io.rabobank.ret.command

import io.rabobank.ret.plugins.RetPlugin
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
    name = "initialize",
    description = ["Initialize plugin"],
)
class PluginInitializeCommand : Runnable {
    @Parameters(
        arity = "1",
        paramLabel = "<plugin file>",
        description = ["Absolute path to plugin"],
    )
    lateinit var pluginFile: String

    override fun run() {
        System.load(pluginFile)
        val isolateThread = RetPlugin.createIsolate()
        RetPlugin.initialize(isolateThread, pluginFile)
    }
}
