package com.devmind.energy.api

import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.TokenService
import com.devmind.energy.service.dto.EnedisTokenResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/enedis")
class AuthenticationApi(private val tokenService: TokenService, private val properties: EnergyProperties) {
    @GetMapping("/token")
    fun token(): EnedisTokenResponseDto =
        tokenService.token ?: throw IllegalArgumentException("Token not found")

    @GetMapping("/account")
    fun redirectToEnedisAccount(): RedirectView {
        val clientId = properties.clientId
        val duration = properties.duration
        val state = "XDEV123"
        return RedirectView(
            "https://mon-compte-particulier.enedis.fr/dataconnect/v1/oauth2/authorize?client_id=$clientId&duration=$duration&response_type=code&state=$state"
        )
    }
}
