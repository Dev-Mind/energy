package com.devmind.energy.api

import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.EnedisRedirectService
import com.devmind.energy.service.TokenService
import com.devmind.energy.service.dto.EnedisRedirectResponseDto
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
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(RestDocumentationExtension::class)
class AuthenticationApiDocumentationTest {
    private lateinit var mockMvc: MockMvc
    private val tokenService = mockk<TokenService>()
    private val properties = mockk<EnergyProperties>()
    private val enedisRedirectService = mockk<EnedisRedirectService>()

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        every { properties.clientId } returns "sample-client-id"
        every { properties.duration } returns "P12M"

        mockMvc = MockMvcBuilders.standaloneSetup(AuthenticationApi(tokenService, properties, enedisRedirectService))
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

    @Test
    fun `should generate rest docs snippets for redirect endpoint`() {
        every {
            enedisRedirectService.handleRedirect("XYZ", "134567281", "12345;67890")
        } returns EnedisRedirectResponseDto(
            state = "XYZ",
            code = "134567281",
            usagePointIds = listOf("12345", "67890"),
            validFrom = java.time.Instant.parse("2026-08-13T10:00:00Z"),
            validUntil = java.time.Instant.parse("2027-08-13T10:00:00Z")
        )

        mockMvc.perform(
            get("/api/enedis/redirect")
                .param("state", "XYZ")
                .param("usage_point_id", "12345;67890")
                .param("code", "134567281")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "enedis-redirect",
                    queryParameters(
                        parameterWithName("state").description("Opaque state linked to the user account"),
                        parameterWithName("usage_point_id").description("One or more PRMs separated by `;`"),
                        parameterWithName("code").description("Authorization code returned by Enedis")
                    ),
                    responseFields(
                        fieldWithPath("state").description("Echoed OAuth state"),
                        fieldWithPath("code").description("Echoed OAuth authorization code"),
                        fieldWithPath("usagePointIds").description("Parsed PRM list"),
                        fieldWithPath("validFrom").description("Consent validity start timestamp"),
                        fieldWithPath("validUntil").description("Consent validity end timestamp")
                    ),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Authentication")
                            .summary("Handle Enedis redirect callback")
                            .description("Parses state/code/usage_point_id, normalizes PRMs, and returns the computed validity range.")
                            .queryParameters(
                                parameterWithName("state").description("Opaque state linked to the user account"),
                                parameterWithName("usage_point_id").description("One or more PRMs separated by `;`"),
                                parameterWithName("code").description("Authorization code returned by Enedis")
                            )
                            .responseFields(
                                fieldWithPath("state").description("Echoed OAuth state"),
                                fieldWithPath("code").description("Echoed OAuth authorization code"),
                                fieldWithPath("usagePointIds").description("Parsed PRM list"),
                                fieldWithPath("validFrom").description("Consent validity start timestamp"),
                                fieldWithPath("validUntil").description("Consent validity end timestamp")
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
