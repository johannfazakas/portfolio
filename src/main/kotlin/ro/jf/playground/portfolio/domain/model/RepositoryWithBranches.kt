package ro.jf.playground.portfolio.domain.model

data class RepositoryWithBranches(
    val id: Int,
    val name: String,
    val owner: Owner,
    val branches: List<Branch>
) {
    constructor(repository: Repository, branches: List<Branch>) : this(
        id = repository.id,
        name = repository.name,
        owner = repository.owner,
        branches = branches
    )
}
