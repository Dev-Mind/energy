package com.devmind.energy.service.dto

import java.time.Instant

data class EnedisRedirectResponseDto(
    val state: String,
    val code: String,
    val usagePointIds: List<String>,
    val validFrom: Instant,
    val validUntil: Instant
)
