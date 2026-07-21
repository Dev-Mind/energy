package com.devmind.energy.service

import com.devmind.energy.service.dto.CustomerUsagePointsResponse
import com.devmind.energy.service.dto.ApiException
import com.devmind.energy.service.dto.MeterReadingResponse
import java.net.URI
import java.time.LocalDate
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.util.UriBuilder


@Service
class DataConnectService(
    private val restClient: RestClient,
    private val tokenService: TokenService
) {

    fun getConsumptionLoadCurve(start: LocalDate, end: LocalDate, usagePointId: kotlin.String): MeterReadingResponse? {
        return getMeterReading("/metering_data_clc/v5/consumption_load_curve", start, end, usagePointId)
    }

    fun getProductionLoadCurve(start: LocalDate, end: LocalDate, usagePointId: String): MeterReadingResponse? {
        return getMeterReading("/metering_data_plc/v5/production_load_curve", start, end, usagePointId)
    }


    fun getContracts(usagePointId: String): CustomerUsagePointsResponse? =
        getJson(CustomerUsagePointsResponse::class.java) { builder ->
            builder.path("/customers_upc/v5/usage_points/contracts")
                .queryParam("usage_point_id", usagePointId)
                .build()
        }

    private fun getMeterReading(
        path: String,
        start: LocalDate,
        end: LocalDate,
        usagePointId: String
    ): MeterReadingResponse? =
        getJson(MeterReadingResponse::class.java) { builder ->
            builder.path(path).addMeteringParams(start, end, usagePointId).build()
        }

    private fun UriBuilder.addMeteringParams(
        start: LocalDate,
        end: LocalDate,
        usagePointId: String
    ): UriBuilder =
        queryParam("start", start)
            .queryParam("end", end)
            .queryParam("usage_point_id", usagePointId)

    private fun <T : Any> getJson(
        responseType: Class<T>,
        uriFunction: (UriBuilder) -> URI,
    ): T? {
        try {
            return restClient.get()
                .uri { uriFunction(it) }
                .accept(APPLICATION_JSON)
                .headers { it.setBearerAuth(tokenService.accessToken) }
                .retrieve()
                .body(responseType)
        } catch (exception: RestClientResponseException) {
            throw ApiException(exception.statusCode, exception.responseBodyAsString)
        }
    }
}
