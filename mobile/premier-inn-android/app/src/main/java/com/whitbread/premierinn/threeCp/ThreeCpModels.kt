package com.whitbread.premierinn.threeCp

import com.whitbread.premierinn.ciol.entity.AuthorizeCardWebViewResponse
import com.whitbread.premierinn.data.common.EMPTY_STRING

data class ThreeCpViewStates(
    val inFlight: Boolean = false,
    val webViewHtml: String = EMPTY_STRING,
    val sessionID: String? = null,
    val templateID: String? = null,
    val loadWebViewData: Boolean = false,
    val isStatusSuccess: Boolean? = null,
    val message: String = EMPTY_STRING,
    val completeTrigger: String = EMPTY_STRING,
    val iPageLoadedTrigger: String = EMPTY_STRING,
    val iPagePaymentUrlTrigger: String = EMPTY_STRING,
    val iPage3dsCompleteTrigger: String = EMPTY_STRING,
    val iPageW2PNotificationTrigger: String = EMPTY_STRING,
    val urlOnBackPressed: String = EMPTY_STRING
)

sealed class ThreeCpViewEvents {
    class Load3CPIPageEvent(
        val iPageHtml: String = EMPTY_STRING
    ) : ThreeCpViewEvents()

    data class GenericError(val error: Throwable?) : ThreeCpViewEvents()

    data class AuthorizeCardCompletedEvent(
        val authorizeCardWebviewResponse: AuthorizeCardWebViewResponse
    ) : ThreeCpViewEvents()
}
