package com.whitbread.premierinn.ciol.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class HotelPreferenceUiModel (
    val code: String,
    val preferenceGroup: String,
    val label: String
) : Parcelable
