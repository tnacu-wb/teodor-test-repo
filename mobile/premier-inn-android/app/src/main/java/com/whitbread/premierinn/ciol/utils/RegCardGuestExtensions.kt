package com.whitbread.premierinn.ciol.utils

import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.data.common.EMPTY_STRING

fun RegCardGuest.getTitle(): String = when (this) {
    is AdditionalGuestUiModel -> EMPTY_STRING
    is LeadGuestUiModel -> title ?: EMPTY_STRING
}
fun RegCardGuest.getFirstName(): String = when(this) {
    is AdditionalGuestUiModel -> firstName
    is LeadGuestUiModel -> additionalGuestUiModel.firstName
}

fun RegCardGuest.getLastName(): String = when(this) {
    is AdditionalGuestUiModel -> lastName
    is LeadGuestUiModel -> additionalGuestUiModel.lastName
}

fun RegCardGuest.getAddressLineOne(): String? = when(this) {
    is AdditionalGuestUiModel -> null
    is LeadGuestUiModel -> address.addressLine1
}

fun RegCardGuest.getNationality(): String = when(this) {
    is AdditionalGuestUiModel -> nationality
    is LeadGuestUiModel -> additionalGuestUiModel.nationality
}

fun RegCardGuest.getPassportNumber(): String = when(this) {
    is AdditionalGuestUiModel -> passportNumber
    is LeadGuestUiModel -> additionalGuestUiModel.passportNumber
}

fun RegCardGuest.getReservationId(): String = when(this) {
    is AdditionalGuestUiModel -> reservationId
    is LeadGuestUiModel -> additionalGuestUiModel.reservationId
}

fun RegCardGuest.getId(): String = when(this) {
    is AdditionalGuestUiModel -> id
    is LeadGuestUiModel -> additionalGuestUiModel.id
}

fun RegCardGuest.getDateOfBirth(): String = when(this) {
    is AdditionalGuestUiModel -> dateOfBirth
    is LeadGuestUiModel -> additionalGuestUiModel.dateOfBirth
}

fun RegCardGuest.getProfileId(): String? = when(this) {
    is AdditionalGuestUiModel -> profileId
    is LeadGuestUiModel -> additionalGuestUiModel.profileId
}

fun List<RegCardGuest>.copy() = this.map {
    when(it) {
        is AdditionalGuestUiModel -> it.copy()
        is LeadGuestUiModel -> it.copy()
    }
}
