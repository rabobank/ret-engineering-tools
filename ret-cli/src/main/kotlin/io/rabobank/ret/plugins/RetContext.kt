package io.rabobank.ret.plugins

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class RetContext(
    val command: String,
    val environment: String,
    val gitRepository: String?,
    val gitBranch: String?,
)
