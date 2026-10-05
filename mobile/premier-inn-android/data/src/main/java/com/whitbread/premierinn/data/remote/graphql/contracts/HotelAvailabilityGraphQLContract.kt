package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface HotelAvailabilityGraphQLContract {
    data class HotelAvailabilityData(
            @SerializedName("data") val data: Data?,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("hotelAvailability") val hotelAvailability: HotelAvailability?,
        @SerializedName("ratesInformationV2") val ratesInformationV2: RatesInformation?,
        @SerializedName("packages") val packages: PackagesGraphQLContract.Packages?,
        @SerializedName("roomTypeInformation") val roomTypeInformation: RoomTypeInformation?,
    )

    data class HotelAvailability(
            @SerializedName("available") val available: Boolean,
            @SerializedName("hotelId") val hotelId: String,
            @SerializedName("endDate") val endDate: String,
            @SerializedName("startDate") val startDate: String,
            @SerializedName("limitedAvailability") val limitedAvailability: Boolean,
            @SerializedName("roomRates") val roomRates: List<RoomRate>?,
    )

    data class RoomRate(
            @SerializedName("ratePlanCode") val ratePlanCode: String,
            @SerializedName("cellCode") val cellCode: String?,
            @SerializedName("promotionCode") val promotionCode: String?,
            @SerializedName("roomTypes") val roomTypes: List<RoomTypes>
    )

    data class RoomTypes(
            @SerializedName("adults") val adults: Int,
            @SerializedName("children") val children: Int,
            @SerializedName("cotRequested") val cotRequested: Boolean,
            @SerializedName("roomType") val roomType: String,
            @SerializedName("rooms") val rooms: List<Room>
    )

    data class Room(
            @SerializedName("cotAvailable") val cotAvailable: Boolean,
            @SerializedName("pmsRoomType") val pmsRoomType: String,
            @SerializedName("roomClass") val roomClass: String,
            @SerializedName("roomPriceBreakdown") val roomPriceBreakdown: RoomPriceBreakdown,
            @SerializedName("silentSubstitution") val silentSubstitution: Boolean,
            @SerializedName("specialRequests") val specialRequests: List<String>? = null,
    )

    data class RoomPriceBreakdown(
            @SerializedName("currencyCode") val currencyCode: String,
            @SerializedName("dailyPrices") val dailyPrices: List<DailyPrice>,
            @SerializedName("totalNetAmount") val totalNetAmount: Double,
            @SerializedName("packageCode") val packageCode: String?,
            @SerializedName("packageAmount") val packageAmount: Double?,
            @SerializedName("baseRateAmount") val baseRateAmount: Double?
    )

    data class DailyPrice(
            @SerializedName("date") val date: String,
            @SerializedName("netPrice") val netPrice: Double
    )

    data class RatesInformation(
            @SerializedName("rateClassifications") val rateClassifications: List<RateClassificationExtraInfo>
    )

    data class RateClassificationExtraInfo(
            @SerializedName("rateClassification") val rateClassification: String,
            @SerializedName("rateOrder") val rateOrder: String,
            @SerializedName("rateName") val rateName: String,
            @SerializedName("rateDescription") val rateDescription: String,
            @SerializedName("rateLongDescription") val rateLongDescription: String,
            @SerializedName("rateNotes") val rateNotes: String,
            @SerializedName("rateTags") val rateTags: List<String>
    )

    data class RoomTypeInformation(
            @SerializedName("roomTypes") val roomTypes: List<RoomTypesInfo>)

    data class RoomTypesInfo(
            @SerializedName("roomTypeCode") val roomTypeCode: List<String>,
            @SerializedName("roomCategory") val roomCategory: String?,
            @SerializedName("roomLabel") val roomLabel: String,
            @SerializedName("roomDescription") val roomDescription: String,
            @SerializedName("roomImage") val roomImage: String?,
            @SerializedName("groupId") val groupId: String?)
}