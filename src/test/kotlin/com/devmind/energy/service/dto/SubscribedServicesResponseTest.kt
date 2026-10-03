package com.devmind.energy.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class SubscribedServicesResponseTest {

    private val mapper = JsonMapper.builder().build()

    @Test
    fun `deserializes an explicit null serviceSouscrit`() {
        val response = mapper.readValue(
            """{"nbTotalServices":0,"serviceSouscrit":null}""",
            SubscribedServicesResponse::class.java
        )

        assertThat(response.nbTotalServices).isZero()
        assertThat(response.services).isEmpty()
    }

    @Test
    fun `deserializes subscribed services and ignores unknown properties`() {
        val response = mapper.readValue(
            """{"nbTotalServices":1,"serviceSouscrit":[{"pointId":"11111111111111","etatCode":"ACTIF","unknown":"x"}]}""",
            SubscribedServicesResponse::class.java
        )

        assertThat(response.services).hasSize(1)
        assertThat(response.services[0].pointId).isEqualTo("11111111111111")
        assertThat(response.services[0].etatCode).isEqualTo("ACTIF")
    }

    @Test
    fun `omits the optional filters when they are not set`() {
        val json = mapper.writeValueAsString(SubscribedServicesRequest(autorisationId = "88006"))

        assertThat(json)
            .contains("\"autorisationId\":\"88006\"")
            .contains("\"comptage\":false")
            .doesNotContain("etatCode")
            .doesNotContain("serviceType")
    }

    @Test
    fun `serializes the optional filters when they are set`() {
        val json = mapper.writeValueAsString(
            SubscribedServicesRequest(autorisationId = "88006", etatCode = "ACTIF", serviceType = "ACCES")
        )

        assertThat(json)
            .contains("\"etatCode\":\"ACTIF\"")
            .contains("\"serviceType\":\"ACCES\"")
    }
}
