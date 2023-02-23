package ro.jf.playground.portfolio.infrastructure.github.transfer

import ro.jf.playground.portfolio.domain.model.Owner

data class GithubOwner(
    val id: Int,
    val login: String,
) {
    fun toModel(): Owner = Owner(
        id = id,
        login = login,
    )
}
