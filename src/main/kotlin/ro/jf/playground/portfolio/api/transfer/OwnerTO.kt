package ro.jf.playground.portfolio.api.transfer

import ro.jf.playground.portfolio.domain.model.Owner

data class OwnerTO(
    val login: String
) {
    constructor(owner: Owner) : this(
        login = owner.login
    )
}
