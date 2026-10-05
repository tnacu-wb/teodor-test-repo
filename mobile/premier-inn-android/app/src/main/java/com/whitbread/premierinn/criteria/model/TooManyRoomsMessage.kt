package com.whitbread.premierinn.criteria.model

import android.os.Parcelable
import com.whitbread.premierinn.criteria.CallUsConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class TooManyRoomsMessage(val messageText: String, val callUsConfig: CallUsConfig): Parcelable