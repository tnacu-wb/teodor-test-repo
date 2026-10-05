package com.whitbread.premierinn.amend.amendAndPay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.utils.HtmlUtils
import com.whitbread.premierinn.databinding.ActivityAmendAndPayBinding
import com.whitbread.premierinn.domain.common.getLastNChars
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.threeCp.ThreeCpActivity.Companion.createThreeCpIntentAmend
import com.whitbread.premierinn.threeCp.ThreeCpInputAmend
import dagger.hilt.android.AndroidEntryPoint

const val THREE_C_P_REQUEST_AMEND: Int = 818

@AndroidEntryPoint
class AmendAndPayActivity : BaseActivity<ActivityAmendAndPayBinding>() {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val tempBasketRef by lazy { requireNotNull(intent.getStringExtra(TEMP_BASKET_REF)) }
    private val originalBasketReference by lazy { requireNotNull(intent.getStringExtra(UUID_BASKET_REFERENCE)) }
    private val token by lazy { requireNotNull(intent.getStringExtra(TOKEN)) }
    private val amendSummaryDomain by lazy {
        intent.getSerializableExtra(AMEND_SUMMARY_DOMAIN) as AmendSummaryDomain
    }
    private val billingAddress by lazy {
        intent.getStringExtra(AMEND_BILLING_ADDRESS) as String
    }
    private val currency by lazy {
        intent.getStringExtra(CURRENCY) as String
    }
    var spannableTermsAndConditions : Spanned? = null

    private val viewModel: AmendAndPayViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityAmendAndPayBinding {
        return ActivityAmendAndPayBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar("Amend and Pay", true)

        setUpTncPrivacyPolicyAndBillingDetails()
        setupSavedCardView()

        viewModel.states()
            .distinctUntilChanged()
            .subscribe(::render)
            .addTo(disposable)

        binding.amendAndPayConfirmButton.setOnClickListener {
            binding.amendAndPayConfirmButton.setLoadingState(true)
            viewModel.confirmAmendLogicCheck(tempBasketRef, originalBasketReference, token)
        }
    }

    private fun setUpTncPrivacyPolicyAndBillingDetails() {
        spannableTermsAndConditions = HtmlUtils.parseTags(
            applicationContext.getString(
                R.string.review_amends_terms_and_conditions,
                getString(R.string.terms_conditions_web_url)
            )
        )
        binding.amendAndPayTermsAndConditionText.text = spannableTermsAndConditions
        binding.amendAndPayTermsAndConditionText.movementMethod = LinkMovementMethod.getInstance()

        binding.amendAndPayPaymentBillingAddressDetails.text = billingAddress
    }

    private fun setupSavedCardView() {
        val cardDisplayName = amendSummaryDomain.paymentCardDetailsDomain?.cardName +
                "(${applicationContext.getString(
                    R.string.masked_card_number,
                    amendSummaryDomain.paymentCardDetailsDomain?.cardNumberMasked?.getLastNChars(4)
                )})"
        val imageUrl = Urls.CONTENT_BASE_URL + amendSummaryDomain.paymentCardDetailsDomain?.cardLogoSrc

        val expiryDate = applicationContext.getString(R.string.card_expires, amendSummaryDomain.paymentCardDetailsDomain?.expirationDate)
        binding.amendAndPaySavedCardHolder.cardTypeLabel.text = cardDisplayName
        binding.amendAndPaySavedCardHolder.cardTypeImage.load(imageUrl)
        binding.amendAndPaySavedCardHolder.paymentCardExpiryLabel.text = expiryDate
        if (!amendSummaryDomain.paymentCardDetailsDomain?.cardHolderName.isNullOrEmpty()) {
            binding.amendAndPaySavedCardHolder.cardHolderLabel.visibility = View.VISIBLE
            binding.amendAndPaySavedCardHolder.cardHolderLabel.text = amendSummaryDomain.paymentCardDetailsDomain?.cardHolderName
        } else {
            binding.amendAndPaySavedCardHolder.cardHolderLabel.visibility = View.GONE
        }
    }

    private fun render(state: AmendAndPayState) {
        binding.amendAndPayPrivacyPolicyLabel.text = fromHtml(state.privacyPolicyText, Html.FROM_HTML_MODE_COMPACT)
        binding.amendAndPayPrivacyPolicyLabel.movementMethod = LinkMovementMethod.getInstance()

        binding.amendAndPayPriceLabel.text = state.totalCost

        val confirmAmendResp = state.confirmAmendLogicResponse
        confirmAmendResp?.let {
            if (it.paymentRequiredDetailsDomain != null) {
                binding.amendAndPayConfirmButton.setLoadingState(false)
                val threeCpInputAmend = ThreeCpInputAmend(confirmAmendResp.paymentRequiredDetailsDomain!!.paymentRedirectDomain)
                createThreeCpIntentAmend(this, threeCpInputAmend).also { intent ->
                    startActivityForResult(intent, THREE_C_P_REQUEST_AMEND)
                }
            } else {
                binding.amendAndPayConfirmButton.setLoadingState(false)
                Toast.makeText(this, "Unable to launch payment", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when(requestCode) {
            THREE_C_P_REQUEST_AMEND -> if (resultCode == RESULT_OK)
                Toast.makeText(this, "Payment successful polling left", Toast.LENGTH_LONG).show()



        }
    }

    companion object {
        private const val EXTRA_AMEND_INPUT = "amend_reservation_input"
        private const val TEMP_BASKET_REF = "temp_basket_ref"
        private const val UUID_BASKET_REFERENCE = "uuid_basket_ref"
        private const val TOKEN = "token"
        const val AMEND_SUMMARY_DOMAIN = "amend_summary_domain"
        private const val AMEND_BILLING_ADDRESS = "amend_billing_address"
        const val CURRENCY = "amend_currency"

        @JvmStatic
        fun createIntent(
            context: Context,
            input: ManageBookingInput,
            amendSummaryDomain: AmendSummaryDomain? = null,
            billingAddress: String,
            currency: String,
            tempBasketRef: String? = null,
            uuidBasketReference: String? = null,
            token: String? = null
        ): Intent {
            return Intent(context, AmendAndPayActivity::class.java).apply {
                putExtra(EXTRA_AMEND_INPUT, input)
                putExtra(TEMP_BASKET_REF, tempBasketRef)
                putExtra(UUID_BASKET_REFERENCE, uuidBasketReference)
                putExtra(TOKEN, token)
                putExtra(AMEND_SUMMARY_DOMAIN, amendSummaryDomain)
                putExtra(AMEND_BILLING_ADDRESS, billingAddress)
                putExtra(CURRENCY, currency)

            }
        }
    }

}