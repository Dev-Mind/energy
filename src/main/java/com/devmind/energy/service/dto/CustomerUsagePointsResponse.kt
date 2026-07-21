package com.devmind.energy.service.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CustomerUsagePointsResponse(
    val customer: Customer?
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Customer(
        val customerId: String,
        val usagePoints: List<UsagePointContainer>
    )


    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePointContainer(
        val usagePoint: UsagePoint,
        val contracts: Contracts
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePoint(
        val usagePointId: String,
        val usagePointStatus: String,
        val meterType: String,
        val usagePointAddresses: UsagePointAddresses?
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Contracts(
        val segment: String,
        val subscribedPower: String,
        val lastActivationDate: String,
        val distributionTariff: String,
        val lastDistributionTariffChangeDate: String,
        val offpeakHours: String?,
        val contractStatus: String?
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePointAddresses(
        val street: String,
        val locality: String,
        val postalCode: String,
        val inseeCode: String?,
        val city: String?,
        val country: String?,
        val geoPoints: GeoPoints?
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class GeoPoints(
        val latitude: String?,
        val longitude: String?,
        val altitude: String?
    )
}
