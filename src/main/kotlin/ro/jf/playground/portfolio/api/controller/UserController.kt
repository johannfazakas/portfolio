package ro.jf.playground.portfolio.api.controller

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ro.jf.playground.portfolio.api.transfer.BranchTO
import ro.jf.playground.portfolio.api.transfer.RepositoriesTO
import ro.jf.playground.portfolio.api.transfer.RepositoryTO

private val logger = KotlinLogging.logger { }

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    @GetMapping("/{username}/repositories")
    @ResponseStatus(HttpStatus.OK)
    fun getUserRepositories(@PathVariable username: String): Mono<RepositoriesTO> {
        logger.info("Get user repositories >> username = $username.")
        return Mono.just(
            RepositoriesTO(
                repositories = listOf(
                    RepositoryTO(
                        name = "repo-1",
                        ownerLogin = "owner",
                        branches = listOf(
                            BranchTO(
                                name = "main",
                                lastCommitSha = "abcdef"
                            )
                        )
                    )
                )
            )
        )
    }
}
