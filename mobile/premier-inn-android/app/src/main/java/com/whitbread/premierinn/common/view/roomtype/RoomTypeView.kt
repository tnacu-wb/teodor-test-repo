package com.whitbread.premierinn.common.view.roomtype

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.ViewRoomOptionBinding

class RoomTypeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewRoomOptionBinding: ViewRoomOptionBinding =
        ViewRoomOptionBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            applyAttributes(it)
        }
        isClickable = true
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoomTypeView)

        setOptionIcon(typedArray.getResourceId(R.styleable.RoomTypeView_icon, 0))
        setOptionIconColor(typedArray.getResourceId(R.styleable.RoomTypeView_iconColor, 0))
        setOptionName(typedArray.getResourceId(R.styleable.RoomTypeView_name, 0))
        setOptionDescription(typedArray.getResourceId(R.styleable.RoomTypeView_description, 0))
        setOptionTextColor(typedArray.getResourceId(R.styleable.RoomTypeView_foregroundColor, 0))
        setOptionBackgroundDrawable(typedArray.getResourceId(R.styleable.RoomTypeView_backgroundDrawable, 0))

        typedArray.recycle()
    }

    private fun setOptionIcon(@DrawableRes drawableRes: Int) {
        if (drawableRes != 0) {
            viewRoomOptionBinding.icon.setImageResource(drawableRes)
        }
    }

    private fun setOptionName(@StringRes stringRes: Int) {
        if (stringRes != 0) {
            viewRoomOptionBinding.name.setText(stringRes)
        }
    }

    private fun setOptionDescription(@StringRes stringRes: Int) {
        if (stringRes != 0) {
            viewRoomOptionBinding.description.setText(stringRes)
        }
    }

    fun setOptionTextColor(@ColorRes colorRes: Int) {
        if (colorRes != 0) {
            val foregroundColorValue = ContextCompat.getColor(context, colorRes)
            viewRoomOptionBinding.name.setTextColor(foregroundColorValue)
            viewRoomOptionBinding.description.setTextColor(foregroundColorValue)
        }
    }

    fun setOptionIconColor(@ColorRes colorRes: Int) {
        if (colorRes != 0) {
            val foregroundColorValue = ContextCompat.getColor(context, colorRes)
            viewRoomOptionBinding.icon.setColorFilter(foregroundColorValue)
        }
    }

    fun setOptionBackgroundDrawable(@DrawableRes drawableRes: Int) {
        if (drawableRes != 0) {
            setBackgroundResource(drawableRes)
        }
    }
}