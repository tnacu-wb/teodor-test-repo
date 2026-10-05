package com.whitbread.premierinn.bathroomselection

import com.whitbread.premierinn.accessiblebathroomselection.AccessibleRoomSizeOption
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.summary.SummaryNavigation
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownInput

data class BathroomSelectionState(
    val accessibleRoomChoices: List<AccessibleRoomChoices>,
    val nonAccessibleRoomChoices: List<NonAccessibleRoom>,
    val totalCost: String = EMPTY_STRING_DOMAIN,
    val deviceLocaleProvider: DeviceLocaleProvider,
    val bookingFlowInput: BookingFlowInput? = null)

data class AccessibleRoomChoices(val roomId: Int,
                                 val occupantsSubheading: String,
                                 val roomSizeDescription: String,
                                 val showSizeChangeButton: Boolean,
                                 val toolTipText: String?,
                                 val firstBathroomOption: BathroomOption,
                                 val secondBathroomOption: BathroomOption?,
                                 val firstBathroomSelected: Boolean,
                                 val secondBathroomSelected: Boolean,
                                 val totalCostFirst: ParcelablePrice,
                                 val totalCostSecond: ParcelablePrice)


data class NonAccessibleRoom(val roomId: Int, val occupantsSubheading: String, val roomSizeDescription: String,
                             val totalCost: ParcelablePrice)

data class BathroomOption(val bathroomName: String,
                          val bathroomDescription: String,
                          val isBathroomSelected: Boolean,
                          val warningMessage: String?,
                          val bathroomType: BathroomType)

sealed class BathroomSelectionEvent {
    data class SubmitEvent(val summaryInput: SummaryInput): BathroomSelectionEvent()
    data object GenericFailureEvent : BathroomSelectionEvent()
    data object CreateReservationFailureEvent : BathroomSelectionEvent()
    data class BookingInformationErrorEvent(val basketReference: String, val updatedSummaryInput: SummaryInput) : BathroomSelectionEvent()
    data class OpenLoginDetails(val basketReference: String) : BathroomSelectionEvent()
    data class OpenReviewBookActivity(val reviewBookingInput: ReviewBookingInput) : BathroomSelectionEvent()
    data object OpenGuestDetailsActivity : BathroomSelectionEvent()
    data class OpenAdditionalInfoActivity(val reviewBookingInput: ReviewBookingInput) : BathroomSelectionEvent()
    data class ShowPriceBreakdownEvent(val summaryBreakdownInput: SummaryBreakdownInput) : BathroomSelectionEvent()
    data class ShowSizeSelectionEvent(val roomId: Int,
                                      val currentlySelectedSize: AccessibleRoomSizeOption): BathroomSelectionEvent()
}
