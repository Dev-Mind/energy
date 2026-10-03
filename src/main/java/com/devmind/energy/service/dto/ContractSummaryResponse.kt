package com.devmind.energy.service.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ContractSummaryResponse(
    val segments: List<String>? = null,
    val generationLastActivationDate: String? = null,
    val consumptionLastActivationDate: String? = null,
    val lastSubscribedPowerChangeDate: String? = null,
    val servicesLevel: Int? = null
)
