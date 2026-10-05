package com.whitbread.premierinn.common.view

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.whitbread.premierinn.common.utils.AnimationUtils
import com.whitbread.premierinn.databinding.ViewRateLoadingBinding

class RateLoadingView: RelativeLayout {

    lateinit var binding: ViewRateLoadingBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        binding = ViewRateLoadingBinding.inflate(LayoutInflater.from(context), this);

        val animation = AnimatorSet()
        animation.playTogether(
                AnimationUtils.alphaInfiniteRepeat(binding.rateNameLoadingField),
                AnimationUtils.alphaInfiniteRepeat(binding.ratePriceLoadingField),
                AnimationUtils.alphaInfiniteRepeat(binding.rateDetailsLoadingField)
        )
        animation.startDelay = 500
        animation.start()
    }
}