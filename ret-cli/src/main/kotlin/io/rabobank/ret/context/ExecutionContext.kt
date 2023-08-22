package io.rabobank.ret.context

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ExecutionContext {

    private val gitContext = GitContext.create()

    fun repositoryName() = gitContext?.repositoryName()
    fun branchName() = gitContext?.branchName()
}
