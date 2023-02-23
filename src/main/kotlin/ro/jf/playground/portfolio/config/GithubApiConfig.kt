package ro.jf.playground.portfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

private const val GITHUB_JSON = "application/vnd.github+json"
private const val GITHUB_API_VERSION_KEY = "X-GitHub-Api-Version"

@Configuration
class GithubApiConfig(
    @Value("\${githubApi.baseUrl}") private val githubApiBaseUrl: String,
    @Value("\${githubApi.version}") private val githubApiVersion: String,
) {
    @Bean(name = ["githubApiWebClient"])
    fun githubApiWebClient(): WebClient = WebClient.builder()
        .baseUrl(githubApiBaseUrl)
        .defaultHeader(HttpHeaders.ACCEPT, GITHUB_JSON)
        .defaultHeader(GITHUB_API_VERSION_KEY, githubApiVersion)
        .build()
}
