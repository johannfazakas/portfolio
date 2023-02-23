package ro.jf.playground.portfolio.domain.model

data class Repository(
    val id: Long,
    val name: String,
    val owner: Owner,
    val fork: Boolean
)
