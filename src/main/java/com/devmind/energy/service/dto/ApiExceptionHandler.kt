package com.devmind.energy.service.dto

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ApiException::class)
    fun handleEnedisApiException(exception: ApiException): ResponseEntity<String> {
        return ResponseEntity.status(exception.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(exception.responseBody)
    }
}
