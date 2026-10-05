package com.whitbread.premierinn.domain.graphql.common

import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain


fun List<PackagesSelectionDomain>.getSelectedUpsellsWithoutDuplicates() =
    this.map { it.copy() }.distinctBy { it.id }.map { item ->
        val itemNoOfSelections = this.filter { it.id == item.id }.sumOf { it.noOfSelections }
        item.noOfSelections = itemNoOfSelections
        item
    }