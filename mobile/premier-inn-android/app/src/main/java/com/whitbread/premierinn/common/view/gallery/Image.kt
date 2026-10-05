package com.whitbread.premierinn.common.view.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(@JvmField val imageUrl: String, @JvmField val badge: Badge?) : Parcelable
