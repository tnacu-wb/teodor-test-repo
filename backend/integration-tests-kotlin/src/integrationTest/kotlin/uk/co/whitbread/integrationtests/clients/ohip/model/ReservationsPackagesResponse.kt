package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Response of `GET /ohip/v1/reservations/ancillaries`: one [RoomsSelections] entry per
 * requested reservation id, in the requested order, plus the rate plan code of the first
 * reservation read. `roomsSelections` is null when Opera decoded no reservation at all.
 */
@Serializable
data class ReservationsPackagesResponse(
    val roomsSelections: List<RoomsSelections>? = null,
    val ratePlanCode: String? = null,
)

/** The packages attached to one reservation; `packagesSelection` is null when it carries none. */
@Serializable
data class RoomsSelections(
    val packagesSelection: List<PackagesSelection>? = null,
)

/** One package (ancillary) on a reservation. */
@Serializable
data class PackagesSelection(
    val id: String? = null,
    val price: Double? = null,
    val noSelections: Int? = null,
    val scheduledList: List<String>? = null,
    val packageGroup: String? = null,
    val ratePlanCode: String? = null,
)
