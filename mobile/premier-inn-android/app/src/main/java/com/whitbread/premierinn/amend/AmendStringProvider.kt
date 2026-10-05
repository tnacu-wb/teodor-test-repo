package com.whitbread.premierinn.amend

import android.content.Context
import androidx.annotation.StringRes
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.utils.formattedNumberOfNights
import com.whitbread.premierinn.common.utils.guestsAndRooms
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.roomcriteria.getStringResourceName
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AmendStringProvider @Inject constructor(private val context: Context, val deviceLocaleProvider: DeviceLocaleProvider): StringResourceProvider(context, deviceLocaleProvider) {

    val getCheckAvailabilityText = context.getString(R.string.amend_check_availability)
    val updateButtonText = context.getString(R.string.update_button)
    val updateErrorMessage = context.getString(R.string.amend_update_error)
    val continueButtonText = context.getString(R.string.button_text_continue)
    val balanceOutstandingTitle = context.getString(R.string.review_amends_balance_outstanding)
    val balanceOutstandingTextPayOnArrival = context.getString(R.string.review_amends_balance_description_poa)
    val refundTitle = context.getString(R.string.review_amends_refund_title)
    val refundDescription = context.getString(R.string.review_amends_refund_description)
    val balanceDescriptionRefund = context.getString(R.string.review_amends_balance_description_refund)
    val totalPaidText = context.getString(R.string.review_amends_total_paid_title)
    val previousPaidText = context.getString(R.string.review_amends_previous_total_title)
    val noAvailabilityErrorMessage = context.getString(R.string.amend_calendar_results_fully_booked)
    val availabilityErrorMessage = context.getString(R.string.amend_calendar_results_error_message)
    val availabilityChangeDatesErrorMessage = context.getString(R.string.amend_calendar_change_dates_error_message)
    val genericErrorMessage = context.getString(R.string.generic_error_description)
    val noAvailabilityRoomsErrorMessage = context.getString(R.string.amend_update_room_no_availability_error)
    val availabilityRoomErrorMessage = context.getString(R.string.amend_update_room_availability_error)
    val mealChangedTitle = context.getString(R.string.review_amends_meals_changed)
    val cotRemoved = context.getString(R.string.review_amends_cot_removed)
    val cotAdded = context.getString(R.string.review_amends_cot_added)
    val dateChangedTitle = context.getString(R.string.review_amends_dates_changed)
    val bookingSummaryTitle = context.getString(R.string.review_amends_new_booking_title)
    val trackAmendUpsellsError = context.getString(R.string.amend_extras_available_upsells_error)
    val amendErrorTitleGeneric = context.getString(R.string.amend_error_title)
    val amendUnsuccessfulTitle = context.getString(R.string.review_amends_confirm_amend_failure_title)
    val basketStatusOpenErrorMessage = context.getString(R.string.review_amends_basket_status_open_desc_poa)
    val basketStatusFailedErrorMessage = context.getString(R.string.review_amends_basket_status_failed_desc_poa)
    val basketStatusAmendingErrorMessage = context.getString(R.string.review_amends_basket_status_Amending_desc_poa)
    val confirmAmendCallFailureMessage = context.getString(R.string.review_amends_confirm_amend_failure)

    fun getNoMoreAvailabilityText(roomType: RoomType): String {
        val roomName = roomType.getStringResourceName(context)
        return String.format(context.getString(R.string.amend_no_availability), roomName)
    }

    fun getTotalPriceText(totalCost: PriceDomain): String {
        return if (totalCost.amount > 0.0f) {
            val priceFormat = PriceFormat.format(totalCost.amount, totalCost.currency, deviceLocaleProvider)
            String.format(getString(R.string.amend_positive_price_difference), priceFormat)
        } else if (totalCost.amount == 0.0f) {
            val priceFormat = PriceFormat.format(totalCost.amount, totalCost.currency, deviceLocaleProvider)
            String.format(getString(R.string.amend_difference), priceFormat)
        } else {
            val negativePrice = totalCost.amount * -1
            val priceFormat = PriceFormat.format(negativePrice, totalCost.currency, deviceLocaleProvider)
            String.format(getString(R.string.amend_negative_price_difference), priceFormat)
        }
    }

    fun guestAndRooms(numberOfGuests: Int, numberOfRooms: Int) : String {
       return context.getString(R.string.guests_and_rooms,
                context.resources.getQuantityString(R.plurals.guests, numberOfGuests, numberOfGuests),
                context.resources.getQuantityString(R.plurals.rooms, numberOfRooms, numberOfRooms))
    }

    fun dateChangeDescription(dates: Pair<LocalDate, LocalDate>): String {
        return dates.formattedNumberOfNights(context)
    }

    fun priceChangeText(price: String, @StringRes resId: Int): String {
        return context.getString(resId, price)
    }

    fun guestChangeText(fullName: String): String {
        return context.getString(R.string.review_amends_guest_changed, fullName)
    }

    fun roomAddedText(fullName: String): String {
        return context.getString(R.string.review_amends_room_added, fullName)
    }

    fun roomRemovedText(fullName: String): String {
        return context.getString(R.string.review_amends_room_removed, fullName)
    }

    fun extrasChangesTitleText(): String {
        return context.getString(R.string.review_amends_extras_changes_title)
    }

    fun extrasRemovedDescriptionText(extrasDescription: String): String {
        return context.getString(R.string.review_amends_extras_removed_description, extrasDescription)
    }

    fun extrasAddedDescriptionText(extrasDescription: String): String {
        return context.getString(R.string.review_amends_extras_added_description, extrasDescription)
    }

    fun roomTypeChangedText(roomType: RoomType): String {
        return context.getString(R.string.review_amends_room_type_changed,
                roomType.getStringResourceName(context))
    }

    fun infantRemovedText(numberOfInfantsRemoved: Int): String {
        return getQuantityString(R.plurals.review_amends_infant_removed, numberOfInfantsRemoved, numberOfInfantsRemoved)
    }

    fun infantAddedText(numberOfInfantsAdded: Int): String {
        return getQuantityString(R.plurals.review_amends_infant_added, numberOfInfantsAdded, numberOfInfantsAdded)
    }

    fun childrenRemovedText(numberOfChildrenRemoved: Int): String {
        return getQuantityString(R.plurals.review_amends_children_removed, numberOfChildrenRemoved, numberOfChildrenRemoved)
    }

    fun childrenAddedText(numberOfChildrenAdded: Int): String {
        return getQuantityString(R.plurals.review_amends_children_added, numberOfChildrenAdded, numberOfChildrenAdded)
    }

    fun adultRemovedText(numberOfAdultsRemoved: Int): String {
        return getQuantityString(R.plurals.review_amends_adult_removed, numberOfAdultsRemoved, numberOfAdultsRemoved)
    }

    fun adultAddedText(numberOfAdultsAdded: Int): String {
        return getQuantityString(R.plurals.review_amends_adult_added, numberOfAdultsAdded, numberOfAdultsAdded)
    }

    fun roomChangedTitleText(guest: String): String {
        return context.getString(R.string.review_amends_room_changed_title, guest)
    }

    fun guestAndRoomChangeDescription(numberOfGuests: Int, numberOfRooms: Int): String {
        return guestsAndRooms(context, numberOfGuests, numberOfRooms)
    }

    fun mealChangeDescription(meal: String, guests: Int, nights: Int): String {
        return context.getString(R.string.two_string_placeholder, meal,
                context.getString(R.string.in_parenthesis, context.getString(R.string.summary_breakdown_guests_and_nights,
                        context.resources.getQuantityString(R.plurals.guests, guests, guests),
                        context.resources.getQuantityString(R.plurals.nights, nights, nights))))
    }
}