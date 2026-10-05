package com.whitbread.premierinn.ciol.uimodel

import android.content.res.Resources
import android.os.Parcelable
import com.whitbread.premierinn.R
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoBottomSheetData (
    val headerTitle: String = EMPTY_STRING,
    val headerSubTitle: String = EMPTY_STRING,
    val understoodMessage: String = EMPTY_STRING,
    val buttonText: String = EMPTY_STRING
) : Parcelable {
    companion object {
        fun startCheckInData(resources: Resources) = InfoBottomSheetData(
            resources.getString(R.string.understood_title),
            resources.getString(R.string.understood_sub_title),
            resources.getString(R.string.understood_message),
            resources.getString(R.string.understood_button_text)
        )

        fun showPibaMessageData(resources: Resources) = InfoBottomSheetData(
            resources.getString(R.string.piba_understood_title),
            resources.getString(R.string.understood_sub_title),
            resources.getString(R.string.piba_understood_message),
            resources.getString(R.string.confirm_checkin)
        )
    }
}
