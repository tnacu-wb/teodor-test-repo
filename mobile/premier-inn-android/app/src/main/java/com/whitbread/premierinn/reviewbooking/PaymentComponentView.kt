package com.whitbread.premierinn.reviewbooking

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.PaymentTimingRule
import com.whitbread.premierinn.common.SavedCardType
import com.whitbread.premierinn.common.mapper.toCardIconPath
import com.whitbread.premierinn.common.mapper.toCardIconPathOpera
import com.whitbread.premierinn.common.mapper.toCardName
import com.whitbread.premierinn.common.utils.CardTypeEnumOpera
import com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING
import com.whitbread.premierinn.common.view.InfoMessageBoxView
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.databinding.PibaCnpPaymentDetailsBinding
import com.whitbread.premierinn.databinding.ViewPaymentComponentBinding
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod.ParcelableReasons.CARD_EXPIRED_BEFORE_DEPARTURE
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod.ParcelableReasons.CARD_NOT_ACCEPTED_AT_HOTEL
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod.ParcelableReasons.CARD_NOT_VALID_FOR_RATE
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod.ParcelableReasons.EXPIRED
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod.ParcelableReasons.PIBA_ALLOWED_ONLY_IN_UK
import com.whitbread.premierinn.reviewbooking.mappers.getExpiryDate
import com.whitbread.premierinn.reviewbooking.mappers.getLastFourDigitsForDisabledSavedCard
import com.whitbread.premierinn.reviewbooking.mappers.getLastNChars
import com.whitbread.premierinn.reviewbooking.mappers.getListOfApplicablePaymentMethod
import com.whitbread.premierinn.reviewbooking.mappers.getPaymentOptions
import com.whitbread.premierinn.reviewbooking.mappers.getReasonForDisabledSavedCard
import com.whitbread.premierinn.reviewbooking.mappers.toPaymentTimingRule
import io.reactivex.disposables.CompositeDisposable

class PaymentComponentView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val paymentTypeSelectionRelay: PublishRelay<PaymentRadioButtonView> =
        PublishRelay.create()
    private val paymentTypeViews = mutableListOf<PaymentRadioButtonView>()
    private var compositeDisposable: CompositeDisposable? = null
    private var savedCardView: PaymentRadioButtonView? = null
    private var businessAccountCardView: PaymentRadioButtonView? = null
    private var googlePayCardView: PaymentRadioButtonView? = null
    private var newCardView: PaymentRadioButtonView? = null
    private var payPalCardView: PaymentRadioButtonView? = null
    private var reserveWithoutCardView: PaymentRadioButtonView? = null
    private lateinit var selectedPaymentTimingChoice: PaymentTimingChoice
    private var reviewBookingModel: ReviewBookModel? = null
    private var selectedPaymentViewRelay: PublishRelay<PaymentRadioButtonView> = PublishRelay.create()
    private var paymentTimingChoiceRelay: PublishRelay<PaymentTimingChoice> = PublishRelay.create()

    private lateinit var viewPaymentComponentBinding: ViewPaymentComponentBinding
    private lateinit var pibaCnpPaymentDetailsBinding: PibaCnpPaymentDetailsBinding
    private var isPaymentOutage: Boolean = false
    private var isBusinessWithNoCardsAssigned = false
    private var isBusinessCustomer: Boolean = false

    init {
        inflateView()
    }

    private fun inflateView() {
        viewPaymentComponentBinding = ViewPaymentComponentBinding.inflate(LayoutInflater.from(context), this, true)
        pibaCnpPaymentDetailsBinding = PibaCnpPaymentDetailsBinding.bind(viewPaymentComponentBinding.root)
    }

    fun loadPaymentContent(
        reviewBookingModel: ReviewBookModel,
        isBusinessCustomer: Boolean,
        viewCompositeDisposable: CompositeDisposable,
        listOfCountries: MutableList<CountryDomain>,
        featurePaypal: Boolean,
        featureGooglePay: Boolean
    ) {
        this.reviewBookingModel = reviewBookingModel
        this.compositeDisposable = viewCompositeDisposable
        this.isBusinessCustomer = isBusinessCustomer

        listenToPaymentTypeSelections()

        val listOfApplicablePaymentMethod = reviewBookingModel.reviewBookingInput
            .paymentDetailsInput()
            ?.paymentMethodsDetailInput()?.parcelablePaymentMethods?.getListOfApplicablePaymentMethod()
            ?.filter { it.getPaymentOptions().isNotEmpty() }
        val getReasonForSavedCard = reviewBookingModel.reviewBookingInput
            .paymentDetailsInput()
            ?.paymentMethodsDetailInput()?.parcelablePaymentMethods?.getReasonForDisabledSavedCard()
        val lastFourDigitsOfCard = reviewBookingModel.reviewBookingInput
            .paymentDetailsInput()
            ?.paymentMethodsDetailInput()?.parcelablePaymentMethods?.getLastFourDigitsForDisabledSavedCard()

        showInfoBoxForSavedCardIfApplicable(getReasonForSavedCard, lastFourDigitsOfCard)

        listOfApplicablePaymentMethod?.let { list ->
            if (list.isNotEmpty()) {
                if (list.any { PaymentTimingRule.RESERVE_WITHOUT_CARD.name in it.getPaymentOptions() }) {
                    reserveWithoutCardView = createReserveWithoutCardType()
                    viewPaymentComponentBinding.paymentTypeHolder.addView(reserveWithoutCardView)
                    if (list.any { it.getPaymentOptions() == listOf(PaymentTimingRule.RESERVE_WITHOUT_CARD.name) }) {
                        isPaymentOutage = true
                        addPaymentTypeInfo(
                            createInfoBox(
                                context.getString(
                                    R.string.payments_down_info_message
                                ), true
                            )
                        )
                    }
                }

                listOfApplicablePaymentMethod.filter {
                    it.getPaymentOptions()
                        .contains(PaymentTimingRule.PAY_NOW.name) || it.getPaymentOptions()
                        .contains(PaymentTimingRule.PAY_ON_ARRIVAL.name)
                }.forEach { paymentMethod ->
                    reviewBookingModel.bookingFlowInput?.let {
                        var acceptedCreditCards = mutableListOf<AcceptedCreditCard>()
                        reviewBookingModel.reviewBookingInput.acceptedCreditCards()?.let { cards ->
                            if (cards.isNotEmpty()) {
                                acceptedCreditCards =
                                    reviewBookingModel.reviewBookingInput.acceptedCreditCards()!!
                                        .distinct().toMutableList()
                            }
                        }
                        when (paymentMethod.type) {
                            PaymentMethodType.SAVED_CARD.name -> {
                                paymentMethod.parcelableCard?.let {
                                    savedCardView = createSavedPaymentCardType(paymentMethod)
                                    viewPaymentComponentBinding.paymentTypeHolder.addView(
                                        savedCardView
                                    )
                                    if (paymentMethod.parcelableCard.cardType in listOf(
                                            PaymentCardType.AT.name,
                                            PaymentCardTypeOpera.PI.name,
                                            PaymentCardTypeOpera.BD.name
                                        )
                                    ) {
                                        viewPaymentComponentBinding.pibaDetailsHolder.showDinnerAllowance =
                                            reviewBookingModel.bookingFlowInput.isDinnerAvailable
                                    }
                                }
                            }

                            PaymentMethodType.NEW_CARD.name -> {
                                newCardView = createNewPaymentCardType(
                                    reviewBookingModel.reviewBookingInput,
                                    paymentMethod,
                                    acceptedCreditCards
                                )
                                viewPaymentComponentBinding.paymentTypeHolder.addView(newCardView)

                                viewPaymentComponentBinding.cardHolderAddress.setListOfCountries(
                                    listOfCountries
                                )
                                viewPaymentComponentBinding.cardHolderAddress.loadComponentContent(
                                    reviewBookingModel.reviewBookingInput
                                )
                            }

                            PaymentMethodType.NEW_PIBA.name -> {
                                if (paymentMethod.subType == PaymentMethodType.NEW_PIBA_EURO.toString()) {

                                    getPibaEuroAcceptedCardType(acceptedCreditCards)?.let { acceptedCard ->
                                        setBusinessAccountCard(
                                            acceptedCard,
                                            paymentMethod,
                                            listOfCountries
                                        )
                                    } ?: setBusinessAccountCard(
                                        AcceptedCreditCard.builder()
                                            .creditCardCode(PaymentCardTypeOpera.BD.name).name(
                                                CardTypeEnumOpera.BD.name
                                            ).build(), paymentMethod, listOfCountries
                                    )
                                } else {
                                    getPibaAcceptedCardType(acceptedCreditCards)?.let { acceptedCard ->
                                        setBusinessAccountCard(
                                            acceptedCard,
                                            paymentMethod,
                                            listOfCountries
                                        )
                                    } ?: setBusinessAccountCard(
                                        AcceptedCreditCard.builder()
                                            .creditCardCode(PaymentCardTypeOpera.PI.name).name(
                                                CardTypeEnumOpera.PI.name
                                            ).build(), paymentMethod, listOfCountries
                                    )
                                }
                                viewPaymentComponentBinding.pibaDetailsHolder.showDinnerAllowance =
                                    true
                            }

                            PaymentMethodType.PAYPAL.name -> {
                                if (featurePaypal) {
                                    getPayPalAcceptedCardType(acceptedCreditCards)?.let { acceptedCard ->
                                        payPalCardView =
                                            createNewPayPalPayCardType(acceptedCard, paymentMethod)
                                        viewPaymentComponentBinding.paymentTypeHolder.addView(
                                            payPalCardView
                                        )
                                    }
                                }
                            }

                            PaymentMethodType.GP.name -> {
                                if (featureGooglePay) {
                                    googlePayCardView = createNewGooglePayCardType(paymentMethod)
                                    viewPaymentComponentBinding.paymentTypeHolder.addView(
                                        googlePayCardView
                                    )
                                    viewPaymentComponentBinding.cardHolderAddress.setListOfCountries(
                                        listOfCountries
                                    )
                                    viewPaymentComponentBinding.cardHolderAddress.loadComponentContent(
                                        reviewBookingModel.reviewBookingInput
                                    )
                                }
                            }
                        }
                    }
                }
                reOrderPaymentRadioButtons(viewPaymentComponentBinding.paymentTypeHolder)
            } else { // we wnter this if payment method list is empty
                if (isBusinessCustomer) {
                    addPaymentTypeInfo(
                        createErrorInfoBox(
                            infoMsg = context.getString(
                                R.string.payment_card_no_available_payment_card
                            )
                        )
                    )
                    isBusinessWithNoCardsAssigned = true
                    viewPaymentComponentBinding.paymentTimingToggleHolder.isVisible = false
                    viewPaymentComponentBinding.pibaDetailsHolder.isVisible = false
                    viewPaymentComponentBinding.cardHolderAddress.isVisible = false
                    viewPaymentComponentBinding.bookingPayOptions.isVisible = false

                }
            }
        }
    }

    private fun setBusinessAccountCard(
        acceptedCard: AcceptedCreditCard,
        paymentMethod: ParcelablePaymentMethod,
        listOfCountries: MutableList<CountryDomain>
    ) {
        businessAccountCardView = createNewBusinessAccountCardType(acceptedCard, paymentMethod)
        viewPaymentComponentBinding.paymentTypeHolder.addView(businessAccountCardView)
        viewPaymentComponentBinding.cardHolderAddress.setListOfCountries(listOfCountries)
        viewPaymentComponentBinding.cardHolderAddress.loadComponentContent(reviewBookingModel!!.reviewBookingInput)
    }

    private fun reOrderPaymentRadioButtons(paymentTypeHolder: LinearLayout) {
        val listOfOrder = listOf(
            "saved_card",
            "new_card",
            "new_piba_card",
            "PayPal",
            "google_pay",
            "reserve_without_card"
        )

        val views = paymentTypeHolder.children.toList()

        val groupedViews = views.groupBy { it.tag }

        val savedCardOrder = listOf(
            SavedCardType.BUSINESS_CENTRALLY_STORED_CARD,
            SavedCardType.BUSINESS_PERSONAL_STORED_CARD,
            SavedCardType.OTHER
        )

        val sortedSavedCards = groupedViews["saved_card"]
            ?.asSequence()
            ?.mapNotNull { it as? PaymentRadioButtonView }
            ?.sortedBy { savedCardOrder.indexOf(it.getSavedCardType()) }
            ?.toList()
            ?: emptyList()

        val otherViews = listOfOrder
            .asSequence()
            .filterNot { it == "saved_card" }
            .mapNotNull { groupedViews[it]?.firstOrNull() }
            .toList()

        paymentTypeHolder.removeAllViews()

        (sortedSavedCards + otherViews).forEach { view ->
            paymentTypeHolder.addView(view)
        }

    }

    private fun showInfoBoxForSavedCardIfApplicable(
        getReasonForSavedCard: String?,
        lastFourDigitsOfCard: String?
    ) {
        if (getReasonForSavedCard != EMPTY_STRING) {
            when (getReasonForSavedCard) {
                EXPIRED.name -> {
                    addPaymentTypeInfo(
                        createInfoBox(
                            context.getString(
                                R.string.payment_card_expired,
                                context.getString(R.string.masked_card_number, lastFourDigitsOfCard)
                            ), true
                        )
                    )

                }
                CARD_EXPIRED_BEFORE_DEPARTURE.name -> {
                    addPaymentTypeInfo(
                        createInfoBox(
                            context.getString(
                                R.string.payment_card_expiring,
                                context.getString(R.string.masked_card_number, lastFourDigitsOfCard)
                            ), false
                        )
                    )
                }
                CARD_NOT_ACCEPTED_AT_HOTEL.name -> {
                    addPaymentTypeInfo(
                        createInfoBox(
                            context.getString(
                                R.string.payment_card_not_accepted_at_hotel,
                                context.getString(R.string.masked_card_number, lastFourDigitsOfCard)
                            ), false
                        )
                    )
                }
                PIBA_ALLOWED_ONLY_IN_UK.name -> {
                    addPaymentTypeInfo(
                        createInfoBox(
                            context.getString(
                                R.string.payment_card_not_accepted_at_hotel,
                                context.getString(R.string.masked_card_number, lastFourDigitsOfCard)
                            ), false
                        )
                    )
                }

                CARD_NOT_VALID_FOR_RATE.name ->
                    addPaymentTypeInfo(
                        createInfoBox(
                            context.getString(
                                R.string.payment_card_not_valid_rate,
                                context.getString(R.string.masked_card_number, lastFourDigitsOfCard)
                            ), true
                        )
                    )
            }
        }
    }

    private fun createSavedPaymentCardType(
        paymentMethod: ParcelablePaymentMethod
    ): PaymentRadioButtonView = PaymentRadioButtonView(context).apply {

        tag = "saved_card"
        showRadioButton(true)
        enablePersonalCardView()
        paymentTypeViews.add(this)

        val card = paymentMethod.parcelableCard
        val savedCardType =
            SavedCardType.entries.find { it.name == card?.cardType } ?: SavedCardType.OTHER

        setSavedCardContent(
            SelectedPaymentDetailsInput(
                cardDisplayName = "${card?.type.toCardName()} (${
                    context.getString(
                        R.string.masked_card_number, card?.token?.getLastNChars(4)
                    )
                })",
                cardType = card?.type,
                savedCardType = card?.cardType,
                cardHolder = card?.cardHolderName.orEmpty(),
                cardExpiry = context.getString(R.string.card_expires, card?.getExpiryDate()),
                cardImage = String.format(Urls.CREDIT_CARD_FORMAT_URL, card?.type.toCardIconPath()),
                cnpEnabled = paymentMethod.cnpOptionAvailable
            )
        )

        setPaymentOptionForSavedCard(paymentMethod.getPaymentOptions())

        val isSelected = when (savedCardType) {
            SavedCardType.BUSINESS_CENTRALLY_STORED_CARD -> true
            SavedCardType.BUSINESS_PERSONAL_STORED_CARD,
            SavedCardType.LEISURE_STORED_CARD-> newCardView == null && businessAccountCardView == null && savedCardView == null
            else -> false
        }

        setSelectedPayment(this, isSelected)
        setOnClickListener { paymentTypeSelectionRelay.accept(this) }
        if (isSelected) {
            performClick()
        }
    }

    private fun createNewPaymentCardType(
        reviewBookingInput: ReviewBookingInput,
        paymentMethod: ParcelablePaymentMethod,
        acceptedCreditCards: List<AcceptedCreditCard>
    ): PaymentRadioButtonView {
        val paymentCardView = PaymentRadioButtonView(context)
        paymentCardView.tag = "new_card"
        paymentTypeViews.add(paymentCardView)
        paymentCardView.showRadioButton(true)
        paymentCardView.enableNewCardView()

        val hotelAcceptsPiba = acceptedCreditCards.getPibaAcceptedCards()
        paymentCardView.loadPaymentCards(
            hotelAcceptsPiba.isNotEmpty(), acceptedCreditCards.newPaymentTypeAcceptedCards()
        )
        // if saved card present -> set to false(since saved card preselected)
        // if no saved card or no business card -> set to true (since 1st card)
        // if no saved card & business card present -> set to false

        val isSelected = if (savedCardView != null) {
            false
        } else  {
            businessAccountCardView == null
        }

        paymentCardView.setPaymentOptionForNewCard(paymentMethod.getPaymentOptions())
        setSelectedPayment(paymentCardView, isSelected)

        paymentCardView.setOnClickListener {
            paymentTypeSelectionRelay.accept(paymentCardView)
        }

        if (isSelected) {
            paymentCardView.performClick()
        }
        return paymentCardView
    }

    private fun createNewPayPalPayCardType(
        acceptedCreditCard: AcceptedCreditCard,
        paymentMethod: ParcelablePaymentMethod
    ): PaymentRadioButtonView {
        val paymentCardView = PaymentRadioButtonView(context)
        paymentCardView.tag = "PayPal"
        paymentTypeViews.add(paymentCardView)
        paymentCardView.showRadioButton(true)
        paymentCardView.enablePayPalView()

        paymentCardView.setPaymentOptionForPayPal(paymentMethod.getPaymentOptions())

        setSelectedPayment(paymentCardView, isSelected)

        paymentCardView.setOnClickListener {
            paymentTypeSelectionRelay.accept(paymentCardView)
        }

        // if saved card present -> set to false(since saved card preselected)
        // if no saved card or no new card -> set to true (since 1st card)
        // if no saved card & new card present-> set to false

        val isSelected = if (savedCardView != null) {
            false
        } else  {
            newCardView == null && reserveWithoutCardView == null
        }

        val paymentCardDetailsInput = SelectedPaymentDetailsInput(
            cardDisplayName = "${acceptedCreditCard.name()}",
            cardType = acceptedCreditCard.creditCardCode(),
            cardImage = String.format(
                Urls.PAYPAL_PAY_FORMAT_URL,
                acceptedCreditCard.creditCardCode().toCardIconPathOpera()
            ),
            cnpEnabled = paymentMethod.cnpOptionAvailable
        )
        paymentCardView.setPayPalCardContent(paymentCardDetailsInput)

        if (isSelected) {
            paymentCardView.performClick()
        }

        return paymentCardView
    }

    private fun createNewBusinessAccountCardType(
        acceptedCreditCard: AcceptedCreditCard,
        paymentMethod: ParcelablePaymentMethod
    ): PaymentRadioButtonView {
        val businessAccountCardView = PaymentRadioButtonView(context)
        businessAccountCardView.tag = "new_piba_card"
        paymentTypeViews.add(businessAccountCardView)
        businessAccountCardView.showRadioButton(true)
        businessAccountCardView.enableBusinessAccountView()

        val paymentCardDetailsInput = SelectedPaymentDetailsInput(
            cardDisplayName = acceptedCreditCard.name(),
            cardType = acceptedCreditCard.creditCardCode(),
            cnpEnabled = true
        )

        // if saved card present -> set to false(since saved card preselected)
        // if no saved card or no new card -> set to true (since 1st card)
        // if no saved card & new card present-> set to false

        val isSelected = if (savedCardView != null) {
            false
        } else  {
            newCardView == null
        }

        businessAccountCardView.setBusinessAccountCardContent(paymentCardDetailsInput)
        businessAccountCardView.setPaymentOptionForNewPIBACard(paymentMethod.getPaymentOptions())

        setSelectedPayment(businessAccountCardView, isSelected)

        businessAccountCardView.setOnClickListener {
            paymentTypeSelectionRelay.accept(businessAccountCardView)
        }

        if (isSelected) {
            businessAccountCardView.performClick()
        }

        return businessAccountCardView
    }

    private fun createReserveWithoutCardType(): PaymentRadioButtonView {
        val paymentCardView = PaymentRadioButtonView(context)
        paymentCardView.tag = "reserve_without_card"
        paymentTypeViews.add(paymentCardView)
        paymentCardView.showRadioButton(true)
        paymentCardView.enableReserveWithoutCardView()

        setSelectedPayment(paymentCardView, isSelected)

        paymentCardView.setOnClickListener {
            paymentTypeSelectionRelay.accept(paymentCardView)
        }

        val isSelected = if (savedCardView != null) {
            false
        } else {
            newCardView == null
        }

        if (isSelected) {
            paymentCardView.performClick()
        }

        return paymentCardView
    }

    private fun createNewGooglePayCardType(
            paymentMethod: ParcelablePaymentMethod
    ): PaymentRadioButtonView {
        val googlePayCardView = PaymentRadioButtonView(context)
        googlePayCardView.tag = "google_pay"
        paymentTypeViews.add(googlePayCardView)
        googlePayCardView.showRadioButton(true)
        googlePayCardView.enableGooglePayView()
        googlePayCardView.googlePaySelected()

        googlePayCardView.setPaymentOptionForGooglePay(paymentMethod.getPaymentOptions())

        setSelectedPayment(googlePayCardView, isSelected)

        googlePayCardView.setOnClickListener {
            paymentTypeSelectionRelay.accept(googlePayCardView)
        }

        // if saved card present -> set to false(since saved card preselected)
        // if no saved card or no new card -> set to true (since 1st card)
        // if no saved card & new card present-> set to false

        val isSelected = if (savedCardView != null) {
            false
        } else  {
            newCardView == null && reserveWithoutCardView == null
        }

        val paymentCardDetailsInput = SelectedPaymentDetailsInput(
            cardDisplayName = "",
            cardType = PaymentMethodType.GP.name
        )
        googlePayCardView.setGooglePayCardContent(paymentCardDetailsInput)

        if (isSelected) {
            googlePayCardView.performClick()
        }

        return googlePayCardView
    }

    private fun createErrorInfoBox(infoMsg: String): InfoMessageBoxView {
        val messageInfoMessageBoxView = InfoMessageBoxView(context)
        messageInfoMessageBoxView.setHtmlText(infoMsg)
                messageInfoMessageBoxView.setIconResource(R.drawable.notifications_alert)
                messageInfoMessageBoxView.background =
                    ContextCompat.getDrawable(context, R.drawable.error_notification_background)
                messageInfoMessageBoxView.setIconColorFilter(R.color.alert_orange)

        messageInfoMessageBoxView.setPadding(35, 35, 35, 35)
        return messageInfoMessageBoxView
    }


    private fun createInfoBox(infoMsg: String, isWarningInfo: Boolean): InfoMessageBoxView {
        val messageInfoMessageBoxView = InfoMessageBoxView(context)
        messageInfoMessageBoxView.setHtmlText(infoMsg)
        when (isWarningInfo) {
            true -> {
                messageInfoMessageBoxView.setIconResource(R.drawable.notifications_alert)
                messageInfoMessageBoxView.background =
                    ContextCompat.getDrawable(context, R.drawable.alert_notification_background)
                messageInfoMessageBoxView.setIconColorFilter(R.color.alert_orange)
            }

            else -> {
                messageInfoMessageBoxView.setIconResource(R.drawable.ic_info)
                messageInfoMessageBoxView.background =
                    ContextCompat.getDrawable(context, R.drawable.information_notification_background)
                messageInfoMessageBoxView.setIconColorFilter(R.color.information_blue)
            }
        }

        messageInfoMessageBoxView.setPadding(35, 35, 35, 35)
        return messageInfoMessageBoxView
    }

    private fun addPaymentTypeInfo(infoView: View) {
        viewPaymentComponentBinding.paymentInformationHolder.isVisible = true
        viewPaymentComponentBinding.paymentInformationHolder.addView(infoView)
    }

    private fun listenToPaymentTypeSelections() {
        compositeDisposable?.add(paymentTypeSelectionRelay
            .subscribe { paymentTypeView ->
                selectPaymentOption(paymentTypeView)
            }
        )
    }

    private fun listenToPaymentTimingToggleSelection() {
        compositeDisposable?.add(viewPaymentComponentBinding.bookingPayNowLaterToggle.state
            .subscribe { state ->
                when (state) {
                    ToggleButtonView.State.LEFT -> {
                        selectedPaymentTimingChoice = PaymentTimingChoice.PAY_LATER
                        viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(context.getString(R.string.review_booking_pay_later_info_message))
                    }
                    ToggleButtonView.State.RIGHT -> {
                        selectedPaymentTimingChoice = PaymentTimingChoice.PAY_NOW
                        viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(context.getString(R.string.review_booking_pay_now_info_message))
                    }

                    else -> {
                        throw IllegalStateException("Illegal button state: $state")
                    }
                }

                paymentTimingChoiceRelay.accept(selectedPaymentTimingChoice)
            })
    }

    private fun selectPaymentOption(paymentRadioButtonView: PaymentRadioButtonView) {
        for (paymentTypeView in paymentTypeViews) {
            paymentTypeView.setRadioButtonCheck(false)
        }

        setSelectedPayment(paymentRadioButtonView, true)
        displayPaymentToggleButton(updatePaymentChoice(paymentRadioButtonView))
    }

    private fun updatePaymentChoice(view: PaymentRadioButtonView): PaymentTimingRule {
        return when(view.tag) {
            "new_card" -> view.getPaymentOptionsForNewCard()?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_ON_ARRIVAL

            "new_piba_card" ->  view.getPaymentOptionsForNewPIBACard()?.filterNot { it ==  PaymentTimingChoice.RESERVE_WITHOUT_CARD.name}?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_ON_ARRIVAL

            "google_pay" ->  view.getPaymentOptionsForGooglePay()?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_NOW

            "saved_card" -> view.getPaymentOptionsForSavedCard()?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_ON_ARRIVAL

            "PayPal" -> view.getPaymentOptionForPayPal()?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_ON_ARRIVAL

            "reserve_without_card" -> PaymentTimingRule.RESERVE_WITHOUT_CARD

            else -> view.getPaymentOptionsForNewCard()?.toPaymentTimingRule() ?: PaymentTimingRule.PAY_ON_ARRIVAL
        }
    }

    private fun displayPaymentToggleButton(timingRule: PaymentTimingRule) {
        when (timingRule) {
            PaymentTimingRule.PAY_NOW -> enableOnlyPayNowView()
            PaymentTimingRule.PAY_LATER -> enableOnlyPayOnArrivalView()
            PaymentTimingRule.RESERVE_WITHOUT_CARD -> enableOnlyReserveWithoutCardView()
            else -> {
                enablePaymentToggleView()
            }
        }
    }

    private fun enableOnlyPayNowView() {
        selectedPaymentTimingChoice = PaymentTimingChoice.PAY_NOW
        viewPaymentComponentBinding.paymentTimingToggleHolder.isVisible = false
        viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(context.getString(R.string.review_booking_pay_now_info_message))
        paymentTimingChoiceRelay.accept(selectedPaymentTimingChoice)
    }

    private fun enableOnlyPayOnArrivalView() {
        selectedPaymentTimingChoice = PaymentTimingChoice.PAY_LATER
        viewPaymentComponentBinding.paymentTimingToggleHolder.isVisible = false
        viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(context.getString(R.string.review_booking_pay_later_info_message))
        paymentTimingChoiceRelay.accept(selectedPaymentTimingChoice)
    }

    private fun enableOnlyReserveWithoutCardView() {
        reviewBookingModel?.bookingFlowInput?.hotelBrand().let {
            when (it) {
                Hotel.Brand.PI.toString(),
                Hotel.Brand.HUB.toString(),
                Hotel.Brand.ZIP.toString() ->
                    viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(
                        context.getString(R.string.payment_details_reserve_without_card_uk_hotel))
                else ->
                    viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(
                        context.getString(R.string.payment_details_reserve_without_card_de_hotel))
            }
        }
        selectedPaymentTimingChoice = PaymentTimingChoice.RESERVE_WITHOUT_CARD
        viewPaymentComponentBinding.paymentTimingToggleHolder.isVisible = false
        paymentTimingChoiceRelay.accept(selectedPaymentTimingChoice)
    }

    private fun enablePaymentToggleView() {
        listenToPaymentTimingToggleSelection()
        viewPaymentComponentBinding.paymentTimingToggleHolder.isVisible = true
        viewPaymentComponentBinding.bookingPaymentTimingBanner.setText(context.getString(R.string.review_booking_pay_later_info_message))
    }

    private fun setSelectedPayment(
        selectedPaymentType: PaymentRadioButtonView,
        isSelected: Boolean
    ) {
        selectedPaymentType.setRadioButtonCheck(isSelected)
        if (isSelected) {
            showOrHidePIBALeisureNonCnp(selectedPaymentType.isSavedPaymentBusinessAccountCardWithCnpEnabled())

            setBillingAddressVisibility(
                selectedPaymentType.tag ==  "new_card" ||
                selectedPaymentType.tag == "new_piba_card" ||
                selectedPaymentType.tag == "google_pay"
            )

            selectedPaymentViewRelay.accept(selectedPaymentType)
        }
    }

    private fun setBillingAddressVisibility(show: Boolean) {
        viewPaymentComponentBinding.cardHolderAddress.visibility = if (show) {
            View.VISIBLE
        } else {
            viewPaymentComponentBinding.cardHolderAddress.isPaymentAddressSameAsBookerAddress = true
            viewPaymentComponentBinding.cardHolderAddress.switch.isChecked =
                viewPaymentComponentBinding.cardHolderAddress.isPaymentAddressSameAsBookerAddress
            View.GONE
        }
    }

    private fun showOrHidePIBALeisureNonCnp(show: Boolean) {
        viewPaymentComponentBinding.pibaDetailsHolder.isVisible = show
        if (!show) {
            viewPaymentComponentBinding.pibaDetailsHolder.resetCnpOptions()
        }
    }

    fun getPaymentDetails(): PublishRelay<PaymentRadioButtonView> {
        return selectedPaymentViewRelay
    }

    fun getPaymentTimingRelay(): PublishRelay<PaymentTimingChoice> {
        return paymentTimingChoiceRelay;
    }

    fun updateAddress(billingAddress: Address) {
        viewPaymentComponentBinding.cardHolderAddress.setAddressFields(billingAddress)
    }

    private fun isBillingAddressValid(): Boolean {
        return if (viewPaymentComponentBinding.cardHolderAddress.isAddressValid()) {
        true
        } else {
            viewPaymentComponentBinding.cardHolderAddress.displayAddressInlineErrors()
            false
        }
    }

    fun isPaymentComponentValid(): Boolean {
        return isBillingAddressValid() &&
                viewPaymentComponentBinding.pibaDetailsHolder.areCnpOptionsValid()
    }

    fun isDinnerBudgetValid(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isDinnerBudgetValid()
    }

    fun billingAddress(): BookingAddress? {
        return reviewBookingModel?.reviewBookingInput?.cardHolderAddress()
    }

    fun getPaymentTimingSelection(): String {
        return selectedPaymentTimingChoice.name
    }

    fun getAlcoholAllowed(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isAlcoholSelected
    }

    fun getCnpAuth(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isPaymentCnpSelected
    }

    fun getCarParkingAllowed(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isBusinessParkingSelected
    }

    fun getDinnerAllowance(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isDinnerAllowanceSelected
    }

    fun getDinnerBudget(): String {
        return viewPaymentComponentBinding.pibaDetailsHolder.pibaCnpPaymentDetailsBinding.dinnerBudgetInput.text.toString()
    }

    fun getWifiAccessAllowed(): Boolean {
        return viewPaymentComponentBinding.pibaDetailsHolder.isWifiSelected
    }

    fun getMemorableWord(): String {
       return viewPaymentComponentBinding.pibaDetailsHolder.pibaCnpPaymentDetailsBinding.memorableWordInput.text.toString()
    }

    fun getPurchaseOrderNumber(): String {
        return viewPaymentComponentBinding.pibaDetailsHolder.pibaCnpPaymentDetailsBinding.purchaseOrderReference.text.toString()
    }

    fun getCustomerReferenceNumber(): String {
        return viewPaymentComponentBinding.pibaDetailsHolder.pibaCnpPaymentDetailsBinding.customerReference.text.toString()
    }

    fun isPaymentOutage(): Boolean {
        return isPaymentOutage
    }

    fun isBusinessUserWithNoCardsAssigned(): Boolean {
        return isBusinessWithNoCardsAssigned
    }
}