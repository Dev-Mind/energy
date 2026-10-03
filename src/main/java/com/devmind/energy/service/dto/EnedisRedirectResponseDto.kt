package com.devmind.energy.service.dto

import java.time.Instant

data class EnedisRedirectResponseDto(
    val state: String,
    val autorisationId: String,
    val usagePointId: String,
    val validFrom: Instant,
    val validUntil: Instant
)
