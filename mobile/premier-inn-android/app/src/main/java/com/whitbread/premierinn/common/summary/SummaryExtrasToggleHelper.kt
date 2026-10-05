package com.whitbread.premierinn.common.summary

import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import javax.inject.Inject

class SummaryExtrasToggleHelper @Inject constructor () {

    fun updateExtraSelectedState(
        roomItem: SummaryRoomItem,
        extraIndex: Int,
        isSelected: Boolean
    ): SummaryRoomItem {
        val updatedExtras = roomItem.extras.mapIndexed { index, extra ->
            if (index == extraIndex) extra.copy(selected = isSelected)
            else extra
        }
        return roomItem.copy(extras = updatedExtras)
    }

}