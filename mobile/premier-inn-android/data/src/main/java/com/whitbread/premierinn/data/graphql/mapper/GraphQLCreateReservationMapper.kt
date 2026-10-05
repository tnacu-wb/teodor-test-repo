package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.UpdateReservationDomain
import com.whitbread.premierinn.domain.graphql.summary.entity.CreateReservationDomain

fun CreateReservationGraphQLContract.CreateReservationData.mapToCreateReservationGQL(): CreateReservationDomain {
    return CreateReservationDomain(basketReference = this.data?.createReservation?.basketReference ?: EMPTY_STRING)
}

fun UpdateReservationGraphQLContract.UpdateReservationData.mapToUpdateReservationGQL(): UpdateReservationDomain {
    return UpdateReservationDomain(this.data.createReservationGuest.basketReference)
}