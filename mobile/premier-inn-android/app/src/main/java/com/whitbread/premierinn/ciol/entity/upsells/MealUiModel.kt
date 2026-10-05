package com.whitbread.premierinn.ciol.entity.upsells

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealUiModel(
    val id: String,
    val name: String = EMPTY_STRING,
    val price: Double? = null,
    val totalPrice: Double? = null,
    val freeBreakfastOption: Boolean? = null,
    val freeBreakfastCode: String? = null,
    val description: String = EMPTY_STRING,
    val shortDescription: String = EMPTY_STRING,
    val imageSrc: String = EMPTY_STRING,
    val allergyInfoSrc: String? = null,
    val currency: String = GBP,
    var order: Int? = null,
    val preselectedNoOfSelections: Int,
    var noOfSelections: Int,
    var freeBreakfastSelections: Int,
    var displayAsEnabled: Boolean,
    val menu: MenuUiModel? = null,
) : UpsellEntry() {

    fun isSelected() = noOfSelections > 0

    fun isPreSelected() = preselectedNoOfSelections > 0
}

@Parcelize
data class MealUiModelList(
    val mealUiModelList: List<MealUiModel>
) : Parcelable

fun MealDomain.toMealUiModel() = MealUiModel(
    id = id ?: EMPTY_STRING,
    name = name,
    price = price,
    totalPrice = totalPrice,
    freeBreakfastOption = freeBreakfastOption,
    freeBreakfastCode = freeBreakfastCode,
    description = description ?: EMPTY_STRING,
    shortDescription = shortDescription ?: EMPTY_STRING,
    imageSrc = imageSrc ?: EMPTY_STRING,
    allergyInfoSrc = allergyInfoSrc,
    currency = currency ?: GBP,
    order = order,
    preselectedNoOfSelections = preselectedNoOfSelections,
    noOfSelections = numberOfSelections,
    freeBreakfastSelections = freeBreakfastSelections,
    displayAsEnabled = true,
    menu = menu?.convertToUiModel()
)

fun MealUiModel.toMealDomainModel() = MealDomain(
    id = id,
    name = name,
    price = price,
    totalPrice = totalPrice,
    freeBreakfastOption = freeBreakfastOption,
    freeBreakfastCode = freeBreakfastCode,
    description = description,
    imageSrc = imageSrc,
    currency = currency,
    order = order
)
