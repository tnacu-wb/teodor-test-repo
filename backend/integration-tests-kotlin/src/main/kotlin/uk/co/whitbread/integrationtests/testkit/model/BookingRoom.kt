package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@JsonSchema.Description(
    "One room in the booking. Opera reservation read/update stubs require this room to " +
        "have a reservationId, roomType, and adults. The Booking must also have a hotel, " +
        "arrival date, and departure date.",
)
data class BookingRoom(
    @JsonSchema.Description("Opera reservation ID. Must be unique across rooms when present.")
    val reservationId: String? = null,
    @JsonSchema.Description("Opera room type code the room is booked into, e.g. DOUBLE.")
    val roomType: String? = null,
    @JsonSchema.Description(
        "Opera rate plan code this reservation is currently booked on, e.g. SEMIFLEX. " +
            "Presence disambiguates matching entries in the hotel's available-rate catalogue.",
    )
    val ratePlan: String? = null,
    @JsonSchema.Description(
        "Opera room type code this reservation is intended to carry after a room-type change. " +
            "Presence drives the grouped quantity matcher on reservation availability stubs.",
    )
    val roomTypeAfterUpdate: String? = null,
    val adults: Int? = null,
    val children: Int = 0,
    @JsonSchema.Description("Reservation lifecycle state the mocked Opera reservation reports.")
    val status: ReservationStatus = ReservationStatus.ON_HOLD,
    @Serializable(with = IsoLocalDateSerializer::class)
    @JsonSchema.Description(
        "Date this reservation was cancelled (ISO-8601). Only a cancelled room can carry one. " +
            "Presence adds CancellationDate to this room's CDH reservation-search result, which " +
            "is what keeps a cancelled booking inside the rolling one-year window consumers " +
            "filter on; without it a cancelled result is dropped from the search page.",
    )
    val cancellationDate: LocalDate? = null,
    @JsonSchema.Description("Opera source code used to classify the reservation's booking channel.")
    val sourceCode: String = "44",
    @JsonSchema.Description("Guest attached to the room. Presence gates the Opera profile stubs for this room.")
    val guestProfile: GuestProfile? = null,
    @JsonSchema.Description(
        "A second guest Opera already holds on this reservation as a non-primary reservation " +
            "guest, not attached as a reservation profile — the world in which a per-guest " +
            "profile update rather than a create is Opera's correct operation. The reservation " +
            "read renders it as a second reservationGuests entry with primary false and leaves " +
            "reservationProfiles untouched.",
    )
    val accompanyingGuestProfile: GuestProfile? = null,
    @JsonSchema.Description(
        "Opera profile id of the company profile Opera reports attached to this reservation — " +
            "the world of a reservation billed to or booked for a company. Must name a Company " +
            "in Booking.companies by its companyId, the id Opera serves that company's profile " +
            "under. The reservation read renders it as an extra reservationProfiles entry typed " +
            "Company carrying that id typed Profile, alongside the guest entries.",
    )
    val attachedCompanyProfileId: String? = null,
    @JsonSchema.Description("Packages already selected on the reservation, by catalogue code.")
    val selectedPackages: List<SelectedPackage> = emptyList(),
    @JsonSchema.Description(
        "Packages this reservation carries after an ancillary update, by catalogue code. " +
            "Presence makes the reservation-update stub accept only PUT bodies that add the " +
            "packages missing from selectedPackages and remove the ones no longer listed; an " +
            "empty list means every selected package is removed. Absent, updates accept any body.",
    )
    val selectedPackagesAfterUpdate: List<SelectedPackage>? = null,
    @JsonSchema.Description("Opera deposit policy code the reservation carries.")
    val depositPolicyCode: String = "DEP",
    @JsonSchema.Description(
        "Opera deposit policy code reported after this reservation is updated. When absent, " +
            "reservation reads keep reporting depositPolicyCode.",
    )
    val depositPolicyCodeAfterUpdate: String? = null,
    @JsonSchema.Description("Opera routing instructions attached to this reservation.")
    val routingInstructions: List<RoutingInstruction> = emptyList(),
    @JsonSchema.Description("Money already taken against this reservation, in the hotel's currency.")
    val amountAlreadyPaid: Double = 0.0,
    @JsonSchema.Description(
        "Money Opera reports posted on this reservation's cashiering folio, in the hotel's " +
            "currency. Absent it equals amountAlreadyPaid, so the folio and the reservation's own " +
            "money summary agree. Setting it models the realistic temporal case where a payment " +
            "landed on the folio after the summary was computed, which is the only world in which " +
            "the folio total overwriting the summary deposit is observable.",
    )
    val amountPostedOnFolio: Double? = null,
    @JsonSchema.Description("Payment reference attached to a posted Opera deposit for this reservation.")
    val depositPaymentReference: String? = null,
    @JsonSchema.Description(
        "Opera cancellation policies on this reservation, first entry current. Presence adds " +
            "cancellationPolicies to the reservation GET payload used to delete and rewrite " +
            "the first policy.",
    )
    val cancellationPolicies: List<ReservationCancellationPolicy> = emptyList(),
    @JsonSchema.Description(
        "Opera payment card already stored on this reservation. Presence adds paymentCard to the " +
            "reservation GET payload and gates the Opera credit-card-info stub. Distinct from " +
            "Booking.cardPayment, which is the Datatrans checkout instrument.",
    )
    val operaPaymentCard: OperaPaymentCard? = null,
    @JsonSchema.Description(
        "Opera activity-log entries for this reservation. The activity-log stub always installs " +
            "for a reservation room; an empty list serves one synthetic default entry.",
    )
    val activityLogEntries: List<ActivityLogEntry> = emptyList(),
    @JsonSchema.Description(
        "Opera can record mobile pre-check-in for this reservation. Presence gates the Opera " +
            "pre-check-in status stub for this room.",
    )
    val preCheckInAvailable: Boolean = false,
    @JsonSchema.Description(
        "Opera room number assigned to this reservation. Presence gates the Opera front-office " +
            "check-in stub for this room.",
    )
    val assignedRoomId: String? = null,
    @JsonSchema.Description(
        "Completed pre-registration state Opera reports for this reservation. Presence marks " +
            "the reservation GET payload preRegistered and adds its reg-card attachment and " +
            "pre-check-in alert; a reg-card attachment id gates the Opera attachment-delete stub.",
    )
    val preRegistration: PreRegistration? = null,
    @JsonSchema.Description(
        "Opera reservation comments, e.g. Business Notes. Always serialized on the reservation " +
            "GET payload (empty when none) because Opera always reports the comments block.",
    )
    val reservationComments: List<ReservationComment> = emptyList(),
    @JsonSchema.Description(
        "Opera still holds this reservation rather than having committed it. Sets " +
            "roomStay.guarantee.onHold on the reservation GET payload, which is the flag the " +
            "abandon-the-hold cancellation filters on. Unrelated to status ON_HOLD, which drives " +
            "the Opera reservation status instead.",
    )
    val heldInOpera: Boolean = false,
    @JsonSchema.Description(
        "Opera holds no reservation for this room's reservationId: the reservation read answers " +
            "200 with an envelope carrying no reservation member at all, which is the exact " +
            "Opera shape the adapter maps to a not-found reservation. Distinct from an empty " +
            "response body and from a present-but-empty reservation array, both of which fail " +
            "inside the adapter instead.",
    )
    val reservationAbsentInOpera: Boolean = false,
    @JsonSchema.Description(
        "Opera reservation comments this reservation carries after a comment-writing update. " +
            "Presence makes the reservation-update stub accept only PUT bodies that write exactly " +
            "these comments — type, question|questionHeader title, and answer text — against this " +
            "room's reservation; an empty list requires a body whose comments collection is empty. " +
            "Absent, updates accept any body.",
    )
    val reservationCommentsAfterUpdate: List<ReservationComment>? = null,
    @JsonSchema.Description(
        "Opera customReference this reservation carries after an update. Presence makes the " +
            "reservation-update stub accept only PUT bodies writing exactly this customer " +
            "reference against this room's reservation. Absent, updates accept any body.",
    )
    val customReferenceAfterUpdate: String? = null,
    @JsonSchema.Description(
        "Opera character user-defined fields this reservation carries after an update, as " +
            "name/value pairs such as UDFC11 or UDFC35 plus UDFC09. Presence makes the " +
            "reservation-update stub accept only PUT bodies whose single reservation instruction " +
            "carries the Booking hotel and exactly these UDF entries; entry order is not pinned " +
            "and no body reservation identifier is required, because UDF-only Opera bodies " +
            "identify the reservation by URL alone. Absent, updates accept any body.",
    )
    val characterUdfsAfterUpdate: List<CharacterUdf>? = null,
    @JsonSchema.Description(
        "Opera additionalGuestInfo.purposeOfStay this reservation carries after a reason-for-stay " +
            "update. Presence makes the reservation-update stub accept only PUT bodies whose " +
            "single reservation instruction carries the Booking hotel and exactly this purpose of " +
            "stay. Absent, updates accept any body.",
    )
    val purposeOfStayAfterUpdate: String? = null,
    @JsonSchema.Description(
        "Contact-centre agent identifier Opera holds in character UDF UDFC08 after an update. " +
            "Presence makes the reservation-update stub accept exactly two bodies for this room: " +
            "one setting UDFC08 to this value and one clearing it, the cleared body carrying a " +
            "UDFC08 entry with no value member at all because the Opera client encodes NON_NULL. " +
            "Absent, updates accept any body.",
    )
    val ccAgentIdAfterUpdate: String? = null,
    @JsonSchema.Description(
        "Digital reference Opera holds against this reservation once an external-reference update " +
            "is written. Presence makes the reservation-update stub accept only PUT bodies whose " +
            "single reservation instruction carries the Booking hotel, this room's reservation id " +
            "typed Reservation, and exactly one externalReferences entry of this id under " +
            "Booking.bookingReferenceIdContext. Absent, updates accept any body.",
    )
    val externalReferenceAfterUpdate: String? = null,
    @JsonSchema.Description(
        "Opera preference collections held against this reservation. The reservation read renders " +
            "them as preferenceCollection — nothing at all when absent, which is how Opera reports " +
            "a reservation whose preferences were never fetched or set — and the reservation-update " +
            "stub accepts only PUT bodies writing exactly these collections against this room's " +
            "reservation, with each value as its own preference entry.",
    )
    val reservationPreferences: List<ReservationPreferenceCollection>? = null,
    @JsonSchema.Description(
        "Opera alerts this reservation carries after an alert update. Presence makes the " +
            "reservation-update stub accept only PUT bodies whose single reservation instruction " +
            "carries the Booking hotel, this room's reservation id typed Reservation, and exactly " +
            "these alerts; an empty list requires a body whose alerts collection is empty. Absent, " +
            "updates accept any body.",
    )
    val reservationAlertsAfterUpdate: List<ReservationAlert>? = null,
    @JsonSchema.Description(
        "The ReservationContact and Company profile links Opera holds against this reservation " +
            "after a booker/company update is written. Presence makes the reservation-update stub " +
            "accept only PUT bodies whose single reservation instruction carries the Booking " +
            "hotel, this room's reservation id typed Reservation, and exactly two " +
            "reservationProfiles entries — ReservationContact and Company — each carrying its " +
            "stated profile id, or no profileIdList member at all for a null member, the link " +
            "Opera clears. Absent, updates accept any body.",
    )
    val attachedProfilesAfterUpdate: ReservationAttachedProfiles? = null,
    @JsonSchema.Description(
        "Opera profile id this reservation carries as its Company reservation profile after a " +
            "profile attach. Presence makes the reservation-update stub accept only PUT bodies " +
            "whose single reservation instruction — carrying no hotelId, because the attach body " +
            "scopes the hotel by URL and header alone — identifies this room's reservation typed " +
            "Reservation and attaches exactly this profile id typed Profile as the single Company " +
            "reservation profile. Absent, updates accept any body.",
    )
    val attachedCompanyProfileIdAfterUpdate: String? = null,
    @JsonSchema.Description(
        "Opera holds this reservation's first routing folio re-pointed at the attached company " +
            "once a payee-info update is written. It carries no data of its own: with " +
            "attachedCompanyProfileId stated, the reservation-update stub accepts only PUT bodies " +
            "re-pointing routingInstructions[0]'s folio at that company profile id, keeping the " +
            "folio's own folioWindowNo and instruction count, with no hotelId or reservationIdList " +
            "(identity travels in the URL alone); with no attached company it accepts only bodies " +
            "carrying no reservations member at all. False, updates accept any body.",
    )
    val routingPayeeAfterUpdate: Boolean = false,
    @JsonSchema.Description(
        "Email address this reservation's guest/booker profile carries after a booker-email " +
            "update; the reservation's ReservationContact block carries the same address. " +
            "Presence makes the reservation-update stub accept only PUT bodies whose single " +
            "reservation instruction carries the Booking hotel, this room's reservation id typed " +
            "Reservation, and exactly one ReservationContact reservation profile holding this " +
            "room's guest profile id typed Profile with this address inline. Absent, updates " +
            "accept any body.",
    )
    val bookerEmailAfterUpdate: String? = null,
) {
    /**
     * Names the after-update facts stated on this room that all pin the same Opera reservation
     * PUT. The three question-and-answer facts count once because one real Q&A update writes
     * comments, customer reference, and character UDFs in a single body.
     */
    private fun statedReservationUpdateFacts(): List<String> =
        buildList {
            if (selectedPackagesAfterUpdate != null) add("selectedPackagesAfterUpdate")
            if (depositPolicyCodeAfterUpdate != null) add("depositPolicyCodeAfterUpdate")
            if (reservationCommentsAfterUpdate != null ||
                customReferenceAfterUpdate != null ||
                characterUdfsAfterUpdate != null
            ) {
                add(
                    listOfNotNull(
                        reservationCommentsAfterUpdate?.let { "reservationCommentsAfterUpdate" },
                        customReferenceAfterUpdate?.let { "customReferenceAfterUpdate" },
                        characterUdfsAfterUpdate?.let { "characterUdfsAfterUpdate" },
                    ).joinToString("+"),
                )
            }
            if (purposeOfStayAfterUpdate != null) add("purposeOfStayAfterUpdate")
            if (ccAgentIdAfterUpdate != null) add("ccAgentIdAfterUpdate")
            if (externalReferenceAfterUpdate != null) add("externalReferenceAfterUpdate")
            if (reservationPreferences != null) add("reservationPreferences")
            if (reservationAlertsAfterUpdate != null) add("reservationAlertsAfterUpdate")
            if (attachedProfilesAfterUpdate != null) add("attachedProfilesAfterUpdate")
            if (attachedCompanyProfileIdAfterUpdate != null) add("attachedCompanyProfileIdAfterUpdate")
            if (routingPayeeAfterUpdate) add("routingPayeeAfterUpdate")
            if (bookerEmailAfterUpdate != null) add("bookerEmailAfterUpdate")
        }

    init {
        require(reservationId == null || reservationId.isNotBlank()) {
            "bookingRoom.reservationId must not be blank"
        }
        require(ratePlan == null || ratePlan.isNotBlank()) {
            "bookingRoom.ratePlan must not be blank"
        }
        require(roomTypeAfterUpdate == null || roomTypeAfterUpdate.isNotBlank()) {
            "bookingRoom.roomTypeAfterUpdate must not be blank"
        }
        require(sourceCode.isNotBlank()) { "bookingRoom.sourceCode must not be blank" }
        require(cancellationDate == null || status == ReservationStatus.CANCELLED) {
            "bookingRoom.cancellationDate requires status CANCELLED"
        }
        require(depositPolicyCode.isNotBlank()) { "bookingRoom.depositPolicyCode must not be blank" }
        require(depositPolicyCodeAfterUpdate == null || depositPolicyCodeAfterUpdate.isNotBlank()) {
            "bookingRoom.depositPolicyCodeAfterUpdate must not be blank"
        }
        require(amountAlreadyPaid.isFinite() && amountAlreadyPaid >= 0.0) {
            "bookingRoom.amountAlreadyPaid must be finite and non-negative"
        }
        require(
            amountPostedOnFolio == null ||
                (amountPostedOnFolio.isFinite() && amountPostedOnFolio >= 0.0),
        ) {
            "bookingRoom.amountPostedOnFolio must be finite and non-negative"
        }
        require(!reservationAbsentInOpera || reservationId != null) {
            "bookingRoom.reservationAbsentInOpera requires reservationId"
        }
        require(attachedCompanyProfileId == null || attachedCompanyProfileId.isNotBlank()) {
            "bookingRoom.attachedCompanyProfileId must not be blank"
        }
        require(selectedPackagesAfterUpdate == null || reservationId != null) {
            "bookingRoom.selectedPackagesAfterUpdate requires reservationId"
        }
        require(selectedPackagesAfterUpdate == null || depositPolicyCodeAfterUpdate == null) {
            "bookingRoom.selectedPackagesAfterUpdate and depositPolicyCodeAfterUpdate " +
                "state two different updates against one reservation PUT; model one per room"
        }
        val statedUpdateFacts = statedReservationUpdateFacts()
        require(statedUpdateFacts.size <= 1) {
            "bookingRoom states ${statedUpdateFacts.joinToString(" and ")}, which pin the same " +
                "reservation PUT; model one after-update fact per room"
        }
        // Every body-pinned mutation addresses this room's own reservation URL, so its id is
        // required; the deposit-policy transition is a read-side fact and keeps its old freedom.
        val pinnedUpdateFacts = statedUpdateFacts.filterNot { it == "depositPolicyCodeAfterUpdate" }
        require(pinnedUpdateFacts.isEmpty() || reservationId != null) {
            "bookingRoom.${pinnedUpdateFacts.single()} requires reservationId"
        }
        require(customReferenceAfterUpdate == null || customReferenceAfterUpdate.isNotBlank()) {
            "bookingRoom.customReferenceAfterUpdate must not be blank"
        }
        require(purposeOfStayAfterUpdate == null || purposeOfStayAfterUpdate.isNotBlank()) {
            "bookingRoom.purposeOfStayAfterUpdate must not be blank"
        }
        require(ccAgentIdAfterUpdate == null || ccAgentIdAfterUpdate.isNotBlank()) {
            "bookingRoom.ccAgentIdAfterUpdate must not be blank"
        }
        require(externalReferenceAfterUpdate == null || externalReferenceAfterUpdate.isNotBlank()) {
            "bookingRoom.externalReferenceAfterUpdate must not be blank"
        }
        require(
            attachedCompanyProfileIdAfterUpdate == null ||
                attachedCompanyProfileIdAfterUpdate.isNotBlank(),
        ) {
            "bookingRoom.attachedCompanyProfileIdAfterUpdate must not be blank"
        }
        require(bookerEmailAfterUpdate == null || bookerEmailAfterUpdate.isNotBlank()) {
            "bookingRoom.bookerEmailAfterUpdate must not be blank"
        }
        // Without a stated guest profile the reservation read reports the synthetic
        // TEMP-{reservationId} profile id, which no profile mapping ever serves, so the
        // booker-email journey would 404 mid-run instead of failing here.
        require(bookerEmailAfterUpdate == null || guestProfile != null) {
            "bookingRoom.bookerEmailAfterUpdate requires guestProfile: the pinned profile and " +
                "reservation PUTs target the room's own guest profile id"
        }
        require(
            !routingPayeeAfterUpdate ||
                attachedCompanyProfileId == null ||
                routingInstructions.firstOrNull()?.instructions?.isNotEmpty() == true,
        ) {
            "bookingRoom.routingPayeeAfterUpdate with an attached company requires a routing " +
                "instruction with at least one inner instruction: the pinned payee body copies " +
                "routingInstructions[0]'s folio, and an empty folio would pin an unsatisfiable body"
        }
        require(depositPaymentReference == null || depositPaymentReference.isNotBlank()) {
            "bookingRoom.depositPaymentReference must not be blank"
        }
        require(assignedRoomId == null || assignedRoomId.isNotBlank()) {
            "bookingRoom.assignedRoomId must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("One Opera activity-log row for a reservation.")
data class ActivityLogEntry(
    @JsonSchema.Description("Opera action type, e.g. UPDATE RESERVATION.")
    val actionType: String,
    val actionDescription: String,
    @JsonSchema.Description("User name Opera logged for the change.")
    val logUserName: String = "SYSTEM",
) {
    init {
        require(actionType.isNotBlank()) { "activityLogEntry.actionType must not be blank" }
        require(actionDescription.isNotBlank()) { "activityLogEntry.actionDescription must not be blank" }
        require(logUserName.isNotBlank()) { "activityLogEntry.logUserName must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("The Opera cancellation policy currently attached to a reservation.")
data class ReservationCancellationPolicy(
    @JsonSchema.Description("Opera policy schedule id used to delete the current policy.")
    val policyId: String,
    @JsonSchema.Description("Opera absoluteDeadline currently on the policy, as returned by reservation GET.")
    val deadline: String,
    @JsonSchema.Description("Opera revenue type used to calculate the cancellation charge, e.g. Rooms.")
    val revenueType: String,
    val amountPercent: CancellationPolicyAmountPercent,
    @JsonSchema.Description("Opera cancellation policy code, e.g. DOA.")
    val policyCode: String,
    val manual: Boolean,
    val effective: Boolean,
    @JsonSchema.Description("Percentage of the cancellation charge due, from 0 to 100.")
    val percentageDue: Double,
    val comments: String = "",
) {
    init {
        require(policyId.isNotBlank()) { "reservationCancellationPolicy.policyId must not be blank" }
        require(deadline.isNotBlank()) { "reservationCancellationPolicy.deadline must not be blank" }
        require(revenueType.isNotBlank()) { "reservationCancellationPolicy.revenueType must not be blank" }
        require(policyCode.isNotBlank()) { "reservationCancellationPolicy.policyCode must not be blank" }
        require(percentageDue.isFinite() && percentageDue in 0.0..100.0) {
            "reservationCancellationPolicy.percentageDue must be between 0 and 100"
        }
    }
}

@Serializable
@JsonSchema.Description("The amount or percentage used to calculate an Opera cancellation charge.")
data class CancellationPolicyAmountPercent(
    @JsonSchema.Description("Opera calculation basis, e.g. FlatAmount.")
    val basisType: String,
    val nights: Int,
    val percent: Double,
    val amount: Double,
) {
    init {
        require(basisType.isNotBlank()) { "cancellationPolicyAmountPercent.basisType must not be blank" }
        require(nights >= 0) { "cancellationPolicyAmountPercent.nights must not be negative" }
        require(percent.isFinite() && percent in 0.0..100.0) {
            "cancellationPolicyAmountPercent.percent must be between 0 and 100"
        }
        require(amount.isFinite() && amount >= 0.0) {
            "cancellationPolicyAmountPercent.amount must be finite and non-negative"
        }
    }
}

@Serializable
@JsonSchema.Description("An Opera payment card already attached to a reservation, identified by Opera cardId.")
data class OperaPaymentCard(
    @JsonSchema.Description("Opera card id used to look up Front Desk credit-card details.")
    val cardId: String,
    @JsonSchema.Description("Opera card type, e.g. Va.")
    val cardType: String = "Va",
    val cardNumber: String,
    @JsonSchema.Description("ISO-8601 expiration date Opera Front Desk returns, e.g. 2025-03-31.")
    val expirationDate: String,
    val cardHolderName: String? = null,
) {
    init {
        require(cardId.isNotBlank()) { "operaPaymentCard.cardId must not be blank" }
        require(cardType.isNotBlank()) { "operaPaymentCard.cardType must not be blank" }
        require(cardNumber.isNotBlank()) { "operaPaymentCard.cardNumber must not be blank" }
        require(expirationDate.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            "operaPaymentCard.expirationDate must be an ISO-8601 date, e.g. 2025-03-31"
        }
        require(cardHolderName == null || cardHolderName.isNotBlank()) {
            "operaPaymentCard.cardHolderName must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("An Opera reservation routing instruction identifying its destination folio window.")
data class RoutingInstruction(
    val folioWindowNumber: Int,
    @JsonSchema.Description("Opera profile id of the folio's payee, reported as payeeInfo.payeeId.id.")
    val payeeProfileId: String = "500001",
    @JsonSchema.Description(
        "Charge-routing instructions inside this folio. Each one drives exactly one Opera " +
            "cashiering routing-instruction DELETE when the routing instructions are removed.",
    )
    val instructions: List<RoutingFolioInstruction> = emptyList(),
) {
    init {
        require(folioWindowNumber > 0) {
            "routingInstruction.folioWindowNumber must be greater than zero"
        }
        require(payeeProfileId.isNotBlank()) {
            "routingInstruction.payeeProfileId must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "One charge-routing instruction inside a routing-instruction folio. A daily instruction " +
        "spans the whole stay; weekday flags are always reported true by the mocked Opera.",
)
data class RoutingFolioInstruction(
    @JsonSchema.Description("Whether the instruction routes charges daily over the stay's time span.")
    val daily: Boolean = true,
    @JsonSchema.Description("Opera credit limit as its exact wire string, e.g. 250.00.")
    val creditLimit: String? = null,
    @JsonSchema.Description("Opera routing link id joining this instruction to its routing setup.")
    val routingLinkId: String? = null,
    @JsonSchema.Description("Opera transaction codes routed by this instruction.")
    val transactionCodes: List<String> = emptyList(),
    @JsonSchema.Description("Opera billing instruction codes routed by this instruction.")
    val billingCodes: List<String> = emptyList(),
    @JsonSchema.Description(
        "Days to shift this instruction's routed time span away from the booking's arrival date. " +
            "Absent, the instruction spans the stay from arrival, so every instruction in a folio " +
            "shares one start date. A non-zero offset gives the instruction a start date of its " +
            "own, which is the only world in which the consumer's first-instruction date filter " +
            "drops an instruction. Requires a daily instruction, the only shape carrying a span.",
    )
    val startDateOffset: Int? = null,
) {
    init {
        require(startDateOffset == null || daily) {
            "routingFolioInstruction.startDateOffset requires a daily instruction"
        }
        require(creditLimit == null || creditLimit.matches(Regex("""\d+(\.\d+)?"""))) {
            "routingFolioInstruction.creditLimit must be a plain decimal number string"
        }
        require(routingLinkId == null || routingLinkId.isNotBlank()) {
            "routingFolioInstruction.routingLinkId must not be blank"
        }
        require(transactionCodes.all { it.isNotBlank() }) {
            "routingFolioInstruction.transactionCodes must not contain blanks"
        }
        require(billingCodes.all { it.isNotBlank() }) {
            "routingFolioInstruction.billingCodes must not contain blanks"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "Completed Opera pre-registration state for a reservation: the stored registration-card " +
        "attachment and the pre-check-in alert Opera raised when the guest pre-registered.",
)
data class PreRegistration(
    @JsonSchema.Description(
        "Opera attachment id of the stored REG_RES registration card. Presence gates the Opera " +
            "attachment-delete stub for this reservation.",
    )
    val regCardAttachmentId: String? = null,
    @JsonSchema.Description(
        "File name Opera reports for the registration-card attachment. Defaults to a REG_RES-" +
            "prefixed name; only REG_RES-prefixed attachments count as registration cards.",
    )
    val regCardFileName: String? = null,
    @JsonSchema.Description(
        "Alert id of the reservation's Pre-Check-In alert. Presence adds the alert to the " +
            "reservation GET payload for the alert-removal PUT to target.",
    )
    val preCheckInAlertId: String? = null,
    @JsonSchema.Description(
        "Opera has raised the completed-pre-check-in alert on this reservation, the alert whose " +
            "description tells the front desk not to print a registration card. Sets that exact " +
            "description on the reservation GET payload's alert, which is what the adapter " +
            "reads back as deRegCardCompleted. An alert is reported even without " +
            "preCheckInAlertId; the id then derives from the reservation id.",
    )
    val deRegCardCompleted: Boolean = false,
) {
    init {
        require(regCardAttachmentId == null || regCardAttachmentId.isNotBlank()) {
            "preRegistration.regCardAttachmentId must not be blank"
        }
        require(regCardFileName == null || regCardFileName.isNotBlank()) {
            "preRegistration.regCardFileName must not be blank"
        }
        require(regCardFileName == null || regCardAttachmentId != null) {
            "preRegistration.regCardFileName requires regCardAttachmentId"
        }
        require(preCheckInAlertId == null || preCheckInAlertId.isNotBlank()) {
            "preRegistration.preCheckInAlertId must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("One Opera reservation comment, identified by its title, e.g. BUSINESS NOTES.")
data class ReservationComment(
    @JsonSchema.Description("Opera comment title. Exactly BUSINESS NOTES marks a Business Notes comment.")
    val title: String,
    val text: String = "Integration test note",
    @JsonSchema.Description("Opera comment id used by comment-removal change-reservation PUTs.")
    val commentId: String = "1001",
    @JsonSchema.Description("Opera comment type wire value.")
    val type: String = "RESERVATION",
    @JsonSchema.Description(
        "Opera timestamp this comment was created, as `yyyy-MM-dd HH:mm:ss`. Absent, the " +
            "reservation read derives a deterministic stamp from the booking's arrival date, " +
            "because real Opera always reports the audit block and consumers read it unguarded.",
    )
    val createdOn: String? = null,
    @JsonSchema.Description("Opera user id that created this comment.")
    val createdBy: String? = null,
    @JsonSchema.Description(
        "Opera timestamp this comment was last modified, as `yyyy-MM-dd HH:mm:ss`. Absent, the " +
            "reservation read derives a deterministic stamp from the booking's arrival date. " +
            "Consumers order memos by this value, so setting it fixes that order.",
    )
    val modifiedOn: String? = null,
    @JsonSchema.Description("Opera user id that last modified this comment.")
    val modifiedBy: String? = null,
) {
    init {
        require(title.isNotBlank()) { "reservationComment.title must not be blank" }
        require(text.isNotBlank()) { "reservationComment.text must not be blank" }
        require(commentId.isNotBlank()) { "reservationComment.commentId must not be blank" }
        require(type.isNotBlank()) { "reservationComment.type must not be blank" }
        require(createdOn == null || createdOn.matches(OPERA_TIMESTAMP)) {
            "reservationComment.createdOn must be an Opera timestamp, e.g. 2026-09-10 09:10:00"
        }
        require(modifiedOn == null || modifiedOn.matches(OPERA_TIMESTAMP)) {
            "reservationComment.modifiedOn must be an Opera timestamp, e.g. 2026-09-10 09:10:00"
        }
        require(createdBy == null || createdBy.isNotBlank()) {
            "reservationComment.createdBy must not be blank"
        }
        require(modifiedBy == null || modifiedBy.isNotBlank()) {
            "reservationComment.modifiedBy must not be blank"
        }
    }
}

/**
 * The only timestamp shape the adapter's Jackson `Date` deserializer parses in full.
 *
 * Its other accepted pattern, a bare `yyyy-MM-dd`, is lenient, so an ISO instant is silently
 * truncated to midnight rather than rejected — which is why a comment stamp must be stated
 * in this shape.
 */
private val OPERA_TIMESTAMP = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")

@Serializable
@JsonSchema.Description(
    "One Opera character user-defined field held against a reservation, e.g. UDFC11 for a " +
        "purchase order or UDFC08 for a contact-centre agent.",
)
data class CharacterUdf(
    @JsonSchema.Description("Opera character UDF name, e.g. UDFC11.")
    val name: String,
    @JsonSchema.Description("Value Opera holds in that field.")
    val value: String,
) {
    init {
        require(name.isNotBlank()) { "characterUdf.name must not be blank" }
        require(value.isNotBlank()) { "characterUdf.value must not be blank" }
    }
}

@Serializable
@JsonSchema.Description(
    "One Opera reservation preference collection: a preference type and the values held under it.",
)
data class ReservationPreferenceCollection(
    @JsonSchema.Description("Opera preference type code, e.g. SPECIALS.")
    val preferenceType: String,
    @JsonSchema.Description(
        "Human-readable description Opera reports for the preference type. Absent, the " +
            "reservation read reports the collection without one, as Opera does for a type " +
            "whose description was never configured.",
    )
    val preferenceTypeDescription: String? = null,
    @JsonSchema.Description("Preference values held under this type; each becomes its own Opera preference entry.")
    val values: List<ReservationPreferenceValue> = emptyList(),
) {
    init {
        require(preferenceType.isNotBlank()) { "reservationPreferenceCollection.preferenceType must not be blank" }
        require(preferenceTypeDescription == null || preferenceTypeDescription.isNotBlank()) {
            "reservationPreferenceCollection.preferenceTypeDescription must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("One value inside an Opera reservation preference collection.")
data class ReservationPreferenceValue(
    @JsonSchema.Description("Opera preference value code, e.g. HIFLR.")
    val value: String,
    @JsonSchema.Description("Human-readable description Opera reports for the value, when it has one.")
    val description: String? = null,
) {
    init {
        require(value.isNotBlank()) { "reservationPreferenceValue.value must not be blank" }
        require(description == null || description.isNotBlank()) {
            "reservationPreferenceValue.description must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "The ReservationContact and Company profile links Opera holds against a reservation after a " +
        "booker/company update: both entries are always present on the written body, and a null " +
        "member means that entry carries no profile id at all — the link Opera clears, encoded " +
        "with no profileIdList member because the Opera client serializes NON_NULL.",
)
data class ReservationAttachedProfiles(
    @JsonSchema.Description("Profile id of the ReservationContact entry, or null when the booker link is cleared.")
    val bookerProfileId: String? = null,
    @JsonSchema.Description("Profile id of the Company entry, or null when the company link is cleared.")
    val companyProfileId: String? = null,
) {
    init {
        require(bookerProfileId == null || bookerProfileId.isNotBlank()) {
            "reservationAttachedProfiles.bookerProfileId must not be blank"
        }
        require(companyProfileId == null || companyProfileId.isNotBlank()) {
            "reservationAttachedProfiles.companyProfileId must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "One Opera reservation alert, as Opera holds it after an alert update: the alert's identity, " +
        "its front-desk area, and where it is notified.",
)
data class ReservationAlert(
    @JsonSchema.Description("Opera alert id.")
    val id: String,
    @JsonSchema.Description("Opera alert code, e.g. ECNP.")
    val code: String,
    val description: String,
    @JsonSchema.Description(
        "Opera alert area as its exact wire value, e.g. CheckIn, CheckOut, Reservation, Billing, " +
            "or InHouse — the value the adapter's area translation produces, not the request's " +
            "constant name.",
    )
    val area: String,
    @JsonSchema.Description("Opera raises this alert on the front-desk screen.")
    val screenNotification: Boolean = false,
    @JsonSchema.Description("Opera prints this alert.")
    val printerNotification: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "reservationAlert.id must not be blank" }
        require(code.isNotBlank()) { "reservationAlert.code must not be blank" }
        require(description.isNotBlank()) { "reservationAlert.description must not be blank" }
        require(area.isNotBlank()) { "reservationAlert.area must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("A package attached to a room, referencing the hotel's package catalogue by code.")
data class SelectedPackage(
    val code: String,
    @JsonSchema.Description(
        "Quantity Opera reports consumed on the package's schedule. Zero is a real Opera state " +
            "— a package attached to the reservation that nothing is scheduled against — and is " +
            "the only world in which a consumer's positive-quantity filter drops the package.",
    )
    val quantity: Int = 1,
    @JsonSchema.Description(
        "Price Opera posts for the package on each consumption date. When set, the reservation " +
            "read reports the package on its own posting line, which is the only shape in which " +
            "the price survives ohip-adapter's mapping; left null the package prices as zero.",
    )
    val unitPrice: Double? = null,
) {
    init {
        require(code.isNotBlank()) { "selected package code must not be blank" }
        require(quantity >= 0) { "selected package quantity must not be negative" }
        require(unitPrice == null || unitPrice > 0.0) {
            "selected package unitPrice must be greater than zero when set"
        }
    }
}

@Serializable
@JsonSchema.Description("Opera reservation lifecycle states the mocks can report.")
enum class ReservationStatus {
    ON_HOLD,
    RESERVED,
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    NO_SHOW,
}

@Serializable
@JsonSchema.Description("Guest identity the mocked Opera profile endpoints serve for a room.")
data class GuestProfile(
    @JsonSchema.Description("Opera profile ID.")
    val profileId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val addressLine: String,
    val city: String,
    val postcode: String,
    val country: String = "GB",
    val nationality: String = "GB",
    @JsonSchema.Description("Opera language code, e.g. E for English.")
    val language: String = "E",
    val vipStatus: String? = null,
    @JsonSchema.Description(
        "This profile is also the reservation's contact — its booker. Adds a second Opera " +
            "reservation profile typed ReservationContact alongside the guest one, which is the " +
            "profile type consumers select when they read booker identity, billing, or marketing " +
            "preferences; without it no booker profile lookup can resolve.",
    )
    val reservationContact: Boolean = false,
    @JsonSchema.Description(
        "Opera marketing email opt-in state stored on the profile. Absent, the profile carries no " +
            "privacy block at all, which is how Opera reports a profile whose preferences were " +
            "never recorded.",
    )
    val marketingOptIn: Boolean? = null,
)
