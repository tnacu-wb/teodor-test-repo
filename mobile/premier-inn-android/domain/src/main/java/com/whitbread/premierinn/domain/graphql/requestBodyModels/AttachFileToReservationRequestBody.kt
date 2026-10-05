package com.whitbread.premierinn.domain.graphql.requestBodyModels

private const val DESCRIPTION = "Pre-check-in registration card"

data class AttachFileToReservationRequestBody(
        val fileName: String,
        val reservationId: String,
        val hotelId: String,
        val fileAttachment: String,
        val overwriteExistingFile: Boolean = true,
        val description: String = DESCRIPTION,
        val global: Boolean = true,
)
