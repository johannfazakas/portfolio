package ro.jf.playground.portfolio.infrastructure.github

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
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

private const val GET_USER_REPOSITORIES_URL = "users/{username}/repos"
private const val GET_USER_REPOSITORY_BRANCHES_URL = "repos/{username}/{repository}/branches"

private const val USERNAME = "username"
private const val REPOSITORY = "repository"

private const val PAGE_SIZE = 30
private const val PAGE_KEY = "page"
private const val PAGE_SIZE_KEY = "per_page"

@Component
class GithubUserRepositoryAdapter(
    private val githubApiWebClient: WebClient,
) : UserRepositoryProvider {

    override fun getUserRepositories(username: String): Flux<Repository> =
        getAllResourcesPaginated { page ->
            logger.debug("Get repositories by username $username on page $page.")
            val uri = UriComponentsBuilder
                .fromUriString(GET_USER_REPOSITORIES_URL)
                .paginated(page, PAGE_SIZE)
                .buildAndExpand(mapOf(USERNAME to username))
                .toUriString()
            githubApiWebClient.get()
                .uri(uri)
                .retrieve()
                .onStatus({ it.isError }) {
                    it.handleError { code: HttpStatusCode, response ->
                        logger.warn("Error on get repositories from GitHub API with status $code, response $response")
                        when (code) {
                            HttpStatus.NOT_FOUND -> UserNotFoundException(username)
                            else -> ProviderException(response)
                        }
                    }
                }
                .bodyToFlux(GithubRepository::class.java)
                .map(GithubRepository::toModel)
        }

    override fun getUserRepositoryBranches(username: String, repositoryName: String): Flux<Branch> =
        getAllResourcesPaginated { page ->
            logger.debug("Get branches by username $username and repository name $repositoryName on page $page.")
            val uri = UriComponentsBuilder
                .fromUriString(GET_USER_REPOSITORY_BRANCHES_URL)
                .paginated(page, PAGE_SIZE)
                .buildAndExpand(mapOf(USERNAME to username, REPOSITORY to repositoryName))
                .toUriString()
            githubApiWebClient.get()
                .uri(uri)
                .retrieve()
                .onStatus({ it.isError }) {
                    it.handleError { code, response ->
                        logger.warn("Error on get repositories from GitHub API with status $code, response $response")
                        ProviderException(response)
                    }
                }
                .bodyToFlux(GithubBranch::class.java)
                .map(GithubBranch::toModel)
        }

    private fun <T> getAllResourcesPaginated(pageSupplier: (Int) -> Flux<T>): Flux<T> {
        var page = 1
        return pageSupplier(page).collectList()
            .expand {
                if (it.size < PAGE_SIZE) {
                    Flux.empty()
                } else {
                    pageSupplier(++page).collectList()
                }
            }
            .flatMapIterable { it }
    }

    private fun UriComponentsBuilder.paginated(page: Int, pageSize: Int) = this
        .queryParam(PAGE_KEY, page)
        .queryParam(PAGE_SIZE_KEY, pageSize)

    private fun ClientResponse.handleError(mapper: (HttpStatusCode, String) -> Throwable): Mono<Throwable> =
        bodyToMono<String>()
            .defaultIfEmpty("Empty body")
            .map { response -> mapper(this.statusCode(), response) }
}
