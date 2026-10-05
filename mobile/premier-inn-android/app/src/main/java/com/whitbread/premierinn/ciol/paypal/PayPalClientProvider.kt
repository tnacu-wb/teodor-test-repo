package com.whitbread.premierinn.ciol.paypal

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.BrowserSwitchResult
import com.braintreepayments.api.PayPalAccountNonce
import com.braintreepayments.api.PayPalClient
import com.braintreepayments.api.PayPalDataCollector
import com.braintreepayments.api.PayPalListener
import com.braintreepayments.api.PayPalVaultRequest
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.data.common.EMPTY_STRING
import java.util.concurrent.atomic.AtomicBoolean

val PAY_PAL_FEATURE_TAG = PayPalClientProvider::class.simpleName
private val PAY_FLOW_RETURN_URL_SCHEME = "${BuildConfig.APPLICATION_ID}.pay.braintree"

class PayPalClientProvider : PayPalListener {
    private var payPalClient: PayPalClient? = null
    private lateinit var payPalDataCollector: PayPalDataCollector
    private var braintreeClient: BraintreeClient? = null

    private var deviceData: String? = null
    private var payPalNonce: String? = null

    private var accountNonceListener: ((payPalAccountNonce: String?, deviceData: String?, error: Exception?) -> Unit)? =
        null
    private val payPalResponsePropagated = AtomicBoolean(false)

    override fun onPayPalSuccess(payPalAccountNonce: PayPalAccountNonce) {
        Log.i(PAY_PAL_FEATURE_TAG, "onPayPalSuccess")
        payPalNonce = payPalAccountNonce.string
        onAccountNonceReceived()
    }

    override fun onPayPalFailure(error: Exception) {
        Log.e(PAY_PAL_FEATURE_TAG, "onPayPalFailure: ${error.message}")
        // Don't handle the error here, any null nonce will be treated as a payment error
        onAccountNonceReceived()
    }

    /**
     * Make sure to call initialize before retrieving and PayPal component
     */
    fun initialize(activity: Activity, payPalClientToken: String) = this.apply {
        if (payPalClient != null) {
            return this
        }
        Log.d(PAY_PAL_FEATURE_TAG, "Initialising PayPal client")
        braintreeClient =
            BraintreeClient(activity, payPalClientToken, PAY_FLOW_RETURN_URL_SCHEME).apply {
                payPalClient = PayPalClient(this)
                payPalClient?.setListener(this@PayPalClientProvider)
                payPalDataCollector = PayPalDataCollector(this)
            }
    }

    fun tokenizePayPalAccount(
        activity: FragmentActivity,
        agreementDescription: String,
        accountNonceListener: (payPalNonce: String?, deviceData: String?, error: Exception?) -> Unit,
    ) = this.apply {
        Log.d(PAY_PAL_FEATURE_TAG, "Tokenize PayPal account")
        // Whenever restarting the flow, reset the value of the flag
        payPalResponsePropagated.set(false)

        payPalClient?.parseBrowserSwitchResult(activity, activity.intent)
            ?.let { browserSwitchResult ->
                // process kill scenario
                handleBrowserSwitchResult(activity, browserSwitchResult)
            }

        this.accountNonceListener = accountNonceListener
        PayPalVaultRequest().apply {
            billingAgreementDescription = agreementDescription
            payPalClient?.tokenizePayPalAccount(activity, this)
        }
    }

    fun collectDeviceData(activity: FragmentActivity) = this.apply {
        Log.d(PAY_PAL_FEATURE_TAG, "Collecting device data")
        payPalDataCollector.collectDeviceData(activity) { deviceData: String?, error: Exception? ->
            error?.let {
                Log.w(PAY_PAL_FEATURE_TAG, "Error while collecting PayPal data: ${it.message}")
            }

            this.deviceData = deviceData ?: EMPTY_STRING
        }
    }

    fun parseBrowserSwitchResult(activity: FragmentActivity, intent: Intent?) {
        Log.d(PAY_PAL_FEATURE_TAG, "Parsing browser switch result")
        payPalClient?.parseBrowserSwitchResult(activity, intent)?.let {
            handleBrowserSwitchResult(activity, it)
        } ?: run {
            braintreeClient?.deliverBrowserSwitchResult(activity)?.let { result ->
                handleBrowserSwitchResult(activity, result)
            }
        }
    }

    private fun handleBrowserSwitchResult(activity: FragmentActivity, result: BrowserSwitchResult) {
        payPalClient?.onBrowserSwitchResult(result) { payPalAccountNonce, error ->
            payPalAccountNonce?.let {
                payPalNonce = it.string
                onAccountNonceReceived()
            } ?: error?.run {
                Log.e(PAY_PAL_FEATURE_TAG, "Error onBrowserSwitchResult: $message")
                onAccountNonceReceived(this)
            }

            // clear pending request to guard against additional browser switch result invocations
            payPalClient?.clearActiveBrowserSwitchRequests(activity)
        }
    }

    private fun onAccountNonceReceived(error: Exception? = null) {
        if (!payPalResponsePropagated.getAndSet(true)) {
            Log.d(
                PAY_PAL_FEATURE_TAG,
                "PayPal tokenization completed; completed with error: ${error != null}"
            )
            accountNonceListener?.invoke(payPalNonce, deviceData, error)
        }
    }
}
