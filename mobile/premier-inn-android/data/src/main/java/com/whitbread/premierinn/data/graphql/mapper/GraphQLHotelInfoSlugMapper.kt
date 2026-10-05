package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoSlugGraphQLContract
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelInformationSlugDomain

fun HotelInfoSlugGraphQLContract.HotelInfoData.mapToHotelInformationSlugGQL(): HotelInformationSlugDomain {
    this.data.hotelInformation?.let {
        return HotelInformationSlugDomain(
            brand = data.hotelInformation.brand,
            name = data.hotelInformation.name,
            hotelId = data.hotelInformation.hotelId
        )
    } ?: return HotelInformationSlugDomain.createEmptyDomain()
}
