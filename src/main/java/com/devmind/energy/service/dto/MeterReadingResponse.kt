package com.devmind.energy.service.dto

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MeterReadingResponse(
    val meterReading: MeterReading?
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MeterReading(
        val usagePointId: String?,
        val start: String?,
        val end: String?,
        val quality: String?,
        val readingType: ReadingType?,
        val intervalReading: List<IntervalReading>
    )
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ReadingType(
        val measurementKind: String?,
        val measuringPeriod: String?,
        val unit: String?,
        val aggregate: String?
    )
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class IntervalReading(
        val value: String,
        val date: String,
        val intervalLength: String,
        val measureType: String
    )
}
