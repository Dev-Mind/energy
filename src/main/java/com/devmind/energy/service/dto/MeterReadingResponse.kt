package com.devmind.energy.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

/**
 * Reponse de l'API mesure d'Enedis.
 *
 * Volontairement permissif : tous les champs sont nullables, typés `String` et les proprietes
 * inconnues sont ignorees. `measureType` en particulier n'est PAS un enum cote serveur, car les
 * valeurs du referentiel Enedis (`CDC`, `PMAX`, `IDX`, `ENERGIE`, `ITC`...) peuvent evoluer : un
 * enum strict ferait echouer la deserialisation de toute la reponse sur une seule valeur inconnue.
 * Les clients de cette API doivent appliquer la meme prudence.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MeterReadingResponse(
    val meterReading: MeterReading? = null
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MeterReading(
        val usagePointId: String? = null,
        val start: String? = null,
        val end: String? = null,
        val quality: String? = null,
        val readingType: ReadingType? = null,
        val intervalReading: List<IntervalReading>? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ReadingType(
        val measurementKind: String? = null,
        val measuringPeriod: String? = null,
        val unit: String? = null,
        val aggregate: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class IntervalReading(
        val value: String? = null,
        val date: String? = null,
        val intervalLength: String? = null,
        val measureType: String? = null
    )
}
