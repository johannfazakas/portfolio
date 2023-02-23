package ro.jf.playground.portfolio.infrastructure.github

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import ro.jf.playground.portfolio.domain.error.UserNotFoundException
import ro.jf.playground.portfolio.domain.model.Branch
import ro.jf.playground.portfolio.domain.model.Commit
import ro.jf.playground.portfolio.domain.model.Owner
import ro.jf.playground.portfolio.domain.model.Repository
import ro.jf.playground.portfolio.domain.service.UserRepositoryProvider

@Component
class GithubUserRepositoryAdapter : UserRepositoryProvider {

    private val usersMap = mapOf(
        "johannfazakas" to listOf(
            Repository(
                id = 11,
                name = "my-repo",
                owner = Owner(
                    id = 21,
                    login = "test-user"
                ),
                fork = false
            )
        ),
        "test-user" to listOf(
            Repository(
                id = 11,
                name = "my-repo",
                owner = Owner(
                    id = 21,
                    login = "test-user"
                ),
                fork = false
            )
        )
    )

    private val branchesMap = mapOf(
        "my-repo" to listOf(
            Branch(
                name = "master",
                commit = Commit(sha = "1a2b3c")
            ),
            Branch(
                name = "feature-123",
                commit = Commit(sha = "4d5e6f")
            ),
        )
    )

    override fun getUserRepositories(username: String): Flux<Repository> =
        usersMap[username]?.toFlux() ?: throw UserNotFoundException(username)

    override fun getUserRepositoryBranches(username: String, repositoryName: String): Flux<Branch> =
        branchesMap[repositoryName]?.toFlux() ?: Flux.empty()
}
