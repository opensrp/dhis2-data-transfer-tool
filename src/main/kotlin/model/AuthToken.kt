package model

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthToken(@JsonProperty("access_token") val accessToken: String)
