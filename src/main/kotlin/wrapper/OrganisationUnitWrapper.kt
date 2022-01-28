package wrapper

import model.OrganisationUnit
import model.Pager

data class OrganisationUnitWrapper(var pager: Pager?, var organisationUnits: List<OrganisationUnit>?)
