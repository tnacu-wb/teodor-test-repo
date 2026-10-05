package com.whitbread.premierinn.ciol.entity.upsells

import android.content.Context
import android.os.Parcelable
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getFoodUpsellNameFromCode
import com.whitbread.premierinn.ciol.utils.getFreeBreakfastSelections
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getMealType
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.entity.MealType
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSelectedPackages
import kotlinx.parcelize.Parcelize

sealed class UpsellItem : Parcelable

sealed class UpsellEntry : UpsellItem(), Parcelable

sealed class UpsellHeader : UpsellItem(), Parcelable

@Parcelize
data class UpsellsSelectedHeader(val selectedUpsells: Int) : UpsellHeader()
@Parcelize
object UpsellsUnselectedHeader : UpsellHeader()

@Suppress("UNCHECKED_CAST")
fun List<UpsellDomainItem>.convertToUpsellItemList() = this.map { upsellDomainItem ->
    when(upsellDomainItem) {
        is ExtrasItemDomain -> upsellDomainItem.toExtrasItemUiModel()
        is MealDomain -> upsellDomainItem.toMealUiModel()
    }
}.toMutableList()

fun List<MealUiModel>.toBreakfastUiModel() = BreakfastUiModel(
    minPrice = this.minByOrNull { it.price ?: 0.0 }?.price,
    maxPrice = this.maxByOrNull { it.price ?: 0.0 }?.price,
    currency = this.firstOrNull()?.currency ?: GBP,
    imageUrl = this.firstOrNull()?.imageSrc ?: EMPTY_STRING,
    meals = this,
    displayAsEnabled = true
)

fun MutableList<UpsellEntry>.organizeToCategories() = this.let { upsellEntries ->
    val breakfastList = upsellEntries.filter { upsell ->
        upsell is MealUiModel && upsell.id.getMealType() == MealType.BREAKFAST
    }
    upsellEntries.removeAll(breakfastList)
    val breakfastItem = (breakfastList as List<MealUiModel>).toBreakfastUiModel()

    if (breakfastItem.meals.isNotEmpty()) {
        upsellEntries.add(0, breakfastItem)
    }
    upsellEntries
}

/**
 * Since we are working with MutableLists, we need to deep copy the list when passing as parameter,
 * in order to not pass the entire instance and prevent unpredictable changes to the list
 */
@JvmName("deepCopyUpsellItemList")
fun MutableList<UpsellItem>.copy() = this.map {
    when(it) {
        is BreakfastUiModel -> it.copy()
        is ExtrasItemUiModel -> it.copy()
        is MealUiModel -> it.copy()
        is UpsellsSelectedHeader -> it.copy()
        UpsellsUnselectedHeader -> it
    }
}

@JvmName("deepCopyUpsellEntryList")
fun MutableList<UpsellEntry>.copy() = this.map {
    when(it) {
        is BreakfastUiModel -> it.copy()
        is ExtrasItemUiModel -> it.copy()
        is MealUiModel -> it.copy()
    }
}

@JvmName("deepCopyImmutableUpsellEntryList")
fun List<UpsellEntry>.copy() = this.map {
    when(it) {
        is BreakfastUiModel -> it.copy()
        is ExtrasItemUiModel -> it.copy()
        is MealUiModel -> it.copy()
    }
}

fun List<UpsellEntry>.addItems(upsellsToUpdate: MutableList<UpsellEntry>) {
    this.forEach {
        when (it) {
            is BreakfastUiModel -> upsellsToUpdate.addAll(it.meals)
            is ExtrasItemUiModel -> upsellsToUpdate.add(it)
            is MealUiModel -> upsellsToUpdate.add(it)
        }
    }
}

fun UpsellEntry.convertToAmendSelectedPackages() = AmendSelectedPackages(
    id = this.getId(),
    noOfSelections = this.getNumberOfSelections()
)

fun UpsellEntry.buildUpsellPairForUpsellDetails(upsellItems: List<UpsellItem>, hasChildren: Boolean?) = when (this) {
    is ExtrasItemUiModel -> {
        this.id.getUpsellType() to listOf(this).map { it.copy() }
    }

    is BreakfastUiModel -> {
        val upsells = mutableListOf<UpsellEntry>()
        upsells.addAll(this.meals)
        // Add kidsMeal if the any MealDeal accepts freeBreakfast and children are present in booking
        if (this.meals.any { it.freeBreakfastOption == true } && hasChildren == true) {
            upsellItems
                .find { it is MealUiModel && it.id.isKidsMeal() }
                ?.let {
                    (it as MealUiModel).apply {
                        order = 999
                        noOfSelections = this.getFreeBreakfastSelections()
                    }
                    upsells.add(it)
                }
        }
        UpsellType.BREAKFAST to upsells.copy()
    }

    is MealUiModel -> {
        val upsells = mutableListOf<UpsellEntry>(this)
        // Add kidsMeal if the MealDeal accepts freeBreakfast and children are present in booking
        if (this.freeBreakfastOption == true && hasChildren == true) {
            upsellItems
                .find { it is MealUiModel && it.id.isKidsMeal() }
                ?.let {
                    (it as MealUiModel).apply {
                        order = 999
                        noOfSelections = this.getFreeBreakfastSelections()
                    }
                    upsells.add(it)
                }
        }
        UpsellType.MEAL_DEAL to upsells.copy()
    }
}

/**
 * Method used to retrieve all selected upsells names based on an upsellEntry type
 */
fun List<UpsellEntry>.getSelectedUpsellNamesWithKids(context: Context, upsellItem: UpsellEntry): String {
    var childrenBreakfastSelections = 0
    val upsellNamesBuilder = StringBuilder()

    this.filter { it.getId().getUpsellType() == upsellItem.getId().getUpsellType() && it.getNumberOfSelections() > 0 }
        .filterNot { it.getId().isKidsMeal() }
        .forEach {
            if (it is MealUiModel && it.freeBreakfastSelections > 0) {
                childrenBreakfastSelections += it.freeBreakfastSelections
            }
            upsellNamesBuilder.append(it.getUpsellName(context)).append(StringUtils.LINE_BREAK)
        }

    if (childrenBreakfastSelections > 0) {
        upsellNamesBuilder.append(
            UpsellItemId.FREE_CHILD_BREAKFAST.id.getFoodUpsellNameFromCode(
                context, childrenBreakfastSelections
            )
        )
    }

    return upsellNamesBuilder.toString().trim()
}
