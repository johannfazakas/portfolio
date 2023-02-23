package ro.jf.playground.portfolio.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import java.io.File

fun WireMockServer.givenMockResponseOnGetRepositories(username: String) {
    val responseBody = File("src/integration/resources/mock/github-repositories.json").readText()
    stubFor(
        get(urlEqualTo("/users/$username/repos"))
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
        get(urlEqualTo("/users/$username/repos"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}

fun WireMockServer.givenMockResponseOnGetBranches(username: String, repositoryName: String) {
    val responseBody = File("src/integration/resources/mock/github-repo-branches.json").readText()
    stubFor(
        get(urlEqualTo("/repos/$username/$repositoryName/branches"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
            )
    )
}
