package ro.jf.playground.portfolio.api.error

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import ro.jf.playground.portfolio.api.transfer.ErrorTO

private const val REQUESTED_MEDIA_TYPE_NOT_SUPPORTED_MESSAGE = "Requested media type is not supported."
private const val INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error."

private val logger = KotlinLogging.logger { }

@ControllerAdvice
class PortfolioErrorHandler {

    @ExceptionHandler(NotAcceptableStatusException::class)
    fun handle(exception: NotAcceptableStatusException): ResponseEntity<ErrorTO> {
        logger.warn("Requested media type is not supported", exception)
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ErrorTO(
                    status = HttpStatus.NOT_ACCEPTABLE.value(),
                    message = REQUESTED_MEDIA_TYPE_NOT_SUPPORTED_MESSAGE
                )
            )

    }

    @ExceptionHandler(Exception::class)
    fun handle(exception: Exception): ResponseEntity<ErrorTO> {
        logger.error("Internal error.", exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorTO(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    message = INTERNAL_SERVER_ERROR_MESSAGE
                )
            )
    }
}
