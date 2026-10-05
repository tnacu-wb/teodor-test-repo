package com.whitbread.premierinn.editpaymentmethods

import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.customer.PaymentCard
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.CardTypeEnum
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails
import com.whitbread.premierinn.paymentmethods.analytics.CardAnalyticsData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class SubmitPaymentMethodsFormApiTransformer(
    private val updateCustomerPaymentDetails: UpdateCustomerPaymentDetails,
    private val existingCustomerDetails: Customer,
    private val trackingAnalytics: TrackingAnalytics,
    private val crashlyticsLogger: LogService
)
    : ObservableTransformer<List<Input>, SubmitFormResult> {
    private lateinit var updatedPaymentCard: com.whitbread.premierinn.domain.customer.entity.PaymentCard

    override fun apply(formInputs: Observable<List<Input>>): ObservableSource<SubmitFormResult> {
        return formInputs.flatMap { originalInputs ->
            Observable.just(inputsToPaymentCard(originalInputs, CardTypeEnum.VI.name))
                .flatMap { paymentCard: PaymentCard ->
                    updatedPaymentCard =
                        com.whitbread.premierinn.domain.customer.entity.PaymentCard(
                            number = paymentCard.cardNumber() ?: EMPTY_STRING,
                            cardType = paymentCard.cardType() ?: CardTypeEnum.VI.name,
                            holdersFullName = paymentCard.cardHolderName()
                                ?: (existingCustomerDetails.fullName.firstName + " " + existingCustomerDetails.fullName.lastName),
                            expiryDate = paymentCard.expiryDate() ?: EMPTY_STRING
                        )
                    updateCustomerPaymentDetails
                        .invoke(
                            UpdateCustomerPaymentDetails.Params(
                                updatedPaymentCard,
                                existingCustomerDetails.address
                            )
                        )
                        .mapToAsyncResult()
                }.map { state: AsyncResult<Nothing> ->
                when (state) {
                    is AsyncResult.Success -> {
                        updateCustomerPaymentDetails.updateAndSaveCustomerPaymentDetailsToSharedPreferences(
                            updatedPaymentCard
                        )
                        logCardUpdateSuccess(CardTypeEnum.VI.name)
                        SubmitFormResult.serverSuccess()
                    }

                    is AsyncResult.Error -> {
                        crashlyticsLogger.logException(state.error, state.error.message)
                        SubmitFormResult.serverError()
                    }

                    is AsyncResult.Loading -> SubmitFormResult.inFlight()
                }
            }
        }
    }

    private fun logCardUpdateSuccess(cardType: String) {
        val analyticsData = CardAnalyticsData(cardType)
        existingCustomerDetails.paymentCard?.let {
            trackingAnalytics.track(AnalyticsConstants.ScreenState.CARD_REPLACED, analyticsData)
        } ?: run {
            trackingAnalytics.track(AnalyticsConstants.ScreenState.CARD_ADDED, analyticsData)
        }
    }

    private fun inputsToPaymentCard(formInputs: List<Input>, cardType: String): PaymentCard {
        return paymentInputsToPaymentCard(formInputs, cardType)
    }

    private fun paymentInputsToPaymentCard(inputs: List<Input>, cardType: String): PaymentCard {
        val paymentCardBuilder = PaymentCard.builder()
        for (input in inputs) {
            when (input.id()) {
                R.id.til_card_details_form_card_number -> paymentCardBuilder.cardNumber(input.value().toString())
                R.id.til_card_details_form_expiry_date -> paymentCardBuilder.expiryDate(input.value().toString())
                R.id.til_card_details_form_issue_number -> paymentCardBuilder.issueNumber(input.value().toString())
                R.id.til_card_details_form_name_on_card -> paymentCardBuilder.cardHolderName(input.value().toString())
                R.id.til_card_details_form_start_date -> paymentCardBuilder.startDate(input.value().toString())
            }
        }
        return paymentCardBuilder.cardType(cardType).build()
    }

}
