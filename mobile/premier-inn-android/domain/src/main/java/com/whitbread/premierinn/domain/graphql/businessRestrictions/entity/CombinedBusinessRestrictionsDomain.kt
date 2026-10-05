package com.whitbread.premierinn.domain.graphql.businessRestrictions.entity

data class CombinedBusinessRestrictionsDomain(
    val roomsLimitationPI: RoomsLimitation?,
    val arrivalDateLimitationPI: ArrivalDateLimitation?,
    val nightsLimitationPI: NightsLimitation?,
    val roomsLimitationBB: RoomsLimitation?,
    val arrivalDateLimitationBB: ArrivalDateLimitation?,
    val nightsLimitationBB: NightsLimitation?,
    val roomsLimitationEMPLOYEE: RoomsLimitation?,
    val arrivalDateLimitationEMPLOYEE: ArrivalDateLimitation?,
    val nightsLimitationEMPLOYEE: NightsLimitation?
)

data class RoomsLimitation(
    val maxRooms: Int?,
    val maxRoomsAmend: Int?
)

data class ArrivalDateLimitation(
    val maxArrivalDate: Int?
)

data class NightsLimitation(
    val maxNights: Int?
)