package com.whitbread.premierinn.domain.graphql.utils

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem

fun UpsellDomainItem.getId() = when(this) {
    is ExtrasItemDomain -> this.id
    is MealDomain -> this.id
}

fun UpsellDomainItem.getName() = when(this) {
    is ExtrasItemDomain -> this.name
    is MealDomain -> this.name
}

fun UpsellDomainItem.getPrice() : PriceDomain {
    return when(this) {
        is ExtrasItemDomain -> PriceDomain(this.price?.toFloat()?.coerceAtLeast(0f) ?: 0f, this.currency ?: GBP)
        is MealDomain -> PriceDomain(this.price?.toFloat() ?: 0f, this.currency ?: GBP)
    }
}

fun UpsellDomainItem.getShortDescription() = when(this) {
    is ExtrasItemDomain -> EMPTY_STRING_DOMAIN
    is MealDomain -> this.shortDescription
}

fun UpsellDomainItem.getDescription() = when(this) {
    is ExtrasItemDomain -> this.description
    is MealDomain -> this.description
}

fun UpsellDomainItem.getNumberOfSelections() = when(this) {
    is ExtrasItemDomain -> this.numberOfSelections
    is MealDomain -> this.numberOfSelections
}

fun UpsellDomainItem.isFreeForChildren() = when(this) {
    is ExtrasItemDomain -> false
    is MealDomain -> this.isFreeForChildren()
}

fun MealDomain.isFreeForChildren(): Boolean {
    return this.freeBreakfastCode != null && freeBreakfastCode.isNotEmpty()
}

fun UpsellDomainItem.updatePreselectedNoOfSelections(noOfSelections: Int) = when(this) {
    is ExtrasItemDomain -> this.preselectedNoOfSelections = noOfSelections
    is MealDomain -> this.preselectedNoOfSelections = noOfSelections
}
