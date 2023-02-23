package ro.jf.playground.portfolio.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import ro.jf.playground.portfolio.api.transfer.ErrorTO
import ro.jf.playground.portfolio.api.transfer.RepositoriesTO


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `should return user repositories when getting user repositoriess`() {
        val username = "test-user"

        val exchange = webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        exchange
            .expectStatus().isOk
            .expectBody(RepositoriesTO::class.java)
            .value {
                assertThat(it.repositories).hasSize(1)
                assertThat(it.repositories[0].name).isEqualTo("my-repo")
                assertThat(it.repositories[0].owner.login).isEqualTo("test-user")
                assertThat(it.repositories[0].branches).hasSize(2)
                assertThat(it.repositories[0].branches[0].name).isEqualTo("master")
                assertThat(it.repositories[0].branches[0].lastCommit.sha).isEqualTo("1a2b3c")
            }
    }

    @Test
    fun `should return error when getting user repositories and requesting xml response`() {
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
}