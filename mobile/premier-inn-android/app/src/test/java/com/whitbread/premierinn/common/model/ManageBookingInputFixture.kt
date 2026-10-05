package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import org.threeten.bp.LocalDate

object ManageBookingInputFixture {

    fun aManageBookingInput(
            hotelCode: String = "LONMON",
            surname: String = "Onabanjo",
            arrivalDate: LocalDate = LocalDate.now(),
            departureDate: LocalDate = LocalDate.now().plusDays(2),
            referenceNumber: String = "BER2334242",
            amendable: Boolean = true,
            cancellable: Boolean = true,
            isBusinessBooking: Boolean = false,
            dinnerAllowance: Float = 0f): ManageBookingInput {
        return ManageBookingInput.builder()
                .hotelCode(hotelCode)
                .surname(surname)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .bookingReference(referenceNumber)
                .amendable(amendable)
                .cancellable(cancellable)
                .isEmployeeBooking(false)
                .isBusinessBooking(isBusinessBooking)
                .dinnerAllowance(dinnerAllowance)
                .build()
    }
}