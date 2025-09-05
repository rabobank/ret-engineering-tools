package io.rabobank.ret.context

import io.rabobank.ret.configuration.version.VersionProperties
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ExecutionContext(
    private val versionProperties: VersionProperties = VersionProperties(),
) {
    private val gitContext = GitContext.create()

    fun repositoryName() = gitContext?.repositoryName()

    fun branchName() = gitContext?.branchName()

    fun version() = versionProperties.getAppVersion()
}
