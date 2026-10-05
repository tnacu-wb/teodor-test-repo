package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPackagesRepository
import com.whitbread.premierinn.domain.graphql.common.getSelectedUpsellsWithoutDuplicates
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.utils.getId
import com.whitbread.premierinn.domain.graphql.utils.updatePreselectedNoOfSelections
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

typealias GetUpsellsResult = Result<UpsellsResultData, DataError.Network>

data class UpsellsResultData(
    val preselectedRoomSelections: List<RoomSelectionDomain>?,
    val availableUpsells: MutableList<UpsellDomainItem>
)

class GetUpsellsUseCase @Inject constructor(
    private val graphQLPackagesRepository: GraphQLPackagesRepository,
) {

    suspend operator fun invoke(packagesRequestBody: HotelPackagesRequestBody): Flow<GetUpsellsResult> =
        graphQLPackagesRepository.getPackages(packagesRequestBody).transform { result ->
            when (result) {
                is Result.Success -> {
                    val availableUpsellItems = mutableListOf<UpsellDomainItem>().apply {
                        addAll(result.data.meals)
                        addAll(result.data.mealsKids)
                        result.data.extrasItems?.let { addAll(it) }
                    }

                    // Update number of preselections for the availableUpsells
                    result.data.roomSelection
                        ?.flatMap { it.packagesSelection }
                        ?.getSelectedUpsellsWithoutDuplicates()
                        ?.forEach { packageSelection ->
                            availableUpsellItems
                                .firstOrNull { it.getId() == packageSelection.id }
                                ?.updatePreselectedNoOfSelections(packageSelection.noOfSelections)
                        }

                    emit(Result.Success(
                        UpsellsResultData(
                            result.data.roomSelection,
                            availableUpsellItems
                        )
                    ))
                }

                is Result.Error -> emit(result)
            }
        }
}

