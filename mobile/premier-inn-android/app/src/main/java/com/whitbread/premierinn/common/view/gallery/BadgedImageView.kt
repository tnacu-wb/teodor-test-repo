package com.whitbread.premierinn.common.view.gallery

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.databinding.ViewLabelledImageBinding

class BadgedImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var viewLabelledImageBinding: ViewLabelledImageBinding

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        viewLabelledImageBinding = ViewLabelledImageBinding.inflate(LayoutInflater.from(getContext()), this)
    }

    fun loadImage(imageUrl: String?, @DrawableRes placeHolder: Int) {
        viewLabelledImageBinding.image.loadNoAnimation(imageUrl, placeHolder)
    }

    fun putBadge(text: String, @ColorRes bgColor: Int) {
        viewLabelledImageBinding.roundel.text = text
        viewLabelledImageBinding.roundel.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, bgColor))
        viewLabelledImageBinding.roundel.visibility = View.VISIBLE
    }

    fun removeBadge() {
        viewLabelledImageBinding.roundel.visibility = View.GONE
    }
}