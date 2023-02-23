package ro.jf.playground.portfolio.domain.error

data class UserNotFoundException(
    val username: String,
    override val message: String = "User could not be found by username $username."
) : RuntimeException(message)
