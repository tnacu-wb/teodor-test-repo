package com.whitbread.premierinn

import com.adobe.marketing.mobile.AdobeCallback
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.Target
import com.adobe.marketing.mobile.target.TargetRequest
import com.akamai.botman.CYFMonitor
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.BaseApplication
import com.whitbread.premierinn.common.akamai.PIAkamaiHelper
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.dynatrace.DynatraceHelper.initDynatrace
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.persistence.AdobeABPersistenceManager
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.adobeAb.TwinRoomVariant
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.push.PushData
import dagger.hilt.android.HiltAndroidApp
import java.util.Objects
import javax.inject.Inject

@HiltAndroidApp
class PiApplication : BaseApplication() {
    @Inject lateinit var logService: LogService

    @Inject lateinit var analytics: TrackingAnalytics

    @Inject lateinit var persistenceManager: SimplePersistenceManager

    @Inject lateinit var isFeatureOn: IsFeatureOn
    @Inject lateinit var adobeManager: AdobeABPersistenceManager
    @Inject lateinit var appConfiguration: AppConfiguration

    override fun initSdks() {
        // Reset on every app launch - persists during session but always starts fresh on app start
        persistenceManager.setFreeBreakfastPromotionCode(EMPTY_STRING)

        initialiseAdobeABFramework()
        initialiseFCMKeys()
        initDynatrace()
        initialiseAkamaiSdk()
    }

    private fun initialiseFCMKeys() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener( { task: Task<String?>? ->
                if (!task!!.isSuccessful) {
                    logService.logException(
                        Objects.requireNonNull<Exception?>(task.getException()),
                        "Fetching FCM registration token failed"
                    )
                    return@addOnCompleteListener
                }
                val token: String = task.getResult()!!
                val pushData = PushData(token)

                persistenceManager.storeFirebaseToken(token)
                MobileCore.setPushIdentifier(token)
                analytics.trackAction(AnalyticsConstants.Action.PUSHID_ACTION, pushData)
            })
    }

    private fun initialiseAdobeABFramework() {
        val targetRequest = TargetRequest(
            getString(R.string.adobe_mbox_name),
            null,
            "",
            AdobeCallback { value: String? ->
                val gson = Gson()
                try {
                    val twinRoomVariant =
                        gson.fromJson(value, TwinRoomVariant::class.java)
                    if (twinRoomVariant != null) {
                        if (twinRoomVariant.ratesSeparated == getString(R.string.adobe_twin_room_variant)) {
                            adobeManager.setTwinRoomPreference(true)
                        }
                    }
                } catch (exception: JsonSyntaxException) {
                    logService.logException(
                        exception,
                        "JsonSyntaxException while reading Adobe AB variant"
                    )
                }
            })
        val requests: MutableList<TargetRequest?> = ArrayList()
        requests.add(targetRequest)
        Target.retrieveLocationContent(requests, null)
    }

    private fun initialiseAkamaiSdk() {
        CYFMonitor.enableBackground();
        // Verified this and it should be a graphql url
        val baseurl = appConfiguration.graphQLUrl;
        PIAkamaiHelper.initialiseSdkWithBaseurl(this, baseurl, analytics);
    }

}