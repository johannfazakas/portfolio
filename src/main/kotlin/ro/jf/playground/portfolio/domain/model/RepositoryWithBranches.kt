package ro.jf.playground.portfolio.domain.model

data class RepositoryWithBranches(
    val id: Int,
    val name: String,
    val owner: Owner,
    val branches: List<Branch>
)
