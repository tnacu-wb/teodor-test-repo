package com.whitbread.premierinn.common.akamai

import android.app.Application
import com.akamai.botman.CYFMonitor
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.api.response.ErrorBody
import com.whitbread.premierinn.common.analytics.TrackingAnalytics

object PIAkamaiHelper {

    fun initialiseSdkWithBaseurl(application: Application, baseUrl: String, analytics: TrackingAnalytics) {
        CYFMonitor.initializeSDK(application, baseUrl)
        CYFMonitor.setSDKInitCallBack(object : CYFMonitor.CYFSDKInitCallback {
            override fun onSDKInitSuccess() {
                if (BuildConfig.STAGING) {
                    CYFMonitor.setLogLevel(CYFMonitor.ERROR)
                }
            }

            override fun onSDKInitFailure(message: String) {
                analytics.trackError(ErrorBody.create(-1, "Error initializing Akamai BMP SDK: $message"))
            }
        })
    }

    fun sensorData(): String {
         return CYFMonitor.getSensorData()
    }

    fun runIntegrationTests() {
            CYFMonitor.runIntegrationChecks()
    }
}