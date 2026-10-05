package com.whitbread.premierinn.domain.graphql.guestDetails.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.FormattedAddressDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PartialAddressDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.repository.GraphQLGuestDetailsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import javax.inject.Inject

//M
class GraphQLGuestDetailsUseCase @Inject constructor (
    private val graphQLGuestDetailsRepository: GraphQLGuestDetailsRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val isCustomerLoggedIn: IsCustomerLoggedIn
) {

    fun createReservationGuest(input: CreateReservationGuestRequestBody): Single<CreateReservationGuestDomain> {
        return isCustomerLoggedIn()
            .flatMap { isLoggedIn ->
                if (isLoggedIn) {
                    authenticationRepository.getIdToken()
                        .flatMap { token ->
                            graphQLGuestDetailsRepository.createReservationGuest(token, input)
                        }
                        .retryWhen(getFreshIdTokenAndRetryOnce())
                        .subscribeOn(Schedulers.io())
                } else {
                    graphQLGuestDetailsRepository.createReservationGuest(null, input)
                        .subscribeOn(Schedulers.io())
                }
            }
    }

    fun getPartialAddress(input: PartialAddressRequestBody): Single<PartialAddressDomain> {
        return graphQLGuestDetailsRepository.getPartialAddress(input)
    }

    fun getFormattedAddress(id: String): Single<FormattedAddressDomain> {
        return graphQLGuestDetailsRepository.getFormattedAddress(URLEncoder.encode(id,"UTF-8"))
    }
}