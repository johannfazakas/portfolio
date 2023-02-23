package ro.jf.playground.portfolio.api.transfer

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorTO(
    val status: Int,
    // explicitly requested to serialize it with capitalized key
    @JsonProperty(value = "Message")
    val message: String
)
