package ro.jf.playground.portfolio.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubBranch
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubCommit
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubOwner
import ro.jf.playground.portfolio.infrastructure.github.transfer.GithubRepository
import java.io.File
import kotlin.random.Random


fun WireMockServer.givenMockRepositories(username: String) {
    val responseBody = File("src/integration/resources/mock/github-repositories.json").readText()
    stubFor(
        get(urlPathEqualTo("/users/$username/repos"))
            .withGithubHeaders()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun WireMockServer.givenRandomPagedRepositories(username: String, page: Int, pageSize: Int, repositoriesCount: Int) {
    val repositories = List(repositoriesCount) { randomRepository() }
    val responseBody = jacksonObjectMapper().writeValueAsString(repositories)
    stubFor(
        get(urlEqualTo("/users/$username/repos?page=$page&per_page=$pageSize"))
            .withGithubHeaders()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun WireMockServer.givenNotFoundUserOnGetRepositories(username: String) {
    val responseBody = File("src/integration/resources/mock/github-repositories-not-found-user.json").readText()
    stubFor(
        get(urlPathEqualTo("/users/$username/repos"))
            .withGithubHeaders()
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun WireMockServer.givenMockBranches(username: String, repositoryName: String) {
    val responseBody = File("src/integration/resources/mock/github-repo-branches.json").readText()
    stubFor(
        get(urlPathEqualTo("/repos/$username/$repositoryName/branches"))
            .withGithubHeaders()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun WireMockServer.givenRandomPagedBranches(
    user: String,
    repository: String,
    page: Int,
    pageSize: Int,
    branchesCount: Int
) {
    val branches = List(branchesCount) { randomBranch() }
    val responseBody = jacksonObjectMapper().writeValueAsString(branches)
    stubFor(
        get(urlEqualTo("/repos/$user/$repository/branches?page=$page&per_page=$pageSize"))
            .withGithubHeaders()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun randomRepository() = GithubRepository(
    id = Random.nextLong(),
    name = Random.nextBytes(10).toString(),
    owner = GithubOwner(
        id = Random.nextLong(),
        login = Random.nextBytes(10).toString()
    ),
    fork = Random.nextBoolean()
)

fun randomBranch() = GithubBranch(
    name = Random.nextBytes(10).toString(),
    commit = GithubCommit(
        sha = Integer.toHexString(Random.nextInt())
    )
)

private fun MappingBuilder.withGithubHeaders(): MappingBuilder = this
    .withHeader("X-GitHub-Api-Version", equalTo("2022-11-28"))
    .withHeader("Accept", equalTo("application/vnd.github+json"))
