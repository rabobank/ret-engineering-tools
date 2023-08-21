package io.rabobank.ret.configuration

import java.nio.file.Path

interface Config {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
    fun configure(function: (ConfigurationProperty) -> Unit)
    fun configFile(): Path
    fun prompt(function: (Question) -> Answer): List<Answer>
}

data class Question(
    val key: String,
    val prompt: String,
    val required: Boolean = false,
)

data class Answer(
    val key: String,
    val answer: String,
)
