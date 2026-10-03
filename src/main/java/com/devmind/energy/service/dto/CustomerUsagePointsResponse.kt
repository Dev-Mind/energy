package com.devmind.energy.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

/**
 * Reponse de l'API clients d'Enedis.
 *
 * Tous les champs sont nullables et toutes les proprietes inconnues sont ignorees : il s'agit d'un
 * contrat externe que nous ne maitrisons pas. Un type non-nullable transforme le moindre
 * `"cle": null` renvoye par Enedis en 500 `RestClientException` cote appelant, car la valeur par
 * defaut Kotlin ne s'applique que lorsque la cle est absente du JSON, jamais lorsqu'elle est
 * presente avec la valeur null.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CustomerUsagePointsResponse(
    val customer: Customer? = null
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Customer(
        val customerId: String? = null,
        val usagePoints: List<UsagePointContainer>? = null
    )


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePointContainer(
        val usagePoint: UsagePoint? = null,
        val contracts: Contracts? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePoint(
        val usagePointId: String? = null,
        val usagePointStatus: String? = null,
        val meterType: String? = null,
        val usagePointAddresses: UsagePointAddresses? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Contracts(
        val segment: String? = null,
        val subscribedPower: String? = null,
        val lastActivationDate: String? = null,
        val distributionTariff: String? = null,
        val lastDistributionTariffChangeDate: String? = null,
        val offpeakHours: String? = null,
        val contractStatus: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class UsagePointAddresses(
        val street: String? = null,
        val locality: String? = null,
        val postalCode: String? = null,
        val inseeCode: String? = null,
        val city: String? = null,
        val country: String? = null,
        val geoPoints: GeoPoints? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class GeoPoints(
        val latitude: String? = null,
        val longitude: String? = null,
        val altitude: String? = null
    )
}
