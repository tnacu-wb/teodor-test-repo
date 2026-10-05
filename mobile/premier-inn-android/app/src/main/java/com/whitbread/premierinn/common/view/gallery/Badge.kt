package com.whitbread.premierinn.common.view.gallery

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Badge(@JvmField val text: String, @JvmField @ColorRes val bgColor: Int) : Parcelable