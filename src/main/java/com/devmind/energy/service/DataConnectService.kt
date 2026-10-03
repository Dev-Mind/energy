package com.devmind.energy.service

import com.devmind.energy.service.dto.ApiException
import com.devmind.energy.service.dto.ContractSummaryResponse
import com.devmind.energy.service.dto.MeterReadingResponse
import com.devmind.energy.EnergyProperties
import com.devmind.energy.service.dto.SubscribedServicesRequest
import com.devmind.energy.service.dto.SubscribedServicesResponse
import java.net.URI
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.util.UriBuilder


@Service
class DataConnectService(
    private val restClient: RestClient,
    private val tokenService: TokenService,
    private val properties: EnergyProperties
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DataConnectService::class.java)
    }

    fun getDailyConsumption(start: LocalDate, end: LocalDate, usagePointId: String): MeterReadingResponse? {
        return getMeterReading("${properties.meteringPath}/consommation_quotidienne", start, end, usagePointId)
    }

    fun getDailyProduction(start: LocalDate, end: LocalDate, usagePointId: String): MeterReadingResponse? {
        return getMeterReading("${properties.meteringPath}/production_quotidienne", start, end, usagePointId)
    }

    fun getConsumptionLoadCurve(start: LocalDate, end: LocalDate, usagePointId: kotlin.String): MeterReadingResponse? {
        return getMeterReading("${properties.meteringPath}/courbe_de_charge_consommation", start, end, usagePointId)
    }

    fun getProductionLoadCurve(start: LocalDate, end: LocalDate, usagePointId: String): MeterReadingResponse? {
        return getMeterReading("${properties.meteringPath}/courbe_de_charge_production", start, end, usagePointId)
    }


    fun getContracts(usagePointId: String): ContractSummaryResponse? =
        getJson(ContractSummaryResponse::class.java) { builder ->
            builder.path("/synth_contrat_auto/v1")
                .queryParam("usage_point_id", usagePointId)
                .build()
        }

    fun getUsagePointId(autorisationId: String): String {
        val response = postJson(
            SubscribedServicesResponse::class.java,
            SubscribedServicesRequest(idAutorisation = autorisationId),
            personneId = autorisationId
        ) { builder -> builder.path(properties.subscribedServicesPath).build() }

        val activePointIds = response.services
            .filter { it.etatCode == null || it.etatCode.equals("ACTIF", ignoreCase = true) }
            .mapNotNull { it.pointId }
            .distinct()

        if (activePointIds.size != 1) {
            throw IllegalStateException(
                "Expected exactly one active usage point for authorization, got ${activePointIds.size}"
            )
        }
        return activePointIds.single()
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
        queryParam("dateDebut", start)
            .queryParam("dateFin", end)
            .queryParam("pointId", usagePointId)

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
            logger.error("Error while calling Enedis endpoint: status={}", exception.statusCode.value(), exception)
            throw ApiException(exception.statusCode, exception.responseBodyAsString)
        } catch (exception: Exception) {
            logger.error("Unexpected error while calling Enedis endpoint", exception)
            throw exception
        }
    }

    private fun <T : Any> postJson(
        responseType: Class<T>,
        request: Any,
        personneId: String? = null,
        uriFunction: (UriBuilder) -> URI,
    ): T {
        try {
            return restClient.post()
                .uri { uriFunction(it) }
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers { headers ->
                    headers.setBearerAuth(tokenService.accessToken)
                    if (!personneId.isNullOrBlank()) {
                        headers.set("personneId", personneId)
                    }
                }
                .body(request)
                .retrieve()
                .body(responseType)!!
        } catch (exception: RestClientResponseException) {
            logger.error("Error while calling Enedis endpoint: status={}", exception.statusCode.value(), exception)
            throw ApiException(exception.statusCode, exception.responseBodyAsString)
        } catch (exception: Exception) {
            logger.error("Unexpected error while calling Enedis endpoint", exception)
            throw exception
        }
    }
}
