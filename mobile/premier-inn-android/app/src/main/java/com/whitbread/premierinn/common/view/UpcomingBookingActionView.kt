package com.whitbread.premierinn.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R

class UpcomingBookingActionView(context: Context, attributeSet: AttributeSet?) : ConstraintLayout(context, attributeSet) {

    private lateinit var upcomingBookingTv: TextView
    init {
        init(attributeSet)
    }

    private fun init(attributeSet: AttributeSet?) {
        inflate(context, R.layout.upcoming_booking_action, this)
        upcomingBookingTv = findViewById(R.id.upcoming_booking_action_text)
        bindCustomAttributes(attributeSet)
    }

    private fun bindCustomAttributes(attributeSet: AttributeSet?) {
        if (attributeSet == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.UpcomingBookingActionView)

        val backgroundRes = typedArray.getResourceId(R.styleable.UpcomingBookingActionView_android_background, 0)
        if (backgroundRes != 0) {
            setBackgroundResource(backgroundRes)
        }

        val textIdRes = typedArray.getResourceId(R.styleable.UpcomingBookingActionView_android_id, 0)
        if (textIdRes != 0) {
            setTextId(textIdRes)
        }

        val textRes = typedArray.getResourceId(R.styleable.UpcomingBookingActionView_android_text, 0)
        if (textRes != 0) {
            setText(textRes)
        }

        val textColorRes = typedArray.getResourceId(R.styleable.UpcomingBookingActionView_android_textColor, 0)
        if (textColorRes != 0) {
            setTextColorResource(textColorRes)
        }

        typedArray.recycle()
    }

    private fun setTextId(textIdRes: Int) {
        upcomingBookingTv.id = textIdRes
    }

    private fun setText(textRes: Int) {
        upcomingBookingTv.setText(textRes)
    }

    fun setText(text: String) {
        upcomingBookingTv.text = text
    }

    private fun setTextColorResource(@ColorRes textColorResource: Int) {
        upcomingBookingTv.setTextColor(ContextCompat.getColor(context, textColorResource))
    }
}