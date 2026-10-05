package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class AllowedBrandsInfo(
    @SerializedName("allowedBrands") val allowedBrands: List<String>
)