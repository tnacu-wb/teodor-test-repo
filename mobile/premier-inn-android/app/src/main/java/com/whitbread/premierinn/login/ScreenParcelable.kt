package com.whitbread.premierinn.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenParcelable(val screen: String, val trackingScreen: String) : Parcelable