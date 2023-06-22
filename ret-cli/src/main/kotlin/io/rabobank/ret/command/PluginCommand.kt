package io.rabobank.ret.command

import picocli.CommandLine.Command

@Command(
    name = "plugin",
    description = ["Initialize or update RET plugins"],
    subcommands = [
        PluginInitializeCommand::class,
        PluginListCommand::class,
        PluginInstallCommand::class,
    ],
)
class PluginCommand
