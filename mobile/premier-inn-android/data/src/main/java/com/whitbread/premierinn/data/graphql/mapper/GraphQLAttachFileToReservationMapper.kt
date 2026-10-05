package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.AttachFileToReservationGraphQLContract
import com.whitbread.premierinn.domain.graphql.ciol.entity.AttachFileToReservationDomain


fun AttachFileToReservationGraphQLContract.AttachFileToReservation.toAttachFileToReservationDomain(): AttachFileToReservationDomain {
    return AttachFileToReservationDomain(status = this.status, message = this.message)
}
