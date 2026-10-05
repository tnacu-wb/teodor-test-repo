package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CancelReservationGraphQLContract
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain

fun CancelReservationGraphQLContract.CancelReservationData.mapToCancelReservationGQL(): CancelReservationDomain {
    return CancelReservationDomain(
            basketReference = this.data.cancelReservation.basketReference
    )
}