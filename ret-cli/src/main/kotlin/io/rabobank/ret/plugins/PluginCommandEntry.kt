package io.rabobank.ret.plugins

import picocli.CommandLine.Model.CommandSpec

data class PluginCommandEntry(val name: String, val commandSpec: CommandSpec)


