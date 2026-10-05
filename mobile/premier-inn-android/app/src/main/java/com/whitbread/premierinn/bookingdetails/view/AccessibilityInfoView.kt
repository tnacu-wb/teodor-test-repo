package com.whitbread.premierinn.bookingdetails.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.common.utils.bind

class AccessibilityInfoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var telephoneNumber: String

    private val descriptionText by bind<TextView>(R.id.booking_details_accessibility_desc)
    private val callButton = bind<TextView>(R.id.booking_details_accessibility_button)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_accessibility_booking_info, this, true)
        callButton.value.setOnClickListener {
            val callIntent = IntentUtils.createTelephoneIntent(telephoneNumber)
            if (IntentUtils.checkIntentResolvedActivity(getContext(), callIntent)) {
                getContext().startActivity(callIntent)
            } else {
                Toast.makeText(getContext().applicationContext, R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setTelephoneNumber(number: String) {
        telephoneNumber = number
    }

    fun setDescriptionText(text: String) {
        descriptionText.text = text
    }

    fun setButtonText(buttonText: String) {
        callButton.value.text = buttonText
    }
}