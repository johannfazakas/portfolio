package ro.jf.playground.portfolio.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import ro.jf.playground.portfolio.api.transfer.RepositoriesTO


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `should return user repositories when username exists`() {
        val username = "test-user"

        val exchange = webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .exchange()

        exchange
            .expectStatus().isOk
            .expectBody(RepositoriesTO::class.java)
            .value {
                assertEquals(1, it.repositories.size)
            }
    }
}