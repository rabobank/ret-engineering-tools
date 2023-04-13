package io.rabobank.ret.context

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ExecutionContext {

    private val gitContext: GitContext? = GitContext.create()

    fun repositoryName(): String? = gitContext?.repositoryName()
    fun branchName(): String? = gitContext?.branchName()
}
