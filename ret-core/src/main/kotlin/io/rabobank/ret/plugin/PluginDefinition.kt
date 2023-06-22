package io.rabobank.ret.plugin

import io.quarkus.runtime.annotations.RegisterForReflection
import java.nio.file.Path

@RegisterForReflection
data class Plugin(
    val pluginDefinition: PluginDefinition,
    val pluginLocation: Path,
)

@RegisterForReflection
data class PluginDefinition(
    val libName: String,
    val version: String = "unknown",
    val commands: List<PluginCommand>,
    val customZshAutocompletion: String?,
)

@RegisterForReflection
data class PluginCommand(
    val name: String,
    val arguments: List<Argument>,
    val options: List<Option>,
    val subcommands: List<PluginCommand>,
    val description: String? = null,
    val hidden: Boolean = false,
)

@RegisterForReflection
data class Argument(
    val name: String,
    val position: String? = null,
    val completionCandidates: List<String> = emptyList(),
    val arity: String? = null,
)

@RegisterForReflection
data class Option(
    val names: List<String>,
    val type: String,
    val completionCandidates: List<String> = emptyList(),
)
