package ro.jf.playground.portfolio.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import ro.jf.playground.portfolio.domain.error.UserNotFoundException
import ro.jf.playground.portfolio.domain.model.Branch
import ro.jf.playground.portfolio.domain.model.Commit
import ro.jf.playground.portfolio.domain.model.Owner
import ro.jf.playground.portfolio.domain.model.Repository

class UserRepositoryServiceTest {

    private val userRepositoryProvider = mock<UserRepositoryProvider>()

    private val userRepositoryService = UserRepositoryService(userRepositoryProvider)

    @Test
    fun `should return empty list when getting repositories by valid username without repositories`() {
        val username = "test-user"
        whenever(userRepositoryProvider.getUserRepositories(username))
            .thenReturn(Flux.empty())

        val response = userRepositoryService.getUserRepositories(username)

        response.`as`(StepVerifier::create)
            .verifyComplete()
        verify(userRepositoryProvider, never()).getUserRepositoryBranches(anyString(), anyString())
    }

    @Test
    fun `should return repositories with branches when getting repositories by valid username`() {
        val username = "test-user"
        val owner = Owner(11, username)
        val repository1 = Repository(21, "repo-1", owner, false)
        val repository2 = Repository(22, "repo-2", owner, false)
        val repo1branch1 = Branch("branch-1", Commit("1a2b"))
        val repo1branch2 = Branch("branch-1", Commit("3c4d"))
        val repo2branch1 = Branch("branch-1", Commit("5e6f"))
        whenever(userRepositoryProvider.getUserRepositories(username))
            .thenReturn(Flux.just(repository1, repository2))
        whenever(userRepositoryProvider.getUserRepositoryBranches(username, repository1.name))
            .thenReturn(Flux.just(repo1branch1, repo1branch2))
        whenever(userRepositoryProvider.getUserRepositoryBranches(username, repository2.name))
            .thenReturn(Flux.just(repo2branch1))

        val response = userRepositoryService.getUserRepositories(username)

        response.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.id).isEqualTo(repository1.id)
                assertThat(it.name).isEqualTo(repository1.name)
                assertThat(it.owner).isEqualTo(owner)
                assertThat(it.branches).containsExactly(repo1branch1, repo1branch2)
            }
            .assertNext {
                assertThat(it.id).isEqualTo(repository2.id)
                assertThat(it.name).isEqualTo(repository2.name)
                assertThat(it.owner).isEqualTo(owner)
                assertThat(it.branches).containsExactly(repo2branch1)
            }
            .verifyComplete()
    }

    @Test
    fun `should return only the not forked repositories when getting repositories by valid username`() {
        val username = "test-user"
        val owner = Owner(11, username)
        val repository1 = Repository(21, "repo-1", owner, false)
        val repository2 = Repository(22, "repo-2", owner, true)
        val repo1branch1 = Branch("branch-1", Commit("1a2b"))
        val repo1branch2 = Branch("branch-1", Commit("3c4d"))

        whenever(userRepositoryProvider.getUserRepositories(username))
            .thenReturn(Flux.just(repository1, repository2))
        whenever(userRepositoryProvider.getUserRepositoryBranches(username, repository1.name))
            .thenReturn(Flux.just(repo1branch1, repo1branch2))

        val response = userRepositoryService.getUserRepositories(username)

        response.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.id).isEqualTo(repository1.id)
                assertThat(it.name).isEqualTo(repository1.name)
                assertThat(it.owner).isEqualTo(owner)
                assertThat(it.branches).containsExactly(repo1branch1, repo1branch2)
            }
            .verifyComplete()
        verify(userRepositoryProvider, never()).getUserRepositoryBranches(username, repository2.name)
    }

    @Test
    fun `should throw not found error when getting repositories by not found username`() {
        val username = "test-user"
        whenever(userRepositoryProvider.getUserRepositories(username))
            .thenReturn(Flux.error(UserNotFoundException(username)))

        val response = userRepositoryService.getUserRepositories(username)

        response.`as`(StepVerifier::create)
            .verifyErrorMatches { it is UserNotFoundException }
    }
}
