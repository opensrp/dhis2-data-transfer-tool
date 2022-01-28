package model

//{
//	"type": "Feature",
//	"id": "ac7ba751-35e8-4b46-9e53-3cbaad193697",
//	"properties": {
//		"status": "Active",
//		"parentId": "07b09ec1-0589-4a98-9480-4c403ac24d59",
//		"name": "TwoTwoTwo_03",
//		"geographicLevel": 0,
//		"version": 0
//	},
//	"locationTags": [
//		{
//			"id": 2,
//			"active": true,
//			"name": "Facility",
//			"description": "Health Facility"
//		}
//	]
//}
data class Location(val type: String = "Feature", val id: String, val name: String, val properties: LocationProperty, val locationTags: List<LocationTag>)
