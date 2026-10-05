package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilitiesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.PreStayHeaderInfo
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AnnouncementDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ContactDetailsDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.GalleryImageDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelAddressDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelCoordinates
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelFacilityDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ImportantInfoDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RestaurantInfoDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RestaurantMenuDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RoomConfigurationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RoomFacility
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabImage
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabItemDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain

fun HotelInfoGraphQLContract.HotelInfoData.mapToHotelInformationGQL(): HotelInformationDomain{
    return this.data.hotelInformation?.let {
         HotelInformationDomain(
                brand = this.data.hotelInformation.brand,
                name = this.data.hotelInformation.name,
                hotelDescription = this.data.hotelInformation.hotelDescription,
                galleryImages =  this.data.hotelInformation.galleryImages?.toGalleryImageDomain(),
                parkingDescription = this.data.hotelInformation.parkingDescription,
                hotelFacilities = this.data.hotelInformation.hotelFacilities?.toHotelFacilityDomain(),
                address = this.data.hotelInformation.address.toAddressDomain(),
                county = this.data.hotelInformation.county,
                directions = this.data.hotelInformation.directions,
                satNavDirections = this.data.hotelInformation.satNavDirections,
                coordinates = this.data.hotelInformation.coordinates.toHotelCoordinatesDomain(),
                roomConfiguration = this.data.hotelInformation.roomConfiguration.toRoomConfigurationDomain(),
                contactDetails = this.data.hotelInformation.contactDetails?.toContactDetailsDomain(),
                restaurantInfo = this.data.hotelInformation.restaurant?.toRestaurantInfoDomain(),
                topSectionImages = this.data.hotelInformation.topSectionImages?.toTopSectionImageDomain(),
                announcement = this.data.hotelInformation.announcement?.toAnnouncementDomain(),
                importantInfo = this.data.hotelInformation.importantInfo?.toImportantInfoDomain(),
                ancillaryCloseOutItems = this.data.hotelInformation.ancillaryCloseOut?.toAncillaryCloseOutDomain(),
                messagingFlag = this.data.hotelInformation.messagingFlag.toMessagingFlagDomain(),
                preStayHeaderInfo = PreStayHeaderInfo(
                    hotelName = this.data.hotelInformation.name,
                    hotelImage = this.data.hotelInformation.topSectionImages?.get(0)?.imageSrc,
                    hotelBrand = this.data.hotelInformation.brand,
                    hotelAddress = this.data.hotelInformation.address.toAddressDomain().addressLine1
                )
        )
    } ?: HotelInformationDomain.createEmptyDomain()
}

private fun HotelInfoGraphQLContract.ImportantInfo.toImportantInfoDomain(): ImportantInfoDomain {
    return ImportantInfoDomain(
        infoItems = this.infoItems.toInfoItemsDomain()
    )
}

private fun List<HotelInfoGraphQLContract.InfoItem>.toInfoItemsDomain(): List<InfoItem> {
    val infoItems = mutableListOf<InfoItem>()
    this.forEach {
        infoItems.add(
            InfoItem(
                text = it.text,
                priority = it.priority,
                startDate = it.startDate,
                endDate = it.endDate
            )
        )
    }
    return infoItems
}

private fun HotelAvailabilitiesGraphQLContract.MessagingFlag.toMessagingFlagDomain(): MessagingFlagDomain {
    return  MessagingFlagDomain(
        text = this.text,
        color = this.color
    )
}

private fun HotelInfoGraphQLContract.Announcement.toAnnouncementDomain(): AnnouncementDomain {
    return AnnouncementDomain(
        showAnnouncement = this.showAnnouncement,
        startDate = this.startDate,
        endDate = this.endDate,
        icon = this.icon,
        text = this.text,
        type = this.type
    )
}

private fun List<HotelInfoGraphQLContract.TopSectionImage>.toTopSectionImageDomain(): List<TopSectionImageDomain> {
    val topSectionImages = mutableListOf<TopSectionImageDomain>()
    this.forEach {
        topSectionImages.add( TopSectionImageDomain(
            imageSrc = it.imageSrc,
            tags = it.tags))
    }
    return topSectionImages
}

private fun HotelInfoGraphQLContract.Restaurant.toRestaurantInfoDomain(): RestaurantInfoDomain {
    return RestaurantInfoDomain(
        name = this.name,
        logoSrc = this.logoSrc,
        restaurantDescription = this.description,
        menus = this.menus.toRestaurantMenuDomain()
    )
}

private fun List<HotelInfoGraphQLContract.Menu>.toRestaurantMenuDomain(): List<RestaurantMenuDomain> {
    val menuItems = mutableListOf<RestaurantMenuDomain>()
    this.forEach {
        menuItems.add(RestaurantMenuDomain(
            name = it.name,
            description = it.description,
            imageSrc = it.imageSrc,
            menuSrc = it.menuSrc,
            menuLabel = it.menuLabel,
            disclaimer = it.disclaimer
        ))
    }
    return menuItems
}

private fun HotelInfoGraphQLContract.ContactDetails.toContactDetailsDomain(): ContactDetailsDomain {
    return ContactDetailsDomain(
            this.phone?: EMPTY_STRING_DOMAIN,
            this.hotelNationalPhone?: EMPTY_STRING_DOMAIN)
}

private fun HotelInfoGraphQLContract.RoomConfiguration.toRoomConfigurationDomain(): RoomConfigurationDomain {
    return RoomConfigurationDomain(
        tabItems = this.tabItems.toTabItemsDomain()
    )
}

private fun List<HotelInfoGraphQLContract.TabItem>.toTabItemsDomain(): List<TabItemDomain> {
    val tabItems = mutableListOf<TabItemDomain>()
    this.forEach {
        tabItems.add(TabItemDomain(
            facilities = it.facilities.toRoomFacilities(),
            images = it.images.toTabImages(),
            roomName = it.roomName,
            roomType = it.roomType,
            roomDescription = it.roomDescription
        ))
    }

    return tabItems
}

private fun List<HotelInfoGraphQLContract.Image>.toTabImages(): List<TabImage> {
    val tabImages = mutableListOf<TabImage>()
    this.forEach {
        tabImages.add(
            TabImage(
                caption = it.caption,
                iconSrc = it.iconSrc,
                imageSrc = it.imageSrc,
                thumbnailSrc = it.thumbnailSrc
            )
        )
    }

    return tabImages
}

private fun List<HotelInfoGraphQLContract.Facility>.toRoomFacilities(): List<RoomFacility> {
    val roomFacility = mutableListOf<RoomFacility>()
    this.forEach {
        roomFacility.add(
            RoomFacility(
                code = it.code ?: EMPTY_STRING,
                description = it.description ?: EMPTY_STRING,
                icon = it.icon ?: EMPTY_STRING,
                isVisible = it.isVisible ?: false,
                name = it.name ?: EMPTY_STRING
            )
        )
    }

    return roomFacility
}

private fun List<HotelInfoGraphQLContract.GalleryImage>.toGalleryImageDomain(): List<GalleryImageDomain> {
    val galleryImages = mutableListOf<GalleryImageDomain>()
    this.forEach {
        galleryImages.add( GalleryImageDomain(
            imageSrc = it.imageSrc))
    }
    return galleryImages
}

private fun List<HotelInfoGraphQLContract.HotelFacility>.toHotelFacilityDomain(): List<HotelFacilityDomain> {
    val facilities = mutableListOf<HotelFacilityDomain>()
    this.forEach {
        facilities.add( HotelFacilityDomain(
            description = it.description ?: EMPTY_STRING,
            title = it.name ?: EMPTY_STRING,
            code = it.code ?: EMPTY_STRING,
            isVisible = true,
            icon = it.icon ?: EMPTY_STRING))
    }
    return facilities
}

private fun HotelInfoGraphQLContract.Address.toAddressDomain(): HotelAddressDomain {
    return HotelAddressDomain(
        addressLine1 = this.addressLine1,
        addressLine2 = this.addressLine2,
        addressLine3 = this.addressLine3,
        country = this.country,
        postcode = this.postcode)
}

private fun HotelInfoGraphQLContract.Coordinates.toHotelCoordinatesDomain(): HotelCoordinates {
    return HotelCoordinates(
        latitude = this.latitude,
        longitude = this.longitude)
}

private fun HotelInfoGraphQLContract.AncillaryCloseOut.toAncillaryCloseOutDomain(): List<AncillaryCloseOutItem> {
    val ancillaryCloseOutItems = mutableListOf<AncillaryCloseOutItem>()
    this.ancillaryCloseOutItems.forEach {
        ancillaryCloseOutItems.add(
            AncillaryCloseOutItem(
                startDate = it.startDate ?: EMPTY_STRING,
                endDate = it.endDate ?: EMPTY_STRING,
                upsellCodes = it.upsellCodes ?: EMPTY_STRING
            )
        )
    }
    return ancillaryCloseOutItems
}

fun HotelAddressDomain.toCommaSeparatedAddress(): String {
    val list = listOf(this.addressLine1, this.addressLine2, this.addressLine3, this.postcode)

    return list.filter { it.isNotEmpty() }.joinToString()
}