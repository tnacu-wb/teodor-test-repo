package com.whitbread.premierinn.domain.graphql.reviewBooking.repository

import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain
import io.reactivex.Single

interface GraphQLReviewBookingRepository {

    fun initiatePayment(input: InitiatePaymentRequestBody): Single<InitiatePaymentDomain>

    fun initiatePaypalPayment(input: InitiatePaymentRequestBody): Single<InitiatePaypalPaymentDomain>

    fun getBasketStatusRevisedPayments(basketReference: String): Single<BasketStatusRevisedPaymentsDomain>

    fun createReservationGuest(token: String?, input: CreateReservationGuestRequestBody): Single<CreateReservationGuestDomain>

    fun getPaymentMethodsAndDonationsAndBookingConfirmation(
        paymentMethodsRequestBody: PaymentMethodsRequestBody,
        token: String?,
        donationInput: DonationsRequestBody?,
        isInnBusinessUser: Boolean
    ): Single<PaymentMethodsWithDonationAndBookingConfirmationGQLDomain>
}