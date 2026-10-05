package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CancelOnHoldReservationGraphQLContract
import com.whitbread.premierinn.domain.graphql.hdp.entity.CancelOnHoldReservationDomain


fun CancelOnHoldReservationGraphQLContract.CancelOnHoldReservationData.mapToCancelOnHoldReservationGQL(): CancelOnHoldReservationDomain {
    return CancelOnHoldReservationDomain(
            basketReference = this.data.cancelReservation.basketReference
    )
}