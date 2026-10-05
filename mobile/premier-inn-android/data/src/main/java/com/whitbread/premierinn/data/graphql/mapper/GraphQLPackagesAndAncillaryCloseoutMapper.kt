package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesAndAncillariesCloseoutGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain

fun PackagesAndAncillariesCloseoutGraphQLContract.PackagesAndAncillariesCloseoutData.mapToPackagesAndAncillaryCloseoutDomain(
    nightsNumber: Int
):
        PackagesAndAncillaryCloseoutDomain {
    return PackagesAndAncillaryCloseoutDomain(
        packages = this.data?.packages.mapToPackagesGraphQL(nightsNumber),
        ancillaryCloseOutItems = this.data?.hotelInformation?.ancillaryCloseOut.toAncillaryCloseOutItemListDomain()
    )
}

private fun HotelInfoGraphQLContract.AncillaryCloseOut?.toAncillaryCloseOutItemListDomain(): List<AncillaryCloseOutItem> {
    return this?.ancillaryCloseOutItems?.map {
        AncillaryCloseOutItem(
            startDate = it.startDate ?: EMPTY_STRING_DOMAIN,
            endDate = it.endDate ?: EMPTY_STRING_DOMAIN,
            upsellCodes = it.upsellCodes ?: EMPTY_STRING_DOMAIN
        )
    } ?: emptyList()
}