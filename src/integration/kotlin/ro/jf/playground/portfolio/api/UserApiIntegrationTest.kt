package ro.jf.playground.portfolio.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `should return user repositories when username exists`() {
        val username = "test-user"

        webClient.get()
            .uri("/api/v1/users/$username/repositories")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("repositories[0]").isEqualTo("repo-1")
            .jsonPath("repositories[1]").isEqualTo("repo-2")
    }
}