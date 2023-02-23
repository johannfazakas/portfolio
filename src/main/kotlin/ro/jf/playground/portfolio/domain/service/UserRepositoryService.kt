package ro.jf.playground.portfolio.domain.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ro.jf.playground.portfolio.domain.model.Repository
import ro.jf.playground.portfolio.domain.model.RepositoryWithBranches

private val logger = KotlinLogging.logger { }

@Service
class UserRepositoryService(
    private val userRepositoryProvider: UserRepositoryProvider
) {
    fun getUserRepositories(username: String): Flux<RepositoryWithBranches> {
        logger.debug("Get repositories by username $username.")
        return userRepositoryProvider.getUserRepositories(username)
            .filter { !it.fork }
            .flatMap { repository -> includeBranchesOnRepository(username, repository) }
    }

    private fun includeBranchesOnRepository(username: String, repository: Repository) =
        userRepositoryProvider.getUserRepositoryBranches(username, repository.name)
            .collectList()
            .map { branches -> RepositoryWithBranches(repository, branches) }
}
