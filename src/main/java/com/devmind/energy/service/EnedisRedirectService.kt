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
    private val clock: Clock,
    private val dataConnectService: DataConnectService,
    private val stateService: EnedisStateService
) {
    fun handleRedirect(state: String, autorisationId: String): EnedisRedirectResponseDto {
        val normalizedState = state.trim()
        if (normalizedState.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "state must not be blank")
        }
        stateService.consume(normalizedState)

        val normalizedAutorisationId = autorisationId.trim()
        if (normalizedAutorisationId.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "autorisation_id must not be blank")
        }

        val validFrom = Instant.now(clock)
        val validUntil = computeValidUntil(validFrom)
        val usagePointId = try {
            dataConnectService.getUsagePointId(normalizedAutorisationId)
        } catch (exception: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, exception.message, exception)
        }

        return EnedisRedirectResponseDto(
            state = normalizedState,
            autorisationId = normalizedAutorisationId,
            usagePointId = usagePointId,
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
