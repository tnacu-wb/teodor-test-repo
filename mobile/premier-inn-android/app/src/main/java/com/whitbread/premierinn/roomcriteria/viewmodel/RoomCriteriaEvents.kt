package com.whitbread.premierinn.roomcriteria.viewmodel

import com.whitbread.premierinn.data.common.EMPTY_STRING

interface RoomCriteriaEvent {
    val roomId: Int?
}

interface DialogContentEvent : RoomCriteriaEvent {
    val title: String get() = EMPTY_STRING
    val message: String
    val phoneNumber: String
    val isGroupFormRequired: Boolean get() = false
}

data class SendSelectionEvent(val selection: RoomCriteriaState,
                              override val roomId: Int? = null) : RoomCriteriaEvent