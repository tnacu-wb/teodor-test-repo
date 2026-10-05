package com.whitbread.premierinn.criteria

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NumberSelectorMaxNumberInfo(val message: String?, val isMessageInfo: Boolean, val callUsConfig: CallUsConfig?): Parcelable

@Parcelize
data class CallUsConfig(val phoneNumber: String, val callChargesMessage: String): Parcelable