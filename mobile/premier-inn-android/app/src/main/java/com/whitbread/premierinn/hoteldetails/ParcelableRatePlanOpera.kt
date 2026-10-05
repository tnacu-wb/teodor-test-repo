package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import com.whitbread.premierinn.common.ParcelableDailyRate
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.domain.common.RoomType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableRatePlanOpera(val code: String,
                                   val rateType: String,
                                   val cellCode: String,
                                   val totalCost: ParcelablePrice,
                                   val cityTax: ParcelablePrice?,
                                   val roomList: List<ParcelableRoomOpera>,
                                   val alternateRoomList:List<ParcelableRoomOpera>?,
                                   val accessibleRoomList:List<ParcelableRoomOpera>?,
                                   val twinRoomList: List<ParcelableRoomOpera>?) : Parcelable

@Parcelize
data class ParcelableRoomOpera(val number: Int, val type: RoomType, val cost: ParcelablePrice,
                               val cityTax: ParcelablePrice?, val lettingType: String, val roomClass: String,
                               val adults: Int, val children: Int, val cot: Boolean, val dailyRates: List<ParcelableDailyRate>,
                               val baseRateAmount: Float?, val specialRequests: List<String>?) : Parcelable

@Parcelize
data class ParcelableRoomType(
    val adults: Int,
    val children: Int,
    val cotRequested: Boolean,
    val roomType: String,
    val roomsDomainList: List<ParcelableRoomOptions>) : Parcelable

@Parcelize
data class ParcelableRoomOptions(
    val pmsRoomType: String,
    val roomClass: String,
    val specialRequests: List<String>?,
    val packageCode: String?,
    val packageAmount: Double?): Parcelable
