package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLHotelPreferencesRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateHotelPreference
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPreferencesRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias UpdateReservationPreferencesResult = Result<Unit, DataError.Network>

private const val CIOL_PREFERENCE_GROUP_KEY = "EVENTS"

class UpdateReservationPreferencesUseCase @Inject constructor(
    private val hotelPreferencesRepository: GraphQLHotelPreferencesRepository
) {
    suspend operator fun invoke(
        hotelId: String,
        reservationsIds: List<String>,
        selectedOccasion: HotelPreferenceDomain,
        previousPreferences: List<HotelPreferenceDomain>
    ) : Flow<UpdateReservationPreferencesResult> {
        val preferencesCollections = mutableListOf<UpdateHotelPreference>().apply {
            // Remove previously selected EVENTS
            previousPreferences
                .filterNot { it.preferenceGroup == CIOL_PREFERENCE_GROUP_KEY }
                .map { it.toUpdateHotelPreference() }
                .let { filteredPreferences ->
                    if (filteredPreferences.isNotEmpty()) {
                        addAll(filteredPreferences)
                    }
                }

            add(selectedOccasion.toUpdateHotelPreference())
        }

        return hotelPreferencesRepository.updateReservationPreferences(
            UpdateReservationPreferencesRequestBody(
                hotelId = hotelId,
                reservationsIds = reservationsIds,
                preferencesCollections = preferencesCollections
            )
        )
    }
}

fun HotelPreferenceDomain.toUpdateHotelPreference() = UpdateHotelPreference(
    preferenceType = preferenceGroup,
    preferences = listOf(code)
)
