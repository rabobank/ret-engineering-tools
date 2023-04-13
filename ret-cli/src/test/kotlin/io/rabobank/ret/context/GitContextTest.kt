package io.rabobank.ret.context

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.StoredConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

internal class GitContextTest {

    private lateinit var mockedGitRepository: Repository
    private lateinit var gitContext: GitContext

    @BeforeEach
    fun before() {
        mockedGitRepository = mock(Repository::class.java)
        gitContext = GitContext(mockedGitRepository)
    }

    @Test
    fun getRepositoryFromRemoteURL() {
        val mockedConfig = mock(StoredConfig::class.java)
        whenever(mockedGitRepository.config).thenReturn(mockedConfig)
        whenever(mockedConfig.getString("remote", "origin", "url"))
            .thenReturn("git@ssh.dev.azure.com:v3/raboweb/Skunk%20Works/rabobank-engineering-tools")

        val actualRepositoryName = gitContext.repositoryName()

        assertThat(actualRepositoryName).isEqualTo("rabobank-engineering-tools")
    }

    @Test
    fun getBranchName() {
        val branchName = "feature/my-branch"
        whenever(mockedGitRepository.branch).thenReturn(branchName)

        val result = gitContext.branchName()
        assertThat(result).isEqualTo(branchName)
    }
}
