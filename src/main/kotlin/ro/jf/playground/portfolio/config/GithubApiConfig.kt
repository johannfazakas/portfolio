package ro.jf.playground.portfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GithubApiConfig(
    @Value("\${githubApi.baseUrl}") private val githubApiBaseUrl: String,
) {
    @Bean(name = ["githubApiWebClient"])
    fun githubApiWebClient(): WebClient = WebClient.create(githubApiBaseUrl)
}
