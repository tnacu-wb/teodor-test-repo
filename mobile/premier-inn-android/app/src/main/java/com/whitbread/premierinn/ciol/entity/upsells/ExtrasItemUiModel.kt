package com.whitbread.premierinn.ciol.entity.upsells

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtrasItemUiModel(
    val id: String,
    val name: String = EMPTY_STRING,
    val price: Double? = null,
    val description: String = EMPTY_STRING,
    val imageSrc: String = EMPTY_STRING,
    val currency: String = GBP,
    val order: Int? = null,
    val available: Int? = null,
    val formattedPrice: String? = null,
    val preselectedNoOfSelections: Int,
    var noOfSelections: Int,
) : UpsellEntry() {
    fun isSelected() = noOfSelections > 0

    fun isPreSelected() = preselectedNoOfSelections > 0
}

fun ExtrasItemDomain.toExtrasItemUiModel() = ExtrasItemUiModel(
    id = id,
    name = name ?: EMPTY_STRING,
    price = price,
    description = description ?: EMPTY_STRING,
    imageSrc = imageSrc ?: EMPTY_STRING,
    currency = currency ?: "GBP",
    order = order,
    available = available,
    formattedPrice = formattedPrice,
    preselectedNoOfSelections = preselectedNoOfSelections,
    noOfSelections = numberOfSelections
)
