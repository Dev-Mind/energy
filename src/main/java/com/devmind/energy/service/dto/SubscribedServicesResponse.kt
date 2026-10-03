package com.devmind.energy.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Corps de la requete POST /subscribed_services/v1.
 * Seuls `autorisationId` et `comptage` sont obligatoires d'apres le guide Enedis.
 */
data class SubscribedServicesRequest(
    val autorisationId: String,
    val comptage: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscribedServicesResponse(
    val nbTotalServices: Int? = null,
    val serviceSouscrit: List<SubscribedService> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscribedService(
    val id: String? = null,
    val pointId: String? = null,
    val serviceCode: String? = null,
    val etatCode: String? = null,
    val etatLibelle: String? = null,
    val mesuresTypeCode: String? = null,
    val mesuresPas: String? = null,
    val dateDebut: String? = null,
    val dateFin: String? = null,
    val sirenBeneficiaire: String? = null,
    val publicationDonnees: Boolean? = null,
    val injection: Boolean? = null,
    val soutirage: Boolean? = null,
    val autorisation: SubscribedServiceAuthorization? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscribedServiceAuthorization(
    val autorisationId: Long? = null,
    val autorisationLibelle: String? = null,
    val autorisationType: String? = null,
    val autorisationStatut: String? = null
)
