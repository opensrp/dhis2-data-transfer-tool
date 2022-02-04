package helper

import AppConstants
import ApplicationProperty
import configuration.Application
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import model.AuthToken
import org.apache.commons.codec.binary.Base64

object OpensrpAuthHelper {

    suspend fun getOpensrpAuthToken(): AuthToken? {
        val client = Application.AppHttpClient.client
        val keycloakUrl = ApplicationProperty.getProperty(AppConstants.Opensrp.KEYCLOAK_URL)
        val keycloakRealm = ApplicationProperty.getProperty(AppConstants.Opensrp.KEYCLOAK_REALM)
        val keycloakClientId = ApplicationProperty.getProperty(AppConstants.Opensrp.KEYCLOAK_CLIENT_ID)
        val keycloakClientSecret = ApplicationProperty.getProperty(AppConstants.Opensrp.KEYCLOAK_CLIENT_SECRET)
        val userName = ApplicationProperty.getProperty(AppConstants.Opensrp.USERNAME) as String
        val password = ApplicationProperty.getProperty(AppConstants.Opensrp.PASSWORD) as String
        val url = "$keycloakUrl/auth/realms/$keycloakRealm/protocol/openid-connect/token"
        val response: HttpResponse = client.submitForm(
                url,
                formParameters = Parameters.build {
                    append("username", userName)
                    append("password", password)
                    append("scope", "profile")
                    append("grant_type", "password")
                },
        ) {
            headers {
                append("Authorization", "Basic ${Base64.encodeBase64String("$keycloakClientId:$keycloakClientSecret".toByteArray())}")
            }
        }
        return response.receive()
    }
}
