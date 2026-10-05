package com.whitbread.premierinn.ciol.usecase

import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.utils.getAddressLineOne
import com.whitbread.premierinn.ciol.utils.getDateOfBirth
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getNationality
import com.whitbread.premierinn.ciol.utils.getPassportNumber
import com.whitbread.premierinn.domain.ciol.usecase.GERMANY_ISO_CODE
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import javax.inject.Inject

class ValidateRegCardGuestDetailsUseCase @Inject constructor(
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase
) {
    operator fun invoke(regCardGuests: List<RegCardGuest>): List<String> {
        val invalidIds = mutableListOf<String>()

        regCardGuests.forEach { guest ->
            when (guest) {
                is LeadGuestUiModel -> if (guest.hasEmptyFields(guest.getAddressLineOne())) {
                    invalidIds.add(guest.getId())
                }

                is AdditionalGuestUiModel -> if (guest.hasEmptyFields()) {
                    invalidIds.add(guest.getId())
                }
            }
        }
        return invalidIds
    }

    private fun RegCardGuest.hasEmptyFields(addressLine1: String? = null): Boolean {
        val isAddressInvalid = this is LeadGuestUiModel && addressLine1?.isEmpty() == true
        val isPassportInvalid =
            getIsoCodeFromCountryNameUseCase(this.getNationality()) != GERMANY_ISO_CODE && this.getPassportNumber().isEmpty()
        return this.getFirstName().isEmpty() || this.getLastName().isEmpty() || this.getNationality().isEmpty()
                || isAddressInvalid || this.getDateOfBirth().isEmpty() || isPassportInvalid
    }
}
