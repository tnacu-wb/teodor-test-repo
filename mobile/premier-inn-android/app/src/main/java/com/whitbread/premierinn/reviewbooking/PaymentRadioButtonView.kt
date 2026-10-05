package com.whitbread.premierinn.reviewbooking

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.common.SavedCardType
import com.whitbread.premierinn.common.mapper.toCardIconPath
import com.whitbread.premierinn.common.mapper.toCardIconPathOpera
import com.whitbread.premierinn.databinding.ItemPaymentTypeViewBinding

class PaymentRadioButtonView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private lateinit var itemPaymentTypeViewBinding: ItemPaymentTypeViewBinding

    private var paymentDetailsInput: SelectedPaymentDetailsInput? = null
    private var listOfPaymentOptionsForSavedCard = mutableListOf<String>()
    private var listOfPaymentOptionsForNewCard = mutableListOf<String>()
    private var listOfPaymentOptionsForNewPIBACard = mutableListOf<String>()
    private var listOfPaymentOptionsForPayPal = mutableListOf<String>()
    private var listOfPaymentOptionsForGooglePay = mutableListOf<String>()

    init {
        inflateView()
    }

    private fun inflateView() {
        itemPaymentTypeViewBinding = ItemPaymentTypeViewBinding.inflate(LayoutInflater.from(context), this)
    }

    fun loadPaymentCards(hotelAcceptsPIBA: Boolean = false, acceptedCardsList: List<AcceptedCreditCard>) {
        val acceptedCards = if (hotelAcceptsPIBA) {
            acceptedCardsList.excludeNonPibaAcceptedCards()
        } else {
            acceptedCardsList
        }
        if (acceptedCards.isNotEmpty()) {
            itemPaymentTypeViewBinding.newCardHolder.paymentCreditCardImages.displayCreditCards(acceptedCards)
        }
    }

    fun showRadioButton(show: Boolean) {
        itemPaymentTypeViewBinding.paymentTypeRadioButton.isVisible = show
    }

    fun setRadioButtonCheck(check: Boolean) {
        itemPaymentTypeViewBinding.paymentTypeRadioButton.isChecked = check
    }

    fun enablePersonalCardView() {
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = false
    }

    fun enableNewCardView() {
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = false
    }

    fun enableBusinessAccountView() {
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = false
    }

    fun enablePayPalView() {
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = false
    }

    fun enableReserveWithoutCardView() {
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = false
    }

    fun enableGooglePayView() {
        itemPaymentTypeViewBinding.googlePlayCardHolder.root.isVisible = true
        itemPaymentTypeViewBinding.savedCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.businessAccountCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.newCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.payPalCardHolder.root.isVisible = false
        itemPaymentTypeViewBinding.reserveWithoutCardHolder.root.isVisible = false
    }

    fun setSavedCardContent(input: SelectedPaymentDetailsInput) {
        this.paymentDetailsInput = input
        paymentDetailsInput?.let { selectedPaymentCardDetails ->
            itemPaymentTypeViewBinding.savedCardHolder.cardTypeLabel.text = selectedPaymentCardDetails.cardDisplayName
            itemPaymentTypeViewBinding.savedCardHolder.cardHolderLabel.text = selectedPaymentCardDetails.cardHolder
            itemPaymentTypeViewBinding.savedCardHolder.paymentCardExpiryLabel.text = selectedPaymentCardDetails.cardExpiry
            itemPaymentTypeViewBinding.savedCardHolder.cardTypeImage.load(selectedPaymentCardDetails.cardImage)
        }
    }

    fun isSavedPaymentBusinessAccountCardWithCnpEnabled(): Boolean {
        return paymentDetailsInput?.cnpEnabled ?: false
    }

    fun setBusinessAccountCardContent(input: SelectedPaymentDetailsInput) {
        this.paymentDetailsInput = input
        paymentDetailsInput?.let {
            if (it.cardType == PaymentCardType.AT.name) {
                itemPaymentTypeViewBinding.businessAccountCardHolder.businessAccountImage.load(
                        String.format(
                                Urls.CREDIT_CARD_FORMAT_URL,
                                it.cardType.toCardIconPath()
                        )
                )
            } else if (it.cardType in listOf(PaymentCardTypeOpera.PI.name, PaymentCardTypeOpera.BD.name)) {
                itemPaymentTypeViewBinding.businessAccountCardHolder.businessAccountImage.load(
                        String.format(Urls.CREDIT_CARD_FORMAT_URL, it.cardType.toCardIconPathOpera()))
                if (it.cardType == PaymentCardTypeOpera.BD.name) {
                    itemPaymentTypeViewBinding.businessAccountCardHolder.businessAccountCardLbl.text =
                        context.getString(R.string.business_account_card_euro)

                    itemPaymentTypeViewBinding.businessAccountCardHolder.businessAccountCardDescription.text =
                        context.getString(R.string.business_account_card_euro_description)

                    itemPaymentTypeViewBinding.businessAccountCardHolder.businessAccountCardDescription.visibility = VISIBLE
                }
            }
        }
    }

    fun setGooglePayCardContent(input: SelectedPaymentDetailsInput) {
        this.paymentDetailsInput = input
        paymentDetailsInput?.let {
            itemPaymentTypeViewBinding.googlePlayCardHolder.googlePayImage.load(
                String.format(
                    Urls.GOOGLE_PAY_FORMAT_URL,
                    it.cardType.toCardIconPathOpera()
                )
            )
        }
    }

    fun getPaymentCardDetailsInput(): SelectedPaymentDetailsInput? {
        return this.paymentDetailsInput
    }

    fun isGooglePaySelected(): Boolean {
        return isSelected
    }

    fun googlePaySelected() {
        isSelected = true
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        itemPaymentTypeViewBinding.paymentTypeRadioButton.setOnClickListener(l)
    }


    fun setPaymentOptionForSavedCard(listOfPaymentOptions: List<String>?) {
        listOfPaymentOptions?.forEach { it ->
            this.listOfPaymentOptionsForSavedCard.add(it)
        }
    }

    fun getPaymentOptionsForSavedCard(): List<String>? {
        return this.listOfPaymentOptionsForSavedCard
    }
    fun setPaymentOptionForNewCard(listOfPaymentOptions: List<String>?) {
        listOfPaymentOptions?.forEach { it ->
            this.listOfPaymentOptionsForNewCard.add(it)
        }
    }

    fun getPaymentOptionsForNewCard(): List<String>? {
        return this.listOfPaymentOptionsForNewCard
    }

    fun setPaymentOptionForNewPIBACard(listOfPaymentOptions: List<String>?) {
        listOfPaymentOptions?.forEach { it ->
            this.listOfPaymentOptionsForNewPIBACard.add(it)
        }
    }

    fun getPaymentOptionsForNewPIBACard(): List<String>? {
        return this.listOfPaymentOptionsForNewPIBACard
    }

    fun setPaymentOptionForPayPal(listOfPaymentOptions: List<String>?) {
        listOfPaymentOptions?.forEach { it ->
            this.listOfPaymentOptionsForPayPal.add(it)
        }
    }

    fun getPaymentOptionForPayPal(): List<String>? {
        return this.listOfPaymentOptionsForPayPal
    }

    fun setPayPalCardContent(paymentCardDetailsInput: SelectedPaymentDetailsInput) {
        this.paymentDetailsInput = paymentCardDetailsInput
        paymentDetailsInput?.let {
            if (it.cardType == PaymentCardType.PP.name) {
                itemPaymentTypeViewBinding.payPalCardHolder.payPalPaymentImage.load(
                   it.cardImage
                )
            }
        }
    }

    fun setPaymentOptionForGooglePay(listOfPaymentOptions: List<String>?) {
        listOfPaymentOptions?.forEach { it ->
            this.listOfPaymentOptionsForGooglePay.add(it)
        }
    }

    fun getPaymentOptionsForGooglePay(): List<String>? {
        return this.listOfPaymentOptionsForGooglePay
    }

    fun getSavedCardType(): SavedCardType {
        return SavedCardType.entries.find { it.name == paymentDetailsInput?.savedCardType }
            ?: SavedCardType.OTHER
    }
}