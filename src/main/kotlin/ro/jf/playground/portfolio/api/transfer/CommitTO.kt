package ro.jf.playground.portfolio.api.transfer

import ro.jf.playground.portfolio.domain.model.Commit

data class CommitTO(
    val sha: String
) {
    constructor(commit: Commit) : this(
        sha = commit.sha
    )
}
