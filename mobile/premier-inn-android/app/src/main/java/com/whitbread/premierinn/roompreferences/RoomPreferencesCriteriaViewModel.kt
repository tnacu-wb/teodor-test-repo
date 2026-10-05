package com.whitbread.premierinn.roompreferences

import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import javax.inject.Inject

private val roomIndex: Int = 1
@HiltViewModel
class RoomPreferencesCriteriaViewModel @Inject constructor(
    getStringResource: GetStringResource,
    criteriaMessageProvider: RoomCriteriaHelper
) : BaseRoomCriteriaViewModel(getStringResource, criteriaMessageProvider, RoomCriteriaState
        .createFromCriteriaList(listOf(RoomCriteria.createWithDefaults().copy(roomNumber = roomIndex)))) {

    fun displayCriteria(roomCriteria: RoomCriteria) {
        applyCriteria(roomIndex, roomCriteria)
    }

    fun roomState(): Observable<RoomCriteria> {
        val index = roomIndex
        return states().map { it.getRoom(index) }
    }

}