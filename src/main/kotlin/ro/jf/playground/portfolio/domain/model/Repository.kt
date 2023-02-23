package ro.jf.playground.portfolio.domain.model

data class Repository(
    val id: Int,
    val name: String,
    val owner: Owner
)
