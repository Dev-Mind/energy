package com.devmind.energy.service

import com.devmind.energy.EnergyProperties
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

class DataConnectServiceTest {
    private lateinit var server: MockRestServiceServer
    private lateinit var service: DataConnectService

    @BeforeEach
    fun setUp() {
        val builder = RestClient.builder().baseUrl("https://gw.ext.prod-sandbox.api.enedis.fr")
        server = MockRestServiceServer.bindTo(builder).build()

        val tokenService = mockk<TokenService>()
        every { tokenService.accessToken } returns "access-token"

        service = DataConnectService(
            builder.build(),
            tokenService,
            EnergyProperties(
                baseUrl = "https://gw.ext.prod-sandbox.api.enedis.fr",
                clientId = "client-id",
                duration = "P12M",
                secret = "secret",
                oauthPath = "/oauth2/v3/token",
                authorizeUrl = "https://example.com/authorize",
                subscribedServicesPath = "/subscribed_services/v1",
                meteringPath = "/mesure_synchrone_auto/v1",
                consumptionLoadCurvePath = "/metering_data_clc/v5/consumption_load_curve",
                productionLoadCurvePath = "/metering_data_plc/v5/production_load_curve",
                applicationName = "ENERGY"
            )
        )
    }

    @Test
    fun `should call the consumption load curve v5 endpoint`() {
        expectLoadCurveRequest(
            "https://gw.ext.prod-sandbox.api.enedis.fr/metering_data_clc/v5/consumption_load_curve" +
                "?start=2019-05-06&end=2019-05-12&usage_point_id=16401220101758"
        )

        service.getConsumptionLoadCurve(
            LocalDate.of(2019, 5, 6),
            LocalDate.of(2019, 5, 12),
            "16401220101758"
        )

        server.verify()
    }

    @Test
    fun `should call the production load curve v5 endpoint`() {
        expectLoadCurveRequest(
            "https://gw.ext.prod-sandbox.api.enedis.fr/metering_data_plc/v5/production_load_curve" +
                "?start=2019-05-06&end=2019-05-12&usage_point_id=16401220101758"
        )

        service.getProductionLoadCurve(
            LocalDate.of(2019, 5, 6),
            LocalDate.of(2019, 5, 12),
            "16401220101758"
        )

        server.verify()
    }

    private fun expectLoadCurveRequest(url: String) {
        server.expect(requestTo(url))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
            .andRespond(withSuccess("""{"meter_reading":{}}""", MediaType.APPLICATION_JSON))
    }
}
