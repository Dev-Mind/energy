package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.MeterReadingResponse
import com.devmind.energy.service.dto.SubscribedServicesResponse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/enedis/metering")
class MeterReadingApi(private val dataConnectService: DataConnectService) {

    companion object {
        private val logger = LoggerFactory.getLogger(MeterReadingApi::class.java)
        private val requestedDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    @GetMapping("/daily-consumption")
    fun getDailyConsumption(
        @RequestParam start: LocalDate,
        @RequestParam end: LocalDate,
        @RequestParam("usagePointId") usagePointId: String
    ): MeterReadingResponse? {
        return dataConnectService.getDailyConsumption(start, end, usagePointId)
    }

    @GetMapping("/daily-production")
    fun getDailyProduction(
        @RequestParam start: LocalDate,
        @RequestParam end: LocalDate,
        @RequestParam("usagePointId") usagePointId: String
    ): MeterReadingResponse? {
        return dataConnectService.getDailyProduction(start, end, usagePointId)
    }

    @GetMapping("/consumption-load-curve")
    fun getConsumptionLoadCurve(
        @RequestParam start: LocalDate,
        @RequestParam end: LocalDate,
        @RequestParam("usagePointId") usagePointId: String
    ): MeterReadingResponse? {
        return dataConnectService.getConsumptionLoadCurve(start, end, usagePointId)
    }

    @GetMapping("/production-load-curve")
    fun getProductionLoadCurve(
        @RequestParam start: LocalDate,
        @RequestParam end: LocalDate,
        @RequestParam("usagePointId") usagePointId: String
    ): MeterReadingResponse? {
        return dataConnectService.getProductionLoadCurve(start, end, usagePointId)
    }

    @GetMapping("/data")
    fun getMeterData(
        @RequestParam prm: String,
        @RequestParam dataType: String,
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): MeterReadingResponse? {
        logger.info(
            "getMeterData called: prm={}, dataType={}, startDate={}, endDate={}",
            prm, dataType, startDate, endDate
        )
        val normalizedPrm = prm.trim()
        if (normalizedPrm.isEmpty()) {
            logger.warn("getMeterData rejected: prm must not be blank")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "prm must not be blank")
        }

        val parsedStartDate = parseRequestedDate(startDate, "startDate")
        val parsedEndDate = parseRequestedDate(endDate, "endDate")

        return getMeterReading(dataType, parsedStartDate, parsedEndDate, normalizedPrm)
    }

    @GetMapping("/data-by-authorization")
    fun getMeterDataByAuthorization(
        @RequestParam autorisationId: String,
        @RequestParam dataType: String,
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): MeterReadingResponse? {
        logger.info(
            "getMeterDataByAuthorization called: autorisationId={}, dataType={}, startDate={}, endDate={}",
            autorisationId, dataType, startDate, endDate
        )
        val normalizedAutorisationId = autorisationId.trim()
        if (normalizedAutorisationId.isEmpty()) {
            logger.warn("getMeterDataByAuthorization rejected: autorisationId must not be blank")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "autorisationId must not be blank")
        }

        val parsedStartDate = parseRequestedDate(startDate, "startDate")
        val parsedEndDate = parseRequestedDate(endDate, "endDate")

        logger.info("Resolving usagePointId for autorisationId={}", normalizedAutorisationId)
        val usagePointId = dataConnectService.getUsagePointId(normalizedAutorisationId)
        logger.info("Resolved usagePointId={} for autorisationId={}", usagePointId, normalizedAutorisationId)

        return getMeterReading(dataType, parsedStartDate, parsedEndDate, usagePointId, normalizedAutorisationId)
    }
    @GetMapping("/usage-point")
    fun getUsagePointId(@RequestParam autorisationId: String): UsagePointResponse {
        logger.info("getUsagePointId called: autorisationId={}", autorisationId)
        val normalizedAutorisationId = autorisationId.trim()
        if (normalizedAutorisationId.isEmpty()) {
            logger.warn("getUsagePointId rejected: autorisationId must not be blank")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "autorisationId must not be blank")
        }

        val usagePointId = dataConnectService.getUsagePointId(normalizedAutorisationId)
        logger.info("Resolved usagePointId={} for autorisationId={}", usagePointId, normalizedAutorisationId)

        return UsagePointResponse(normalizedAutorisationId, usagePointId)
    }

    /**
     * Appel brut de l'API Enedis services_souscrits, sans interpretation de la reponse.
     *
     * Les parametres `etatCode` et `serviceType` sont omis du corps de la requete lorsqu'ils ne
     * sont pas renseignes : cela permet de comparer depuis Swagger la variante documentee dans le
     * guide et la variante minimale acceptee par Enedis.
     */
    @GetMapping("/subscribed-services")
    fun getSubscribedServices(
        @RequestParam autorisationId: String,
        @RequestParam(required = false, defaultValue = "false") comptage: Boolean,
        @RequestParam(required = false) etatCode: String?,
        @RequestParam(required = false) serviceType: String?
    ): SubscribedServicesResponse {
        val normalizedAutorisationId = autorisationId.trim()
        if (normalizedAutorisationId.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "autorisationId must not be blank")
        }

        return dataConnectService.getSubscribedServices(
            autorisationId = normalizedAutorisationId,
            comptage = comptage,
            etatCode = etatCode?.trim()?.ifEmpty { null },
            serviceType = serviceType?.trim()?.ifEmpty { null }
        )
    }

    private fun getMeterReading(
        dataType: String,
        startDate: LocalDate,
        endDate: LocalDate,
        usagePointId: String,
        personneId: String? = null
    ): MeterReadingResponse? =
        when (dataType.trim().lowercase()) {
            "consumption", "consumption_load_curve" ->
                dataConnectService.getConsumptionLoadCurve(startDate, endDate, usagePointId, personneId)

            "production", "production_load_curve" ->
                dataConnectService.getProductionLoadCurve(startDate, endDate, usagePointId, personneId)

            "daily_consumption" ->
                dataConnectService.getDailyConsumption(startDate, endDate, usagePointId, personneId)

            "daily_production" ->
                dataConnectService.getDailyProduction(startDate, endDate, usagePointId, personneId)

            "index_consumption" ->
                dataConnectService.getIndexConsumption(startDate, endDate, usagePointId, personneId)

            "index_production" ->
                dataConnectService.getIndexProduction(startDate, endDate, usagePointId, personneId)

            else -> {
                logger.warn("getMeterReading rejected: unsupported dataType={}", dataType)
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dataType doit valoir consumption, production, daily_consumption, " +
                        "daily_production, index_consumption ou index_production"
                )
            }
        }

    private fun parseRequestedDate(value: String, parameterName: String): LocalDate =
        try {
            LocalDate.parse(value.trim(), requestedDateFormatter)
        } catch (exception: DateTimeParseException) {
            logger.warn("Failed to parse {}={}: {}", parameterName, value, exception.message)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$parameterName must use format YYYY-MM-DD")
        }
}

data class UsagePointResponse(
    val autorisationId: String,
    val usagePointId: String
)
