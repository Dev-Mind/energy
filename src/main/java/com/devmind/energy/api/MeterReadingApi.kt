package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.MeterReadingResponse
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

        return getMeterReading(dataType, parsedStartDate, parsedEndDate, usagePointId)
    }

    private fun getMeterReading(
        dataType: String,
        startDate: LocalDate,
        endDate: LocalDate,
        usagePointId: String
    ): MeterReadingResponse? =
        when (dataType.trim().lowercase()) {
            "consumption" -> dataConnectService.getConsumptionLoadCurve(startDate, endDate, usagePointId)
            "production" -> dataConnectService.getProductionLoadCurve(startDate, endDate, usagePointId)
            else -> {
                logger.warn("getMeterReading rejected: unsupported dataType={}", dataType)
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dataType must be consumption or production"
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
