package com.whitbread.premierinn.domain.graphql.guestDetails.repository

import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.FormattedAddressDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PartialAddressDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.*
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody
import io.reactivex.Single

interface GraphQLGuestDetailsRepository {

    fun createReservationGuest(token: String?, input: CreateReservationGuestRequestBody): Single<CreateReservationGuestDomain>

    fun getPartialAddress(input: PartialAddressRequestBody): Single<PartialAddressDomain>

    fun getFormattedAddress(id: String): Single<FormattedAddressDomain>
}