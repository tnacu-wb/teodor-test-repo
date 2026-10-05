package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Body of `POST /ohip/v1/reservations/attachments`: a base64 PDF linked to a reservation. */
@Serializable
data class ReservationFileAttachmentRequest(
    val fileName: String,
    val reservationId: String,
    val hotelId: String,
    /** Base64-encoded PDF content. */
    val fileAttachment: String,
    val description: String? = null,
    val global: Boolean? = null,
    val overwriteExistingFile: Boolean? = null,
)
