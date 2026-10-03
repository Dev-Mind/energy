package com.devmind.energy.api

import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.EnedisRedirectService
import com.devmind.energy.service.EnedisStateService
import com.devmind.energy.service.TokenService
import com.devmind.energy.service.dto.EnedisRedirectResponseDto
import com.devmind.energy.service.dto.EnedisTokenResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/enedis")
class AuthenticationApi(
    private val tokenService: TokenService,
    private val properties: EnergyProperties,
    private val enedisRedirectService: EnedisRedirectService,
    private val stateService: EnedisStateService
) {
    @GetMapping("/token")
    fun token(): EnedisTokenResponseDto =
        tokenService.token ?: throw IllegalArgumentException("Token not found")

    @GetMapping("/account")
    fun redirectToEnedisAccount(): RedirectView {
        val state = stateService.create()
        val target = UriComponentsBuilder.fromUriString(properties.authorizeUrl)
            .queryParam("client_id", properties.clientId)
            .queryParam("state", state)
            .queryParam("duration", properties.duration)
            .queryParam("response_type", "code")
            .build()
            .encode()
            .toUriString()
        return RedirectView(target)
    }

    @GetMapping("/redirect", "/refirect")
    fun handleEnedisRedirect(
        @RequestParam state: String,
        @RequestParam("autorisation_id") autorisationId: String
    ): EnedisRedirectResponseDto =
        enedisRedirectService.handleRedirect(state, autorisationId)
}
