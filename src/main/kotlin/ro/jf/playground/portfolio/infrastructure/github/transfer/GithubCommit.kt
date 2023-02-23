package ro.jf.playground.portfolio.infrastructure.github.transfer

import ro.jf.playground.portfolio.domain.model.Commit

data class GithubCommit(
    val sha: String,
) {
    fun toModel() = Commit(
        sha = sha,
    )
}