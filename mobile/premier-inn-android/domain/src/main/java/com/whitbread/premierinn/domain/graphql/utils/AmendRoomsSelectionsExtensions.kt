package com.whitbread.premierinn.domain.graphql.utils

import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendRoomsSelections

fun List<AmendRoomsSelections>.deepCopy() = this.map { roomSelectionDomain ->
    roomSelectionDomain.copy(
        reservationId = roomSelectionDomain.reservationId,
        packagesSelection = roomSelectionDomain.packagesSelection.map { it.copy() }
            .toMutableList()
    )
}

/**
 * This is used as previousRoomSelections for Amend functionality to delete upsells
 * As we are not deleting anything, we just need the reservationId with empty upsells list
 */
fun List<AmendRoomsSelections>.buildDeletedRoomSelections() =
    this.deepCopy().onEach { it.packagesSelection.clear() }

fun List<AmendRoomsSelections>.removeEmptySelections() =
    this.deepCopy().toMutableList().onEach { roomSelection ->
        roomSelection.packagesSelection.removeAll { it.noOfSelections == 0 }
    }
