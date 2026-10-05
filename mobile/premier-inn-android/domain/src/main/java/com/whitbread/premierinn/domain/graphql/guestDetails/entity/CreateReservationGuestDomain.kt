package com.whitbread.premierinn.domain.graphql.guestDetails.entity

import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain

data class CreateReservationGuestDomain(
        val basketReference: String,
        val error: List<GraphQLErrorDomain>? = null
)
