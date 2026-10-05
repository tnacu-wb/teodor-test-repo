package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `POST /ohip/v1/rooms/allocate`, mirroring the service's criteria wrapper. */
@Serializable
data class RoomAllocationRequest(
    val criteria: RoomAllocationCriteria,
)

/** The room-assignment criteria: which reservation gets which room. */
@Serializable
data class RoomAllocationCriteria(
    val hotelId: String,
    val reservationIdList: List<ReservationIdEntry>,
    val roomId: String,
    val updateRoomTypeCharged: Boolean = false,
    val roomNumberLocked: Boolean = false,
)

/** One typed reservation identifier in the allocation criteria. */
@Serializable
data class ReservationIdEntry(
    val type: String = "Reservation",
    val id: String,
)

/** Response of `POST /ohip/v1/rooms/allocate`: only Opera's links survive the mapper. */
@Serializable
data class RoomAllocationResponse(
    val links: List<RoomAllocationLink> = emptyList(),
)

/** One Opera HATEOAS link echoed through the allocation response. */
@Serializable
data class RoomAllocationLink(
    val href: String? = null,
    val rel: String? = null,
    val templated: Boolean = false,
    val method: String? = null,
    val operationId: String? = null,
)

/** Response of `POST /ohip/v1/rooms/getVacant`: the hotel's clean-vacant rooms of one type. */
@Serializable
data class VacantRoomsResponse(
    val hotelRoomsDetails: VacantHotelRoomsDetails? = null,
)

/** The hotel id and room list the vacant-rooms mapper keeps from Opera's response. */
@Serializable
data class VacantHotelRoomsDetails(
    val room: List<VacantRoom> = emptyList(),
    val hotelId: String? = null,
)

/** One vacant room; the public DTO exposes only the room id and condition data. */
@Serializable
data class VacantRoom(
    val roomId: String? = null,
)

/** Response of `GET /ohip/v1/rooms/fetchHouseKeepingStatus`: one room's flat status view. */
@Serializable
data class HousekeepingStatusResponse(
    val hotelId: String? = null,
    val roomId: String? = null,
    val status: String? = null,
)

/** Response of `GET /ohip/v1/rooms/fetchReservationWithPreferences`. */
@Serializable
data class ReservationPreferencesResponse(
    val kioskPreferenceCollection: List<KioskPreferenceCollection> = emptyList(),
)

/** One Opera preference collection exposed by the kiosk-facing response. */
@Serializable
data class KioskPreferenceCollection(
    val kioskPreference: List<KioskPreference> = emptyList(),
    val preferenceType: String? = null,
    val preferenceTypeDescription: String? = null,
)

/** One preference value exposed by the kiosk-facing response. */
@Serializable
data class KioskPreference(
    val preferenceValue: String? = null,
    val description: String? = null,
)
