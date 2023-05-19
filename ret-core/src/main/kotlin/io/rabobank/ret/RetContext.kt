package io.rabobank.ret

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class RetContext(
    val command: List<String> = emptyList(),
    val environment: String? = null,
    val gitRepository: String? = null,
    val gitBranch: String? = null,
)
