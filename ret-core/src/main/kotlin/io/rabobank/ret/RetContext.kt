package io.rabobank.ret

import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Context of the current RET execution (to be injected)
 *
 * This class is used to pass information about the context to plugins. Those in turn can use it to base logic upon.
 * Note that this class needs to be registered for reflection in your plugin.
 * @property command the full command, as ran in the terminal. This is used to determine which plugin should be loaded.
 * @property environment the environment from which the RET command is ran. This is a custom environment variable, set by the caller. By default, it is "CLI"
 * @property gitRepository the Git repository, if RET was called from a Git directory.
 * @property gitBranch the current Git branch, if RET was called from a Git repository.
 */
@RegisterForReflection
data class RetContext(
    val command: List<String> = emptyList(),
    val environment: String? = null,
    val gitRepository: String? = null,
    val gitBranch: String? = null,
)
