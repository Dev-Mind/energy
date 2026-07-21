package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.MeterReadingResponse
import java.time.LocalDate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/enedis/metering")
class MeterReadingApi(private val dataConnectService: DataConnectService) {

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
}
