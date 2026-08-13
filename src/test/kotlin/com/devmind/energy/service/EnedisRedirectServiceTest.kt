package com.devmind.energy.service

import com.devmind.energy.EnergyProperties
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class EnedisRedirectServiceTest {
    private val properties = mockk<EnergyProperties>()
    private val clock = Clock.fixed(Instant.parse("2026-08-13T10:00:00Z"), ZoneOffset.UTC)
    private val service = EnedisRedirectService(properties, clock)

    @Test
    fun `should parse redirect payload and compute validity`() {
        every { properties.duration } returns "P12M"

        val response = service.handleRedirect(" XYZ ", " 134567281 ", "12345; 67890 ;12345")

        assertEquals("XYZ", response.state)
        assertEquals("134567281", response.code)
        assertEquals(listOf("12345", "67890"), response.usagePointIds)
        assertEquals(Instant.parse("2026-08-13T10:00:00Z"), response.validFrom)
        assertEquals(Instant.parse("2027-08-13T10:00:00Z"), response.validUntil)
    }

    @Test
    fun `should reject empty usage point list`() {
        every { properties.duration } returns "P12M"

        val exception = assertThrows(ResponseStatusException::class.java) {
            service.handleRedirect("XYZ", "134567281", "; ;")
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }
}
