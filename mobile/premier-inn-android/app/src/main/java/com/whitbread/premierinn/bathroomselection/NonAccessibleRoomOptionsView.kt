package com.whitbread.premierinn.bathroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.mapper.toPriceDomain
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider

class NonAccessibleRoomOptionsView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0): LinearLayout(ctx, attrs, defStyle) {

    private val headingView by bind<TextView>(R.id.roomHeading)
    private val subheadingView by bind<TextView>(R.id.roomOccupants)
    private val changeRoomText by bind<View>(R.id.bathroom_change_type)
    private val bathroomChangeOption by bind<View>(R.id.bathroom_change_type)
    private val tooltipOption by bind<View>(R.id.tooltip)
    private val roomSizeDescription by bind<TextView>(R.id.bathroom_room_size_description)
    private val roomPrice by bind<TextView>(R.id.noBathroomSelectionPrice)
    private val accessibleImage by bind<ImageView>(R.id.roomAccessibleImage)

    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_room_bathroom_nonaccessible, this, true)
    }

    fun setData(roomDataOpera: NonAccessibleRoom, deviceLocaleProvider: DeviceLocaleProvider) {
        headingView.text = resources.getString(R.string.room_number, roomDataOpera.roomId)
        subheadingView.text = roomDataOpera.occupantsSubheading
        val price = PriceFormat.format(roomDataOpera.totalCost.toPriceDomain(), deviceLocaleProvider)
        roomPrice.text = context.resources.getString(R.string.room_total_price, price)
        roomSizeDescription.text = roomDataOpera.roomSizeDescription
        hideAccessibleRoomOptions()
    }

    private fun hideAccessibleRoomOptions() {
        changeRoomText.visibility = View.GONE
        bathroomChangeOption.visibility = View.GONE
        tooltipOption.visibility = View.GONE
        accessibleImage.visibility = View.GONE
    }
}
