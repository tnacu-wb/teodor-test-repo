package com.whitbread.premierinn.domain.common.hoteldetails.entity

import com.whitbread.premierinn.domain.booking.entity.PreStayHeaderInfo
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import java.io.Serializable

data class HotelInformationDomain(
    val brand: String,
    val name: String,
    val hotelDescription: String,
    val galleryImages: List<GalleryImageDomain>?,
    val parkingDescription: String?,
    val hotelFacilities: List<HotelFacilityDomain>?,
    val address: HotelAddressDomain,
    val county: String?,
    val directions: String,
    val satNavDirections: String,
    val coordinates: HotelCoordinates,
    val roomConfiguration: RoomConfigurationDomain,
    val contactDetails: ContactDetailsDomain?,
    val restaurantInfo: RestaurantInfoDomain?,
    val topSectionImages: List<TopSectionImageDomain>?,
    val announcement: AnnouncementDomain?,
    val importantInfo: ImportantInfoDomain?,
    val ancillaryCloseOutItems: List<AncillaryCloseOutItem>?,
    val messagingFlag: MessagingFlagDomain,
    val preStayHeaderInfo: PreStayHeaderInfo? = null
) {
    companion object {
        fun createEmptyDomain(): HotelInformationDomain {
            return HotelInformationDomain(brand = EMPTY_STRING_DOMAIN, name = EMPTY_STRING_DOMAIN,
                    hotelDescription = EMPTY_STRING_DOMAIN, galleryImages = emptyList(),
                    parkingDescription = EMPTY_STRING_DOMAIN,
                    hotelFacilities = emptyList(),
                    address = HotelAddressDomain(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN),
                    county = EMPTY_STRING_DOMAIN,
                    directions = EMPTY_STRING_DOMAIN, satNavDirections = EMPTY_STRING_DOMAIN, coordinates = HotelCoordinates(0f, 0f),
                    roomConfiguration = RoomConfigurationDomain(emptyList()),
                    restaurantInfo = RestaurantInfoDomain(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, emptyList()),
                    contactDetails = null,
                    topSectionImages = emptyList(),
                    announcement = AnnouncementDomain(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN,
                        EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN),
                    importantInfo = ImportantInfoDomain(emptyList()),
                    ancillaryCloseOutItems = emptyList(),
                    messagingFlag = MessagingFlagDomain(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
            )
        }
    }

    fun getListOfParkingCodes(): List<HotelFacilityDomain> {
        val listOfParkingCodes = mutableListOf<HotelFacilityDomain>()
        hotelFacilities?.let { facilityList ->
            val parkingFacility = facilityList.filter { it.code == "CPF" || it.code == "COP" || it.code == "CPP" || it.code == "COC"}
            if (parkingFacility.isNotEmpty()) {
                parkingFacility.forEach { listOfParkingCodes.add(it) }
                return listOfParkingCodes
            } else {
                listOfParkingCodes
            }
        } ?: listOfParkingCodes
        return listOfParkingCodes
    }
}

data class GalleryImageDomain(
    val imageSrc: String
)

data class HotelFacilityDomain(
    val description: String,
    val title: String,
    val code: String,
    val isVisible: Boolean,
    val icon: String
){
    companion object{
        fun createEmptyFacilityWithTitle(title: String):HotelFacilityDomain {
            return HotelFacilityDomain(EMPTY_STRING_DOMAIN, title, EMPTY_STRING_DOMAIN, true, EMPTY_STRING_DOMAIN)
        }
    }
}

data class HotelAddressDomain(
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val country: String,
    val postcode: String
)

data class HotelCoordinates(
    val latitude: Float,
    val longitude: Float
)

data class ContactDetailsDomain(
        val phone: String,
        val nationalPhoneNumber: String
)

data class RestaurantInfoDomain(
    val name : String,
    val logoSrc: String?,
    val restaurantDescription: String?,
    val menus : List<RestaurantMenuDomain>
) {

    val hasDisclaimer: Boolean
        get() = menus.isNotEmpty() && menus[0].disclaimer.isNotBlank()
}

data class RestaurantMenuDomain(
    val name: String,
    val description: String,
    val imageSrc: String,
    val menuSrc: String,
    val menuLabel: String,
    val disclaimer: String
)

data class TopSectionImageDomain(
    val imageSrc: String,
    val tags: List<String>
)

data class RoomConfigurationDomain(
    val tabItems: List<TabItemDomain>
)

data class TabGroupDomain(
    val groupId: String,
    val groupTitle : String
)

data class TabItemDomain(
    val facilities: List<RoomFacility>,
    val images: List<TabImage>,
    val roomName: String,
    val roomDescription: String,
    val roomType: String
)

data class RoomFacility(
    val code: String,
    val description: String,
    val icon: String,
    val isVisible: Boolean,
    val name: String,
)

data class TabImage(
    val caption: String,
    val iconSrc: String,
    val imageSrc: String,
    val thumbnailSrc: String
)

data class AnnouncementDomain(
    val showAnnouncement: String,
    val startDate: String,
    val endDate: String,
    val icon: String,
    val text: String,
    val type: String
)

data class ImportantInfoDomain(
    val infoItems: List<InfoItem>
)

data class InfoItem(
    val text: String,
    val priority: String,
    val startDate: String,
    val endDate: String
): Serializable

data class AncillaryCloseOutItem(
    val startDate: String,
    val endDate: String,
    val upsellCodes: String,
)