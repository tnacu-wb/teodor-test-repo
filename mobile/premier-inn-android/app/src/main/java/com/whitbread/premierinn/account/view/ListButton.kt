package com.whitbread.premierinn.account.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R

class ListButton @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_list_button, this, true)

        attrs?.let {
            bindViewDataFromXmlAttrs(it, view)
        }
    }

    private fun bindViewDataFromXmlAttrs(attrs: AttributeSet, view: View) {
        val buttonText = view.findViewById<TextView>(R.id.button_text)
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ListButton)

        buttonText.text = typedArray.getString(R.styleable.ListButton_text).orEmpty()

        val defaultColor = ContextCompat.getColor(view.context, R.color.grey_dark)
        buttonText.setTextColor(typedArray.getColor(R.styleable.ListButton_textColor, defaultColor))

        typedArray.recycle()
    }
}