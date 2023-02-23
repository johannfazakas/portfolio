package ro.jf.playground.portfolio.domain.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ro.jf.playground.portfolio.domain.error.UserNotFoundException
import ro.jf.playground.portfolio.domain.model.Branch
import ro.jf.playground.portfolio.domain.model.Commit
import ro.jf.playground.portfolio.domain.model.Owner
import ro.jf.playground.portfolio.domain.model.RepositoryWithBranches

@Service
class UserRepositoryService {

    private val usersMap = mapOf(
        "test-user" to listOf(
            RepositoryWithBranches(
                id = 11,
                name = "my-repo",
                owner = Owner(
                    id = 21,
                    login = "test-user"
                ),
                branches = listOf(
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
        )
    )

    fun getUserRepositories(username: String): Flux<RepositoryWithBranches> =
        usersMap[username]
            ?.let { Flux.just(*it.toTypedArray()) }
            ?: Flux.error(UserNotFoundException(username))
}
