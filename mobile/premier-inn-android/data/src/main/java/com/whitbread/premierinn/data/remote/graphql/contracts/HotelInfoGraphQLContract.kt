package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface HotelInfoGraphQLContract{
    data class HotelInfoData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("hotelInformation") val hotelInformation: HotelInformation?
    )

    data class HotelInformation(
        @SerializedName("brand") val brand: String,
        @SerializedName("name") val name: String,
        @SerializedName("hotelDescription") val hotelDescription: String,
        @SerializedName("galleryImages") val galleryImages: List<GalleryImage>?,
        @SerializedName("parkingDescription") val parkingDescription: String?,
        @SerializedName("hotelFacilities") val hotelFacilities: List<HotelFacility>?,
        @SerializedName("address") val address: Address,
        @SerializedName("county") val county: String?,
        @SerializedName("directions") val directions: String,
        @SerializedName("satNavDirections") val satNavDirections: String,
        @SerializedName("roomConfiguration") val roomConfiguration: RoomConfiguration,
        @SerializedName("contactDetails") val contactDetails: ContactDetails?,
        @SerializedName("coordinates") val coordinates: Coordinates,
        @SerializedName("restaurant") val restaurant: Restaurant?,
        @SerializedName("topSectionImages") val topSectionImages: List<TopSectionImage>?,
        @SerializedName("announcement") val announcement: Announcement?,
        @SerializedName("importantInfo") val importantInfo: ImportantInfo?,
        @SerializedName("ancillaryCloseout") val ancillaryCloseOut: AncillaryCloseOut?,
        @SerializedName("messagingFlag") val messagingFlag: HotelAvailabilitiesGraphQLContract.MessagingFlag
    )

    data class GalleryImage(
        @SerializedName("imageSrc") val imageSrc: String
    )

    data class HotelFacility(
        @SerializedName("description") val description: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("code") val code: String?,
        @SerializedName("icon") val icon: String?,
        @SerializedName("isVisible") val isVisible: String?

    )

    data class Address(
        @SerializedName("addressLine1") val addressLine1: String,
        @SerializedName("addressLine2") val addressLine2: String,
        @SerializedName("addressLine3") val addressLine3: String,
        @SerializedName("country") val country: String,
        @SerializedName("postalCode") val postcode: String
    )

    data class Coordinates(
        @SerializedName("latitude") val latitude: Float,
        @SerializedName("longitude") val longitude: Float)

    data class Restaurant(
        @SerializedName("name") val name: String,
        @SerializedName("logoSrc") val logoSrc: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("menus") val menus : List<Menu>
    )

    data class TopSectionImage(
        @SerializedName("imageSrc") val imageSrc : String,
        @SerializedName("tags") val tags : List<String>
    )

    data class Menu(
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("imageSrc") val imageSrc: String,
        @SerializedName("menuSrc") val menuSrc: String,
        @SerializedName("menuLabel") val menuLabel: String,
        @SerializedName("disclaimer") val disclaimer: String
    )

    data class RoomConfiguration(
        @SerializedName("tabItems") val tabItems: List<TabItem>
    )

    data class ContactDetails(
            @SerializedName("phone") val phone: String?,
            @SerializedName("hotelNationalPhone") val hotelNationalPhone: String?,
    )

    data class TabItem(
        @SerializedName("facilities") val facilities: List<Facility>,
        @SerializedName("images") val images: List<Image>,
        @SerializedName("roomName") val roomName: String,
        @SerializedName("roomDescription") val roomDescription: String,
        @SerializedName("roomType") val roomType: String
    )
    data class Facility(
        @SerializedName("code") val code: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("icon") val icon: String?,
        @SerializedName("isVisible") val isVisible: Boolean?,
        @SerializedName("name") val name: String?
    )

    data class Image(
        @SerializedName("caption") val caption: String,
        @SerializedName("iconSrc") val iconSrc: String,
        @SerializedName("imageSrc") val imageSrc: String,
        @SerializedName("thumbnailSrc") val thumbnailSrc: String
    )

    data class Announcement(
        @SerializedName("showAnnouncement") val showAnnouncement: String,
        @SerializedName("startDate") val startDate: String,
        @SerializedName("endDate") val endDate: String,
        @SerializedName("icon") val icon: String,
        @SerializedName("text") val text: String,
        @SerializedName("type") val type: String
    )

    data class ImportantInfo(
        @SerializedName("infoItems") val infoItems: List<InfoItem>
    )

    data class InfoItem(
        @SerializedName("text") val text: String,
        @SerializedName("priority") val priority: String,
        @SerializedName("startDate") val startDate: String,
        @SerializedName("endDate") val endDate: String
    )

    data class AncillaryCloseOut(
        @SerializedName("items") val ancillaryCloseOutItems: List<AncillaryCloseOutItem>
    )

    data class AncillaryCloseOutItem(
        @SerializedName("startDate") val startDate: String?,
        @SerializedName("endDate") val endDate: String?,
        @SerializedName("upsellCodes") val upsellCodes: String?
    )
}