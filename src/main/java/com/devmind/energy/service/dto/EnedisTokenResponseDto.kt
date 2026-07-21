package com.devmind.energy.service.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EnedisTokenResponseDto(
    val accessToken: String,
    val scope: String? = null,
    val tokenType: String? = null,
    val expiresIn: Long = 0
)
