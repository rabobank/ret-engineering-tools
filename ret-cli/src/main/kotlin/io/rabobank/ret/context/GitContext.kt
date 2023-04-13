package io.rabobank.ret.context

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

internal class GitContext internal constructor(private val repository: Repository) {

    fun repositoryName(): String? {
        val remoteOriginURL: String? = repository.config.getString("remote", "origin", "url")
        return remoteOriginURL?.substringAfterLast("/")
    }

    fun branchName(): String? = repository.branch

    companion object {
        fun create(): GitContext? {
            val builder = FileRepositoryBuilder().findGitDir()
            return if (builder.gitDir != null) GitContext(builder.build()) else null
        }
    }
}
