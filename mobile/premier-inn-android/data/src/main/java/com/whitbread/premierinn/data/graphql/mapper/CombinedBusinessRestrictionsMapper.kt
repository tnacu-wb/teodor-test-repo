package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CombinedBusinessRestrictionsGraphQLContract
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.*

fun CombinedBusinessRestrictionsGraphQLContract.CombinedBusinessRestrictionsData.mapToCombinedBusinessRestrictionsGQL(): CombinedBusinessRestrictionsDomain {
    return CombinedBusinessRestrictionsDomain(
        roomsLimitationPI = this.roomsLimitationPI?.let {
            RoomsLimitation(
                it.maxRooms,
                it.maxRoomsAmend
            )
        },
        arrivalDateLimitationPI = this.arrivalDateLimitationPI?.let { ArrivalDateLimitation(it.maxArrivalDate) },
        nightsLimitationPI = this.nightsLimitationPI?.let { NightsLimitation(it.maxNights) },
        roomsLimitationBB = this.roomsLimitationBB?.let {
            RoomsLimitation(
                it.maxRooms,
                it.maxRoomsAmend
            )
        },
        arrivalDateLimitationBB = this.arrivalDateLimitationBB?.let { ArrivalDateLimitation(it.maxArrivalDate) },
        nightsLimitationBB = this.nightsLimitationBB?.let { NightsLimitation(it.maxNights) },
        roomsLimitationEMPLOYEE = this.roomsLimitationEMPLOYEE?.let {
            RoomsLimitation(
                it.maxRooms,
                it.maxRoomsAmend
            )
        },
        arrivalDateLimitationEMPLOYEE = this.arrivalDateLimitationEMPLOYEE?.let {
            ArrivalDateLimitation(
                it.maxArrivalDate
            )
        },
        nightsLimitationEMPLOYEE = this.nightsLimitationEMPLOYEE?.let { NightsLimitation(it.maxNights) }
    )
}