package com.whitbread.premierinn.landing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.TextViewCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.common.utils.LocationUtils.GOOGLE_MAPS_URL
import com.whitbread.premierinn.common.utils.formattedDates
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.DashboardUpcomingBookingBinding
import com.whitbread.premierinn.databinding.UpcomingBookingWidgetBinding
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.dashboard.entity.Action
import com.whitbread.premierinn.domain.dashboard.entity.Content
import com.whitbread.premierinn.domain.dashboard.entity.Room
import java.util.Locale

private const val MAX_NUMBER_OF_ACTION = 3
private const val DIRECTIONS_ACTION = "DIRECTIONS"
private const val UPSELLS_ACTION = "UPSELLS"
private const val BOOKING_DETAILS = "BOOKING_DETAILS"
private const val IMAGE_VIEW_RADIUS = 2f

class UpcomingBookingView(
        val context: Context,
        private val leadGuestDetails: LeadGuestDetails,
        private val upcomingBooking: Content,
        language: String) {

    val upcomingBookingBinding: DashboardUpcomingBookingBinding =
        DashboardUpcomingBookingBinding.inflate(LayoutInflater.from(context))

    val upcomingBookingWidgetBinding: UpcomingBookingWidgetBinding =
        UpcomingBookingWidgetBinding.bind(upcomingBookingBinding.root)

    init {
        val dates = Pair(upcomingBooking.arrivalDate!!, upcomingBooking.departureDate!!)

        upcomingBookingBinding.dashboardUpcomingBookingTitle.text = context.getString(R.string.upcoming_booking_title)
        upcomingBookingBinding.dashboardUpcomingBookingClose.text = context.getString(R.string.upcoming_booking_close)
        upcomingBookingWidgetBinding.upcomingBookingHotelImage.load(upcomingBooking.hotelImage)
        upcomingBookingWidgetBinding.upcomingBookingHotelImage.shapeAppearanceModel =
            upcomingBookingWidgetBinding.upcomingBookingHotelImage.shapeAppearanceModel
                        .toBuilder()
                        .setAllCornerSizes(IMAGE_VIEW_RADIUS)
                        .build()

        upcomingBookingWidgetBinding.upcomingBookingHotelName.text = upcomingBooking.hotelName
        upcomingBookingWidgetBinding.upcomingBookingHotelDate.text = dates.formattedDates(context)

        val actions = upcomingBooking.actions.take(MAX_NUMBER_OF_ACTION)

        if (upcomingBooking.checkedIn) {
            TextViewCompat.setTextAppearance(upcomingBookingWidgetBinding.upcomingBookingRoom, R.style.UpcomingBookingCheckedInStyle)
            upcomingBookingWidgetBinding.upcomingBookingRoom.text = MessageProvider(context.resources).upcomingBookingCheckedIn()
        } else {
            upcomingBookingWidgetBinding.upcomingBookingRoom.text =
                    MessageProvider(context.resources).formatGuestAndRooms(upcomingBooking.rooms, upcomingBooking.guests,
                            upcomingBooking.rooms.size, verifyAllRoomsAreTheSame(upcomingBooking.rooms), language)
        }

        when (actions.size) {
            1 -> {
                setClickListeners(null, null, actions[0])
            }
            2 -> {
                setClickListeners(null, actions[0], actions[1])
            }
            3 -> {
                setClickListeners(actions[0], actions[1], actions[2])
            }
        }
    }

    private fun setClickListeners(
            firstAction: Action?,
            secondAction: Action?,
            thirdAction: Action?
    ) {
        firstAction?.let { action ->
            upcomingBookingWidgetBinding.upcomingBookingFirstAction.visibility = View.VISIBLE
            upcomingBookingWidgetBinding.upcomingBookingFirstAction.setText(firstAction.title)
            upcomingBookingWidgetBinding.upcomingBookingFirstAction.setOnClickListener {
                navigate(action)
            }
        }

        secondAction?.let { action ->
            upcomingBookingWidgetBinding.upcomingBookingSecondAction.visibility = View.VISIBLE
            upcomingBookingWidgetBinding.upcomingBookingSecondAction.setText(secondAction.title)
            upcomingBookingWidgetBinding.upcomingBookingSecondAction.setOnClickListener {
                navigate(action)
            }
        }

        thirdAction?.let { action ->
            upcomingBookingWidgetBinding.upcomingBookingThirdAction.visibility = View.VISIBLE
            upcomingBookingWidgetBinding.upcomingBookingThirdAction.setText(thirdAction.title)
            upcomingBookingWidgetBinding.upcomingBookingThirdAction.setOnClickListener {
                navigate(action)
            }
        }
    }

    private fun verifyAllRoomsAreTheSame(list: List<Room>): Boolean {
        val roomType = mutableListOf<String>()
        list.map { roomType.add(it.type) }
        return roomType.toHashSet().size <= 1
    }

    private fun navigate(dashboardAction: Action) {
        when (dashboardAction.type) {
            DIRECTIONS_ACTION -> {
                navigateToMaps()
            }

//            UPSELLS_ACTION -> {
//                navigateToUpsells()
//            }

            BOOKING_DETAILS -> {
                navigateToBookingDetails()
            }
        }
    }

    private fun navigateToMaps() {
        if (upcomingBooking.map.latitude != null && upcomingBooking.map.longitude != null) {
            val uri = String.format(
                Locale.ENGLISH, GOOGLE_MAPS_URL,
                upcomingBooking.map.latitude, upcomingBooking.map.longitude
            )
            context.startActivity(IntentUtils.createWebLinkIntent(uri))
        }
    }
// TODO: current AmendExtrasActivity was removed and AmendUpsellsActivity was created

//    private fun navigateToUpsells() {
//        val input = ManageBookingInput.builder()
//                .hotelCode(upcomingBooking.hotelCode)
//                .reservationId(upcomingBooking.confirmationNumber)
//                .surname(leadGuestDetails.surname)
//                .arrivalDate(upcomingBooking.arrivalDate)
//                .departureDate(upcomingBooking.departureDate)
//                .amendable(true)
//                .cancellable(true)
//                .isEmployeeBooking(false)
//                .isBusinessBooking(false)
//                .build()
//
//        AmendExtrasActivity.createIntentFromDashboard(context, input).also {
//            context.startActivity(it)
//        }
//    }

    private fun navigateToBookingDetails() {
        context.startActivity(
            BookingDetailsActivity.createIntent(
                context,
                upcomingBooking.confirmationNumber,
                EMPTY_STRING_DOMAIN,
                leadGuestDetails.email,
                null,
                null,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                false,
                EMPTY_STRING,
                EMPTY_STRING
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        )
    }
}