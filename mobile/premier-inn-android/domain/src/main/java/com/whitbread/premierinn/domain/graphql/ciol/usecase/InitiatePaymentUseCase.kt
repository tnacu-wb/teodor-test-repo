package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.GooglePayPayment
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.NormalPayment
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.PayPalPayment
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.await
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias InitiatePaymentResult = Result<InitiatePaymentResultData, DomainError.PaymentError>

class InitiatePaymentUseCase @Inject constructor(
    private val isFeatureOn: IsFeatureOn,
    private val repository: GraphQLReviewBookingRepository,
    private val dispatchers: AppDispatchers
) {

    sealed class InitiatePaymentResultData {
        data class NormalPayment(val htmlContent: String): InitiatePaymentResultData()
        data class GooglePayPayment(
            val iPageSessionIdForGPay: String,
            val providerUrl: String
        ): InitiatePaymentResultData()
        object PayPalPayment : InitiatePaymentResultData()
    }

    operator fun invoke(isPayPal: Boolean, input: InitiatePaymentRequestBody) = flow<InitiatePaymentResult> {
        val usePaypalInitiatePayment = isFeatureOn.invoke(ContentManagedResourceRepository.Key.USE_PAYPAL_INITIATE_PAYMENT_MUTATION)
        runCatching {
            if (isPayPal && usePaypalInitiatePayment) {
                repository.initiatePaypalPayment(input).await()
            } else {
                repository.initiatePayment(input).await()
            }
        }.onSuccess { result ->
            when (result) {
                is InitiatePaymentDomain -> {
                    result.paymentRequiredDetails?.run {
                        if (result.status == "PAYMENT_REQUIRED" && iPageHtml != null) {
                            if (input.createPaymentCriteria.payment.type == "WALLET_GOOGLE") {
                                emit(Result.Success(createGooglePayResult(result)))
                            } else {
                                emit(Result.Success(NormalPayment(iPageHtml)))
                            }
                        } else {
                            emit(Result.Error(DomainError.PaymentError.GenericError(null)))
                        }
                    } ?: run {
                        emit(Result.Error(DomainError.PaymentError.GenericError(null)))
                    }
                }
                is InitiatePaypalPaymentDomain -> {
                    emit(Result.Success(PayPalPayment))
                }
            }
        }.onFailure { error ->
            emit(Result.Error(DomainError.PaymentError.CouldNotInitiatePaymentError(error.message)))
        }
    }.flowOn(dispatchers.io)

    private fun createGooglePayResult(result: InitiatePaymentDomain) =
        GooglePayPayment(
            iPageSessionIdForGPay = result.paymentRequiredDetails?.sessionId ?: EMPTY_STRING_DOMAIN,
            providerUrl = result.paymentRequiredDetails?.providerUrl ?: EMPTY_STRING_DOMAIN
        )
}
