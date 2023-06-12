package io.rabobank.ret

import io.quarkus.picocli.runtime.annotations.TopCommand
import io.rabobank.ret.command.ConfigureCommand
import io.rabobank.ret.command.PluginCommand
import io.rabobank.ret.configuration.version.RetVersionProvider
import picocli.CommandLine

@TopCommand
@CommandLine.Command(
    name = "ret",
    versionProvider = RetVersionProvider::class,
    subcommands = [
        PluginCommand::class,
        ConfigureCommand::class,
    ],
)
class RetEntryCommand {
    @CommandLine.Option(
        names = ["-v", "--version"],
        versionHelp = true,
        description = ["print version information"],
    )
    var versionRequested = false

    @CommandLine.Option(
        names = ["--help", "-h"],
        description = ["Show help information"],
        scope = CommandLine.ScopeType.INHERIT,
        usageHelp = true,
    )
    var help: Boolean = false
}
