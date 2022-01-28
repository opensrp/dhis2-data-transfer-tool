package helper

import AppConstants
import ApplicationProperty
import configuration.Application
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import model.*
import utils.Utils
import utils.Utils.idToUUID
import wrapper.OrganisationUnitLevelWrapper
import wrapper.OrganisationUnitWrapper
import kotlin.system.exitProcess

open class LocationImportHelper {

    private suspend fun fetchOrganisationUnitLevels(): HashMap<Int, OrganisationUnitLevel> {
        val organisationUnitLevelMap: HashMap<Int, OrganisationUnitLevel> = HashMap()
        val client = Application.AppHttpClient.client
        val dhis2Url = ApplicationProperty.getProperty(AppConstants.Dhis2.URL)
        val locationTagSuffix: String =
            ApplicationProperty.getProperty(AppConstants.Opensrp.LOCATION_TAG_SUFFIX) as String
        val url = "$dhis2Url/api/organisationUnitLevels"
        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Basic ${Application.Dhis2Auth.authBase64}")
            }
            parameter("fields", "id,name,level")
            parameter("paging", "false")
        }

        val organisationUnitLevelWrapper: OrganisationUnitLevelWrapper = response.receive()
        val organisationUnitLevels: List<OrganisationUnitLevel>? = organisationUnitLevelWrapper.organisationUnitLevels

        if (organisationUnitLevels != null) {
            for (organizationUnitLevel in organisationUnitLevels) {
                organisationUnitLevelMap[organizationUnitLevel.level] = organizationUnitLevel
                organizationUnitLevel.name = "${organizationUnitLevel.name}${locationTagSuffix}"
            }
        }

        return organisationUnitLevelMap
    }

    suspend fun loadLocationTags() {
        val organisationUnitLevelMap: HashMap<Int, OrganisationUnitLevel> = fetchOrganisationUnitLevels()
        val locationTagNameMap: HashMap<String, LocationTag> = fetchLocationTags()

        val client = Application.AppHttpClient.client
        val opensrpUrl = ApplicationProperty.getProperty(AppConstants.Opensrp.URL)
        val url = "$opensrpUrl/opensrp/rest/location-tag"
        val locationTags: MutableList<LocationTag> = mutableListOf()
        val getLocationTagIdByName: Boolean =
            (ApplicationProperty.getProperty(AppConstants.Opensrp.LOCATION_TAG_GET_ID_BY_NAME) as String).toBoolean()

        if (!getLocationTagIdByName) {
            for (organisationUnitLevel in organisationUnitLevelMap.values) {
                if (locationTagNameMap.containsKey(organisationUnitLevel.name)) {
                    Application.ApplicationLogger.logger.error("LocationTag with the same name exists ${organisationUnitLevel.name}, Kindly fix before import begins")
                    exitProcess(1)
                }
            }
        }
        for (organisationUnitLevel in organisationUnitLevelMap.values) {
            try {
                val locationTag = LocationTag(0, organisationUnitLevel.name, true, organisationUnitLevel.name)
                locationTags.add(locationTag)


                if (getLocationTagIdByName && locationTagNameMap.containsKey(organisationUnitLevel.name)) {
                    locationTag.id = locationTagNameMap[organisationUnitLevel.name]?.id
                }

                val response: HttpResponse = client.put(url) {
                    body = locationTag

                    headers {
                        append("Authorization", "Bearer ${Application.OpensrpAuth.authToken()?.accessToken}")
                        append("Content-Type", "application/json")
                    }
                }
                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.Accepted)
                    Application.ApplicationLogger.logger.info("Loaded ${organisationUnitLevel.name} LocationTag")
                else
                    Application.ApplicationLogger.logger.error("Problem occurred when loading ${organisationUnitLevel.name} locationTag")
            } catch (e: Exception) {
                Application.ApplicationLogger.logger.error(e)
            }
        }
        Utils.writeToCsv(locationTags, "locationTags.csv")
    }

    private suspend fun fetchLocationTags(): HashMap<String, LocationTag> {
        val locationTagNameMap: HashMap<String, LocationTag> = hashMapOf()
        val client = Application.AppHttpClient.client
        val opensrpUrl = ApplicationProperty.getProperty(AppConstants.Opensrp.URL)
        val url = "$opensrpUrl/opensrp/rest/location-tag"
        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Bearer ${Application.OpensrpAuth.authToken()?.accessToken}")
                append("Content-Type", "application/json")
            }
        }

        val locationTags: List<LocationTag> = response.receive()
        for (locationTag in locationTags) {
            locationTagNameMap[locationTag.name] = locationTag
        }
        return locationTagNameMap
    }

    suspend fun loadLocations() {
        val organisationUnitLevelMap: HashMap<Int, OrganisationUnitLevel> = fetchOrganisationUnitLevels()
        val locationTagNameMap: HashMap<String, LocationTag> = fetchLocationTags()
        val client = Application.AppHttpClient.client
        val dhis2Url = ApplicationProperty.getProperty(AppConstants.Dhis2.URL)

        var levelCount = 1
        val locations: MutableList<Location> = mutableListOf()

        while (levelCount <= organisationUnitLevelMap.size) {
            var organisationUnitUrl =
                "$dhis2Url/api/organisationUnits?pageSize=100&level=$levelCount&fields=id,name,parent,level"
            while (organisationUnitUrl.isNotBlank()) {
                val response: HttpResponse = client.get(organisationUnitUrl) {
                    headers {
                        append("Authorization", "Basic ${Application.Dhis2Auth.authBase64}")
                    }
                }
                val organisationUnitWrapper: OrganisationUnitWrapper = response.receive()
                val pager: Pager? = organisationUnitWrapper.pager
                organisationUnitUrl = if (pager?.nextPage != null) pager.nextPage!! else ""

                val organisationUnits: List<OrganisationUnit>? = organisationUnitWrapper.organisationUnits
                if (organisationUnits != null) {
                    locations.addAll(
                        populateLocations(
                            organisationUnitLevelMap,
                            organisationUnits,
                            locationTagNameMap,
                            pager,
                            levelCount
                        )
                    )
                }
            }
            levelCount++
        }
        Application.ApplicationLogger.logger.info("Loaded Locations")
    }

    private suspend fun populateLocations(
        organisationUnitLevelMap: HashMap<Int, OrganisationUnitLevel>,
        organisationUnits: List<OrganisationUnit>,
        locationTagNameMap: HashMap<String, LocationTag>,
        pager: Pager?,
        levelCount: Int
    ): MutableList<Location> {
        val client = Application.AppHttpClient.client
        val opensrpUrl = ApplicationProperty.getProperty(AppConstants.Opensrp.URL)
        val url = "$opensrpUrl/opensrp/rest/location/add"

        val locations: MutableList<Location> = mutableListOf()

        for (organizationUnit in organisationUnits) {
            val organisationUnitLevel: OrganisationUnitLevel? = organisationUnitLevelMap[organizationUnit.level]
            if (organisationUnitLevel != null) {
                val locationTag = LocationTag(
                    locationTagNameMap[organisationUnitLevel.name]?.id,
                    organisationUnitLevel.name,
                    true,
                    organisationUnitLevel.name
                )
                locations.add(
                    Location(
                        "Feature",
                        idToUUID(organizationUnit.id),
                        organizationUnit.name,
                        LocationProperty(
                            "Active",
                            idToUUID(organizationUnit.parent?.id),
                            organizationUnit.name,
                            (organizationUnit.level?.minus(1)!!),
                            0,
                            organizationUnit.id
                        ),
                        mutableListOf(locationTag)
                    )
                )
            }
        }

        val response: HttpResponse = client.post(url) {
            body = locations

            headers {
                append("Authorization", "Bearer ${Application.OpensrpAuth.authToken()?.accessToken}")
                append("Content-Type", "application/json")
            }
            parameter("is_jurisdiction", "true")
        }

        if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.Accepted)
            Application.ApplicationLogger.logger.info("Loaded Locations ${pager?.page}/${pager?.pageCount} , Level:$levelCount/${organisationUnitLevelMap.size}")
        else
            Application.ApplicationLogger.logger.error("Problem occurred when loading locations ${pager?.page}/${pager?.pageCount} , Level:$levelCount/${organisationUnitLevelMap.size}")

        return locations
    }

}
