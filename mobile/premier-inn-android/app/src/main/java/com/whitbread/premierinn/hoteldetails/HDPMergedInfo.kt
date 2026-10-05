package com.whitbread.premierinn.hoteldetails

import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain

data class HDPMergedInfo(
        val input:HotelDetailsInput,
        val hotelInfo: HotelInformationDomain,
        val hotelAvailabilityDomain : HotelAvailabilityDomain,
        val ratePlanOpera: List<RatePlanOpera>,
        val orderedRateDisplayInfo: List<RatesDisplayInfo>,
        val pushToken: String,
        val promoKind: String? = null
)
