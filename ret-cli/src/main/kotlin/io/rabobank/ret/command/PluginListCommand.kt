package io.rabobank.ret.command

import io.rabobank.ret.RetConsole
import io.rabobank.ret.plugins.PluginManager
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    description = ["List available plugin"],
)
class PluginListCommand(private val pluginManager: PluginManager, private val retConsole: RetConsole): Runnable {
    override fun run() {
        pluginManager.plugins.forEach {
            retConsole.out("${it.name}@${it.version}")
        }
    }
}
