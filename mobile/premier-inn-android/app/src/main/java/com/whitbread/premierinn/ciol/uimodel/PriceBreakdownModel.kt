package com.whitbread.premierinn.ciol.uimodel

import android.os.Parcelable
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.convertToUpsellItemList
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.mapper.toPriceDomainModel
import com.whitbread.premierinn.ciol.utils.getCurrency
import com.whitbread.premierinn.ciol.utils.getFreeBreakfastSelections
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.getUpsellPrice
import com.whitbread.premierinn.common.mapper.PriceDomainParcelable
import com.whitbread.premierinn.common.mapper.toParcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdown
import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdownRoomSelection
import com.whitbread.premierinn.domain.ciol.entity.isMealType
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceBreakdownModel(
    val outstandingBalance: PriceDomainParcelable,
    val roomSelections: List<RoomSelection>,
    val nights: Int
) : Parcelable{

    companion object{
        fun createDefault(): PriceBreakdownModel {
            return PriceBreakdownModel(PriceDomain.createDefault().toParcelable(), emptyList(), 0)
        }
    }
}

fun PriceBreakdownModel.mapToDomainModel() = PriceBreakdown(
    outstandingBalance = outstandingBalance.toPriceDomainModel(),
    roomSelections = roomSelections.map { it.mapToPriceBreakdownRoomSelection() },
    nights = nights
)

fun RoomSelection.mapToPriceBreakdownRoomSelection() = PriceBreakdownRoomSelection(
    reservationId = reservationId,
    packagesSelection = selectedUpsells.map { it.mapToUpsellDomain() }
)

fun UpsellEntry.mapToUpsellDomain(): UpsellDomainItem = if (this.getId().isMealType()) {
    MealDomain(
        id = getId(),
        name = getUpsellName(),
        price = getUpsellPrice(),
        currency = getCurrency(),
        numberOfSelections = getNumberOfSelections(),
        freeBreakfastSelections = getFreeBreakfastSelections()
    )
} else {
    ExtrasItemDomain(
        id = getId(),
        name = getUpsellName(),
        price = getUpsellPrice(),
        currency = getCurrency(),
        numberOfSelections = getNumberOfSelections()
    )
}

fun PriceBreakdown.mapToUiModel() = PriceBreakdownModel(
    outstandingBalance = outstandingBalance.mapToPriceDomainParcelable(),
    roomSelections = roomSelections.map { it.mapToRoomSelectionUiModel() },
    nights = nights
)

fun PriceBreakdownRoomSelection.mapToRoomSelectionUiModel() = RoomSelection(
    reservationId = reservationId ?: EMPTY_STRING,
    selectedUpsells = packagesSelection.convertToUpsellItemList()
)

fun PriceDomain.mapToPriceDomainParcelable() = PriceDomainParcelable(
    amount = amount,
    currency = currency
)
