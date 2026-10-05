package com.whitbread.premierinn.landing

import android.content.Context
import android.content.Intent
import com.whitbread.premierinn.landing.model.LandingInputModel

const val LANDING_BUNDLE = "landing_bundle_model"

object LandingActivityIntent {

    @JvmOverloads
    fun create(
        context: Context,
        bundle: LandingInputModel? = null
    ): Intent {
        return Intent(context, LandingActivity::class.java).apply {
            putExtra(LANDING_BUNDLE, bundle)
        }
    }
}