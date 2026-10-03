package com.devmind.energy.service.dto

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ApiExceptionHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    }

    @ExceptionHandler(ApiException::class)
    fun handleEnedisApiException(exception: ApiException): ResponseEntity<String> {
        logger.error(
            "Enedis request failed: status={}, body={}",
            exception.statusCode.value(),
            exception.responseBody
        )
        return ResponseEntity.status(exception.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(exception.responseBody)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseEntity<Map<String, Any>> {
        logger.warn("Request rejected: status={}, reason={}", exception.statusCode.value(), exception.reason)
        return ResponseEntity.status(exception.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                mapOf(
                    "status" to exception.statusCode.value(),
                    "message" to (exception.reason ?: exception.message)
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(exception: Exception): ResponseEntity<Map<String, Any>> {
        logger.error("Unexpected error while handling request", exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                mapOf(
                    "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error" to exception.javaClass.simpleName,
                    "message" to (exception.message ?: "No message available")
                )
            )
    }
}
