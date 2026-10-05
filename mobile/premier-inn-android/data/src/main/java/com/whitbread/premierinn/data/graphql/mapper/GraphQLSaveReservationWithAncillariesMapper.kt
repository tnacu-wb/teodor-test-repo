package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.SaveReservationWithAncillariesGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.summary.entity.SaveReservationWithAncillariesDomain

fun SaveReservationWithAncillariesGraphQLContract.SaveReservationWithAncillariesData.mapToSaveReservationGQL(): SaveReservationWithAncillariesDomain {
    return SaveReservationWithAncillariesDomain(
        saveReservation = this.data?.saveReservation ?: EMPTY_STRING_DOMAIN
    )
}