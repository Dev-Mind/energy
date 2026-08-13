package com.devmind.energy.service

import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.dto.EnedisRedirectResponseDto
import java.time.Clock
import java.time.Instant
import java.time.Period
import java.time.ZoneOffset
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class EnedisRedirectService(
    private val properties: EnergyProperties,
    private val clock: Clock
) {
    fun handleRedirect(state: String, code: String, usagePointId: String): EnedisRedirectResponseDto {
        val normalizedState = state.trim()
        if (normalizedState.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "state must not be blank")
        }

        val normalizedCode = code.trim()
        if (normalizedCode.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "code must not be blank")
        }

        val usagePointIds = usagePointId.split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        if (usagePointIds.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "usage_point_id must contain at least one PRM")
        }

        val validFrom = Instant.now(clock)
        val validUntil = computeValidUntil(validFrom)

        return EnedisRedirectResponseDto(
            state = normalizedState,
            code = normalizedCode,
            usagePointIds = usagePointIds,
            validFrom = validFrom,
            validUntil = validUntil
        )
    }

    private fun computeValidUntil(validFrom: Instant): Instant {
        val configuredDuration = properties.duration.trim()
        if (configuredDuration.isEmpty()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "energy.duration must not be blank")
        }

        val period = try {
            Period.parse(configuredDuration)
        } catch (_: RuntimeException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "energy.duration must be an ISO-8601 period")
        }

        return validFrom.atZone(ZoneOffset.UTC).plus(period).toInstant()
    }
}
