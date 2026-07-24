package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.MeterReadingResponse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.LocalDate
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
        private val requestedDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-dd-MM")
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
        val normalizedPrm = prm.trim()
        if (normalizedPrm.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "prm must not be blank")
        }

        val start = parseRequestedDate(startDate, "startDate")
        val end = parseRequestedDate(endDate, "endDate")

        if (end.isBefore(start)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be on or after startDate")
        }

        return when (dataType.trim().lowercase()) {
            "consumption" -> dataConnectService.getConsumptionLoadCurve(start, end, normalizedPrm)
            "production" -> dataConnectService.getProductionLoadCurve(start, end, normalizedPrm)
            else -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "dataType must be consumption or production"
            )
        }
    }

    private fun parseRequestedDate(value: String, parameterName: String): LocalDate =
        try {
            LocalDate.parse(value.trim(), requestedDateFormatter)
        } catch (_: DateTimeParseException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$parameterName must use format YYYY-DD-MM")
        }
}
