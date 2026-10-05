package com.whitbread.premierinn.roomcriteria

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.RoomType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableRoomCriteria(val numberOfAdults: Int,
                                  val numberOfChildren: Int,
                                  val numberOfInfants: Int,
                                  val includeCot: Boolean,
                                  val roomType: RoomType,
                                  val roomNumber: Int = 1,
                                  val roomId: String? = EMPTY_STRING,
                                  val hotelBrand: String? = EMPTY_STRING) : Parcelable