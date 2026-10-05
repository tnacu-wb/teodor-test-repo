package com.whitbread.premierinn.common.utils

import com.google.common.truth.Truth.assertThat
import org.mockito.kotlin.*
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.api.response.availability.ContactDetails
import com.whitbread.premierinn.api.response.availability.HotelInfo
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CHARGEABLE_PHONE_DESC
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.NON_CHARGEABLE_PHONE_DESC
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class ExtensionsKtTest {

    private val logger = mock<LogService>()
    private val stringResourceProvider = mock<StringResourceProvider>()
    private val getStringResource = mock<GetStringResource>()

    @Test
    @Parameters(method = "callUiModelParams")
    fun buildCallUiModelTest(triple: Triple<Boolean, Hotel.ContactDetails, CallUsUiModel>) {

        mock<StringResourceProvider> {
            on { stringResourceProvider.getString(R.string.call_hotel) } doReturn "Call hotel"
            on { stringResourceProvider.getString(R.string.call_us) } doReturn "Call us"
        }

        mock<GetStringResource> {
            on { getStringResource(NON_CHARGEABLE_PHONE_DESC) } doReturn "Calls are charged at the national rate"
            on { getStringResource(CHARGEABLE_PHONE_DESC) } doReturn "Calls are charged from 13p a minute"
            on { getStringResource(CUSTOMER_SERVICE_NUMBER) } doReturn "03330038101"
        }

        val (chargeable, contacts, expectedModel) = triple

        val model = buildCallUiModel(chargeable = chargeable,
                contacts = contacts,
                stringResource = getStringResource,
                stringProvider = stringResourceProvider)

        assertThat(model).isEqualTo(expectedModel)
    }

    @Test
    fun `mapToHotelContactDetailsDomain should return null`() {
        val hotelINfo = mock<HotelInfo>()
        whenever(hotelINfo.contactDetails()).thenReturn(null)

        assertThat(mapToHotelContactDetailsDomain(hotelINfo)).isNull()
    }

    @Test
    fun `mapToHotelContactDetailsDomain should NOT return  null`() {
        val contactDetailsJson = """{"phone": "0871 622 0731", "hotelNationalPhone": "0333 234 6493"}""".trimIndent()

        val hotelINfo = mock<HotelInfo>()
        whenever(hotelINfo.contactDetails()).thenReturn(InstanceFactory.GSON.fromJson(contactDetailsJson, ContactDetails::class.java))

        assertThat(mapToHotelContactDetailsDomain(hotelINfo)).isNotNull()
    }

    @Test
    fun `mapToHotelContactDetailsDomain should return  null (case2)`() {
        val contactDetailsJson = """{"phone": null, "hotelNationalPhone": "0333 234 6493"}""".trimIndent()

        val hotelINfo = mock<HotelInfo>()
        whenever(hotelINfo.contactDetails()).thenReturn(InstanceFactory.GSON.fromJson(contactDetailsJson, ContactDetails::class.java))

        assertThat(mapToHotelContactDetailsDomain(hotelINfo)).isNull()
    }

    @Test
    fun `mapToHotelContactDetailsDomain should return null (case3)`() {
        val contactDetailsJson = """ {"phone": "0871 622 0731", "hotelNationalPhone": null}""".trimIndent()

        val hotelINfo = mock<HotelInfo>()
        whenever(hotelINfo.contactDetails()).thenReturn(InstanceFactory.GSON.fromJson(contactDetailsJson, ContactDetails::class.java))

        assertThat(mapToHotelContactDetailsDomain(hotelINfo)).isNull()
    }

    private fun callUiModelParams(): Any {
        return arrayOf(
                Triple(true, null, CallUsUiModel.builder().order(0).label("Call us")
                        .telCostInfo("Calls are charged at the national rate").telNumber("03330038101").build()),
                Triple(false, null, CallUsUiModel.builder().order(0).label("Call us")
                        .telCostInfo("Calls are charged at the national rate").telNumber("03330038101").build()),
                Triple(true, Hotel.ContactDetails(freePhone = "123", chargeablePhone = "321"), CallUsUiModel.builder().order(0)
                        .label("Call hotel").telCostInfo("Calls are charged from 13p a minute").telNumber("321").build()),
                Triple(false, Hotel.ContactDetails(freePhone = "123", chargeablePhone = "321"), CallUsUiModel.builder()
                        .order(0).label("Call hotel").telCostInfo("Calls are charged at the national rate")
                        .telNumber("123").build())
        )
    }

    private fun nonNetworkErrors(): Any {
        return arrayOf(
                ApiThrowable.Http(500),
                ApiThrowable.Http(404),
                ApiThrowable.Generic(IllegalArgumentException())
        )
    }
}