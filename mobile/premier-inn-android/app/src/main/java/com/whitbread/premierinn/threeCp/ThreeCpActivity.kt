package com.whitbread.premierinn.threeCp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.EXTRA_CARD_TYPE
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT_AMEND
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT_AUTHORIZE_CARD
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT_SAVE_CARD
import com.whitbread.premierinn.common.EXTRA_THREE_C_P
import com.whitbread.premierinn.common.EXTRA_THREE_C_P_AMEND_RESULT
import com.whitbread.premierinn.common.EXTRA_TRANSACTION_ID
import com.whitbread.premierinn.common.PAYMENT_SUCCESS
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.utils.argument
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ActivityThreeCpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThreeCpActivity : BaseActivity<ActivityThreeCpBinding>() {

    private var isActivityChangingConfigurations = false
    private var startingTime: Long = 0
    private var stopTime: Long = 0
    private var isIPageLoading = false
    private var isSuccessMessage: Boolean? = null
    private var cardType : String = EMPTY_STRING
    private var transactionId : String = EMPTY_STRING

    private var iPageCompleteTrigger = EMPTY_STRING
    private var iPageLoadedTrigger = EMPTY_STRING
    private var iPageW2PNotificationTrigger: String = EMPTY_STRING
    private var iPage3dsCompleteTrigger: String = EMPTY_STRING
    private var iPagePaymentUrlTrigger: String = EMPTY_STRING
    private var urlOnBackPressed: String = EMPTY_STRING

    companion object {
        const val AUTHORIZE_CARD_RESPONSE_EXTRA = "AUTHORIZE_CARD_RESULT_EXTRA"
        private const val PAYMENT_CARD_SELECTED = "PAYMENT_CARD_SELECTED"
        private const val PAYMENT_TAKEN_NOW = "PAYMENT_TAKEN_NOW"
        private const val IS_AMEND_FLOW = "IS_AMEND_FLOW"
        private const val IS_PAY_AND_CHECK_IN_FLOW = "IS_PAY_AND_CHECK_IN_FLOW"
        private const val IS_PAY_AND_CHECK_IN_FOR_REG_CARD = "IS_PAY_AND_CHECK_IN_FOR_REG_CARD"
        private const val IS_AUTHORIZE_CARD_FLOW = "IS_AUTHORIZE_CARD_FLOW"
        private const val IS_SAVE_CARD_FLOW = "IS_SAVE_CARD_FLOW"
        private const val SHOULD_SHOW_INFO_BANNER = "SHOULD_SHOW_INFO_BANNER"
        private const val CARD_TYPE_QUERY_PARAM = "CardType"
        private const val TRANSACTION_ID_QUERY_PARAM = "TxID"
        private const val JS_EVENT_LISTENER_SCRIPT = """javascript:(function() {
                    parent.addEventListener('message', event => {
                        Android3CP.receiveMessage(JSON.stringify(event.data));});
                    })();"""
        @JvmStatic
        fun Context.createThreeCpIntent(input: ThreeCpInput,
                                        paymentCardSelected: String,
                                        paymentTakenNow: String): Intent {
            return Intent(this, ThreeCpActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT, input)
                putExtra(PAYMENT_CARD_SELECTED,paymentCardSelected)
                putExtra(PAYMENT_TAKEN_NOW,paymentTakenNow)
            }
        }

        @JvmStatic
        fun createThreeCpIntentAmend(context: Context, input: ThreeCpInputAmend): Intent {
            return Intent(context, ThreeCpActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT_AMEND, input)
                putExtra(IS_AMEND_FLOW, true)
            }
        }

        @JvmStatic
        fun createThreeCpIntentSaveCard(context: Context, input: ThreeCpInputSaveCard): Intent {
            return Intent(context, ThreeCpActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT_SAVE_CARD, input)
                putExtra(IS_SAVE_CARD_FLOW, true)
            }
        }

        @JvmStatic
        fun createThreeCpIntentPayAndCheckIn(context: Context, input: ThreeCpInputPayAndCheckIn, isForRegCard: Boolean, shouldShowInfoBanner: Boolean): Intent {
            return Intent(context, ThreeCpActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN, input)
                putExtra(IS_PAY_AND_CHECK_IN_FLOW, true)
                putExtra(IS_PAY_AND_CHECK_IN_FOR_REG_CARD, isForRegCard)
                putExtra(SHOULD_SHOW_INFO_BANNER, shouldShowInfoBanner)
            }
        }

        @JvmStatic
        fun createThreeCpIntentAuthorizeCard(context: Context, input: ThreeCpInputAuthorizeCard): Intent {
            return Intent(context, ThreeCpActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT_AUTHORIZE_CARD, input)
                putExtra(IS_AUTHORIZE_CARD_FLOW, true)
                putExtra(SHOULD_SHOW_INFO_BANNER, true)
            }
        }
    }

    private val intentInput by argument<ThreeCpInput>(EXTRA_THREE_CP_INPUT)
    private val intentInputAmend by argument<ThreeCpInputAmend>(EXTRA_THREE_CP_INPUT_AMEND)
    private val intentInputPayAndCheckIn by argument<ThreeCpInputPayAndCheckIn>(EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN)
    private val intentInputAuthorizeCard by argument<ThreeCpInputAuthorizeCard>(EXTRA_THREE_CP_INPUT_AUTHORIZE_CARD)
    private val intentInputSaveCard by argument<ThreeCpInputSaveCard>(EXTRA_THREE_CP_INPUT_SAVE_CARD)
    private val isAmend by lazy { intent.getBooleanExtra(IS_AMEND_FLOW, false) }
    private val isPayAndCheckIn by lazy { intent.getBooleanExtra(IS_PAY_AND_CHECK_IN_FLOW, false) }
    private val isPayAndCheckInForRegCard by lazy { intent.getBooleanExtra(IS_PAY_AND_CHECK_IN_FOR_REG_CARD, false) }
    private val isAuthorizeCard by lazy { intent.getBooleanExtra(IS_AUTHORIZE_CARD_FLOW, false) }
    private val isSaveCard by lazy { intent.getBooleanExtra(IS_SAVE_CARD_FLOW, false) }
    private val shouldShowInfoBanner by lazy { intent.getBooleanExtra(SHOULD_SHOW_INFO_BANNER, false) }
    private val paymentCardSelected by argument<String>(PAYMENT_CARD_SELECTED)
    private val paymentTakenNow by argument<String>(PAYMENT_TAKEN_NOW)
    private val autoCompositeDisposable: AutoCompositeDisposable by lazy {
        AutoCompositeDisposable(
            lifecycle
        )
    }

    private val viewModel: ThreeCpViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        if (!isActivityChangingConfigurations) {
            startingTime = System.currentTimeMillis()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        isActivityChangingConfigurations = this.isChangingConfigurations
    }

    private fun calculateStopTime(): String {
        if (!isActivityChangingConfigurations) {
            stopTime = System.currentTimeMillis()
        }

        val elapsedTime = stopTime.minus(startingTime)
        return String.format("%.3f", elapsedTime.div(1000.0))
    }

    override fun inflateBinding(inflater: LayoutInflater): ActivityThreeCpBinding {
        return ActivityThreeCpBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthorizeCard) {
            setTheme(R.style.AppTheme_CheckInOnline)
        }

        initToolbar()

        setupInfoBanner()

        initWebView()

        viewModel.events()
            .subscribe {
                when (it) {
                    is ThreeCpViewEvents.GenericError -> {
                        showToast(it.error?.localizedMessage ?: "Payment Error")
                        finish()
                    }

                    is ThreeCpViewEvents.Load3CPIPageEvent -> {
                        viewModel.prepareWebViewHtml(
                            iPageHtml = it.iPageHtml
                        )
                    }

                    is ThreeCpViewEvents.AuthorizeCardCompletedEvent -> {
                        val resultIntent = Intent().apply {
                            putExtra(AUTHORIZE_CARD_RESPONSE_EXTRA, it.authorizeCardWebviewResponse)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }.addTo(autoCompositeDisposable)

        viewModel.states()
            .distinctUntilChanged()
            .subscribe(::render)
            .addTo(autoCompositeDisposable)


        if (isAmend) {
            intentInputAmend.run {
                viewModel.onPrepareWebViewData(iPageHtml)
            }
        } else if (isPayAndCheckIn) {
            intentInputPayAndCheckIn.run {
                viewModel.onPrepareWebViewData(iPageHtml)
            }
        } else if (isAuthorizeCard) {
            intentInputAuthorizeCard.run {
                viewModel.onPrepareWebViewData(iPageHtml)
            }
        } else if (isSaveCard) {
            intentInputSaveCard.run {
                viewModel.onPrepareWebViewData(iPageHtml)
            }
        }  else {
            intentInput.run {
                viewModel.onPrepareWebViewData(
                    iPageHtml, Pair(sessionID, template)
                )
            }
        }
    }

    override fun onBackPressed() {
        // There are 3 urls which signal the payment processing
        // service.aspx complete3ds.aspx W2PNotification.aspx
        // reversing this will not work hence i have left the body as it is
        if (urlOnBackPressed.contains(iPagePaymentUrlTrigger) ||
            urlOnBackPressed.contains(iPage3dsCompleteTrigger) ||
            urlOnBackPressed.contains(iPageW2PNotificationTrigger)) {
            // Do nothing
        } else {
                isSuccessMessage?.let { paymentComplete ->
                    when (paymentComplete) {
                        true -> {
                            if (isAmend || isPayAndCheckIn || isAuthorizeCard || isSaveCard) {
                                val intentForAmend = Intent(this, ThreeCpActivity::class.java).apply {
                                    putExtra(EXTRA_THREE_C_P_AMEND_RESULT, PAYMENT_SUCCESS)
                                    if (isPayAndCheckInForRegCard) {
                                        putExtra(EXTRA_TRANSACTION_ID, transactionId)
                                    }
                                }
                                setResult(Activity.RESULT_OK, intentForAmend)
                            } else {
                                val intent = Intent(this, ThreeCpActivity::class.java).apply {
                                    putExtra(EXTRA_THREE_C_P, PAYMENT_SUCCESS)
                                }
                                setResult(Activity.RESULT_OK, intent)
                            }
                        }
                        false -> {
                            setResult(Activity.RESULT_OK)
                        }
                    }
                }
                    setResult(Activity.RESULT_CANCELED)

                super.onBackPressed()
        }
    }

    fun render(state: ThreeCpViewStates) {

        if (state.loadWebViewData) {
            binding.webview.loadDataWithBaseURL("", state.webViewHtml!!, "text/html", "UTF-8", "")
        }

        if (state.message.isNotEmpty()) {
            if (isAmend || isPayAndCheckIn || isAuthorizeCard || isSaveCard) {
                val intentForAmend = Intent(this, ThreeCpActivity::class.java).apply {
                    putExtra(EXTRA_THREE_C_P_AMEND_RESULT, state.message)
                    putExtra(EXTRA_CARD_TYPE, cardType)
                    if (isPayAndCheckInForRegCard) {
                        putExtra(EXTRA_TRANSACTION_ID, transactionId)
                    }
                }

                setResult(Activity.RESULT_OK, intentForAmend)
                finish()
            } else {
                val intentForBookingFlow = Intent(this, ThreeCpActivity::class.java).apply {
                    putExtra(EXTRA_THREE_C_P, state.message)
                    putExtra(EXTRA_CARD_TYPE, cardType)
                }

                setResult(Activity.RESULT_OK, intentForBookingFlow)
                finish()
            }
        }

        isIPageLoading = state.inFlight
        urlOnBackPressed = state.urlOnBackPressed
        iPagePaymentUrlTrigger = state.iPagePaymentUrlTrigger
        iPage3dsCompleteTrigger = state.iPage3dsCompleteTrigger
        iPageW2PNotificationTrigger = state.iPageW2PNotificationTrigger

        if (isIPageLoading) {
            iPageCompleteTrigger = state.completeTrigger
            iPageLoadedTrigger = state.iPageLoadedTrigger
        }
    }

    private fun initToolbar() {
        if (isAuthorizeCard || isPayAndCheckInForRegCard) {
            binding.toolbarRegCard.visibility = View.VISIBLE
            if (isPayAndCheckInForRegCard) {
                binding.toolbarTitle.text = getString(R.string.pay_and_check_in_toolbar_title)
            }
            setSupportActionBar(binding.toolbarRegCard)

            supportActionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_chevron_left
                )
            )

            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setDisplayShowTitleEnabled(false)
            }
        } else {
            binding.toolbar.visibility = View.VISIBLE
            setSupportActionBar(binding.toolbar)
            supportActionBar?.let {
                if (isSaveCard) {
                    it.title = getString(R.string.ipage_toolbar_title_save_card)
                } else {
                    it.title = getString(R.string.ipage_toolbar_title)
                }
                it.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private fun setupInfoBanner() {
        if (isPayAndCheckInForRegCard) {
            binding.infoBanner.setText(R.string.reg_card_pay_and_check_in_info_text)
        }
        binding.infoBanner.visibility = if (shouldShowInfoBanner) View.VISIBLE else View.GONE
    }

    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    private fun initWebView() {

        if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            WebViewCompat.addDocumentStartJavaScript(
                binding.webview,
                JS_EVENT_LISTENER_SCRIPT,
                setOf("*")
            )
        }

        binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.builtInZoomControls = false

        binding.webview.webViewClient = object : WebViewClientCompat() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                    // Fallback for when DOCUMENT_START_SCRIPT is not supported
                    binding.webview.loadUrl(JS_EVENT_LISTENER_SCRIPT)

                }

                try {
                    viewModel.triggerLoading(true)
                } catch (throwable: Throwable) {
                    viewModel.triggerLoading(false)
                    viewModel.triggerError(Exception("WebView:onPageStatedError [${throwable.javaClass.name} : ${throwable.message ?: EMPTY_STRING}]"))
                }

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                try {
                        isSuccessMessage = url?.contains(iPageCompleteTrigger) ?:false && !isAuthorizeCard
                        isSuccessMessage?.let { isComplete ->
                            if (isComplete) {
                                cardType = Uri.parse(url).getQueryParameter(CARD_TYPE_QUERY_PARAM) ?: EMPTY_STRING
                                transactionId = Uri.parse(url).getQueryParameter(TRANSACTION_ID_QUERY_PARAM) ?: EMPTY_STRING
                                viewModel.triggerSuccess(PAYMENT_SUCCESS)
                            }
                        }

                    url?.let { iPageLoadedTriggerUrl ->
                        if (iPageLoadedTriggerUrl == iPageLoadedTrigger) {
                            viewModel.trackIPageData(
                                calculateStopTime(),
                                paymentCardSelected,
                                paymentTakenNow,
                                intentInput
                            )
                            viewModel.triggerLoading(false)
                        }
                    }
                } catch (throwable: Throwable) {
                    viewModel.triggerLoading(false)
                    viewModel.triggerError(Exception(getString(R.string.ipage_on_page_finish_error,
                        throwable.javaClass.name, throwable.message ?: EMPTY_STRING
                    )))
                }
            }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceErrorCompat
                ) {
                    viewModel.triggerLoading(false)
                    viewModel.triggerError(Exception(getString(R.string.ipage_on_recieved_error,
                        error.errorCode.toString(), error.description)))
                }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                viewModel.triggerLoading(false)
                viewModel.triggerError(Exception(getString(R.string.ipage_on_http_recieved_error, errorResponse.statusCode.toString(),
                errorResponse.reasonPhrase)))
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                viewModel.triggerLoading(false)
                viewModel.triggerError(Exception(getString(R.string.ipage_on_ssl_recieved_error,
                error?.primaryError.toString(), error?.url)))
            }

            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                view?.url?.let { viewModel.backPressedWithUrl(it) }

                return super.shouldOverrideKeyEvent(view, event)
            }
        }

        binding.webview.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun loadedResource() {
                viewModel.triggerLoading(false)
            }

            @JavascriptInterface
            fun reportUnExpectedJsError() {
                viewModel.triggerError(IllegalStateException("IPage Js Unexpected unexpectedError"))
            }

            @JavascriptInterface
            fun receiveMessage(event: String) {
                viewModel.handleWebViewEvents(event)
            }

        }, "Android3CP")
    }
}
