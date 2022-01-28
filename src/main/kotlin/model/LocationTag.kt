package model

import com.fasterxml.jackson.annotation.JsonInclude

data class LocationTag(@JsonInclude(JsonInclude.Include.NON_NULL)
                       var id: Int? = 0, val name: String, val active: Boolean = true, val description: String = "")
