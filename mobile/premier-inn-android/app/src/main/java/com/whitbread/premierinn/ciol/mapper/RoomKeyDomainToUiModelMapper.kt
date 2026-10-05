package com.whitbread.premierinn.ciol.mapper

import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain

fun RoomKeyInstructionsDomain.convertToUiModel(bookingReference: String?, hotelImage: String? = null, hotelId: String?) =
    RoomKeyInstructionsModel(
        hotelImage = hotelImage,
        contentTitle = this.title,
        roomKeyInstructions = this.description,
        bookingReference = bookingReference ?: EMPTY_STRING,
        hotelId = hotelId ?: EMPTY_STRING
    )
