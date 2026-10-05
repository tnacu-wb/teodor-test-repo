package com.whitbread.premierinn.common.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.GlideApp

class ShapeableNetworkImageView(context: Context, attributeSet: AttributeSet?) : ShapeableImageView(context, attributeSet) {

    fun load(url: String?) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.drawable.image_no_hotel)
                .error(R.drawable.image_no_hotel)
                .into(this)
    }
}