package com.whitbread.premierinn.domain.graphql.hdp.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain

data class HotelAvailabilityDomain(
    val available: Boolean,
    val hotelId: String,
    val endDate: String,
    val startDate: String,
    val limitedAvailability: Boolean,
    val roomRateDomainList: List<RoomRateDomain>?,
    val packages: DataPackagesDomain?,
    val listOfRatesClassification: List<RateClassificationsDomain>,
    val listOfRoomTypeInfo:List<RoomTypeInfoDomain>,
    val error: List<GraphQLErrorDomain>?,
    val promoKind: String? = null
    ) {
    companion object {
        fun createDefaultAvailability() : HotelAvailabilityDomain {
            return HotelAvailabilityDomain(false, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN,
                                            EMPTY_STRING_DOMAIN, false, null, null,
                                            emptyList(), emptyList(), null, null)
        }
    }
}

    data class RoomRateDomain(
        val ratePlanCode: String,
        val cellCode: String,
        val promotionCode: String?,
        val roomTypesDomainList: List<RoomTypeDomain>, // This is each room details
    )

    data class RoomTypeDomain(
        val adults: Int,
        val children: Int,
        val cotRequested: Boolean,
        val roomType: String,
        val roomOptionsDomainList: List<RoomOptionsDomain>// room options :This will only be one but in case of options like accessible 2 would be there
    )
    data class RoomOptionsDomain( //
        val cotAvailable: Boolean,
        val pmsRoomType: String,
        val roomPriceBreakdownDomain: RoomPriceBreakdownDomain,
        val roomClass: String,
        val silentSubstitution: Boolean,
        val specialRequests: List<String>?
    )
    data class RoomPriceBreakdownDomain(
        val currencyCode: String,
        val dailyPricesDomainList: List<DailyPriceDomain>,
        val totalNetAmount: Double,
        val packageCode: String?,
        val packageAmount: Double?,
        val baseRateAmount: Double?
    )
    data class DailyPriceDomain(
        val date: String,
        val netPrice: Double
    )

sealed class Action {
    object Loading : Action()
    object Success : Action()
    object Error : Action()
}

