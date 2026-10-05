package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.ConfirmPreCheckOutGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckOutConfirmationDomain

fun ConfirmPreCheckOutGraphQLContract.PreCheckOutConfirmation.toPreCheckOutConfirmationDomain(): PreCheckOutConfirmationDomain {
    return PreCheckOutConfirmationDomain(basketStatus = this.basketStatus)
}
