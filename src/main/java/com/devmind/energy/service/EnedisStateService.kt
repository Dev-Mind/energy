package com.devmind.energy.service

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@Service
class EnedisStateService(private val clock: Clock) {
    private val states = ConcurrentHashMap<String, Instant>()
    private val stateLifetime = Duration.ofMinutes(10)

    fun create(): String {
        val state = UUID.randomUUID().toString()
        states[state] = Instant.now(clock).plus(stateLifetime)
        return state
    }

    fun consume(state: String) {
        val expiresAt = states.remove(state)
        if (expiresAt == null || !Instant.now(clock).isBefore(expiresAt)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "state is unknown or expired")
        }
    }
}
