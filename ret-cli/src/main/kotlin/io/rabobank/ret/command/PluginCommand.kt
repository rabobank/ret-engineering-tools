package io.rabobank.ret.command

import io.rabobank.ret.util.Logged
import picocli.CommandLine.Command

@Command(
    name = "plugin",
    description = ["Initialize or update RET plugins"],
    subcommands = [PluginInitializeCommand::class],
)
@Logged
class PluginCommand
