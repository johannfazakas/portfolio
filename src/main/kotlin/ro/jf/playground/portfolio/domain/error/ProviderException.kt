package ro.jf.playground.portfolio.domain.error

data class ProviderException(override val message: String) : RuntimeException(message)
