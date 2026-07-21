package com.devmind.energy.service

import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.dto.ApiException
import com.devmind.energy.service.dto.EnedisTokenResponseDto
import java.time.Clock
import java.time.Instant
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import kotlin.math.max
import kotlin.math.min

@Service
class TokenService(
    private val restClient: RestClient,
    private val properties: EnergyProperties,
    private val clock: Clock
) {
    private var cachedToken: CachedToken? = null

    @get:Synchronized
    val token: EnedisTokenResponseDto?
        get() {
            val now = Instant.now(clock)
            if (cachedToken != null && now.isBefore(cachedToken!!.refreshAt)) {
                return cachedToken!!.response
            }

            val response = fetchToken()
            cachedToken = CachedToken(response, computeRefreshAt(response, now))
            return response
        }

    val accessToken: String
        get() = this.token!!.accessToken

    private fun fetchToken(): EnedisTokenResponseDto {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
            add("client_id", properties.clientId)
            add("client_secret", properties.secret)
        }

        try {
            val response: EnedisTokenResponseDto = restClient.post()
                .uri(properties.oauthPath)
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body<EnedisTokenResponseDto>(EnedisTokenResponseDto::class.java)!!

            check(response.accessToken.isNotBlank()) {
                "Enedis token response did not contain an access token"
            }
            return response
        } catch (exception: RestClientResponseException) {
            throw ApiException(exception.statusCode, exception.responseBodyAsString)
        }
    }

    private fun computeRefreshAt(response: EnedisTokenResponseDto, now: Instant): Instant? {
        val expiresInSeconds = max(response.expiresIn, 0)
        var skewSeconds = min(300, max(30, expiresInSeconds / 10))
        if (skewSeconds >= expiresInSeconds) {
            skewSeconds = 0
        }
        return now.plusSeconds(expiresInSeconds - skewSeconds)
    }

    @JvmRecord
    private data class CachedToken(val response: EnedisTokenResponseDto?, val refreshAt: Instant?)
}
