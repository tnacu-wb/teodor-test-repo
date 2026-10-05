package com.whitbread.premierinn.common.dynatrace

import com.dynatrace.android.agent.DTXAction
import com.dynatrace.android.agent.Dynatrace
import com.dynatrace.android.agent.conf.DataCollectionLevel
import com.dynatrace.android.agent.conf.UserPrivacyOptions

object DynatraceHelper {

    fun initDynatrace() {
        Dynatrace.applyUserPrivacyOptions(
            UserPrivacyOptions.builder()
                .withDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
                .withCrashReportingOptedIn(false)
                .withCrashReplayOptedIn(false)
                .build()
        )
    }

    fun trackAction(double: Double, actionName: String, actionKey: String) {
        val trackDouble: DTXAction? = Dynatrace.enterAction(actionName)
        trackDouble?.reportValue(actionKey, double)
        trackDouble?.leaveAction()
    }
}