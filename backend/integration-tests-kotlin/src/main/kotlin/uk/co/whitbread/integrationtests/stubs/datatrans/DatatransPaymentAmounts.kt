package uk.co.whitbread.integrationtests.stubs.datatrans

import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.math.BigDecimal
import java.time.temporal.ChronoUnit

// Amount facts shared by every Datatrans initialisation mapping. Both the Secure Fields and the
// Mobile SDK init stubs match on the minor-unit amount the payment service asks the gateway for,
// so the conversion lives here once rather than being copied per channel.

/**
 * Minor-unit exponents Datatrans expects per currency, mirroring the payment service's own table.
 * Anything absent is treated as a two-decimal currency, exactly as the service does.
 */
private val CURRENCY_EXPONENTS =
    mapOf(
        "GBP" to 2,
        "EUR" to 2,
        "USD" to 2,
        "CHF" to 2,
        "JPY" to 0,
        "KRW" to 0,
    )

/** Converts the stay total into the minor units Datatrans is asked for. */
internal fun minorUnits(
    booking: Booking,
    rate: Rate,
): Long {
    val nights = ChronoUnit.DAYS.between(booking.arrival!!, booking.departure!!)
    val total = BigDecimal.valueOf(rate.nightlyRate).multiply(BigDecimal.valueOf(nights))
    val exponent = CURRENCY_EXPONENTS[booking.hotel.currency.uppercase()] ?: 2
    return total.movePointRight(exponent).longValueExact()
}

/** Resolves the rate priced for the paid room, matching how the reservation stubs pair them. */
internal fun selectedRateForPayment(
    booking: Booking,
    room: BookingRoom,
): Rate? =
    booking.hotel.availableRates.firstOrNull { rate ->
        rate.roomType == room.roomType &&
            rate.adults == room.adults &&
            rate.children == room.children
    }
