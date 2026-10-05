package com.whitbread.premierinn.hoteldetails

import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel

data class RatesDisplayInfo(val price: PriceDomain, val lettingType: String, val rateCode: String, val model: RateBoxUiModel) {
    fun getRoomVariantFromModel(): RoomVariant {
        return model.roomVariant()
    }

    fun getRoomClass(): String {
        return model.roomVariant().roomClass
    }
}