package com.devmind.energy.service.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Corps de la requete POST /subscribed_services/v1.
 *
 * `comptage` pilote la nature de la reponse : `false` renvoie la liste des services, `true` ne
 * renvoie que leur nombre (`nbTotalServices`) avec `serviceSouscrit` a null.
 *
 * `etatCode` et `serviceType` sont documentes comme obligatoires dans le guide mais Enedis rejette
 * la requete (400) lorsqu'ils sont envoyes avec les valeurs du guide. Ils sont donc facultatifs ici
 * et omis du JSON quand ils valent null, ce qui permet de tester les deux variantes sans redeployer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SubscribedServicesRequest(
    val autorisationId: String,
    val comptage: Boolean = false,
    val etatCode: String? = null,
    val serviceType: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscribedServicesResponse(
    val nbTotalServices: Int? = null,
    /**
     * Enedis renvoie explicitement `"serviceSouscrit": null` lorsque l'autorisation n'a aucun
     * service, ce qui interdit d'utiliser un type non-nullable ici : la valeur par defaut Kotlin
     * ne s'applique pas quand la cle est presente dans le JSON.
     */
    val serviceSouscrit: List<SubscribedService>? = null
) {
    @get:JsonIgnore
    val services: List<SubscribedService>
        get() = serviceSouscrit.orEmpty()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubscribedService(
    val id: String? = null,
    val pointId: String? = null,
    val serviceCode: String? = null,
    val etatCode: String? = null,
    val etatLibelle: String? = null,
    val mesuresTypeCode: String? = null,
    val mesuresPas: String? = null,
    val mesuresCorrigees: Boolean? = null,
    val periodiciteTransmission: String? = null,
    val espaceDynamique: String? = null,
    val dateDebut: String? = null,
    val dateFin: String? = null,
    val sirenTitulaire: String? = null,
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
