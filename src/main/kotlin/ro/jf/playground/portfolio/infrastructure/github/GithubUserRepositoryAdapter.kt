package ro.jf.playground.portfolio.infrastructure.github

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.jf.playground.portfolio.domain.error.ProviderException
import ro.jf.playground.portfolio.domain.error.UserNotFoundException
import ro.jf.playground.portfolio.domain.model.Branch
import ro.jf.playground.portfolio.domain.model.Repository
import ro.jf.playground.portfolio.domain.service.UserRepositoryProvider
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubBranch
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubRepository

private val logger = KotlinLogging.logger { }

@Component
class GithubUserRepositoryAdapter(
    private val githubApiWebClient: WebClient,
) : UserRepositoryProvider {
    override fun getUserRepositories(username: String): Flux<Repository> {
        logger.debug("Get repositories by username $username.")
        return githubApiWebClient.get()
            .uri("users/$username/repos")
            .retrieve()
            .onStatus({ it.isError }) {
                it.handleError { code: HttpStatusCode, response ->
                    when (code) {
                        HttpStatus.NOT_FOUND -> UserNotFoundException(username)
                        else -> ProviderException(response)
                    }
                }
            }
            .bodyToFlux(GithubRepository::class.java)
            .map(GithubRepository::toModel)
    }

    override fun getUserRepositoryBranches(username: String, repositoryName: String): Flux<Branch> {
        logger.debug("Get branches by username $username and repository name $repositoryName.")
        return githubApiWebClient.get()
            .uri("repos/$username/$repositoryName/branches")
            .retrieve()
            .onStatus({ it.isError }) { it.handleError { _, response -> ProviderException(response) } }
            .bodyToFlux(GithubBranch::class.java)
            .map(GithubBranch::toModel)
    }

    private fun ClientResponse.handleError(mapper: (HttpStatusCode, String) -> Throwable): Mono<Throwable> =
        bodyToMono<String>()
            .defaultIfEmpty("Empty body")
            .map { response -> mapper(this.statusCode(), response) }
}
