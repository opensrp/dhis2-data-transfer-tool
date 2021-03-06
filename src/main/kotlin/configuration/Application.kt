package configuration

import AppConstants
import ApplicationProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import helper.IndicatorCsvHelper
import helper.LocationImportHelper
import helper.OpensrpAuthHelper.getOpensrpAuthToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.AuthToken
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory

class Application {

    object ApplicationCsvMapper {
        val csvMapper = CsvMapper().apply {
            registerModules(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
        }
    }

    object AppHttpClient {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120000
                connectTimeoutMillis = 120000
                socketTimeoutMillis = 120000
            }
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.NONE
            }
        }
    }

    object Dhis2Auth {
        private val username: String = ApplicationProperty.getProperty(AppConstants.Dhis2.USERNAME) as String
        private val password: String = ApplicationProperty.getProperty(AppConstants.Dhis2.PASSWORD) as String
        val authBase64: String = Base64.encodeBase64String("$username:$password".toByteArray())
    }

    object OpensrpAuth {
        private var authToken: AuthToken? = null

        fun authToken(): AuthToken? {
            if (authToken == null) {
                runBlocking {
                    launch {
                        authToken = getOpensrpAuthToken()
                    }
                }
            }
            return authToken
        }

    }

    companion object {
        val logger = LoggerFactory.getLogger(javaClass)

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                logger.error("Command Line Arguments missing")
                return
            } else {

                runBlocking {
                    launch {
                        if (args.size < 2) {
                            logger.error("Command Line Arguments missing")
                            return@launch
                        }
                        if (args.contains(AppConstants.Args.IMPORT) && ((args[args.indexOf(AppConstants.Args.IMPORT) + 1] == "tl") || (args[args.indexOf(
                                AppConstants.Args.IMPORT
                            ) + 1] == AppConstants.Args.LOCATION_AND_TAG))
                        ) {
                            logger.info("Importing Locations and Location tags")
                            LocationImportHelper().loadLocationTags()
                            LocationImportHelper().loadLocations()
                        } else if (args.contains(AppConstants.Args.IMPORT) && (args[args.indexOf(AppConstants.Args.IMPORT) + 1] == AppConstants.Args.LOCATION)) {
                            logger.info("Importing Locations")
                            LocationImportHelper().loadLocations()
                        } else if (args.contains(AppConstants.Args.IMPORT) && (args[args.indexOf(AppConstants.Args.IMPORT) + 1] == AppConstants.Args.LOCATION_TAG)) {
                            logger.info("Importing Location tags")
                            LocationImportHelper().loadLocationTags()
                        } else if (args.size > 2 && args.contains(AppConstants.Args.EXPORT) && (args[args.indexOf(
                                AppConstants.Args.EXPORT
                            ) + 1] == AppConstants.Args.INDICATOR)
                        ) {
                            if (args.contains(AppConstants.Args.DATASET) && (args[args.indexOf(AppConstants.Args.DATASET) + 1] != "")) {
                                logger.info("Exporting indicators")
                                val dataSets = args[args.indexOf(AppConstants.Args.DATASET) + 1]
                                val indicatorCsvHelper = IndicatorCsvHelper()
                                val dataSetArr = dataSets.split(",")
                                for (dataSet in dataSetArr) {
                                    val dataSetObject = indicatorCsvHelper.getDataSetDetails(dataSet)
                                    if (dataSetObject != null) {
                                        indicatorCsvHelper.fetchIndicators(dataSetObject)
                                    } else {
                                        logger.error("DataSet: $dataSet not found")
                                    }
                                }
                            } else {
                                logger.error("Arguments mismatch $args")
                            }
                        } else {
                            logger.error("Arguments mismatch $args")
                        }
                    }
                }
            }
        }

    }
}


