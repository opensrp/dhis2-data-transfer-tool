package pojo

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("indicator", "indicatorDescription", "dhisId", "categoryOptionComboId", "categoryOptionComboName")
data class IndicatorCsv(
        val indicator: String,
        val indicatorDescription: String,
        val dhisId: String,
        val categoryOptionComboId: String,
        val categoryOptionComboName: String
)
