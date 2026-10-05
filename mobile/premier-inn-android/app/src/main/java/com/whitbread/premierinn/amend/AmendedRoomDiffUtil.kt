package com.whitbread.premierinn.amend

import androidx.recyclerview.widget.DiffUtil
import javax.inject.Inject

class AmendedRoomDiffUtil @Inject constructor() : DiffUtil.ItemCallback<AmendReservationState.AmendedRoom>() {
    override fun areItemsTheSame(
            oldItem: AmendReservationState.AmendedRoom,
            newItem: AmendReservationState.AmendedRoom
    ) = oldItem.roomId == newItem.roomId

    override fun areContentsTheSame(
            oldItem: AmendReservationState.AmendedRoom,
            newItem: AmendReservationState.AmendedRoom
    ) = oldItem == newItem
}