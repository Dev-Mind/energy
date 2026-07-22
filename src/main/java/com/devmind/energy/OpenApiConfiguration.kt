package com.devmind.energy

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration(
    @Value("\${springdoc.server-url}") private val serverUrl: String
) {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(Info().title("Dev-Mind Energy API"))
        .addServersItem(
            Server().url(serverUrl).description("REST API documentation generated from Spring REST Docs snippet")
        )
}
