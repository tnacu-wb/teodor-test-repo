package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.PreCheckInGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.PreCheckInStatusDomain

fun PreCheckInGraphQLContract.PreCheckInStatus.toPreCheckInStatusDomain(): PreCheckInStatusDomain {
    return PreCheckInStatusDomain(status = this.status, message = this.message)
}
