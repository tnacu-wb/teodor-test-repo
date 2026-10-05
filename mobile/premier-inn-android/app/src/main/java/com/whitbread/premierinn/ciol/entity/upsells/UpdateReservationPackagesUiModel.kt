package com.whitbread.premierinn.ciol.entity.upsells

import android.os.Parcelable
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToAmendRoomsSelectionsList
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.utils.buildDeletedRoomSelections
import com.whitbread.premierinn.domain.graphql.utils.removeEmptySelections
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateReservationPackagesUiModel(
    val basketReference: String,
    val roomSelectionList: List<RoomSelection>,
    val hotelId: String,
    val arrivalDate: String,
    val departureDate: String
) : Parcelable

fun UpdateReservationPackagesUiModel.toUpdateReservationPackagesRequestBody() =
    UpdateReservationPackagesRequestBody(
        basketReferenceId = basketReference,
        hotelId = hotelId,
        arrivalDate = arrivalDate,
        departureDate = departureDate,
        roomSelections = roomSelectionList.convertToAmendRoomsSelectionsList().removeEmptySelections(),
        deletedRoomSelections = roomSelectionList.convertToAmendRoomsSelectionsList().buildDeletedRoomSelections()
    )