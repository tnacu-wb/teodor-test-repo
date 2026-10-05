package com.whitbread.premierinn.common.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.contentSquare.CSQMaskingRegistryHelper
import javax.inject.Inject

/**
 * Base class for Jetpack Compose activities.
 * Provides back navigation tracking consistent with BaseActivity.
 */
abstract class BaseComponentActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: TrackingAnalytics

    @Inject
    lateinit var cSQMaskingRegistryHelper: CSQMaskingRegistryHelper

    private var shouldTrackOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onRestart() {
        super.onRestart()
        // Signal that we're returning from back stack
        shouldTrackOnResume = true
    }

    override fun onStop() {
        super.onStop()
        // Mark that we're leaving so returning via onResume (even without onRestart) will track
        shouldTrackOnResume = true
    }

    override fun onResume() {
        super.onResume()
        analytics.activityOnResume(this)

        // Track destination screen when returning from back navigation
        if (shouldTrackOnResume) {
            analytics.trackActivityBackNavigation(this)
            shouldTrackOnResume = false
        }
    }

    override fun onPause() {
        super.onPause()
        analytics.activityOnPause()
    }
}
