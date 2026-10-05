package com.whitbread.premierinn.additionalinformation

import com.contentsquare.android.api.model.CustomVar
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.*
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.MY_PREMIER_INN
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString

data class AdditionalInformationAnalyticsData(val rateCode: String,
                                              val selectedRooms: List<RoomBooking>
): AnalyticsData {

    override fun contextData(): MutableMap<String, String> {

        return mutableMapOf(
            SCREEN_TYPE to MY_PREMIER_INN
        )
    }

    override fun customCSQVars(): List<CustomVar> {
        return listOf(
            CustomVar(7, KEY_RATE_CODE, rateCode),
            CustomVar(19, KEY_LETTING_TYPE, toColonSeparatedString(bookedLettingTypes())),
        )
    }

    private fun bookedLettingTypes(): MutableList<String?> {
        val roomLettingTypes: MutableList<String?> = ArrayList()
        for (bookingRoom in selectedRooms) {
            roomLettingTypes.add(bookingRoom.lettingCode)
        }
        return roomLettingTypes
    }
}
