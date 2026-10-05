package com.whitbread.premierinn.common.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.contentSquare.CSQMaskingRegistryHelper
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    lateinit var cSQMaskingRegistryHelper: CSQMaskingRegistryHelper

    @Inject
    lateinit var analytics: TrackingAnalytics

    private var shouldTrackOnResume = false
    private var firstResumeHandled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cSQMaskingRegistryHelper = CSQMaskingRegistryHelper()
        cSQMaskingRegistryHelper.maskRegisteredViews(this)
        // No flag reset here; we only skip tracking on first visible resume
    }

    override fun onResume() {
        super.onResume()

        // Skip tracking on the first ever resume; track subsequent returns
        if (!firstResumeHandled) {
            firstResumeHandled = true
            return
        }

        if (shouldTrackOnResume) {
            analytics.trackFragmentBackNavigation(this)

            shouldTrackOnResume = false
        }
    }

    override fun onPause() {
        super.onPause()
        // Mark that we might be returning
        shouldTrackOnResume = true
    }

}