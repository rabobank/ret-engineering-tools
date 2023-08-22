package io.rabobank.ret.config

import io.quarkus.picocli.runtime.PicocliCommandLineFactory
import io.rabobank.ret.RetConsole
import io.rabobank.ret.plugins.PluginLoader
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import picocli.CommandLine
import picocli.CommandLine.Help.Ansi.AUTO
import picocli.CommandLine.Help.ColorScheme

@ApplicationScoped
class CommandLineConfiguration(private val pluginLoader: PluginLoader) {

    @Produces
    fun customCommandLine(factory: PicocliCommandLineFactory, retConsole: RetConsole): CommandLine {
        val commandLine = factory.create()
            .setExecutionExceptionHandler(ExceptionMessageHandler(retConsole))
            .setExecutionStrategy { CommandLine.RunLast().execute(it) }
            .setColorScheme(ColorScheme.Builder().ansi(AUTO).build())

        pluginLoader.getPluginCommands(commandLine).forEach {
            commandLine.addSubcommand(it.name, it.commandSpec)
        }

        return commandLine
    }
}
