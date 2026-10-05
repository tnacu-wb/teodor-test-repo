package com.whitbread.premierinn.api.response.availability

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Announcement(
        @SerializedName("showAnnouncement") val showAnnouncement: Boolean,
        @SerializedName("startDate") val startDate: String?,
        @SerializedName("endDate") val endDate: String?,
        @SerializedName("text") val message: String?
) : Parcelable