package com.whitbread.premierinn.threeCp

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.ciol.entity.AuthorizeCardWebViewResponse
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.threeCp.analytics.IPageAnalyticsData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val AUTHORIZATION_STATUS_FAILURE = "FAILURE"
private const val PAYMENT_STATUS_KEY = "paymentStatus"

@HiltViewModel
class ThreeCpViewModel @Inject constructor(
        private val resourceProvider: StringResourceProvider,
        private val getStringResource: GetStringResource,
        private val simplePersistenceManager: SimplePersistenceManager,
        private val logger: LogService,
        private val analytics: TrackingAnalytics
) : RxViewModelStore<ThreeCpViewStates, ThreeCpViewEvents>(ThreeCpViewStates()) {

    private lateinit var trackingData: Pair<String, String>

    fun onPrepareWebViewData(
        iPageHtml: String,
        data: Pair<String, String>) {
        trackingData = data
        when {
            iPageHtml.isEmpty() -> publish(ThreeCpViewEvents.GenericError(IllegalArgumentException(
                resourceProvider.emptyError("IPage content"))))
            else -> publish(ThreeCpViewEvents.Load3CPIPageEvent(
                    iPageHtml = iPageHtml
            ))
        }
    }

    fun onPrepareWebViewData(iPageHtml: String) {
        when {
            iPageHtml.isEmpty() -> publish(ThreeCpViewEvents.GenericError(IllegalArgumentException(
                resourceProvider.emptyError("IPage content"))))
            else -> publish(ThreeCpViewEvents.Load3CPIPageEvent(
                iPageHtml = iPageHtml
            ))
        }
    }

    private fun logUnexpectedError(throwable: Throwable) {
        logger.logInfo(
                throwable = throwable,
                tag = ThreeCpViewModel::class.java.canonicalName.orEmpty(),
                message = resourceProvider.unexpectedError)
    }

    fun prepareWebViewHtml(iPageHtml: String) {
        applyState(Reducer {
            it.copy(inFlight = false,
                    loadWebViewData = true,
                    webViewHtml = iPageHtml.fromBase64())
        })
    }

    fun triggerSuccess(successMsg: String) {
        applyState(Reducer { it.copy(message = successMsg) })
    }

    fun backPressedWithUrl(url: String) {
        applyState(Reducer { it.copy(urlOnBackPressed = url) })
    }

    fun triggerLoading(loading: Boolean) {
        when (loading) {
            true -> {
                applyState(Reducer { it.copy(inFlight = loading,
                    completeTrigger = getStringResource.invoke(ContentManagedResourceRepository.Key.IPAGE_COMPLETE_TRIGGER),
                    iPageLoadedTrigger = getStringResource.invoke(ContentManagedResourceRepository.Key.IPAGE_LOADED_TRIGGER),
                    iPagePaymentUrlTrigger = getStringResource.invoke(ContentManagedResourceRepository.Key.IPAGE_URL_PAYMENT_TEXT),
                    iPage3dsCompleteTrigger = getStringResource.invoke(ContentManagedResourceRepository.Key.IPAGE_3DS_COMPLETE_STRING),
                    iPageW2PNotificationTrigger = getStringResource.invoke(ContentManagedResourceRepository.Key.IPAGE_3DS_NOTIFICATION_TEXT))
                })
            }

            else -> {
                applyState(Reducer { it.copy(inFlight = loading, loadWebViewData = loading, iPageLoadedTrigger = EMPTY_STRING) })
            }
        }

    }

    fun triggerError(throwable: Throwable) {
        logUnexpectedError(throwable)
    }

    fun trackIPageData(loadTime: String, paymentCardSelected: String,
                       paymentTakenNow: String, threeCpInput: ThreeCpInput) {
        val pushToken = simplePersistenceManager.getFirebaseToken()
        val analyticData = IPageAnalyticsData(threeCpInput.bookingFlowInput.hotelCode(),
            threeCpInput.reviewBookingInput.guestHistoryNumber(),
            BuildConfig.FLAVOR, threeCpInput.isCustomerLoggedIn, AnalyticsConstants.Type.BOOKING_FLOW, threeCpInput.isBusinessCustomerLoggedIn,
            trackingData.first, trackingData.second, paymentCardSelected, paymentTakenNow, loadTime, pushToken)
        analytics.track(AnalyticsConstants.ScreenState.PAYMENT_3CP_IPAGE, analyticData);
    }

    fun handleWebViewEvents(event: String) {
        if (event.contains(PAYMENT_STATUS_KEY, true)) {
            try {
                publish(ThreeCpViewEvents.AuthorizeCardCompletedEvent(
                    authorizeCardWebviewResponse = Gson().fromJson(
                        JsonParser.parseString(event).asString,
                        AuthorizeCardWebViewResponse::class.java)
                ))
            } catch (e: JsonSyntaxException) {
                publish(ThreeCpViewEvents.AuthorizeCardCompletedEvent(
                    authorizeCardWebviewResponse = AuthorizeCardWebViewResponse(authorizationStatus = AUTHORIZATION_STATUS_FAILURE)
                ))
            }
        }
    }
}

