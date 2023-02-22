package ro.jf.playground.portfolio.api.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ro.jf.playground.portfolio.api.transfer.RepositoriesResponse

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    @GetMapping("/{username}/repositories")
    @ResponseStatus(HttpStatus.OK)
    fun getUserRepositories(@PathVariable username: String): Mono<RepositoriesResponse> =
        Mono.just(RepositoriesResponse(listOf("repo-1", "repo-2")))
}
