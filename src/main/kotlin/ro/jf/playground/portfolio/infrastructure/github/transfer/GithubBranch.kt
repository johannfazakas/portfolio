package ro.jf.playground.portfolio.infrastructure.github.transfer

import ro.jf.playground.portfolio.domain.model.Branch

data class GithubBranch(
    val name: String,
    val commit: GithubCommit,
) {
    fun toModel() = Branch(
        name = name,
        commit = commit.toModel(),
    )
}