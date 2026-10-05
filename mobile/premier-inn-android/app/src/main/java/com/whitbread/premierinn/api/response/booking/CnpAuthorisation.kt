package com.whitbread.premierinn.api.response.booking

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CnpAuthorisation(
    @SerializedName("dinnerAvailable") val dinnerAvailablePIBA: Boolean,
    @SerializedName("dinnerAvailableNonBa") val dinnerAvailableNotPIBA: Boolean
): Parcelable