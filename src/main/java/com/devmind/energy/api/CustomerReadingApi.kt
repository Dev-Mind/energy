package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.CustomerUsagePointsResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/enedis/customers")
class CustomerReadingApi(private val dataConnectService: DataConnectService) {

    @GetMapping("/contracts")
    fun getContracts(@RequestParam("usagePointId") usagePointId: String): CustomerUsagePointsResponse? {
        return dataConnectService.getContracts(usagePointId)
    }
}
