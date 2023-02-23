package ro.jf.playground.portfolio.infrastructure.github.transfer

import ro.jf.playground.portfolio.domain.model.Repository

data class GithubRepository(
    val id: Long,
    val name: String,
    val owner: GithubOwner,
    val fork: Boolean,
) {
    fun toModel() = Repository(
        id = id,
        name = name,
        owner = owner.toModel(),
        fork = fork,
    )
}