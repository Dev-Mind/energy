package com.devmind.energy.api

import com.devmind.energy.service.TokenService
import com.devmind.energy.service.dto.EnedisTokenResponseDto
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(RestDocumentationExtension::class)
class AuthenticationApiDocumentationTest {
    private lateinit var mockMvc: MockMvc
    private val tokenService = mockk<TokenService>()

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(AuthenticationApi(tokenService))
            .`apply`<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    fun `should generate rest docs snippets for token endpoint`() {
        every { tokenService.token } returns sampleToken()

        mockMvc.perform(get("/api/enedis/token").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(
                document(
                    "enedis-token",
                    responseFields(
                        fieldWithPath("access_token").description("OAuth access token"),
                        fieldWithPath("scope").description("Granted OAuth scopes"),
                        fieldWithPath("token_type").description("OAuth token type"),
                        fieldWithPath("expires_in").description("Token lifetime in seconds")
                    ),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Authentication")
                            .summary("Get Enedis access token")
                            .description("Returns the current Enedis OAuth token.")
                            .responseFields(
                                fieldWithPath("access_token").description("OAuth access token"),
                                fieldWithPath("scope").description("Granted OAuth scopes"),
                                fieldWithPath("token_type").description("OAuth token type"),
                                fieldWithPath("expires_in").description("Token lifetime in seconds")
                            )
                            .build()
                    )
                )
            )
    }

    private fun sampleToken() = EnedisTokenResponseDto(
        accessToken = "sample-access-token",
        scope = "sample-scope",
        tokenType = "Bearer",
        expiresIn = 3600
    )
}
