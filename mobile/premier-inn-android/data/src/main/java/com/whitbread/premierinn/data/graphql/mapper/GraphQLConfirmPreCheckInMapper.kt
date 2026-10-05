package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.PreCheckInConfirmationGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckInConfirmationDomain

fun PreCheckInConfirmationGraphQLContract.PreCheckInConfirmation.toPreCheckInConfirmationDomain(): PreCheckInConfirmationDomain {
    return PreCheckInConfirmationDomain(basketStatus = this.basketStatus)
}
