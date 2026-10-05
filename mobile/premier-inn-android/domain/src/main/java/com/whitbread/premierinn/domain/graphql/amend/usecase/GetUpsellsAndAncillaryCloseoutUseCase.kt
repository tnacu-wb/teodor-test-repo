package com.whitbread.premierinn.domain.graphql.amend.usecase

import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
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

typealias GetUpsellAndAncillaryCloseoutResult = Result<PackagesAndAncillariesCloseOutData, DataError.Network>

data class PackagesAndAncillariesCloseOutData(
    val preselectedRoomSelections: List<RoomSelectionDomain>?,
    val availableUpsells: MutableList<UpsellDomainItem>,
    val ancillaryCloseout: List<AncillaryCloseOutItem>
)

class GetUpsellsAndAncillaryCloseoutUseCase @Inject constructor(
    private val graphQlAmendRepository: GraphQLAmendRepository,
) {

    suspend operator fun invoke(packagesRequestBody: HotelPackagesRequestBody): Flow<GetUpsellAndAncillaryCloseoutResult> =
        graphQlAmendRepository.getPackagesAndAncillariesCloseoutInfo(packagesRequestBody).transform { result ->
            when (result) {
                is Result.Success -> {
                    val availableUpsellItems = mutableListOf<UpsellDomainItem>().apply {
                        addAll(result.data.packages.packages.meals)
                        addAll(result.data.packages.packages.mealsKids)
                        result.data.packages.packages.extrasItems?.let { addAll(it) }
                    }

                    // Update number of preselections for the availableUpsells
                    result.data.packages.packages.roomSelection
                        ?.flatMap { it.packagesSelection }
                        ?.getSelectedUpsellsWithoutDuplicates()
                        ?.forEach { packageSelection ->
                            availableUpsellItems
                                .firstOrNull { it.getId() == packageSelection.id }
                                ?.updatePreselectedNoOfSelections(packageSelection.noOfSelections)
                        }

                    emit(
                        Result.Success(
                            PackagesAndAncillariesCloseOutData(
                                result.data.packages.packages.roomSelection,
                                availableUpsellItems,
                                ancillaryCloseout = result.data.ancillaryCloseOutItems
                            )
                        )
                    )
                }

                is Result.Error -> emit(result)
            }
        }

}