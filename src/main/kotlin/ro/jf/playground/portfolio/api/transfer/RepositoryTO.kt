package ro.jf.playground.portfolio.api.transfer

import ro.jf.playground.portfolio.domain.model.RepositoryWithBranches

data class RepositoryTO(
    val name: String,
    val owner: OwnerTO,
    val branches: List<BranchTO>
) {
    constructor(repository: RepositoryWithBranches) : this(
        name = repository.name,
        owner = OwnerTO(repository.owner),
        branches = repository.branches.map(::BranchTO)
    )
}
