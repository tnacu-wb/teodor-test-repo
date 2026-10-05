package com.whitbread.premierinn.domain.graphql.reviewBooking.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import io.reactivex.Single
import javax.inject.Inject
import io.reactivex.schedulers.Schedulers

class GraphQLReviewBookingUseCase @Inject constructor(
    val repository: GraphQLReviewBookingRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce
) {

    fun initiatePayment(input: InitiatePaymentRequestBody): Single<InitiatePaymentDomain> {
        return repository.initiatePayment(input)
    }

    fun initiatePaypalPayment(input: InitiatePaymentRequestBody): Single<InitiatePaypalPaymentDomain> {
        return repository.initiatePaypalPayment(input)
    }

    fun getBasketStatusRevisedPayments(basketReference: String): Single<BasketStatusRevisedPaymentsDomain> {
        return repository.getBasketStatusRevisedPayments(basketReference)
    }

    fun createReservationGuest(input: CreateReservationGuestRequestBody): Single<CreateReservationGuestDomain> {
        return isCustomerLoggedIn()
            .flatMap { isLoggedIn ->
                if (isLoggedIn) {
                    authenticationRepository.getIdToken()
                        .flatMap { token ->
                            repository.createReservationGuest(token, input)
                        }
                        .retryWhen(getFreshIdTokenAndRetryOnce())
                        .subscribeOn(Schedulers.io())
                } else {
                    repository.createReservationGuest(null, input)
                        .subscribeOn(Schedulers.io())
                }
            }
    }

    fun getPaymentMethodsWithDonationAndBookingConfirmation(
        paymentMethodsRequestBody: PaymentMethodsRequestBody,
        donationsRequestBody: DonationsRequestBody?,
        isInnBusinessUser: Boolean
    ): Single<PaymentMethodsWithDonationAndBookingConfirmationGQLDomain> {
        return isCustomerLoggedIn()
            .flatMap { isLoggedIn ->
                if (isLoggedIn) {
                    authenticationRepository.getIdToken()
                        .flatMap { token ->
                            repository.getPaymentMethodsAndDonationsAndBookingConfirmation(
                                paymentMethodsRequestBody,
                                token,
                                donationsRequestBody,
                                isInnBusinessUser
                            )
                        }
                        .retryWhen(getFreshIdTokenAndRetryOnce())
                        .subscribeOn(Schedulers.io())
                } else {
                    repository.getPaymentMethodsAndDonationsAndBookingConfirmation(
                        paymentMethodsRequestBody,
                        null,
                        donationsRequestBody,
                        isInnBusinessUser
                    )
                        .subscribeOn(Schedulers.io())
                }
            }
    }

}