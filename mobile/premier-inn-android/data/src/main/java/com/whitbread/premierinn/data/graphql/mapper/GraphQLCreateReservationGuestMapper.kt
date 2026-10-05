package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGuestGraphQLContract
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain

fun CreateReservationGuestGraphQLContract.CreateReservationGuestData.mapToCreateReservationGuestGQL(): CreateReservationGuestDomain {
    val listOfErrors = mutableListOf<GraphQLErrorDomain>()
    this.errors?.let {
        it.forEach { error ->
            listOfErrors.add(GraphQLErrorDomain(
                    path = error.path ?: emptyList(),
                    errorType = error.errorType,
                    message = error.message
            ))
        }
    }
    return CreateReservationGuestDomain(
            basketReference = this.data.createReservationGuest.basketReference,
            error = listOfErrors
    )
}