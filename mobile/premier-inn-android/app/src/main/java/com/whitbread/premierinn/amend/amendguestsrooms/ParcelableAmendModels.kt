package com.whitbread.premierinn.amend.amendguestsrooms

import android.os.Parcelable
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableAmendTotal(val title: String, val description: String, val amount: String)
    : Parcelable

@Parcelize
data class ParcelableGuest(val roomNumber: Int,
                           val roomId: String,
                           val title: String,
                           val firstName: String,
                           val lastName: String,
                           val guestHistoryNumber: String?,
                           val address: ParcelableAddress?,
                           val emailAddress: String?,
                           val phoneNumber: String?) : Parcelable