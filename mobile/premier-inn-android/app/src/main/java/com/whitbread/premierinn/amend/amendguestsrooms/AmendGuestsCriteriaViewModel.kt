package com.whitbread.premierinn.amend.amendguestsrooms

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ADDED_ROOM_INDEX_KEY
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ROOM_INDEX_KEY
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import javax.inject.Inject


@HiltViewModel
class AmendGuestsCriteriaViewModel @Inject constructor(
    getStringResource: GetStringResource,
    criteriaMessageProvider: RoomCriteriaHelper,
    private val savedStateHandle: SavedStateHandle,
    ) : BaseRoomCriteriaViewModel(getStringResource, criteriaMessageProvider, RoomCriteriaState
        .createFromCriteriaList(listOf(RoomCriteria.createWithDefaults().copy(roomNumber = savedStateHandle.get<Int>(ROOM_INDEX_KEY) ?: 0))))
{

    private val roomIndex: Int by lazy {
        savedStateHandle.get<Int>(ROOM_INDEX_KEY) ?: 0
    }
    private val addRoomIndex: Int by lazy {
        savedStateHandle.get<Int>(ADDED_ROOM_INDEX_KEY) ?: -1
    }

    fun displayCriteria(roomCriteria: RoomCriteria) {
        applyCriteria(roomIndex, roomCriteria)
    }

    fun displayCriteriaToNewRoomAdded() {
        if (addRoomIndex != -1) {
            applyCriteriaToNewRoomAdded()
        }
    }

    fun roomState(): Observable<RoomCriteria> {
        val index = if (addRoomIndex != -1) addRoomIndex else roomIndex
        return states().map { it.getRoom(index) }
    }

}