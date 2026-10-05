package com.whitbread.premierinn.roomcriteria.viewmodel

import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.roomcriteria.adultsDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.adultsIncrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.childrenDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.childrenIncrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.cotAllowed
import com.whitbread.premierinn.domain.roomcriteria.infantsDecrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.infantsIncrementAllowed
import com.whitbread.premierinn.domain.roomcriteria.roomTypeChangeAllowed
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import javax.inject.Inject

open class BaseRoomCriteriaViewModel @Inject constructor(
    private val getStringResource: GetStringResource,
    private val criteriaMessageProvider: RoomCriteriaHelper,
    initState: RoomCriteriaState)
    : RxViewModelStore<RoomCriteriaState, RoomCriteriaEvent>(initState) {

    fun applyCriteria(roomId: Int, roomCriteria: RoomCriteria) {
        applyState(Reducer { state -> state.copyWithUpdatedRoom(roomId, roomCriteria) })
    }

    fun applyCriteriaToNewRoomAdded() {
        applyState(Reducer { state -> state.copyWithRoomAdded() })
    }

    fun onAdultsNumberIncreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (adultsIncrementAllowed()) {
            applyState(Reducer { state -> state.copyWithAdultsIncremented(roomId) })
        } else publish(MaxAdultConstraintEvent(roomId))
    }

    fun onAdultsNumberDecreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (adultsDecrementAllowed()) {
            applyState(Reducer { state -> state.copyWithAdultsDecremented(roomId) })
        }
    }

    fun onChildrenNumberIncreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (childrenIncrementAllowed()) {
            applyState(Reducer { state -> state.copyWithChildrenIncremented(roomId) })
        } else publish(MaxChildrenConstraintEvent(roomId))
    }

    fun onChildrenNumberDecreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (childrenDecrementAllowed()) {
            applyState(Reducer { state -> state.copyWithChildrenDecremented(roomId) })
        }
    }

    fun onInfantsNumberIncreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (infantsIncrementAllowed()) {
            applyState(Reducer { state -> state.copyWithInfantsIncremented(roomId) })
        } else publish(MaxInfantsConstraint.create(
                roomId = roomId,
                title = criteriaMessageProvider.maxInfantsErrorTitle,
                message = criteriaMessageProvider.getMaxInfantsErrorMessage(getStringResource(Key.NON_CHARGEABLE_PHONE_DESC).lowercase()),
                phoneNumber = getStringResource(Key.GROUP_BOOKINGS_NUMBER)
        ))
    }

    fun onInfantsNumberDecreased(roomId: Int) = with(currentState().getRoom(roomId)) {
        if (infantsDecrementAllowed()) {
            applyState(Reducer { state -> state.copyWithInfantsDecremented(roomId) })
        }
    }

    fun onCotEnabled(roomId: Int, enabled: Boolean) = with(currentState().getRoom(roomId)) {
        if (enabled && !cotAllowed()) {
            publish(AccessibleCotConstraint.create(
                    roomId = roomId,
                    title = criteriaMessageProvider.accessibleCotTitle,
                    message = criteriaMessageProvider.getAccessibleCotMessage(getStringResource(Key.NON_CHARGEABLE_PHONE_DESC).lowercase()),
                    phoneNumber = getStringResource(Key.GROUP_BOOKINGS_NUMBER)
            ))
        } else {
            applyState(Reducer { state -> state.copyWithCotToggled(roomId, enabled) })
        }
    }

    fun onRoomTypeChanged(roomId: Int, roomType: RoomType) = with(currentState().getRoom(roomId)) {
        if (roomTypeChangeAllowed(roomType)) {
            applyState(Reducer { state -> state.copyWithRoomTypeChanged(roomId, roomType) })
        }
    }

    data class MaxAdultConstraintEvent(override val roomId: Int) : RoomCriteriaEvent
    data class MaxChildrenConstraintEvent(override val roomId: Int) : RoomCriteriaEvent

    class MaxInfantsConstraint private constructor(override val title: String,
                                                   override val message: String,
                                                   override val phoneNumber: String,
                                                   override val roomId: Int) : DialogContentEvent {
        companion object {
            fun create(title: String, message: String, phoneNumber: String, roomId: Int): MaxInfantsConstraint {
                return MaxInfantsConstraint(title = title, message = message, phoneNumber = phoneNumber, roomId = roomId)
            }
        }
    }

    class AccessibleCotConstraint private constructor(override val title: String,
                                                      override val message: String,
                                                      override val phoneNumber: String,
                                                      override val roomId: Int?) : DialogContentEvent {
        companion object {
            fun create(title: String, message: String, phoneNumber: String, roomId: Int): AccessibleCotConstraint {
                return AccessibleCotConstraint(title = title, message = message, phoneNumber = phoneNumber, roomId = roomId)
            }
        }
    }
}