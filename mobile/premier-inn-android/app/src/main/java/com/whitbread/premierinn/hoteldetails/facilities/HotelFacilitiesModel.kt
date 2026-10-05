package com.whitbread.premierinn.hoteldetails.facilities

import android.os.Parcelable
import com.whitbread.premierinn.api.response.availability.Facility
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotelFacilitiesModel(val hotelFacilities: List<Facility>,
                                val roomFeatures: List<RoomFeaturesModel>) : Parcelable
