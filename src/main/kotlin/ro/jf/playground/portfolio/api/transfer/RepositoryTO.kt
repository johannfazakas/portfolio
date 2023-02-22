package ro.jf.playground.portfolio.api.transfer

data class RepositoryTO(
    val name: String,
    val ownerLogin: String,
    val branches: List<BranchTO>
)
