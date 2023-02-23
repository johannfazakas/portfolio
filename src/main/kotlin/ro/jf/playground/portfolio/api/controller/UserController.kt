package ro.jf.playground.portfolio.api.controller

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ro.jf.playground.portfolio.api.transfer.RepositoriesTO
import ro.jf.playground.portfolio.api.transfer.RepositoryTO
import ro.jf.playground.portfolio.domain.service.UserRepositoryService

private val logger = KotlinLogging.logger { }

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userRepositoryService: UserRepositoryService
) {
    @GetMapping("/{username}/repositories")
    @ResponseStatus(HttpStatus.OK)
    fun getUserRepositories(@PathVariable username: String): Mono<RepositoriesTO> {
        logger.info("Get user repositories >> username = $username.")
        return userRepositoryService.getUserRepositories(username)
            .collectList()
            .map { RepositoriesTO(it.map(::RepositoryTO)) }
    }
}
