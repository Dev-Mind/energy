package com.devmind.energy.service.dto

data class SubscribedServicesRequest(
    val etatCode: String = "ACTIF",
    val serviceType: String = "ACCES",
    val comptage: Boolean = false,
    val idAutorisation: String
)

data class SubscribedServicesResponse(
    val nbTotalServices: Int? = null,
    val services: List<SubscribedService> = emptyList()
)

data class SubscribedService(
    val id: String? = null,
    val pointId: String? = null,
    val serviceCode: String? = null,
    val etatCode: String? = null,
    val mesuresTypeCode: String? = null,
    val injection: Boolean? = null,
    val soutirage: Boolean? = null
)
