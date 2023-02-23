package ro.jf.playground.portfolio.domain.service

import reactor.core.publisher.Flux
import ro.jf.playground.portfolio.domain.model.Branch
import ro.jf.playground.portfolio.domain.model.Repository

interface UserRepositoryProvider {
    fun getUserRepositories(username: String): Flux<Repository>
    fun getUserRepositoryBranches(username: String, repositoryName: String): Flux<Branch>
}
