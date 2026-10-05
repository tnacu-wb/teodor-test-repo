package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

interface HotelAvailabilitiesGraphQLContract {

    data class HotelAvailabilitiesData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("hotelAvailabilities") val hotelAvailabilities: HotelAvailabilities?
    )

    data class HotelAvailabilities(
        @SerializedName("multiHotelAvailabilities") val multiHotelAvailabilities: List<SingleHotelAvailability>,
    )

    data class SingleHotelAvailability(
        @SerializedName("hotelId") val hotelId: String,
        @SerializedName("name") val name: String,
        @SerializedName("hotelAvailability") val hotelAvailability: ShortHotelAvailability,
        @SerializedName("hotelInformation") val hotelInformation: ShortHotelInformation
    )

    data class ShortHotelAvailability(
        @SerializedName("distance") val distance: Float,
        @SerializedName("lowestRoomRate") val lowestRoomRate: LowestRoomRate?,
        @SerializedName("available") val available: Boolean,
        @SerializedName("limitedAvailability") val limitedAvailability: Boolean,
        @SerializedName("pmsSource") val pmsSource: String,
        @SerializedName("cellCode") val cellCode: String? = null

    )

    data class LowestRoomRate(
        @SerializedName("netTotal") val netTotal: Float,
        @SerializedName("currencyCode") val currencyCode: String
    )

    data class ShortHotelInformation(
        @SerializedName("brand") val brand: String,
        @SerializedName("thumbnailImages") val thumbnailImages: List<ThumbnailImage>?,
        @SerializedName("hotelFacilities") val hotelFacilities: List<FacilityItem>?,
        @SerializedName("messagingFlag") val messagingFlag: MessagingFlag,
        @SerializedName("coordinates") val coordinates: Coordinates,
    )

    data class ThumbnailImage(
        @SerializedName("imageSrc") val imageSrc: String?,
    )

    data class FacilityItem(
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String,
    )

    data class MessagingFlag(
        @SerializedName("text") val text: String,
        @SerializedName("color") val color: String
    )

    data class Coordinates(
        @SerializedName("latitude") val latitude: Float,
        @SerializedName("longitude") val longitude: Float
    )
}