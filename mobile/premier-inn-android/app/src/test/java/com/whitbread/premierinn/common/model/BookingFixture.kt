package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import org.threeten.bp.LocalDate
import java.util.*

object BookingFixture {

    fun aBooking(
            bookingReference: String = "BER2334242",
            leadGuestSurname: String = "Joshua",
            arrivalDate: LocalDate = LocalDate.now(),
            departureDate: LocalDate = arrivalDate.plusDays(3),
            hotelCode: String = HotelFixture.aHotel().code,
            hotelName: String = "Moonlight Sonata",
            numberOfRooms: Int = 1,
            numberOfGuests : Int = 1,
            leadGuestFullName: String = "Joshua Onabanjo",
            rateType: String = "N/A",
            totalCost: PriceDomain = PriceDomain(208.99f, "GBP"),
            balanceOutstanding: PriceDomain = PriceDomain(208.99f, "GBP"),
            prepaidAmount: PriceDomain? = null,
            amendable: Boolean = true,
            cancelable: Boolean = true,
            isCancelled: Boolean = false,
            isLinkedToAccount: Boolean = true,
            cardFeeApplies: Boolean = false,
            details: Booking.Details? = null,
            upsells: List<Upsell> = listOf(UpsellFixture.aUpsell()),
            amendRestrictions: AmendRestrictions = AmendRestrictions.createWithDefaults(),
            booker: Guest? = null,
            rooms: List<Booking.Room>? = Collections.emptyList())  : Booking {
        return Booking(
                bookingReference,
                leadGuestSurname,
                arrivalDate,
                departureDate,
                hotelCode,
                hotelName,
                numberOfRooms,
                numberOfGuests,
                leadGuestFullName,
                rateType,
                totalCost,
                balanceOutstanding,
                prepaidAmount,
                amendable,
                cancelable,
                isCancelled,
                isLinkedToAccount,
                cardFeeApplies,
                details,
                upsells,
                amendRestrictions,
                booker,
                rooms
        )
    }

        fun aOperaBooking(
                bookingReference: String = "AJK9232575",
                leadGuestSurname: String = "Test",
                arrivalDate: LocalDate = LocalDate.now(),
                departureDate: LocalDate = arrivalDate.plusDays(3),
                hotelCode: String = HotelFixture.aHotel().code,
                hotelName: String = "London Gatwick Airport",
                numberOfRooms: Int = 1,
                numberOfGuests : Int = 1,
                leadGuestFullName: String = "Test Opera",
                rateType: String = "N/A",
                totalCost: PriceDomain = PriceDomain(208.99f, "GBP"),
                balanceOutstanding: PriceDomain = PriceDomain(208.99f, "GBP"),
                prepaidAmount: PriceDomain? = null,
                amendable: Boolean = true,
                cancelable: Boolean = true,
                isCancelled: Boolean = false,
                isLinkedToAccount: Boolean = true,
                cardFeeApplies: Boolean = false,
                details: Booking.Details? = null,
                upsells: List<Upsell> = listOf(UpsellFixture.aUpsell()),
                amendRestrictions: AmendRestrictions = AmendRestrictions.createWithDefaults(),
                booker: Guest? = null,
                rooms: List<Booking.Room>? = Collections.emptyList())  : Booking {
                return Booking(
                        bookingReference,
                        leadGuestSurname,
                        arrivalDate,
                        departureDate,
                        hotelCode,
                        hotelName,
                        numberOfRooms,
                        numberOfGuests,
                        leadGuestFullName,
                        rateType,
                        totalCost,
                        balanceOutstanding,
                        prepaidAmount,
                        amendable,
                        cancelable,
                        isCancelled,
                        isLinkedToAccount,
                        cardFeeApplies,
                        details,
                        upsells,
                        amendRestrictions,
                        booker,
                        rooms
                )
        }
}