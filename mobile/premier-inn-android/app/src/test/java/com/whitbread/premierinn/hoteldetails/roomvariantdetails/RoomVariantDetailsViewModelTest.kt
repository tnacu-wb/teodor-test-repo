package com.whitbread.premierinn.hoteldetails.roomvariantdetails

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.STANDARD_ROOM_CLASS_OPERA
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelDisclaimerDomain
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDisclaimerUseCase
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class RoomVariantDetailsViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val publishRoomVariantDetails = SingleSubject.create<List<Hotel.RoomVariantDetails>>()
    private val publishHotelDisclaimer = SingleSubject.create<HotelDisclaimerDomain>()
    private val graphQLHotelDetailsUseCase: GraphQLHotelDetailsUseCase = mockk()
    private val graphQLHotelDisclaimerUseCase: GraphQLHotelDisclaimerUseCase = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private lateinit var viewModel : RoomVariantDetailsViewModel

    private val savedStateHandle: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { deviceLocaleProvider.getCountryIfRegion(Locale.UK) } returns LANGUAGE_ENGLISH
        every { graphQLHotelDetailsUseCase.fetchRoomVariantsDetails(any(), any(), any()) } returns publishRoomVariantDetails
        every { graphQLHotelDisclaimerUseCase.invoke(any()) } returns publishHotelDisclaimer
        every { savedStateHandle.get<String>("hotel_code_key") } returns "BANBRI"
        every { savedStateHandle.get<String>("hotel_brand_key") } returns "PI"

        viewModel = RoomVariantDetailsViewModel(
            savedStateHandle,
            graphQLHotelDetailsUseCase,
            graphQLHotelDisclaimerUseCase,
            deviceLocaleProvider)
    }

    @Test
    fun `should get RoomVariantsDetails successfully`() {

        val testObserver = viewModel.states().test()

        publishRoomVariantDetails.onSuccess(roomVariantDetailsMock)
        publishHotelDisclaimer.onSuccess(HotelDisclaimerDomain(EMPTY_STRING))

        testObserver
            .assertValueAt(0) { it.result is AsyncResult.Loading }
            .assertValueAt(1) { it.result is AsyncResult.Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    private val roomVariantDetailsMock = listOf(Hotel.RoomVariantDetails(
        "Standard twin", RoomVariant("Standard", STANDARD_ROOM_CLASS_OPERA), "A description",
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID3/ID3-bathroom.jpg",
        features = listOf(
            Hotel.RoomVariantDetails.Feature(name = "Hairdryer", details =""),
            Hotel.RoomVariantDetails.Feature(name = "Powerful shower", details="")
        ),
        null, "Twin"
    ))

}