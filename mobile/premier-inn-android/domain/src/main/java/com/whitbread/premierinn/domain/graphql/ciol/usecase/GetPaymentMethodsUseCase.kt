package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.authentication.usecase.RefreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.common.PAY_NOW
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPaymentMethodsRepository
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodGQDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

typealias GetPaymentMethodsResult = Result<List<PaymentMethodGQDomain>, DataError.Network>

private const val PAYPAL_CARD_TYPE = "PAYPAL"
private const val GOOGLE_PAY_CARD_TYPE = "GP"
private const val APPLE_PAY_CARD_TYPE = "AP"

class GetPaymentMethodsUseCase @Inject constructor(
    private val graphQLPaymentMethodsRepository: GraphQLPaymentMethodsRepository,
    private val isFeatureOn: IsFeatureOn,
    private val refreshIdTokenAndRetryOnce: RefreshIdTokenAndRetryOnce,
) {

    suspend operator fun invoke(paymentMethodsRequestBody: PaymentMethodsRequestBody, isPibaCPFlow: Boolean = false): Flow<GetPaymentMethodsResult> =
        graphQLPaymentMethodsRepository.getPaymentMethods(paymentMethodsRequestBody)
            .retry(retries = 1) { cause ->
                refreshIdTokenAndRetryOnce(cause)
            }.catch {
                emit(Result.Error(DataError.Network.GraphQlError(it.message)))
            }.transform { result ->
                when (result) {
                    is Result.Error -> emit(result)

                    is Result.Success -> emit(
                        Result.Success(
                            result.data
                                .asSequence()
                                .filter(::paymentOptionIsEnabled)
                                .filter { paymentMethodHasPayNowOption(it, isPibaCPFlow) }
                                .filterNot(::paymentOptionIsPaypal)
                                .filterNot(::paymentOptionIsGooglePay)
                                .filterNot(::paymentOptionIsApplePay)
                                .toList()
                        )
                    )
                }
            }

    private fun paymentMethodHasPayNowOption(paymentMethod: PaymentMethodGQDomain, isPibaCPFlow: Boolean) =
        paymentMethod.paymentOptions.any { it.type == PAY_NOW && (it.enabled || isPibaCPFlow) }

    private fun paymentOptionIsEnabled(paymentMethod: PaymentMethodGQDomain) = paymentMethod.enabled

    private fun paymentOptionIsPaypal(paymentMethod: PaymentMethodGQDomain) =
        paymentMethod.type == PAYPAL_CARD_TYPE && !isPaypalAccepted()

    private fun paymentOptionIsGooglePay(paymentMethod: PaymentMethodGQDomain) =
        paymentMethod.type == GOOGLE_PAY_CARD_TYPE && !isGooglePayAccepted()

    private fun paymentOptionIsApplePay(paymentMethod: PaymentMethodGQDomain) =
        paymentMethod.type == APPLE_PAY_CARD_TYPE

    private fun isPaypalAccepted() =
        isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_PAYPAL)

    private fun isGooglePayAccepted() =
        isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_GOOGLE_PAY)
}
