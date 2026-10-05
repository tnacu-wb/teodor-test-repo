package com.whitbread.premierinn.hoteldetails.roomvariantdetails

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.hotel.entity.Hotel

data class RoomVariantDetailsState(
        val deviceLocaleProvider: DeviceLocaleProvider,
        val result: AsyncResult<List<Hotel.RoomVariantDetails>>? = null) {

    val contentList: List<Hotel.RoomVariantDetails>?
        get() {
            return if (result is AsyncResult.Success) {
                result.data
            } else null
        }

    val language: String = deviceLocaleProvider.getDeviceLanguage()

}