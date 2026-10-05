package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@JsonSchema.Description(
    "A hotel this scenario knows. Presence gates the Opera hotel-config, room-types, and AEM " +
        "hotel stubs; availableRates and packageCatalogue gate the rate and package stubs.",
)
data class Hotel(
    @JsonSchema.Description("Opera hotel code, e.g. HEAPTI.")
    val hotelId: String,
    @JsonSchema.Description("Three-letter marketing short code, e.g. AQN.")
    val shortId: String,
    val name: String,
    val addressLine: String,
    val city: String,
    val postcode: String,
    val country: String = "GB",
    @JsonSchema.Description("VAT jurisdiction code returned by mocked Opera cashiering, e.g. UK.")
    val vatRegion: String = "UK",
    val phone: String,
    @JsonSchema.Description("ISO currency the hotel trades in; drives package and rate currencies.")
    val currency: String = "GBP",
    val timeZone: String = "Europe/London",
    val roomCount: Int = 134,
    val chainCode: String = "WHBOC001",
    @JsonSchema.Description("Brand code, e.g. PI for Premier Inn.")
    val brandCode: String = "PI",
    @JsonSchema.Description(
        "Room types and stock the mocked Opera room-types and availability capabilities return for this hotel.",
    )
    val availableRoomTypes: List<HotelRoomType> = emptyList(),
    @JsonSchema.Description(
        "Rates the hotel offers. Presence gates the Opera rate-plan and rate-info stubs and, with room stock, " +
            "the reservation-scoped availability capability.",
    )
    val availableRates: List<Rate> = emptyList(),
    @JsonSchema.Description("Sellable inventory items. Presence gates the Opera item-inventory stub.")
    val itemInventory: HotelItemInventory? = null,
    @JsonSchema.Description(
        "Packages and donation packages the hotel sells. Presence gates the Opera package, " +
            "restaurant, and donation stubs.",
    )
    val packageCatalogue: PackageCatalogue? = null,
    @JsonSchema.Description(
        "Opera cancellation-reason catalogue for this hotel. Presence gates the Opera " +
            "cancellation-reasons stub, including an empty list.",
    )
    val cancellationReasons: List<HotelCancellationReason>? = null,
    @JsonSchema.Description(
        "Rate-plan cancellation policy configuration. A non-empty list gates the Opera " +
            "policy-schedules and cancel-policy-configs stubs.",
    )
    val cancellationPolicyRules: List<HotelCancellationPolicyRule> = emptyList(),
    @JsonSchema.Description("Promotion codes the hotel offers. A non-empty list gates the Opera promotion-codes stub.")
    val promotionCodes: List<HotelPromotionCode> = emptyList(),
    @JsonSchema.Description(
        "On-sale/migration status the Opera hotel-details endpoint reports. Presence gates the " +
            "Opera hotel-details-status stub.",
    )
    val onSaleStatus: HotelOnSaleStatus? = null,
    @JsonSchema.Description("Preference catalogue by group. A non-empty list gates the Opera hotel-preferences stub.")
    val preferenceGroups: List<HotelPreferenceGroup> = emptyList(),
    @JsonSchema.Description(
        "Sell restrictions the Opera restrictions endpoint reports. A non-empty list gates the " +
            "Opera restrictions stub.",
    )
    val restrictions: List<HotelRestriction> = emptyList(),
    @JsonSchema.Description(
        "The hotel's Opera front-office physical-room inventory with housekeeping state. A " +
            "non-empty list gates the Opera vacant-rooms and housekeeping-overview stubs.",
    )
    val physicalRooms: List<HotelPhysicalRoom> = emptyList(),
    @JsonSchema.Description(
        "Whether the hotel takes new-card payment through Datatrans rather than 3CP. When true " +
            "the AEM hotel-detail stub reports isDataTransEnabled and adds a Datatrans payment " +
            "provider alongside the 3CP one, which is what makes payment-methods-entity-service " +
            "resolve NEW_CARD to Datatrans instead of 3CP.",
    )
    val dataTransEnabled: Boolean = false,
)

@Serializable
@JsonSchema.Description(
    "One physical Opera room with its front-office and housekeeping state, as reported by the " +
        "Opera vacant-rooms and housekeeping-overview endpoints.",
)
data class HotelPhysicalRoom(
    @JsonSchema.Description("Opera room number, e.g. 101.")
    val roomId: String,
    @JsonSchema.Description("Opera room type code the room belongs to, e.g. DOUBLE.")
    val roomType: String,
    @JsonSchema.Description("Opera housekeeping room status, e.g. Clean or Dirty.")
    val housekeepingStatus: String = "Clean",
    @JsonSchema.Description("Opera front-office room status, e.g. Vacant or Occupied.")
    val frontOfficeStatus: String = "Vacant",
    @JsonSchema.Description("Floor the room is on; null when the scenario does not care.")
    val floor: String? = null,
) {
    init {
        require(roomId.isNotBlank()) { "hotelPhysicalRoom.roomId must not be blank" }
        require(roomType.isNotBlank()) { "hotelPhysicalRoom.roomType must not be blank" }
        require(housekeepingStatus.isNotBlank()) {
            "hotelPhysicalRoom.housekeepingStatus must not be blank"
        }
        require(frontOfficeStatus.isNotBlank()) {
            "hotelPhysicalRoom.frontOfficeStatus must not be blank"
        }
        require(floor == null || floor.isNotBlank()) { "hotelPhysicalRoom.floor must not be blank" }
    }
}

@Serializable
@JsonSchema.Description(
    "One rate plan's Opera cancellation policy configuration: the policy-schedules row that " +
        "maps the rate plan to a policy code, and that policy's cancel-penalty offsets.",
)
data class HotelCancellationPolicyRule(
    @JsonSchema.Description("Rate plan code the policy schedule applies to, e.g. SEMIFLEX.")
    val ratePlanCode: String,
    @JsonSchema.Description("Opera cancellation policy code, e.g. DOA.")
    val policyCode: String,
    @JsonSchema.Description("Days before arrival when the cancellation deadline falls.")
    val offsetFromArrivalDays: Int = 2,
    @JsonSchema.Description("Time of day of the deadline, HH:mm:ss.")
    val offsetDropTime: String = "18:00:00",
    @JsonSchema.Description("Penalty description Opera returns for the policy.")
    val description: String,
) {
    init {
        require(ratePlanCode.isNotBlank()) { "hotelCancellationPolicyRule.ratePlanCode must not be blank" }
        require(policyCode.isNotBlank()) { "hotelCancellationPolicyRule.policyCode must not be blank" }
        require(offsetFromArrivalDays >= 0) { "hotelCancellationPolicyRule.offsetFromArrivalDays must not be negative" }
        require(offsetDropTime.matches(Regex("""\d{2}:\d{2}:\d{2}"""))) {
            "hotelCancellationPolicyRule.offsetDropTime must be HH:mm:ss"
        }
        require(description.isNotBlank()) { "hotelCancellationPolicyRule.description must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One Opera promotion code with its booking and stay windows.")
data class HotelPromotionCode(
    @JsonSchema.Description("Opera promotion code, e.g. SUMMER25.")
    val code: String,
    @JsonSchema.Description("Promotion display name Opera returns as the default text.")
    val name: String,
    @Serializable(with = IsoLocalDateSerializer::class)
    val bookingStartDate: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val bookingEndDate: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val stayStartDate: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val stayEndDate: LocalDate,
) {
    init {
        require(code.isNotBlank()) { "hotelPromotionCode.code must not be blank" }
        require(name.isNotBlank()) { "hotelPromotionCode.name must not be blank" }
        require(!bookingEndDate.isBefore(bookingStartDate)) {
            "hotelPromotionCode.bookingEndDate must be on or after bookingStartDate"
        }
        require(!stayEndDate.isBefore(stayStartDate)) {
            "hotelPromotionCode.stayEndDate must be on or after stayStartDate"
        }
    }
}

@Serializable
@JsonSchema.Description("On-sale flag and PMS source the Opera hotel-details endpoint reports for the hotel.")
data class HotelOnSaleStatus(
    val onSale: Boolean = true,
    @JsonSchema.Description("PMS source code; OPERA reports as Opera, anything else reports as BART.")
    val pmsSource: String = "OPERA",
) {
    init {
        require(pmsSource.isNotBlank()) { "hotelOnSaleStatus.pmsSource must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One Opera preference group and the preferences it contains.")
data class HotelPreferenceGroup(
    @JsonSchema.Description("Opera preference group code, e.g. PILLOW.")
    val groupCode: String,
    val preferences: List<HotelPreference> = emptyList(),
) {
    init {
        require(groupCode.isNotBlank()) { "hotelPreferenceGroup.groupCode must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One Opera preference row within a preference group.")
data class HotelPreference(
    val code: String,
    val description: String,
    val orderSequence: Int = 1,
    val housekeeping: Boolean = false,
) {
    init {
        require(code.isNotBlank()) { "hotelPreference.code must not be blank" }
        require(description.isNotBlank()) { "hotelPreference.description must not be blank" }
    }
}

@Serializable
@JsonSchema.Description(
    "One Opera sell restriction for an inclusive date period. Scope fields are null for a " +
        "house-level restriction.",
)
data class HotelRestriction(
    @JsonSchema.Description("Opera restriction status code, e.g. Open or Close.")
    val status: String,
    @Serializable(with = IsoLocalDateSerializer::class)
    val start: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val end: LocalDate,
    @JsonSchema.Description("Room type the restriction applies to; null for house-level.")
    val roomType: String? = null,
    @JsonSchema.Description("Rate plan the restriction applies to; null for house-level.")
    val ratePlanCode: String? = null,
) {
    init {
        require(status.isNotBlank()) { "hotelRestriction.status must not be blank" }
        require(!end.isBefore(start)) { "hotelRestriction.end must be on or after start" }
    }
}

@Serializable
@JsonSchema.Description("One room type row in the hotel's Opera inventory.")
data class HotelRoomType(
    @JsonSchema.Description("Opera room class, e.g. ST.")
    val roomClass: String,
    @JsonSchema.Description("Opera room type code, e.g. DOUBLE.")
    val roomType: String,
    @JsonSchema.Description("Rooms of this type available; zero means sold out.")
    val numberOfRooms: Int,
    val accessible: Boolean = false,
    @JsonSchema.Description(
        "Inclusive periods whose room count overrides numberOfRooms; periods must not overlap.",
    )
    val inventoryPeriods: List<RoomInventoryPeriod> = emptyList(),
) {
    init {
        inventoryPeriods
            .sortedBy { period -> period.startDate }
            .zipWithNext()
            .forEach { (current, next) ->
                require(current.endDate.isBefore(next.startDate)) {
                    "hotelRoomType.inventoryPeriods must not overlap"
                }
            }
    }
}

@Serializable
@JsonSchema.Description("Room availability that overrides a room type's default count for an inclusive date period.")
data class RoomInventoryPeriod(
    @Serializable(with = IsoLocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val endDate: LocalDate,
    val numberOfRooms: Int,
) {
    init {
        require(!endDate.isBefore(startDate)) {
            "roomInventoryPeriod.endDate must be on or after startDate"
        }
        require(numberOfRooms >= 0) {
            "roomInventoryPeriod.numberOfRooms must not be negative"
        }
    }
}

@Serializable
@JsonSchema.Description("Sellable inventory items (e.g. cots) the mocked Opera item-inventory endpoint returns.")
data class HotelItemInventory(
    val items: List<HotelInventoryItem> = emptyList(),
)

@Serializable
@JsonSchema.Description("One Opera cancellation reason returned by the hotel's list-of-values catalogue.")
data class HotelCancellationReason(
    @JsonSchema.Description("Opera reason code, e.g. CXL or ILL.")
    val code: String,
    val name: String,
    val description: String,
    val active: Boolean = true,
) {
    init {
        require(code.isNotBlank()) { "hotelCancellationReason.code must not be blank" }
        require(name.isNotBlank()) { "hotelCancellationReason.name must not be blank" }
        require(description.isNotBlank()) { "hotelCancellationReason.description must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One sellable inventory item with its total and remaining availability.")
data class HotelInventoryItem(
    val code: String,
    val name: String,
    val total: Int,
    val available: Int = total,
    val description: String = name,
) {
    init {
        require(code.isNotBlank()) { "hotelInventoryItem.code must not be blank" }
        require(name.isNotBlank()) { "hotelInventoryItem.name must not be blank" }
        require(total >= 0) { "hotelInventoryItem.total must not be negative" }
        require(available in 0..total) {
            "hotelInventoryItem.available must be between zero and total"
        }
    }
}
