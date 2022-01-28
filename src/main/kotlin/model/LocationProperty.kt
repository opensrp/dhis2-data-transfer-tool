package model

//	"properties": {
//		"status": "Active",
//		"parentId": "07b09ec1-0589-4a98-9480-4c403ac24d59",
//		"name": "TwoTwoTwo_03",
//		"geographicLevel": 0,
//		"version": 0
//	},
data class LocationProperty(val status: String, val parentId: String? = null, val name: String,
                            val geographicLevel: Int = 0, val version: Int = 0,
                            val externalId: String?)
