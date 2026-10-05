package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import com.whitbread.premierinn.summary.SummaryInput
import kotlinx.parcelize.Parcelize

@Parcelize
data class BathroomSelectionInput(val provisionalSummaryInput: SummaryInput,
                                  val parcelableRoom: List<ParcelableRoomOpera>,
                                  val parcelableAccessibleRoom: List<ParcelableRoomOpera>,
                                  val parcelableTwinRoom: List<ParcelableRoomOpera>,
                                  val roomTypesDomainList: List<ParcelableRoomType>): Parcelable
