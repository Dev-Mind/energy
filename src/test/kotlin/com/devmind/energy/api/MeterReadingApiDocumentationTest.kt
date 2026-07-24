package com.devmind.energy.api

import com.devmind.energy.service.DataConnectService
import com.devmind.energy.service.dto.MeterReadingResponse
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(RestDocumentationExtension::class)
class MeterReadingApiDocumentationTest {
    private lateinit var mockMvc: MockMvc
    private val dataConnectService = mockk<DataConnectService>()

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(MeterReadingApi(dataConnectService))
            .`apply`<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    fun `should generate rest docs snippets for unified consumption endpoint`() {
        every {
            dataConnectService.getConsumptionLoadCurve(
                LocalDate.of(2026, 7, 24),
                LocalDate.of(2026, 7, 25),
                "12345678901234"
            )
        } returns sampleReadingResponse()

        mockMvc.perform(
            get("/api/enedis/metering/data")
                .param("prm", "12345678901234")
                .param("dataType", "consumption")
                .param("startDate", "2026-24-07")
                .param("endDate", "2026-25-07")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "enedis-meter-data",
                    queryParameters(
                        parameterWithName("prm").description("PRM / usage point identifier"),
                        parameterWithName("dataType").description("Requested data type: `consumption` or `production`"),
                        parameterWithName("startDate").description("Start date using format `YYYY-DD-MM`"),
                        parameterWithName("endDate").description("End date using format `YYYY-DD-MM`")
                    ),
                    responseFields(
                        fieldWithPath("meter_reading").description("Meter reading payload"),
                        fieldWithPath("meter_reading.usage_point_id").description("Usage point identifier"),
                        fieldWithPath("meter_reading.start").description("Start timestamp returned by Enedis"),
                        fieldWithPath("meter_reading.end").description("End timestamp returned by Enedis"),
                        fieldWithPath("meter_reading.quality").description("Reading quality"),
                        fieldWithPath("meter_reading.reading_type").description("Reading type metadata"),
                        fieldWithPath("meter_reading.reading_type.measurement_kind").description("Measurement kind"),
                        fieldWithPath("meter_reading.reading_type.measuring_period").description("Measurement period"),
                        fieldWithPath("meter_reading.reading_type.unit").description("Reading unit"),
                        fieldWithPath("meter_reading.reading_type.aggregate").description("Aggregation mode"),
                        fieldWithPath("meter_reading.interval_reading").description("Interval readings"),
                        fieldWithPath("meter_reading.interval_reading[].value").description("Measured value"),
                        fieldWithPath("meter_reading.interval_reading[].date").description("Reading timestamp"),
                        fieldWithPath("meter_reading.interval_reading[].interval_length").description("Reading interval length"),
                        fieldWithPath("meter_reading.interval_reading[].measure_type").description("Measure type")
                    ),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Metering")
                            .summary("Get meter data by PRM")
                            .description("Retrieves the Enedis token server-side, then loads either consumption or production data for a PRM.")
                            .queryParameters(
                                parameterWithName("prm").description("PRM / usage point identifier"),
                                parameterWithName("dataType").description("Requested data type: `consumption` or `production`"),
                                parameterWithName("startDate").description("Start date using format `YYYY-DD-MM`"),
                                parameterWithName("endDate").description("End date using format `YYYY-DD-MM`")
                            )
                            .responseFields(
                                fieldWithPath("meter_reading").description("Meter reading payload"),
                                fieldWithPath("meter_reading.usage_point_id").description("Usage point identifier"),
                                fieldWithPath("meter_reading.start").description("Start timestamp returned by Enedis"),
                                fieldWithPath("meter_reading.end").description("End timestamp returned by Enedis"),
                                fieldWithPath("meter_reading.quality").description("Reading quality"),
                                fieldWithPath("meter_reading.reading_type").description("Reading type metadata"),
                                fieldWithPath("meter_reading.reading_type.measurement_kind").description("Measurement kind"),
                                fieldWithPath("meter_reading.reading_type.measuring_period").description("Measurement period"),
                                fieldWithPath("meter_reading.reading_type.unit").description("Reading unit"),
                                fieldWithPath("meter_reading.reading_type.aggregate").description("Aggregation mode"),
                                fieldWithPath("meter_reading.interval_reading").description("Interval readings"),
                                fieldWithPath("meter_reading.interval_reading[].value").description("Measured value"),
                                fieldWithPath("meter_reading.interval_reading[].date").description("Reading timestamp"),
                                fieldWithPath("meter_reading.interval_reading[].interval_length").description("Reading interval length"),
                                fieldWithPath("meter_reading.interval_reading[].measure_type").description("Measure type")
                            )
                            .build()
                    )
                )
            )

        verify(exactly = 1) {
            dataConnectService.getConsumptionLoadCurve(
                LocalDate.of(2026, 7, 24),
                LocalDate.of(2026, 7, 25),
                "12345678901234"
            )
        }
    }

    @Test
    fun `should route unified production endpoint`() {
        every {
            dataConnectService.getProductionLoadCurve(
                LocalDate.of(2026, 7, 24),
                LocalDate.of(2026, 7, 25),
                "12345678901234"
            )
        } returns sampleReadingResponse()

        mockMvc.perform(
            get("/api/enedis/metering/data")
                .param("prm", "12345678901234")
                .param("dataType", "production")
                .param("startDate", "2026-24-07")
                .param("endDate", "2026-25-07")
        )
            .andExpect(status().isOk)

        verify(exactly = 1) {
            dataConnectService.getProductionLoadCurve(
                LocalDate.of(2026, 7, 24),
                LocalDate.of(2026, 7, 25),
                "12345678901234"
            )
        }
    }

    @Test
    fun `should reject invalid unified endpoint parameters`() {
        mockMvc.perform(
            get("/api/enedis/metering/data")
                .param("prm", "12345678901234")
                .param("dataType", "invalid")
                .param("startDate", "2026-24-07")
                .param("endDate", "2026-25-07")
        )
            .andExpect(status().isBadRequest)
    }

    private fun sampleReadingResponse() = MeterReadingResponse(
        meterReading = MeterReadingResponse.MeterReading(
            usagePointId = "12345678901234",
            start = "2026-07-24T00:00:00+02:00",
            end = "2026-07-25T00:00:00+02:00",
            quality = "BRUTE",
            readingType = MeterReadingResponse.ReadingType(
                measurementKind = "energy",
                measuringPeriod = "PT30M",
                unit = "Wh",
                aggregate = "sum"
            ),
            intervalReading = listOf(
                MeterReadingResponse.IntervalReading(
                    value = "120",
                    date = "2026-07-24T00:00:00+02:00",
                    intervalLength = "PT30M",
                    measureType = "consumption"
                )
            )
        )
    )
}
