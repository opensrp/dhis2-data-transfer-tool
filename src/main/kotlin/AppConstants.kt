class AppConstants {
    object Dhis2 {
        const val URL: String = "dhis2.url"
        const val USERNAME: String = "dhis2.username"
        const val PASSWORD: String = "dhis2.password"
        const val INDICATOR_FILE_NAME: String = "indicator.csv.name"
    }

    object Opensrp {
        const val URL: String = "opensrp.url"
        const val USERNAME: String = "opensrp.username"
        const val PASSWORD: String = "opensrp.password"
        const val KEYCLOAK_URL: String = "opensrp.keycloak.url"
        const val KEYCLOAK_REALM: String = "opensrp.keycloak.realm"
        const val KEYCLOAK_CLIENT_SECRET: String = "opensrp.keycloak.client.secret"
        const val KEYCLOAK_CLIENT_ID: String = "opensrp.keycloak.client.id"
        const val LOCATION_TAG_SUFFIX: String = "location.tag.suffix"
        const val LOCATION_TAG_GET_ID_BY_NAME: String = "location.tag.getIdByName"
    }

    object Args {
        const val IMPORT: String = "-import"
        const val EXPORT: String = "-export"
        const val LOCATION: String = "l"
        const val LOCATION_TAG: String = "t"
        const val LOCATION_AND_TAG: String = "lt"
        const val INDICATOR: String = "i"
        const val DATASET: String = "-ds"
    }
}
