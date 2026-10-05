package com.whitbread.premierinn.amend.amendupsells

import android.os.Parcelable
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

@Parcelize
data class AmendUpsellsInput(
    val manageBookingInput: ManageBookingInput,
    val temporaryBasketReference: String,
    val totalAdults: Int,
    val totalChildren: Int,
    val hotelBrand: String,
    val hotelName: String,
    val galleryImages: List<String>? = emptyList(),
    val bookingFlowId: String,
    val arrivalDeparturePair: Pair<LocalDate, LocalDate>
) : Parcelable

fun AmendUpsellsInput.createPackagesRequestBody(
    deviceLocaleProvider: DeviceLocaleProvider
): HotelPackagesRequestBody {
   return HotelPackagesRequestBody(
        hotelId = manageBookingInput.hotelCode(),
        startDate = arrivalDeparturePair.first.toString(),
        endDate = arrivalDeparturePair.second.toString(),
        adultsNumber = totalAdults,
        childrenNumber = totalChildren,
        nightsNumber = ChronoUnit.DAYS.between(manageBookingInput.arrivalDate(), manageBookingInput.departureDate()).toInt(),
        language = deviceLocaleProvider.getDeviceLanguage().lowercase(),
        country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
        bookingFlowId = bookingFlowId,
        basketReferenceId = temporaryBasketReference,
        channel = if (manageBookingInput.isBusinessBooking) Channel.BB else Channel.PI)
}
