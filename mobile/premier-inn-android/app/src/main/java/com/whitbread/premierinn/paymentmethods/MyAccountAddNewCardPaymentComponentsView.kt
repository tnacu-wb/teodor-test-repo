package com.whitbread.premierinn.paymentmethods

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ViewMyAccountAddNewCardPaymentComponentBinding
import com.whitbread.premierinn.reviewbooking.PaymentCardType
import com.whitbread.premierinn.reviewbooking.PaymentRadioButtonView
import com.whitbread.premierinn.reviewbooking.SelectedPaymentDetailsInput
import io.reactivex.disposables.CompositeDisposable

class MyAccountAddNewCardPaymentComponentsView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val paymentTypeViews = mutableListOf<PaymentRadioButtonView>()
    private var compositeDisposable = CompositeDisposable()

    private lateinit var viewAddNewCardPaymentComponentsBinding: ViewMyAccountAddNewCardPaymentComponentBinding
    private var isPIBASelected = false
    private var isCNPToggleOn = false

    init {
        inflateView()
        loadPaymentContent()
    }

    private fun inflateView() {
        viewAddNewCardPaymentComponentsBinding =
            ViewMyAccountAddNewCardPaymentComponentBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun loadPaymentContent() {
        val newCardView = createPaymentOption("new_card", PaymentRadioButtonView::enableNewCardView)
        val businessAccountCardView =
            createPaymentOption("new_piba_card", PaymentRadioButtonView::enableBusinessAccountView)

        with(viewAddNewCardPaymentComponentsBinding.paymentTypeHolder) {
            addView(newCardView)
            addView(businessAccountCardView)
        }

        paymentTypeViews.forEach { paymentView ->
            paymentView.setOnClickListener {
                handlePaymentSelection(paymentView)
            }
        }

        reOrderPaymentRadioButtons()
        handlePaymentSelection(newCardView)

        viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.setOnCheckedChangeListener { _, isChecked ->
            isCNPToggleOn = isChecked
            showOrHidePIBALeisureNonCnp(isPIBASelected && isCNPToggleOn)
        }
    }

    private fun createPaymentOption(
        tag: String,
        setupAction: PaymentRadioButtonView.() -> Unit
    ): PaymentRadioButtonView {
        return PaymentRadioButtonView(context).apply {
            this.tag = tag
            var isPIBA = false
            val acceptedCreditCards = ArrayList<AcceptedCreditCard>()
            if (tag == "new_card") {
                acceptedCreditCards.addAll(listOf(
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.AC.name).schemeLogo("/content/dam/global/booking/Mastercard.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.AM.name).schemeLogo("/content/dam/global/booking/AX.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.DI.name).schemeLogo("/content/dam/global/booking/dinersclub.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.DL.name).schemeLogo("/content/dam/global/booking/Visa_Debit.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.EL.name).schemeLogo("/content/dam/global/booking/Electron_white_v.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.MA.name).schemeLogo("/content/dam/global/booking/maestro.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.MD.name).schemeLogo("/content/dam/global/booking/MD.jpg").build(),
                        AcceptedCreditCard.builder().creditCardCode(PaymentCardType.VI.name).schemeLogo("/content/dam/global/booking/VC.jpg").build()))
            } else {
                isPIBA = true
                setBusinessAccountCardContent(SelectedPaymentDetailsInput(cardDisplayName = PaymentCardType.AT.name, cardType = PaymentCardType.AT.name))
            }
            loadPaymentCards(isPIBA, acceptedCreditCards)
            showRadioButton(true)
            setupAction()
            paymentTypeViews.add(this)
        }
    }

    private fun reOrderPaymentRadioButtons() {
        val order = listOf("new_card", "new_piba_card")
        val sortedViews = viewAddNewCardPaymentComponentsBinding.paymentTypeHolder.children
            .toList()
            .sortedBy { order.indexOf(it.tag) }

        viewAddNewCardPaymentComponentsBinding.paymentTypeHolder.removeAllViews()
        sortedViews.forEach { viewAddNewCardPaymentComponentsBinding.paymentTypeHolder.addView(it) }
    }

    private fun handlePaymentSelection(selectedView: PaymentRadioButtonView) {
        paymentTypeViews.forEach { it.setRadioButtonCheck(it == selectedView) }
        if (selectedView.tag == "new_piba_card") {
            isPIBASelected = true
            viewAddNewCardPaymentComponentsBinding.separator.isVisible = true
            viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.isVisible = true
            viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.isChecked = false
            showOrHidePIBALeisureNonCnp(isPIBASelected && isCNPToggleOn)
        } else {
            isPIBASelected = false
            viewAddNewCardPaymentComponentsBinding.separator.isVisible = false
            viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.isVisible = false
            viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.isChecked = false
        }
    }

    private fun showOrHidePIBALeisureNonCnp(show: Boolean) {
        viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWord.isVisible = show
        viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWordInput.isVisible = show
        viewAddNewCardPaymentComponentsBinding.addNewCardMemorableInfoIcon.isVisible = show
        viewAddNewCardPaymentComponentsBinding.addNewCardMemorableInfoLabel.isVisible = show
    }

    private fun validateMemWord(): Boolean {
        listenOnMemWordTextChanges()
        return if (viewAddNewCardPaymentComponentsBinding.addNewCardPaymentCnpToggle.isChecked
            && viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWordInput.text.isEmpty()) {
            viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWord.error =
                context.getString(R.string.review_booking_card_type_not_present_memorable_word_error_message)
            false
        } else {
            viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWord.error = EMPTY_STRING
            true
        }
    }

    private fun listenOnMemWordTextChanges() {
        compositeDisposable.add(viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWordInput.textChanges()
            .map { it.toString() }
            .subscribe { password ->
                password.isNotEmpty().let { hasValue ->
                    if (hasValue) {
                        viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWord.error =
                            EMPTY_STRING
                    }
                }
            })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable?.clear()
    }

    fun isPIBASelected(): Boolean {
        return isPIBASelected
    }

    fun isCNPToggleOn(): Boolean {
        return isCNPToggleOn
    }

    fun getMemorableWord(): String {
        return viewAddNewCardPaymentComponentsBinding.addNewCardMemorableWordInput.text.toString()
    }

    fun isMemorableWordValid(): Boolean {
        return validateMemWord()
    }
}
