package com.whitbread.premierinn.ciol.entity

import android.os.Parcelable
import com.whitbread.premierinn.ciol.utils.getReservationId
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAdditionalDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetailsRegCard
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class DeRegCardAddressUiModel(
    val addressLine1: String = EMPTY_STRING,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val cityName: String = EMPTY_STRING,
    val country: String = EMPTY_STRING,
    val postalCode: String = EMPTY_STRING,
    val addressType: String = EMPTY_STRING,
    val addressId: String? = null
): Parcelable

sealed interface RegCardGuest: Parcelable

@Parcelize
data class AdditionalGuestUiModel(
    val id :String = EMPTY_STRING,
    val profileId: String? = EMPTY_STRING,
    val firstName: String = EMPTY_STRING,
    val lastName: String = EMPTY_STRING,
    val dateOfBirth: String = EMPTY_STRING,
    val nationality: String = EMPTY_STRING,
    val passportNumber: String = EMPTY_STRING,
    val sameAsBooker: Boolean = false,
    val reservationId: String,
    val isAccompanyingGuest: Boolean = true
) : RegCardGuest, Parcelable

@Parcelize
data class LeadGuestUiModel(
    val title: String? = EMPTY_STRING,
    val address: DeRegCardAddressUiModel = DeRegCardAddressUiModel(),
    val additionalGuestUiModel: AdditionalGuestUiModel,
) : RegCardGuest, Parcelable

fun RegCardGuest.mapToStayingGuestDetailsRegCard(leadBookerFirstName: String = EMPTY_STRING, leadBookerLastname: String = EMPTY_STRING) =
    when (this) {
    is AdditionalGuestUiModel -> StayingGuestDetailsRegCard(
        firstName = this.firstName,
        lastName = this.lastName,
        profileId = this.profileId,
        address = null,
        additionalDetails = StayingGuestAdditionalDetails(
            dob = this.dateOfBirth,
            passportNumber = this.passportNumber.ifBlank { null },
            nationality = this.nationality
        ),
        sameAsBooker = false,
        reservationId = getReservationId(),
        isAccompanyingGuest = true,
    )

    is LeadGuestUiModel -> StayingGuestDetailsRegCard(
        title = title,
        firstName = this.additionalGuestUiModel.firstName,
        lastName = this.additionalGuestUiModel.lastName,
        profileId = this.additionalGuestUiModel.profileId,
        address = StayingGuestAddress(
            addressLine1 = this.address.addressLine1,
            addressLine2 = this.address.addressLine2,
            addressLine3 = this.address.addressLine3,
            cityName = this.address.cityName,
            countryCode = this.address.country,
            postalCode = this.address.postalCode,
            addressType = this.address.addressType,
        ),
        additionalDetails = StayingGuestAdditionalDetails(
            dob = this.additionalGuestUiModel.dateOfBirth,
            passportNumber = this.additionalGuestUiModel.passportNumber.ifBlank { null },
            nationality = this.additionalGuestUiModel.nationality
        ),
        sameAsBooker =
        leadBookerFirstName == this.additionalGuestUiModel.firstName && leadBookerLastname == this.additionalGuestUiModel.lastName,
        reservationId = getReservationId(),
        isAccompanyingGuest = false,
    )
}

fun ReservationGuestUiModel.mapToRegCardGuest(leadBookerDetails: LeadBookerDetailsUiModel): RegCardGuest {
    if (!this.isAccompanyingGuest) {
        val leadGuestSameAsBooker = leadBookerDetails.leadBookerFirstName == this.firstName
                && leadBookerDetails.leadBookerLastName == this.lastName
        return LeadGuestUiModel(
            title = this.title,
            address = DeRegCardAddressUiModel(
                addressLine1 = this.address?.addressLine1 ?: EMPTY_STRING,
                addressLine2 = this.address?.addressLine2 ?: EMPTY_STRING,
                addressLine3 = this.address?.addressLine3 ?: EMPTY_STRING,
                cityName = this.address?.cityName?.ifEmpty { this.address.addressLine4 } ?: EMPTY_STRING,
                postalCode = this.address?.postalCode ?: EMPTY_STRING,
                country = this.address?.country ?: EMPTY_STRING
            ),
            additionalGuestUiModel = AdditionalGuestUiModel(
                id = UUID.randomUUID().toString(),
                profileId = this.profileId,
                firstName = this.firstName,
                lastName = this.lastName,
                dateOfBirth = this.dateOfBirth ?: EMPTY_STRING,
                nationality = this.nationality ?: EMPTY_STRING,
                passportNumber = this.passportNumber ?: EMPTY_STRING,
                sameAsBooker = leadGuestSameAsBooker,
                reservationId = this.reservationId,
                isAccompanyingGuest = false
            )
        )
    } else {
        return AdditionalGuestUiModel(
            id = UUID.randomUUID().toString(),
            firstName = this.firstName,
            lastName = this.lastName,
            dateOfBirth = this.dateOfBirth ?: EMPTY_STRING,
            nationality = this.nationality ?: EMPTY_STRING,
            passportNumber = this.passportNumber ?: EMPTY_STRING,
            sameAsBooker = false,
            reservationId = this.reservationId,
            isAccompanyingGuest = true
        )
    }
}
