package com.whitbread.premierinn.bookingdetails

import com.whitbread.premierinn.R
import com.whitbread.premierinn.bookingdetails.BookingBasicsUiModel.BannerMessage
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.format.formatted
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.common.RoomType
import io.reactivex.functions.Function

const val RESPONSE_SUCCESS = "Success"
private const val RESPONSE_EMAIL_EXISTS = "EmailAlreadyExistsError"

class BookingUiModelMapper(
    private val stringProvider: StringResourceProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val isCheckInOnlineEnabledUseCase: IsCheckInOnlineEnabledUseCase
) : Function<Booking, BookingBasicsUiModel> {
    private var justBookedEmail: String? = null
    private var accountResponse: String? = null

    fun initParams(justBookedEmail: String?, accountResponse: String?) {
        this.justBookedEmail = justBookedEmail
        this.accountResponse = accountResponse
    }

    override fun apply(item: Booking): BookingBasicsUiModel {
        return BookingBasicsUiModel(
                booking = item,
                numberOfNightsFormatted = stringProvider.getNights(item.numberOfNights.toInt()),
                numberOfRoomsFormatted = stringProvider.getRooms(item.numberOfRooms, item.rooms?.allAccessible()
                        ?: false),
                checkInDateFormatted = item.arrivalDate.format(DateFormat.WEEKDAY_DAY_MONTH),
                checkOutDateFormatted = item.departureDate.format(DateFormat.WEEKDAY_DAY_MONTH),
                totalCostFormatted = item.totalCost.formatted(deviceLocaleProvider),
                justBooked = justBookedEmail != null,
                bannerMessage = item.bannerMessage(justBookedEmail, stringProvider,
                    accountResponse, deviceLocaleProvider),
                isCheckInOnlineEnabled = item.isCheckInOnlineAvailable,
                isCheckInOnlineFlagEnabled = isCheckInOnlineEnabledUseCase(),
                isCheckOutOnlineEnabled = item.isCheckOutOnlineAvailable,
                infoMessage = item.infoBoxMessage(),
                isPrepaid = item.prepaid,
                basketStatus = item.basketStatus
        )
    }

    private fun List<Booking.Room>.allAccessible(): Boolean {
        return isNotEmpty() && all { roomBooking -> roomBooking.roomType == RoomType.ACCESSIBLE }
    }

    private fun Booking.infoBoxMessage(): String? {
        if (this.isCancelled) {
            return null
        }
        return null
    }
}

fun Booking.bannerMessage(justBookedEmail: String?, stringProvider: StringResourceProvider,
                          accountResponse: String?, deviceLocaleProvider: DeviceLocaleProvider): BannerMessage? {
    return when {
        // The order of these is important as multiple cases can be true
        this.isCancelled -> BannerMessage(BannerMessage.Type.CANCELLED, stringProvider.getString(R.string.booking_details_canceled))
        justBookedEmail != null -> prepareJustBookedStatusMessage(stringProvider, this,
            justBookedEmail, accountResponse, deviceLocaleProvider)
        else -> null
    }
}

private fun prepareJustBookedStatusMessage(stringProvider: StringResourceProvider, booking: Booking,
                                           emailAddress: String?, accountResponse: String?, deviceLocaleProvider: DeviceLocaleProvider): BannerMessage  {
    var message = stringProvider.getString(R.string.my_bookings_confirmation_message_first_part, emailAddress ?: EMPTY_STRING)
    var messageType = BannerMessage.Type.SUCCESS

    message = if (booking.prepaid) {
        message.plus(stringProvider.getString(R.string.my_bookings_confirmation_message_paid_in_full))
    } else message.plus(stringProvider.getString(R.string.my_bookings_confirmation_message_pay_on_arrival, booking.totalCost.formatted(deviceLocaleProvider)))

    if (accountResponse.isNullOrEmpty().not()) {
        when (accountResponse) {
            RESPONSE_SUCCESS -> {
                message = message.plus(stringProvider.getString(R.string.my_bookings_confirmation_message_account_created))
            }
            RESPONSE_EMAIL_EXISTS -> {
                message = message.plus(stringProvider.getString(R.string.my_bookings_confirmation_message_email_exists))
                messageType = BannerMessage.Type.INFORMATION
            }
            else -> {
                message = message.plus(stringProvider.getString(R.string.my_bookings_confirmation_message_account_error))
                messageType = BannerMessage.Type.INFORMATION
            }
        }
    }
    return BannerMessage(messageType, message)
}