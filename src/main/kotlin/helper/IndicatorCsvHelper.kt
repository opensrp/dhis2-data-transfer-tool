package helper

import AppConstants
import ApplicationProperty
import configuration.Application
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import model.DataSet
import pojo.IndicatorCsv
import utils.Utils
import wrapper.DataSetWrapper

open class IndicatorCsvHelper {

    suspend fun getDataSetDetails(dataSetName: String): DataSet? {
        val client = Application.AppHttpClient.client
        val dhis2Url = ApplicationProperty.getProperty(AppConstants.Dhis2.URL)
        val url = "$dhis2Url/api/dataSets"
        var dataSetObject: DataSet? = null

        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Basic ${Application.Dhis2Auth.authBase64}")
            }

            parameter("paging", "false")

            parameter("fields", "id,name")

        }

        val dataSetWrapper: DataSetWrapper = response.receive()

        if (dataSetWrapper != null) {
            for (dataSet in dataSetWrapper.dataSets) {
                if (dataSetName != null) {
                    if (dataSetName == dataSet.name) {
                        dataSetObject = dataSet
                        break
                    }
                }
            }
        }
        return dataSetObject
    }

    suspend fun fetchIndicators(dataSet: DataSet) {
        val dhis2Url = ApplicationProperty.getProperty(AppConstants.Dhis2.URL)
        val url = "$dhis2Url/api/dataSets/${dataSet.id}"

        val client = Application.AppHttpClient.client
        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Basic ${Application.Dhis2Auth.authBase64}")
            }
            parameter(
                "fields",
                "id,name,dataSetElements[id,name,dataElement[id,name,categoryCombo[id,name,categoryOptionCombos[id,name]]]"
            )
        }

        val dataSetResponse: DataSet = response.receive()
        val dataSetElements = dataSetResponse.dataSetElements

        val indicators: MutableList<IndicatorCsv> = mutableListOf()

        if (dataSetElements != null) {
            for (dataSetElement in dataSetElements) {
                val dataElement = dataSetElement.dataElement ?: return
                val dataElementName = dataElement.name.trim()
                val dataElementId = dataElement.id.trim()
                val categoryCombo = dataElement.categoryCombo
                val categoryComboName = categoryCombo.name.trim()
                val categoryComboId = categoryCombo.id
                if (categoryCombo.categoryOptionCombos != null) {
                    for (categoryOptionCombo in categoryCombo.categoryOptionCombos) {
                        val categoryComboOptionName = categoryOptionCombo.name.trim()
                        val categoryComboOptionId = categoryOptionCombo.id.trim()
                        indicators.add(
                            IndicatorCsv(
                                dataElementName,
                                "$dataElementName $categoryComboOptionName",
                                dataElementId,
                                categoryComboOptionId,
                                categoryComboOptionName
                            )
                        )
                    }
                }
            }
        }

        val indicatorCsvFileName: String =
            ApplicationProperty.getProperty(AppConstants.Dhis2.INDICATOR_FILE_NAME) as String
        Utils.writeToCsv(indicators, "${dataSet.name.replace(" ", "_")}-$indicatorCsvFileName")
    }
}
