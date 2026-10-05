package com.whitbread.premierinn.data.upsellavailable

import com.whitbread.premierinn.data.common.mapToPrice
import com.whitbread.premierinn.data.common.toPriceEntity
import com.whitbread.premierinn.domain.common.UpsellAttachment
import com.whitbread.premierinn.domain.common.UpsellAvailable

fun UpsellItemAvailableEntity.toDomain(): UpsellAvailable {
    return UpsellAvailable(
        description = this.description,
        foodUpsell = this.foodUpsell,
        freeBreakfastTrigger = this.freeBreakfastTrigger,
        availableForChildren = this.availableForChildren,
        code = this.code,
        freeBreakfastCode = this.freeBreakfastCode,
        legend = this.legend,
        unitCost = this.unitCost.mapToPrice()!!,
        freeBreakfastOption = this.freeBreakfastOption,
        attachments = this.attachments.mapToAttachmentsDomain()
    )
}

private fun List<UpsellAttachmentEntity?>.mapToAttachmentsDomain(): List<UpsellAttachment?> {
    return this.map {
        it.mapToAttachmentDomain()
    }
}

private fun UpsellAttachmentEntity?.mapToAttachmentDomain(): UpsellAttachment? {
    return UpsellAttachment(path = this?.path, label = this?.label)
}

fun UpsellAvailable.toAmendedUpsellItemsAvailableEntity(): UpsellItemAvailableEntity {
    return UpsellItemAvailableEntity(
        description = legend,
        foodUpsell = true,
        freeBreakfastTrigger = freeBreakfastTrigger,
        availableForChildren = availableForChildren,
        code = this.code,
        freeBreakfastCode = code,
        legend = this.legend,
        unitCost = unitCost.toPriceEntity(),
        freeBreakfastOption = freeBreakfastOption,
        attachments = emptyList()
    )
}

/**
 * Converting List[UpsellItemAvailableEntity] to List[UpsellAvailable]
 * **/
fun List<UpsellItemAvailableEntity>.toUpsellsAvailable(): List<UpsellAvailable> {
    return this.map {
        it.toDomain()
    }
}