package com.whitbread.premierinn.roomcriteria.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.roomcriteria.ParcelableRooms
import com.whitbread.premierinn.roomcriteria.RoomCriteriaActivity.Companion.ROOM_CRITERIA_SELECTION
import com.whitbread.premierinn.roomcriteria.toRoomCriteriaList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoomCriteriaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStringResource: GetStringResource,
    private val roomCriteriaHelper: RoomCriteriaHelper,
    analytics: TrackingAnalytics,
    private val isCustomerLoggedIn: IsCustomerLoggedIn
) : BaseRoomCriteriaViewModel(
        getStringResource,
        roomCriteriaHelper,
        RoomCriteriaState.createFromCriteriaList(savedStateHandle.get<ParcelableRooms>(ROOM_CRITERIA_SELECTION)?.toRoomCriteriaList()))
{

    init {
        analytics.track(ScreenState.CRITERIA, Type.LOOK_TO_BOOK)
    }

    fun onRoomAdded() {
        val canAdd = !currentState().hasMaxRooms()
        if (canAdd) {
            applyState(Reducer { it.copyWithRoomAdded() })
        } else publish(MaxRoomConstraintEvent.create(getStringResource, roomCriteriaHelper))
    }

    fun onRoomRemoved(roomId: Int) = applyState(Reducer { it.copyWithRoomRemoved(roomId) })

    fun onSubmitSelection() = publish(SendSelectionEvent(currentState()))

    fun isBusinessUser() : Boolean { return isCustomerLoggedIn.isLoggedInAsBusinessCustomer() }

    class MaxRoomConstraintEvent private constructor(override val message: String,
                                                     override val phoneNumber: String,
                                                     override val isGroupFormRequired: Boolean,
                                                     override val roomId: Int? = null) : DialogContentEvent {
        companion object {
            fun create(resource: GetStringResource, helper: RoomCriteriaHelper): MaxRoomConstraintEvent {
                val phoneNumber = resource(Key.GROUP_BOOKINGS_NUMBER)
                return create(
                        message = helper.getMaxRoomsErrorMessage(phoneNumber),
                        phoneNumber = resource(Key.GROUP_BOOKINGS_NUMBER),
                        isGroupFormRequired = helper.isMaxRoomsGroupFormRequired()
                )
            }

            fun create(message: String, phoneNumber: String, isGroupFormRequired: Boolean): MaxRoomConstraintEvent {
                return MaxRoomConstraintEvent(message = message, phoneNumber = phoneNumber, isGroupFormRequired = isGroupFormRequired)
            }
        }
    }
}