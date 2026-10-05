package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.addItems
import com.whitbread.premierinn.ciol.entity.upsells.buildUpsellPairForUpsellDetails
import com.whitbread.premierinn.ciol.entity.upsells.copy
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.buildUpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.deepCopy
import com.whitbread.premierinn.ciol.entity.upsells.details.getCurrentRoomSelections
import com.whitbread.premierinn.ciol.entity.upsells.organizeToCategories
import com.whitbread.premierinn.ciol.fragments.UPSELLS_FEATURE_TAG
import com.whitbread.premierinn.ciol.uilogic.FormatUpsellsList
import com.whitbread.premierinn.ciol.utils.disableMultiMealSelection
import com.whitbread.premierinn.ciol.utils.updateNumberOfFreeBreakfastSelections
import com.whitbread.premierinn.ciol.utils.updateNumberOfSelections
import com.whitbread.premierinn.ciol.utils.updateRoomSelections
import com.whitbread.premierinn.ciol.utils.updateUpsells
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_SELECT_ROOM
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.hasChildren
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RoomSelectionViewModel @Inject constructor(
    private val formatUpsellsList: FormatUpsellsList,
    private val trackingAnalytics: TrackingAnalytics
) : ViewModel() {

    private val _state = MutableStateFlow(RoomSelectionState())
    val state: StateFlow<RoomSelectionState> = _state

    data class RoomSelectionState(
        val bookingConfirmation: BookingConfirmation? = null,
        val preselectedRoomSelections: List<RoomSelection> = emptyList(),
        val roomSelections: List<RoomSelection> = emptyList(),
        val upsellItems: List<UpsellItem> = emptyList(),
        val upsellEntry: UpsellEntry? = null,
        val upsellDetails: UpsellDetailsModel? = null,
        val isError: Boolean = false
    )

    fun onScreenOpened(
        bookingConfirmation: BookingConfirmation?,
        preselectedRoomSelections: List<RoomSelection>?,
        roomSelections: List<RoomSelection>?,
        upsellItems: List<UpsellItem>?,
        upsellEntry: UpsellEntry?,
        preStayUiModel: PreStayUiModel?
    ) {
        if (bookingConfirmation != null && !roomSelections.isNullOrEmpty() && !upsellItems.isNullOrEmpty()) {
            _state.update {
                it.copy(
                    bookingConfirmation = bookingConfirmation,
                    preselectedRoomSelections = preselectedRoomSelections ?: emptyList(),
                    roomSelections = roomSelections,
                    upsellItems = upsellItems,
                    upsellEntry = upsellEntry
                )
            }
        } else {
            Log.w(UPSELLS_FEATURE_TAG, "bookingConfirmation, roomSelections or upsellItems were null or empty")
        }
        logCiolAnalytics(trackingAnalytics, preStayUiModel, CIOL_SELECT_ROOM)
    }

    fun onRoomClicked(upsellItem: UpsellEntry, currentRoomId: String, roomName: String) {
        val (upsellType, availableUpsells) = upsellItem
            .buildUpsellPairForUpsellDetails(_state.value.upsellItems, _state.value.bookingConfirmation?.hasChildren())

        // Make sure that roomSelections only contains the selected upsells for the current room
        val currentRoomSelections = _state.value.roomSelections.getCurrentRoomSelections(currentRoomId, upsellType)

        val upsellsToUpdate = availableUpsells.copy().toMutableList()

        currentRoomSelections.forEach { roomSelection ->
            if (roomSelection.selectedUpsells.isEmpty()) {
                // If room doesn't have any selected upsell, just update noOfSelections to 0
                upsellsToUpdate.forEach {
                    it.updateNumberOfSelections(0)
                    it.updateNumberOfFreeBreakfastSelections(0)
                }
            } else {
                upsellsToUpdate.updateUpsells(roomSelection)
            }
        }

        _state.value.bookingConfirmation?.let { nonNullBookingConfirmation ->
            val upsellDetails = buildUpsellDetailsModel(
                roomId = currentRoomId,
                roomSelections = currentRoomSelections,
                availableUpsells = upsellsToUpdate,
                bookingConfirmation = nonNullBookingConfirmation,
                roomName = roomName,
                upsellType = upsellType,
                bookingId = nonNullBookingConfirmation.bookingReference
            )

            _state.update {
                it.copy(upsellDetails = upsellDetails)
            }
        } ?: run {
            _state.update { it.copy(isError = true) }
        }
    }

    fun onActionPerformed() {
        _state.update { it.copy(upsellDetails = null) }
    }

    fun onUpsellDetailsClosed(roomSelections: List<RoomSelection>) {
        val currentRoomSelections = _state.value.roomSelections.deepCopy()

        // Upsell items from the state contain a breakfast item, which can not be mapped correctly
        val upsellsToUpdate = mutableListOf<UpsellEntry>()
        _state.value.upsellItems
            .filterIsInstance<UpsellEntry>()
            .copy()
            .addItems(upsellsToUpdate)

        roomSelections.forEach { roomSelection ->
            upsellsToUpdate.updateUpsells(roomSelection)
            currentRoomSelections.updateRoomSelections(roomSelection)
        }

        val totalRoomsAdults = _state.value.bookingConfirmation?.reservationByIdList?.sumOf { it.roomStay.adultsNumber }?.toInt() ?: -1

        _state.update {
            it.copy(
                upsellItems = formatUpsellsList(
                    upsellsToUpdate.toMutableList()
                        .organizeToCategories()
                        .disableMultiMealSelection(totalRoomsAdults)
                ),
                roomSelections = currentRoomSelections
            )
        }
    }
}
