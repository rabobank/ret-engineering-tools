package io.rabobank.ret.plugins

data class Repository(
    val name: String,
    val url: String
)

data class Plugin(
    val name: String,
    val version: String,
    val osx: PluginFile,
    val linux: PluginFile
)

data class PluginFile(
    val amd64: String?,
    val arm64: String?
)
