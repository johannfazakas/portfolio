package ro.jf.playground.portfolio.infrastructure

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
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import ro.jf.playground.portfolio.domain.error.UserNotFoundException
import ro.jf.playground.portfolio.infrastructure.github.GithubUserRepositoryAdapter
import ro.jf.playground.portfolio.utils.givenMockResponseOnGetBranches
import ro.jf.playground.portfolio.utils.givenMockResponseOnGetRepositories
import ro.jf.playground.portfolio.utils.givenNotFoundUserOnGetRepositories

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GithubUserRepositoryAdapterIntegrationTest(
    @Value("\${mockServer.port}") val mockServerPort: Int
) {

    @Autowired
    private lateinit var githubUserRepositoryAdapter: GithubUserRepositoryAdapter

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
    fun `should return repositories when getting user repositories`() {
        val username = "test-user"
        wireMockServer.givenMockResponseOnGetRepositories(username)

        val response = githubUserRepositoryAdapter.getUserRepositories(username)

        response
            .`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.name).isEqualTo("repo-1")
                assertThat(it.owner.login).isEqualTo(username)
                assertThat(it.fork).isFalse
            }
            .assertNext {
                assertThat(it.name).isEqualTo("repo-2")
                assertThat(it.owner.login).isEqualTo(username)
                assertThat(it.fork).isFalse
            }
            .assertNext {
                assertThat(it.name).isEqualTo("forked-repo")
                assertThat(it.owner.login).isEqualTo(username)
                assertThat(it.fork).isTrue
            }
            .verifyComplete()
    }

    @Test
    fun `should return not found when getting not existing user repositories`() {
        val username = "not-found-user"
        wireMockServer.givenNotFoundUserOnGetRepositories(username)

        val response = githubUserRepositoryAdapter.getUserRepositories(username)

        response
            .`as`(StepVerifier::create)
            .verifyErrorMatches { it is UserNotFoundException && it.username == username }
    }

    @Test
    fun `should return branches when getting user repository branches`() {
        val username = "test-user"
        val repositoryName = "repo-1"
        wireMockServer.givenMockResponseOnGetBranches(username, repositoryName)

        val response = githubUserRepositoryAdapter.getUserRepositoryBranches(username, repositoryName)

        response
            .`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.name).isEqualTo("master")
                assertThat(it.commit.sha).isNotEmpty()
            }
            .assertNext {
                assertThat(it.name).isEqualTo("feature-x")
                assertThat(it.commit.sha).isNotEmpty()
            }
            .verifyComplete()
    }
}