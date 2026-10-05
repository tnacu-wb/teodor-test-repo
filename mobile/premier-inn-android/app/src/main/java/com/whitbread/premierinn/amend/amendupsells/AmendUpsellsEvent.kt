package com.whitbread.premierinn.amend.amendupsells

import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo

sealed interface AmendUpsellsEvent {
    data object NavigateBack : AmendUpsellsEvent
    data object Continue : AmendUpsellsEvent
    data class ShowMenuAndAllergyInfo(val menuAndAllergyInfo: List<ParcelableMenuAndAllergyInfo>) : AmendUpsellsEvent
    data object GeneralError : AmendUpsellsEvent
}