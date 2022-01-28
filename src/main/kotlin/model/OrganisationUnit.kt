package model

data class OrganisationUnit(val id: String, var name: String, var parent: IdObject?, var level: Int?)
