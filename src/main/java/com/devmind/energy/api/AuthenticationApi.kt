package com.devmind.energy.api

import com.devmind.energy.service.TokenService
import com.devmind.energy.service.dto.EnedisTokenResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/enedis/token")
class AuthenticationApi(private val tokenService: TokenService) {
    @GetMapping
    fun token(): EnedisTokenResponseDto =
        tokenService.token ?: throw IllegalArgumentException("Token not found")
}
