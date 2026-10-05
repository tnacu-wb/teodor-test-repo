package com.whitbread.premierinn.roomcriteria

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableRooms(val rooms: List<ParcelableRoomCriteria>) : Parcelable