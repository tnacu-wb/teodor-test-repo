package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreStayInfoRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.domain.utils.await
import javax.inject.Inject

class UpdateReservationGuestsUseCase @Inject constructor(
    private val preStayInfoRepository: GraphQLPreStayInfoRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase,
    private val getCountries: GetCountries
) {

    suspend operator fun invoke(input: CreateReservationGuestRegCardRequestBody) =
        preStayInfoRepository.createReservationGuestForRegCard(getIdToken(), input.replaceNationalityWithCountryCode())

    private fun CreateReservationGuestRegCardRequestBody.replaceNationalityWithCountryCode() =
        this.copy(
            stayingGuests = this.stayingGuests.map { stayingRegCardGuest ->
                stayingRegCardGuest.copy(
                    address = getIsoCodeFromCountryNameUseCase(stayingRegCardGuest.address?.countryCode)?.let { countryCode ->
                        stayingRegCardGuest.address?.copy(
                            countryCode = countryCode
                        )
                    },
                    additionalDetails = stayingRegCardGuest.additionalDetails.copy(
                        nationality = getIsoCodeFromCountryNameUseCase(stayingRegCardGuest.additionalDetails.nationality)
                    )
                )
            }
        )

    private suspend fun getIdToken(): String? =
        runCatching {
            if (isCustomerLoggedIn().await()) {
                authenticationRepository.getIdToken().await()
            } else {
                null
            }
        }.getOrElse {
            // Do not handle exception, as null comes whenever user is not logged in
            null
        }
}
