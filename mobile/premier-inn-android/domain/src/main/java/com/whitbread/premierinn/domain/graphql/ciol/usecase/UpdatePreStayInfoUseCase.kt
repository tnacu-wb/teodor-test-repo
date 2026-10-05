package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreStayInfoRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdatePreStayInfoRequestBody
import com.whitbread.premierinn.domain.utils.await
import javax.inject.Inject

class UpdatePreStayInfoUseCase @Inject constructor(
    private val preStayRepository: GraphQLPreStayInfoRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase
) {

    suspend operator fun invoke(preStayRequestBody: UpdatePreStayInfoRequestBody) =
        preStayRepository.updatePreStayInfo(getIdToken(), preStayRequestBody.replaceNationalityWithCountryCode())

    private fun UpdatePreStayInfoRequestBody.replaceNationalityWithCountryCode() =
        this.copy(
            stayingGuests = this.stayingGuests.map { stayingGuest ->
                stayingGuest.copy(
                    stayingGuestDetails = stayingGuest.stayingGuestDetails.copy(
                        additionalDetails = stayingGuest.stayingGuestDetails.additionalDetails?.copy(
                            nationality = getIsoCodeFromCountryNameUseCase(stayingGuest.stayingGuestDetails.additionalDetails.nationality)
                        )
                    ),
                    accompanyingGuestDetails = stayingGuest.accompanyingGuestDetails?.copy(
                        additionalDetails = stayingGuest.accompanyingGuestDetails.additionalDetails?.copy(
                            nationality = getIsoCodeFromCountryNameUseCase(stayingGuest.accompanyingGuestDetails.additionalDetails.nationality)
                        )
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
