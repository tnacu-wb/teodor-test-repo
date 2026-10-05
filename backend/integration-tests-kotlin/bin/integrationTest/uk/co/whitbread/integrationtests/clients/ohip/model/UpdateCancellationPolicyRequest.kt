package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import uk.co.whitbread.integrationtests.testkit.model.IsoLocalDateSerializer
import java.time.LocalDate

/** `absoluteDeadline` is `java.util.Date` on the DTO; the service accepts `yyyy-MM-dd`. */
@Serializable
data class UpdateCancellationPolicyRequest(
    val hotelId: String,
    val reservationId: String,
    @Serializable(with = IsoLocalDateSerializer::class)
    val absoluteDeadline: LocalDate,
)
