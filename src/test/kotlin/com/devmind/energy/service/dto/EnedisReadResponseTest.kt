package com.devmind.energy.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

/**
 * Verrouille la tolerance des DTO de lecture : Enedis peut renvoyer des `null` explicites et des
 * proprietes non documentees sans que cela ne transforme la reponse en 500 cote appelant.
 */
class EnedisReadResponseTest {

    private val mapper = JsonMapper.builder().build()

    @Test
    fun `deserializes a meter reading with explicit nulls everywhere`() {
        val response = mapper.readValue(
            """
            {
              "meter_reading": {
                "usage_point_id": null,
                "start": null,
                "end": null,
                "quality": null,
                "reading_type": null,
                "interval_reading": null
              }
            }
            """.trimIndent(),
            MeterReadingResponse::class.java
        )

        assertThat(response.meterReading).isNotNull()
        assertThat(response.meterReading?.intervalReading).isNull()
    }

    @Test
    fun `keeps an unknown measure type as a raw string instead of failing`() {
        val response = mapper.readValue(
            """
            {
              "meter_reading": {
                "usage_point_id": "12345678901234",
                "interval_reading": [
                  {"value": "540", "date": "2026-07-24", "measure_type": "Consumption"},
                  {"value": "320", "date": "2026-07-25", "measure_type": "CDC"}
                ],
                "une_cle_non_documentee": "ignoree"
              }
            }
            """.trimIndent(),
            MeterReadingResponse::class.java
        )

        assertThat(response.meterReading?.intervalReading)
            .extracting<String> { it.measureType }
            .containsExactly("Consumption", "CDC")
    }

    @Test
    fun `deserializes an empty meter reading payload`() {
        val response = mapper.readValue("{}", MeterReadingResponse::class.java)

        assertThat(response.meterReading).isNull()
    }

    @Test
    fun `deserializes customer usage points with explicit nulls and unknown fields`() {
        val response = mapper.readValue(
            """
            {
              "customer": {
                "customer_id": null,
                "usage_points": [
                  {
                    "usage_point": {
                      "usage_point_id": "12345678901234",
                      "usage_point_status": null,
                      "meter_type": null,
                      "usage_point_addresses": null
                    },
                    "contracts": null,
                    "champ_inconnu": true
                  }
                ]
              }
            }
            """.trimIndent(),
            CustomerUsagePointsResponse::class.java
        )

        val container = response.customer?.usagePoints?.single()
        assertThat(container?.usagePoint?.usagePointId).isEqualTo("12345678901234")
        assertThat(container?.contracts).isNull()
    }

    @Test
    fun `deserializes customer usage points with an explicit null list`() {
        val response = mapper.readValue(
            """{"customer":{"customer_id":"abc","usage_points":null}}""",
            CustomerUsagePointsResponse::class.java
        )

        assertThat(response.customer?.usagePoints).isNull()
    }
}
