package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@JsonSchema.Description(
    "One scenario's world of test data. Every field is a gate: mocks are installed only for " +
        "the data that is present, so a Booking describes the world the scenario needs, not " +
        "one endpoint call.",
)
data class Booking(
    @JsonSchema.Description(
        "Hotels this scenario knows. Presence gates the per-hotel Opera and AEM hotel stubs.",
    )
    val hotels: List<Hotel> = emptyList(),
    @JsonSchema.Description(
        "Companies this scenario knows. Presence gates the CDH company-search and per-company " +
            "Opera profile stubs.",
    )
    val companies: List<Company> = emptyList(),
    @Serializable(with = IsoLocalDateSerializer::class)
    @JsonSchema.Description("Stay arrival date (ISO-8601). With departure, forms the scenario's availability window.")
    val arrival: LocalDate? = null,
    @Serializable(with = IsoLocalDateSerializer::class)
    @JsonSchema.Description("Stay departure date (ISO-8601). Must be after arrival.")
    val departure: LocalDate? = null,
    @JsonSchema.Description(
        "Rooms in the booking. Opera reservation stubs also require a hotel, both stay dates, " +
            "and roomType and adults on each selected room. A room with a guestProfile gates " +
            "the Opera profile stubs.",
    )
    val rooms: List<BookingRoom> = emptyList(),
    @JsonSchema.Description("AEM content the scenario needs. Each non-null section gates its own AEM stub.")
    val aem: Aem? = null,
    @JsonSchema.Description(
        "The authenticated user, if the scenario is logged in. Its nested data gates the CDH " +
            "and Worldline account stubs.",
    )
    val loggedUser: LoggedUser? = null,
    @JsonSchema.Description(
        "Digital booking reference Opera holds as the external reference of every reservation in " +
            "this booking. Present when the booking owns a basket; basket-service generates it, " +
            "and Opera may append a per-room suffix that consumers strip.",
    )
    val bookingReference: String? = null,
    @JsonSchema.Description(
        "Legacy or Opera confirmation reference under which CDH indexes this booking. Presence " +
            "gates the CDH reservation-search stub and an empty Opera external-reference result " +
            "so confirmation-number fallback can be modelled coherently.",
    )
    val cdhBookingReference: String? = null,
    @JsonSchema.Description(
        "Opera idContext associated with bookingReference. Defaults to the digital basket context.",
    )
    val bookingReferenceIdContext: String = "WB_DIGITAL",
    @JsonSchema.Description(
        "Person who made the booking, distinct from the guests staying in the rooms. Presence " +
            "adds a Booker object to every CDH reservation-search result, which is what a " +
            "bookerLastName search filters on; without it the results carry no booker at all.",
    )
    val booker: Booker? = null,
    @JsonSchema.Description(
        "Present only when the scenario takes a card payment. Datatrans stubs also require at " +
            "least one hotel and room, both stay dates, and an available rate on the first " +
            "hotel that matches the first room's roomType, adults, and children.",
    )
    val cardPayment: CardPayment? = null,
) {
    val hotel: Hotel
        get() = hotels.firstOrNull() ?: error("booking.hotel requires at least one hotel in booking.hotels")

    val room: BookingRoom
        get() = rooms.singleOrNull() ?: error("booking.room requires exactly one room in booking.rooms")

    init {
        require(bookingReference == null || bookingReference.isNotBlank()) {
            "booking.bookingReference must not be blank"
        }
        require(cdhBookingReference == null || cdhBookingReference.isNotBlank()) {
            "booking.cdhBookingReference must not be blank"
        }
        require(bookingReferenceIdContext.isNotBlank()) {
            "booking.bookingReferenceIdContext must not be blank"
        }
        if (arrival != null && departure != null) {
            require(departure.isAfter(arrival)) { "booking.departure must be after booking.arrival" }
        }
        // The attached company must be a company this world knows, so the company read and
        // amend capabilities its journeys need are installed by the same Booking.
        rooms.forEach { room ->
            room.attachedCompanyProfileId?.let { profileId ->
                require(companies.any { company -> company.companyId == profileId }) {
                    "bookingRoom.attachedCompanyProfileId \"$profileId\" must name a Company " +
                        "in booking.companies by its companyId"
                }
            }
        }
        // The adapter restamps every reservation with ONE globally resolved first-primary-guest
        // profile id (HotelReservationOutPortImpl.getBookerProfileId), and its concurrent
        // unordered reads make the winner nondeterministic when candidates differ — the world is
        // only modellable when every stated guest profile collapses to one id, which also makes
        // each room's pinned PUT id equal to the global one.
        if (rooms.any { it.bookerEmailAfterUpdate != null }) {
            val guestProfileIds = rooms.mapNotNull { it.guestProfile?.profileId }.distinct()
            require(guestProfileIds.size <= 1) {
                "bookingRoom.bookerEmailAfterUpdate requires every room's guestProfile to share " +
                    "one profileId (found $guestProfileIds): the adapter resolves a single " +
                    "booker profile id across all reservation reads"
            }
        }
        // Both facts pin the same own-guest-profile PUT body, so a Booking stating both would
        // silently prove only one of them.
        require(booker?.billingAddress == null || rooms.none { it.bookerEmailAfterUpdate != null }) {
            "booking.booker.billingAddress and bookingRoom.bookerEmailAfterUpdate pin the same " +
                "guest-profile update body: state one per Booking"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "Identity of the person who made the booking, as CDH holds it. Independent of any room's " +
        "guestProfile: the booker need not be staying.",
)
data class Booker(
    val firstName: String,
    val lastName: String,
    @JsonSchema.Description("Booker title, e.g. Mr. Reported only when the scenario states one.")
    val title: String? = null,
    @JsonSchema.Description("Booker email address. Reported only when the scenario states one.")
    val email: String? = null,
    @JsonSchema.Description(
        "Billing address Opera holds on the booker's profiles after a billing-address update. " +
            "Presence makes each room guest profile's Opera update accept only the BILLING-typed " +
            "address body carrying exactly these lines; absent, profile updates stay " +
            "body-permissive.",
    )
    val billingAddress: BillingAddress? = null,
) {
    init {
        require(firstName.isNotBlank()) { "booker.firstName must not be blank" }
        require(lastName.isNotBlank()) { "booker.lastName must not be blank" }
        require(title == null || title.isNotBlank()) { "booker.title must not be blank" }
        require(email == null || email.isNotBlank()) { "booker.email must not be blank" }
    }
}

@Serializable
@JsonSchema.Description(
    "The billing address Opera holds on a profile after a billing-address write: a BILLING-typed " +
        "address whose lines, city, and country the update body carries, marked as the profile's " +
        "primary address when primaryAddress is stated.",
)
data class BillingAddress(
    @JsonSchema.Description("Address lines the written BILLING address carries, in order.")
    val addressLines: List<String>,
    val cityName: String,
    val postalCode: String,
    @JsonSchema.Description("Country the update body carries as address.country.value.")
    val countryCode: String,
    @JsonSchema.Description("Whether Opera holds this as the profile's primary address after the write.")
    val primaryAddress: Boolean = false,
) {
    init {
        require(addressLines.isNotEmpty()) { "billingAddress.addressLines must not be empty" }
        require(addressLines.all { it.isNotBlank() }) { "billingAddress.addressLines must not contain blanks" }
        require(cityName.isNotBlank()) { "billingAddress.cityName must not be blank" }
        require(postalCode.isNotBlank()) { "billingAddress.postalCode must not be blank" }
        require(countryCode.isNotBlank()) { "billingAddress.countryCode must not be blank" }
    }
}
