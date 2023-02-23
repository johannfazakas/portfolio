package ro.jf.playground.portfolio.api.transfer

import ro.jf.playground.portfolio.domain.model.Branch

data class BranchTO(
    val name: String,
    val lastCommit: CommitTO
) {
    constructor(branch: Branch) : this(
        name = branch.name,
        lastCommit = CommitTO(branch.commit)
    )
}
