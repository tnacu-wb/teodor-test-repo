package com.whitbread.premierinn.domain.graphql.bookingDetails.usecase

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelAddressDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelCoordinates
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RoomConfigurationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RoomFacility
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabGroupDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabImage
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TabItemDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.createBooking
import com.whitbread.premierinn.domain.createBookingConfirmation
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test

class GraphQLBookingDetailsUseCaseTest {
    private val graphQLBookingDetailsUseCase : GraphQLBookingDetailsUseCase = mockk()
    private val bookingDetailsRepository : GraphQLBookingDetailsRepository = mockk()
    private val graphQLHotelDetailsRepository : GraphQLHotelDetailsRepository = mockk()

    @Test
    fun `WHEN BookingConfirmation has no hotelname and hotelinfo populates hotel name`() {
        every {
            bookingDetailsRepository.bookingConfirmationForFindBooking(
                reference,
                "gb",
                "en"
            )
        } returns Single.just(testBookingConfirmation)
        every {
            graphQLBookingDetailsUseCase.bookingConfirmationAndManageBookingWithHotelInfo(
                reference, "gb", "en", "PI",
                cancelInformationRequestBody = mockCancelInformationRequestBody
            )
        } returns Observable.just(testBooking.copy(hotelName = "MANOLD"))
        every {
            graphQLHotelDetailsRepository.getHotelInfo(
                "gb",
                "LONON",
                "en"
            )
        } returns Single.just(mockHotelInfo())

        graphQLBookingDetailsUseCase.bookingConfirmationAndManageBookingWithHotelInfo(
            reference,
            "gb",
            "en",
            "PI",
            cancelInformationRequestBody = mockCancelInformationRequestBody
        )
            .test()
            .assertValue { it.bookingReference == "AGB5677" }
            .assertValue { it.hotelName == "MANOLD" }
    }

    fun mockHotelInfo() : HotelInformationDomain {
        val roomfacilityList = mutableListOf<RoomFacility>()
        val listOfTabGroups = mutableListOf<TabGroupDomain>()
        val listOfTabImages = mutableListOf<TabImage>()
        val listOfTabItems = mutableListOf<TabItemDomain>()
        val topSectionImages = mutableListOf<TopSectionImageDomain>()
        val tabgroup1 = TabGroupDomain("single", "Single")
        val tabgroup2 = TabGroupDomain("double", "Double")
        val facility1 = RoomFacility("102", "Double or kingsize super comfy bed",
                "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/102.svg", true,
                "Double or kingsize super comfy bed")
        val facility2 = RoomFacility("104", "Hairdryer",
                "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/104.svg", true,
                "Hairdryer")
        val images1 = TabImage("", "", "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID3/ID3-bathroom.jpg",
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID3/ID3-bathroom.jpg")
        val images2 = TabImage("", "", "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID3/ID3-double-room.jpg",
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID3/ID3-double-room.jpg")

        roomfacilityList.add(facility1)
        roomfacilityList.add(facility2)

        listOfTabGroups.add(tabgroup1)
        listOfTabGroups.add(tabgroup2)

        listOfTabImages.add(images1)
        listOfTabImages.add(images2)

        val tabItem1 = TabItemDomain(roomfacilityList, listOfTabImages, "Single", "Standard double", "double")
        val tabItem2 = TabItemDomain(roomfacilityList, listOfTabImages, "Standard family", "family", "family")

        listOfTabItems.add(tabItem1)
        listOfTabItems.add(tabItem2)

        val roomConfig = RoomConfigurationDomain(listOfTabItems)

        return HotelInformationDomain("PI", "MANOLD","Some hotel description here", null, null, null,
            HotelAddressDomain("Line 1", "Line 2", "",  "United Kingdom (the)",  "EC1N2TD"), "greater-london", "Directions", "Sat nav",
            HotelCoordinates(111f,222f),roomConfig, null, null, topSectionImages, null, null, null, MessagingFlagDomain("New Hotel", ""))
    }

    private val mockCancelInformationRequestBody = CancelInformationRequestBody(
        "BANBRI1234", "BANBRI", "123456", "65675ghg", BookingChannelDetails("PI", "MOBILE", "en")
    )

    companion object {
        val reference = "AGB5677"
        val testBooking = createBooking(reference).copy(hotelName = EMPTY_STRING_DOMAIN)
        val testBookingConfirmation = createBookingConfirmation(reference)
    }
}