package com.devmind.energy.service.dto

import org.springframework.http.HttpStatusCode

class ApiException(val statusCode: HttpStatusCode, val responseBody: String?) :
    RuntimeException("Enedis request failed with status ${statusCode.value()}")
