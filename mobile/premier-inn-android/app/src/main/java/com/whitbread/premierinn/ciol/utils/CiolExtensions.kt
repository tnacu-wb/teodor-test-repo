package com.whitbread.premierinn.ciol.utils

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.GuestsRoomUiModel
import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment.Companion.ITEM_TYPE_ADDRESS
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment.Companion.ITEM_TYPE_EMAIL_ADDRESS
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment.Companion.ITEM_TYPE_PHONE_NUMBER
import com.whitbread.premierinn.ciol.uimodel.PreStayEditItemUIModel
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import org.threeten.bp.format.DateTimeFormatter

const val DATE_FORMAT = "EEE, d MMM"

fun PreStayUiModel.getDates(): String = preStayDetails.startDate
    .format(DateTimeFormatter.ofPattern(DATE_FORMAT))
    .plus(PreStaySharedViewModel.HYPHEN)
    .plus(
        preStayDetails.endDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
    )

fun LeadBookerDetailsUiModel.getPreStayEditItemValueMap(itemType: String): HashMap<PreStayEditItemFieldType, PreStayEditItemUIModel> {
    return when (itemType) {
        ITEM_TYPE_EMAIL_ADDRESS -> hashMapOf(
            PreStayEditItemFieldType.EMAIL_ADDRESS to PreStayEditItemUIModel(leadBookerEmail)
        )
        ITEM_TYPE_PHONE_NUMBER -> hashMapOf(
            PreStayEditItemFieldType.PHONE_NUMBER to PreStayEditItemUIModel(leadBookerPhone)
        )
        ITEM_TYPE_ADDRESS -> hashMapOf(
            PreStayEditItemFieldType.COUNTRY to PreStayEditItemUIModel(address.country),
            PreStayEditItemFieldType.POST_CODE to PreStayEditItemUIModel(address.postalCode),
            PreStayEditItemFieldType.ADDRESS_LINE_1 to PreStayEditItemUIModel(address.addressLine1),
            PreStayEditItemFieldType.ADDRESS_LINE_2 to PreStayEditItemUIModel(address.addressLine2),
            PreStayEditItemFieldType.ADDRESS_LINE_3 to PreStayEditItemUIModel(address.addressLine3)
        )
        else -> HashMap()
    }
}

fun PreStayUiModel.getGeneralGuestsDetails(context: Context): String {
    val adults = context.resources.getQuantityString(
        R.plurals.number_of_adults,
        preStayDetails.numberOfAdults,
        preStayDetails.numberOfAdults
    )
    val children = context.resources.getQuantityString(
        R.plurals.number_of_children,
        preStayDetails.numberOfChildren,
        preStayDetails.numberOfChildren
    )
    val rooms = context.resources.getQuantityString(
        R.plurals.rooms,
        preStayDetails.roomGuests.size,
        preStayDetails.roomGuests.size
    )
    val nights = context.resources.getQuantityString(
        R.plurals.nights,
        preStayDetails.numberOfNights,
        preStayDetails.numberOfNights
    )

    return listOf(
        adults,
        if (preStayDetails.numberOfChildren > 0) {
            children
        } else {
            StringUtils.EMPTY_STRING
        }, if (preStayDetails.roomGuests.size > 1) {
            rooms
        } else {
            StringUtils.EMPTY_STRING
        },
        nights
    ).filter { element -> element.isNotEmpty() }.joinToString()
}

fun PreStayUiModel.mapNationality(languageBasedNationality: String): PreStayUiModel {
    return this.copy(
        preStayDetails = preStayDetails.copy(
            reservationGuests = preStayDetails.reservationGuests.map { reservationGuest ->
                reservationGuest.copy(
                    nationality = reservationGuest.nationality?.ifEmpty { languageBasedNationality } ?: languageBasedNationality,
                )
                                                                     },
            roomGuests = preStayDetails.roomGuests.map { roomGuest ->
                roomGuest.copy(
                    leadGuestNationality = roomGuest.leadGuestNationality.ifEmpty { languageBasedNationality },
                    accompanyingGuestNationality = roomGuest.accompanyingGuestNationality.ifEmpty { languageBasedNationality }
                )
            }
        )
    )
}

fun GuestsRoomUiModel.isSecondGuestPresent() = this.accompanyingGuestTitle.isNotEmpty() && this.accompanyingGuestFirstName.isNotEmpty() && this.accompanyingGuestLastName.isNotEmpty()

fun GuestsRoom.isSecondGuestPresent() = this.accompanyingGuestTitle.isNotEmpty() && this.accompanyingGuestFirstName.isNotEmpty() && this.accompanyingGuestLastName.isNotEmpty()

fun String.getTitle() = this.split(StringUtils.SPACE, limit = 3).getOrNull(0) ?: StringUtils.EMPTY_STRING

fun String.getFirstName() = this.split(StringUtils.SPACE, limit = 3).getOrNull(1) ?: StringUtils.EMPTY_STRING

fun String.getLastName() = this.split(StringUtils.SPACE, limit = 3).getOrNull(2) ?: StringUtils.EMPTY_STRING
