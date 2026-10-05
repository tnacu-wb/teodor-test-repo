package com.whitbread.premierinn.bathroomselection

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.common.view.InfoMessageBoxView
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PriceDomain
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

class BathroomOptionRowView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(ctx, attrs, defStyle) {

    private val onClickSubject: PublishSubject<Any> = PublishSubject.create()
    private lateinit var roomHeading: TextView
    private lateinit var roomDescription: TextView
    private lateinit var radioButton: ImageView
    private lateinit var roomPriceText: TextView
    private lateinit var warningMessageView: InfoMessageBoxView

    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_bathroom_choice, this, true)
        bindViews()
        setOnClickListener { onClickSubject.onNext(object {}) }
    }

    fun setData(
        heading: String,
        description: String,
        isSelected: Boolean,
        warningMessage: String? = null,
        roomPrice: PriceDomain,
        deviceLocaleProvider: DeviceLocaleProvider
    ) {
        roomHeading.text = heading
        roomDescription.text = description
        radioButton.isSelected = isSelected
        warningMessage?.let {
            warningMessageView.visibility = View.VISIBLE
            warningMessageView.setText(it)
        } ?: run {
            warningMessageView.visibility = View.GONE
        }
        roomPriceText.text  = context.resources.getString(R.string.room_total_price,
            PriceFormat.format(roomPrice, deviceLocaleProvider))
    }

    private fun bindViews() {
        roomHeading = bind<TextView>(R.id.bathroom_choice_heading).value
        roomDescription = bind<TextView>(R.id.bathroom_choice_description).value
        radioButton = bind<ImageView>(R.id.bathroom_choice_radio_button).value
        warningMessageView = bind<InfoMessageBoxView>(R.id.bathroom_choice_warning_message).value
        roomPriceText = bind<TextView>(R.id.bathroom_choice_price).value
    }

    fun onClick(): Flowable<Any> {
        return onClickSubject.toFlowable(BackpressureStrategy.LATEST)
    }
}