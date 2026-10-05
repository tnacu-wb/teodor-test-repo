package com.whitbread.premierinn.bathroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding3.view.clicks
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.mapper.toPriceDomain
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.common.PriceDomain
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class AccessibleRoomOptionsView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0): LinearLayout(ctx, attrs, defStyle) {

    private val headingView by bind<TextView>(R.id.roomHeading)
    private val subheadingView by bind<TextView>(R.id.roomOccupants)
    private val toolTip by bind<TextView>(R.id.tooltip)
    private val roomSizeDescription by bind<TextView>(R.id.bathroom_room_size_description)
    private val bathroomChoice1 by bind<BathroomOptionRowView>(R.id.bathroomChoice1)
    private val bathroomChoice2 by bind<BathroomOptionRowView>(R.id.bathroomChoice2)
    private val bathroomSeparator by bind<View>(R.id.bathroomSeparator)
    private val sizeChangeView by bind<View>(R.id.bathroom_change_type)

    private var roomData: AccessibleRoomChoices? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_room_bathroom_options, this, true)
    }

    /**
     * When called for the first time, will create the necessary subviews. When called any subsequent time,
     * it assumes that the number of rooms has not changed and therefore will just update existing views.
     */
    fun setData(roomData: AccessibleRoomChoices,
                deviceLocaleProvider: DeviceLocaleProvider) {
        this.roomData = roomData
        headingView.text = resources.getString(R.string.room_number, roomData.roomId)
        subheadingView.text = roomData.occupantsSubheading
        roomSizeDescription.text = roomData.roomSizeDescription
        sizeChangeView.visibility = if (roomData.showSizeChangeButton) View.VISIBLE else View.GONE
        roomData.toolTipText?.let {
            toolTip.text = it
            toolTip.visibility = View.VISIBLE
        } ?: run {
            toolTip.visibility = View.GONE
        }
        displayBathroomChoiceInView(roomData.firstBathroomOption, bathroomChoice1,
            roomData.totalCostFirst.toPriceDomain(), deviceLocaleProvider)
        if (roomData.secondBathroomOption == null) {
            bathroomChoice2.visibility = View.GONE
            bathroomSeparator.visibility = View.GONE
        } else {
            bathroomChoice2.visibility = View.VISIBLE
            bathroomSeparator.visibility = View.VISIBLE
            displayBathroomChoiceInView(
                roomData.secondBathroomOption,
                bathroomChoice2,
                roomData.totalCostSecond.toPriceDomain(),
                deviceLocaleProvider
            )
        }
    }

    fun onBathroomClicked(): Flowable<BathroomType> {
        val bathroom1Clicks = Flowable.defer { bathroomChoice1.onClick() }.map { roomData!!.firstBathroomOption.bathroomType }
        val bathroom2Clicks = Flowable.defer { bathroomChoice2.onClick() }.map { roomData!!.secondBathroomOption!!.bathroomType }

        return bathroom1Clicks.mergeWith(bathroom2Clicks)
    }

    fun onChangeClicked(): Flowable<Unit> {
        return sizeChangeView.clicks().toFlowable(BackpressureStrategy.DROP)
    }

    private fun displayBathroomChoiceInView(
        bathroomItem: BathroomOption,
        view: BathroomOptionRowView,
        roomPrice: PriceDomain,
        deviceLocaleProvider: DeviceLocaleProvider
    ) {
        view.setData(bathroomItem.bathroomName, bathroomItem.bathroomDescription,
            bathroomItem.isBathroomSelected, bathroomItem.warningMessage, roomPrice, deviceLocaleProvider)
    }

    data class BathroomClickEvent(val roomId: Int, val bathroomType: BathroomType)
}
