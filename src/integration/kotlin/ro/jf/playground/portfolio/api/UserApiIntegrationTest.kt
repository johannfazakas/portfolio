package ro.jf.playground.portfolio.api

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import ro.jf.playground.portfolio.api.transfer.ErrorTO
import ro.jf.playground.portfolio.api.transfer.RepositoriesTO
import ro.jf.playground.portfolio.utils.givenMockBranches
import ro.jf.playground.portfolio.utils.givenMockRepositories
import ro.jf.playground.portfolio.utils.givenNotFoundUserOnGetRepositories


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserApiIntegrationTest(
    @Value("\${mockServer.port}") val mockServerPort: Int
) {
    @Autowired
    private lateinit var webClient: WebTestClient

    private lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun setUp() {
        wireMockServer = WireMockServer(WireMockConfiguration().port(mockServerPort))
        wireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.resetAll()
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `should return user repositories when getting user repositories`() {
        val username = "test-user"
        val repositoryName1 = "repo-1"
        val repositoryName2 = "repo-2"
        wireMockServer.givenMockRepositories(username)
        wireMockServer.givenMockBranches(username, repositoryName1)
        wireMockServer.givenMockBranches(username, repositoryName2)

        val exchange = webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        exchange
            .expectStatus().isOk
            .expectBody(RepositoriesTO::class.java)
            .value {
                assertThat(it.repositories).hasSize(2)
                val repo1 = it.repositories.find { repo -> repo.name == repositoryName1 }
                    ?: throw AssertionError("Repository $repositoryName1 not returned")
                assertThat(repo1.name).isEqualTo(repositoryName1)
                assertThat(repo1.owner.login).isEqualTo(username)
                assertThat(repo1.branches).hasSize(2)
                assertThat(repo1.branches[0].name).isEqualTo("master")
                assertThat(repo1.branches[0].lastCommit.sha).isNotEmpty
            }
    }

    @Test
    fun `should return not acceptable error when getting user repositories and requesting xml response`() {
        val username = "test-user"

        val exchange = webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .accept(MediaType.APPLICATION_XML)
            .exchange()

        exchange
            .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
            .expectBody(ErrorTO::class.java)
            .value {
                assertThat(it.status).isEqualTo(406)
                assertThat(it.message).isNotEmpty()
            }
    }

    @Test
    fun `should return not found error when getting repositories for not existing username`() {
        val username = "i-do-not-exist"
        wireMockServer.givenNotFoundUserOnGetRepositories(username)

        val exchange = webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        exchange
            .expectStatus().isNotFound
            .expectBody(ErrorTO::class.java)
            .value {
                assertThat(it.status).isEqualTo(404)
                assertThat(it.message).contains(username)
            }
    }
}