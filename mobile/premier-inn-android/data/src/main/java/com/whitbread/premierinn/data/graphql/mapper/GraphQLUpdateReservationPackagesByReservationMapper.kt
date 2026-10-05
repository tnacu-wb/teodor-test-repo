package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.UpdateReservationPackagesByReservationGraphQLContract
import com.whitbread.premierinn.domain.graphql.amend.entity.UpdateReservationPackagesResponseDomain

fun UpdateReservationPackagesByReservationGraphQLContract.UpdateReservationPackagesByReservationData.mapToUpdateReservationPackagesByReservationGQL(): UpdateReservationPackagesResponseDomain {
    return UpdateReservationPackagesResponseDomain(
        updateReservationPackagesByReservation = this.data?.updateReservationPackagesByReservation ?: EMPTY_STRING
    )
}