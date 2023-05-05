package io.rabobank.ret.context

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.lib.Repository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GitContextTest {

    private lateinit var mockedGitRepository: Repository
    private lateinit var gitContext: GitContext

    @BeforeEach
    fun before() {
        mockedGitRepository = mockk()
        gitContext = GitContext(mockedGitRepository)
    }

    @Test
    fun getRepositoryFromRemoteURL() {
        every { mockedGitRepository.config } returns mockk {
            every { getString("remote", "origin", "url") } returns "git@ssh.dev.azure.com:v3/raboweb/Skunk%20Works/rabobank-engineering-tools"
        }

        val actualRepositoryName = gitContext.repositoryName()

        assertThat(actualRepositoryName).isEqualTo("rabobank-engineering-tools")
    }

    @Test
    fun getBranchName() {
        val branchName = "feature/my-branch"
        every { mockedGitRepository.branch } returns branchName

        val result = gitContext.branchName()
        assertThat(result).isEqualTo(branchName)
    }
}
