package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.absentJsonPath
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.literal
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CharacterUdf
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.PreRegistration
import uk.co.whitbread.integrationtests.testkit.model.ReservationAlert
import uk.co.whitbread.integrationtests.testkit.model.ReservationAttachedProfiles
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationPreferenceCollection
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

private const val DEFAULT_GUARANTEE_CODE = "NON"
private const val DEFAULT_PAYMENT_METHOD = "CA"
private const val EARLY_CHECK_IN_PACKAGE = "HSCKIN"
private const val LATE_CHECK_OUT_PACKAGE = "HSCOU2"
private const val RESERVATION_UPDATE_SCENARIO_PREFIX = "opera-reservation-update"
private const val SCENARIO_STARTED = "Started"
private const val DEPOSIT_POLICY_UPDATED = "deposit-policy-updated"
const val OPERA_RESERVATION_GET_STUB_ID = "booking.opera.get-reservation"

/**
 * Opera's `ResProfileTypeType` wire value for the reservation's contact, character for character:
 * consumers equality-match this literal to find the booker profile.
 */
private const val RESERVATION_CONTACT_PROFILE_TYPE = "ReservationContact"

/** The only comment audit timestamp shape the adapter's `Date` deserializer parses in full. */
private val COMMENT_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/** Opera user id reported for a comment whose audit facts state no author. */
private const val DEFAULT_COMMENT_AUTHOR = "OPERAUSER"

/** Hour of the arrival date a derived comment stamp lands on. */
private const val COMMENT_MODIFIED_HOUR = 9L

/** Minutes between one room's derived comment stamps and the next room's. */
private const val COMMENT_ROOM_MINUTE_STRIDE = 10L
const val OPERA_RESERVATION_PUT_STUB_ID = "booking.opera.put-reservation"

/**
 * The exact alert description hotel-reservation-entity-service's
 * `OhipAlertUtils.DEREG_CARD_COMPLETED_DESC` matches on, character for character: the value drives
 * a string equality check, not a contains.
 */
const val DEREG_CARD_COMPLETED_ALERT_DESCRIPTION =
    "Do not print registration card, Pre-Check-In completed"

internal data class ReservationReadUpdateStubs(
    val reads: PlannedStub,
    val updates: PlannedStub,
)

private data class ReservationUpdateScenario(
    val name: String,
)

/**
 * Builds the generic reservation read and update stubs with shared state per room.
 *
 * A room carrying `depositPolicyCodeAfterUpdate` starts by returning `depositPolicyCode`.
 * Its existing generic PUT mapping advances only that reservation's scenario, after which GETs
 * return the configured updated policy. A UUID keeps WireMock's globally held scenario state
 * isolated when otherwise identical Bookings are provisioned concurrently.
 *
 * A room carrying `selectedPackagesAfterUpdate` swaps its permissive PUT mapping for
 * package-pinned ones: a basket-create body, plus bodies adding and removing exactly the
 * packages that differ from `selectedPackages`.
 */
internal fun reservationReadUpdateStubs(
    booking: Booking,
    rooms: List<BookingRoom>,
): ReservationReadUpdateStubs {
    val scenarios =
        rooms.map { room ->
            room.depositPolicyCodeAfterUpdate?.let {
                ReservationUpdateScenario(
                    name = "$RESERVATION_UPDATE_SCENARIO_PREFIX:${room.requiredReservationId()}:${UUID.randomUUID()}",
                )
            }
        }

    return ReservationReadUpdateStubs(
        reads =
            PlannedStub(
                id = OPERA_RESERVATION_GET_STUB_ID,
                target = WireMockTarget.OPERA,
                mappings =
                    rooms.flatMapIndexed { index, room ->
                        getReservationMappings(booking, room, scenarios[index])
                    },
            ),
        updates =
            PlannedStub(
                id = OPERA_RESERVATION_PUT_STUB_ID,
                target = WireMockTarget.OPERA,
                mappings =
                    rooms.flatMapIndexed { index, room ->
                        putReservationUpdateMappings(booking, room, scenarios[index])
                    },
            ),
    )
}

/**
 * Builds the generic PUT mappings for one room: the permissive any-body mapping normally, or the
 * mapping(s) pinned to the exact Opera body the room's stated after-update fact makes Opera hold.
 * The company-attach and routing-payee facts get their own condition sets because their real
 * bodies carry no `hotelId` (and the payee body no `reservationIdList` either). The permissive
 * mapping is deliberately absent whenever a fact pins the body, so a PUT carrying the wrong body
 * has nothing to fall through to and the scenario fails instead of silently succeeding.
 */
private fun putReservationUpdateMappings(
    booking: Booking,
    room: BookingRoom,
    scenario: ReservationUpdateScenario?,
): List<StubMapping> {
    room.ccAgentIdAfterUpdate?.let { ccAgentId ->
        return ccAgentUpdateMappings(booking, room, ccAgentId)
    }
    room.attachedCompanyProfileIdAfterUpdate?.let { profileId ->
        return listOf(putReservationMapping(booking, room, companyAttachBodyPatterns(room, profileId)))
    }
    if (room.routingPayeeAfterUpdate) {
        return listOf(putReservationMapping(booking, room, routingPayeeBodyPatterns(room)))
    }
    val mutationBodyPatterns = reservationMutationBodyPatterns(booking, room)
    if (mutationBodyPatterns.isNotEmpty()) {
        return listOf(putReservationMapping(booking, room, mutationBodyPatterns))
    }

    val packagesAfterUpdate =
        room.selectedPackagesAfterUpdate
            ?: return listOf(putReservationMapping(booking, room, scenario = scenario))

    val changedQuantity =
        packagesAfterUpdate.filter { after ->
            room.selectedPackages.any { before -> before.code == after.code && before.quantity != after.quantity }
        }
    require(changedQuantity.isEmpty()) {
        "selectedPackagesAfterUpdate cannot restate a selected package with a different quantity: " +
            changedQuantity.joinToString { it.code }
    }
    val addedPackages =
        packagesAfterUpdate.filter { after -> room.selectedPackages.none { it.code == after.code } }
    val removedPackages =
        room.selectedPackages.filter { before -> packagesAfterUpdate.none { it.code == before.code } }
    require(addedPackages.isNotEmpty() || removedPackages.isNotEmpty()) {
        "selectedPackagesAfterUpdate equals selectedPackages; omit it when no package changes"
    }
    return reservationPackageUpdateMappings(booking, room, addedPackages, removedPackages)
}

/**
 * Builds the body constraints the reservation PUT must satisfy for the after-update facts [room]
 * states, or an empty list when it states none and the permissive mapping still applies.
 *
 * Every fact contributes only its own constraint, so facts that a single real Opera body carries
 * together — the question-and-answer comments, customer reference, and character UDFs — compose
 * into one set of conditions. Reservation identity is required only for the mutations whose real
 * body carries it: a character-UDF-only body identifies its reservation by URL alone.
 */
private fun reservationMutationBodyPatterns(
    booking: Booking,
    room: BookingRoom,
): List<BodyPattern> {
    val comments = room.reservationCommentsAfterUpdate
    val customReference = room.customReferenceAfterUpdate
    val characterUdfs = room.characterUdfsAfterUpdate
    val purposeOfStay = room.purposeOfStayAfterUpdate
    val externalReference = room.externalReferenceAfterUpdate
    val preferences = room.reservationPreferences
    val alerts = room.reservationAlertsAfterUpdate
    val attachedProfiles = room.attachedProfilesAfterUpdate
    val bookerEmail = room.bookerEmailAfterUpdate
    val statesBodyCarryingIdentity =
        comments != null ||
            customReference != null ||
            externalReference != null ||
            preferences != null ||
            alerts != null ||
            attachedProfiles != null ||
            bookerEmail != null
    if (!statesBodyCarryingIdentity && characterUdfs == null && purposeOfStay == null) {
        return emptyList()
    }

    return buildList {
        addAll(singleReservationInstructionPatterns(booking))
        if (statesBodyCarryingIdentity) addAll(reservationIdentityPatterns(room))
        comments?.let { addAll(commentMutationPatterns(it)) }
        customReference?.let { reference ->
            add(BodyPattern(matchesJsonPath = "$.reservations[?(@.customReference == ${literal(reference)})]"))
        }
        characterUdfs?.let { addAll(characterUdfPatterns(it)) }
        purposeOfStay?.let { purpose ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[?(@.additionalGuestInfo.purposeOfStay == ${literal(purpose)})]",
                ),
            )
        }
        externalReference?.let { reference ->
            addAll(externalReferencePatterns(reference))
        }
        preferences?.let { addAll(preferenceMutationPatterns(booking, room, it)) }
        alerts?.let { addAll(alertMutationPatterns(it)) }
        attachedProfiles?.let { addAll(attachedProfileMutationPatterns(it)) }
        bookerEmail?.let { email ->
            addAll(bookerEmailMutationPatterns(guestProfileIdFor(room, room.requiredReservationId()), email))
        }
    }
}

/**
 * Requires exactly the two profile links a booker/company update re-states — a ReservationContact
 * entry and a Company entry — each carrying its stated profile id, or no `profileIdList` member at
 * all for a cleared link, because the Opera client encodes NON_NULL so a cleared link omits the
 * member rather than sending JSON null.
 */
private fun attachedProfileMutationPatterns(attachedProfiles: ReservationAttachedProfiles): List<BodyPattern> =
    listOf(
        reservationProfileEntryPattern("ReservationContact", attachedProfiles.bookerProfileId),
        reservationProfileEntryPattern("Company", attachedProfiles.companyProfileId),
        absentJsonPath("$.reservations[*].reservationProfiles.reservationProfile[2]"),
    )

/**
 * Matches one reservation-profile entry of [profileType] carrying [profileId] as its leading
 * profile id, or carrying no `profileIdList` at all when [profileId] is null — the cleared link.
 */
private fun reservationProfileEntryPattern(
    profileType: String,
    profileId: String?,
): BodyPattern {
    val idCondition = profileId?.let { "@.profileIdList[0].id == ${literal(it)}" } ?: "!(@.profileIdList)"
    return BodyPattern(
        matchesJsonPath =
            "$.reservations[*].reservationProfiles.reservationProfile" +
                "[?(@.reservationProfileType == ${literal(profileType)} && $idCondition)]",
    )
}

/**
 * Requires exactly one ReservationContact reservation profile re-stamping the resolved guest
 * profile id typed `Profile` with the new email address inline — the body a booker-email update
 * writes back to every reservation after the CRM profile itself is amended.
 */
private fun bookerEmailMutationPatterns(
    profileId: String,
    email: String,
): List<BodyPattern> =
    listOf(
        BodyPattern(
            matchesJsonPath =
                "$.reservations[*].reservationProfiles.reservationProfile" +
                    "[?(@.reservationProfileType == \"ReservationContact\" && " +
                    "@.profileIdList[0].id == ${literal(profileId)} && " +
                    "@.profileIdList[0].type == \"Profile\" && " +
                    "@.profile.emails.emailInfo[0].email.emailAddress == ${literal(email)})]",
        ),
        absentJsonPath("$.reservations[*].reservationProfiles.reservationProfile[1]"),
    )

/**
 * Builds the body constraints for the Company-only profile-attach PUT: a single reservation
 * instruction identifying this room's reservation typed `Reservation` and attaching exactly the
 * stated profile id typed `Profile` as the single Company reservation profile.
 *
 * The attach body carries no `hotelId` — the hotel travels in the URL and `x-hotelid` header
 * alone — so this is its own condition set rather than a reuse of
 * [singleReservationInstructionPatterns].
 */
private fun companyAttachBodyPatterns(
    room: BookingRoom,
    profileId: String,
): List<BodyPattern> =
    buildList {
        add(absentJsonPath("$.reservations[1]"))
        addAll(reservationIdentityPatterns(room))
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.reservations[*].reservationProfiles.reservationProfile" +
                        "[?(@.reservationProfileType == \"Company\")]",
            ),
        )
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.reservations[*].reservationProfiles.reservationProfile[*].profileIdList" +
                        "[?(@.id == ${literal(profileId)} && @.type == \"Profile\")]",
            ),
        )
        add(
            absentJsonPath("$.reservations[*].reservationProfiles.reservationProfile[1]"),
        )
        add(
            BodyPattern(
                not =
                    BodyPattern(
                        matchesJsonPath =
                            "$.reservations[*].reservationProfiles.reservationProfile[*].profileIdList[1]",
                    ),
            ),
        )
    }

/**
 * Builds the body constraints for the routing payee-info PUT, whose body carries neither
 * `hotelId` nor `reservationIdList` — identity travels in the URL alone — so neither
 * [singleReservationInstructionPatterns] nor [reservationIdentityPatterns] applies.
 *
 * With a company attached, the single instruction re-points `routingInstructions[0]`'s folio at
 * the attached company's profile id typed `Profile`, keeping the read folio's own `folioWindowNo`
 * and instruction count. With no attached company, the write is the bare change envelope: the
 * body must carry no `reservations` member at all.
 */
private fun routingPayeeBodyPatterns(room: BookingRoom): List<BodyPattern> {
    val companyProfileId =
        room.attachedCompanyProfileId
            ?: return listOf(absentJsonPath("$.reservations"))
    // BookingRoom.init guarantees a routing instruction with a non-empty inner instruction list
    // whenever routingPayeeAfterUpdate is stated with an attached company.
    val routingInstruction = room.routingInstructions.first()
    return listOf(
        BodyPattern(
            matchesJsonPath =
                "$.reservations[?(@.routingInstructions[0].folio.payeeInfo.payeeId.id == " +
                    "${literal(companyProfileId)} && " +
                    "@.routingInstructions[0].folio.payeeInfo.payeeId.type == \"Profile\")]",
        ),
        BodyPattern(
            matchesJsonPath =
                "$.reservations[?(@.routingInstructions[0].folio.folioWindowNo == " +
                    "${routingInstruction.folioWindowNumber})]",
        ),
        BodyPattern(matchesJsonPath = "$.reservations[*].routingInstructions[0].folio.instructions[0]"),
        BodyPattern(
            not =
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[*].routingInstructions[0].folio" +
                            ".instructions[${routingInstruction.instructions.size}]",
                ),
        ),
        absentJsonPath("$.reservations[*].routingInstructions[1]"),
        absentJsonPath("$.reservations[1]"),
    )
}

/**
 * Requires exactly one reservation instruction, carrying the Booking hotel: every reservation
 * mutation ohip-adapter sends addresses a single reservation, so a body carrying a second
 * instruction is a different mutation and must not match.
 */
private fun singleReservationInstructionPatterns(booking: Booking): List<BodyPattern> =
    listOf(
        BodyPattern(matchesJsonPath = "$.reservations[?(@.hotelId == ${literal(booking.hotel.hotelId)})]"),
        absentJsonPath("$.reservations[1]"),
    )

/**
 * Requires the body to identify exactly this room's reservation, typed `Reservation`: the
 * mutations whose Opera body carries identity always carry precisely one such entry.
 */
private fun reservationIdentityPatterns(room: BookingRoom): List<BodyPattern> =
    listOf(
        BodyPattern(
            matchesJsonPath =
                "$.reservations[*].reservationIdList" +
                    "[?(@.id == ${literal(room.requiredReservationId())} && @.type == \"Reservation\")]",
        ),
        absentJsonPath("$.reservations[*].reservationIdList[1]"),
    )

/**
 * Requires exactly the stated Opera comments — type, `question|questionHeader` title, and answer
 * text — and no further comment entry. An explicitly empty fact requires a body whose comments
 * collection is present and empty, which is what a Q&A update carrying no answerable entry sends.
 */
private fun commentMutationPatterns(comments: List<ReservationComment>): List<BodyPattern> =
    buildList {
        add(BodyPattern(matchesJsonPath = "$.reservations[*].comments"))
        comments.forEach { comment ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[*].comments[?(@.comment.type == ${literal(comment.type)} && " +
                            "@.comment.commentTitle == ${literal(comment.title)} && " +
                            "@.comment.text.value == ${literal(comment.text)})]",
                ),
            )
        }
        add(
            BodyPattern(
                not = BodyPattern(matchesJsonPath = "$.reservations[*].comments[${comments.size}]"),
            ),
        )
    }

/**
 * Requires exactly the stated character user-defined fields and no extra entry, without pinning
 * their array order: the stub body model can express membership and cardinality, so composition
 * plus an absent extra entry is the durable proof of the exact UDF set.
 */
private fun characterUdfPatterns(characterUdfs: List<CharacterUdf>): List<BodyPattern> =
    buildList {
        characterUdfs.forEach { udf ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[*].userDefinedFields.characterUDFs" +
                            "[?(@.name == ${literal(udf.name)} && @.value == ${literal(udf.value)})]",
                ),
            )
        }
        add(
            BodyPattern(
                not =
                    BodyPattern(
                        matchesJsonPath =
                            "$.reservations[*].userDefinedFields.characterUDFs[${characterUdfs.size}]",
                    ),
            ),
        )
    }

/**
 * The Opera external-reference id context ohip-adapter-service always writes, character for
 * character: it comes from the deployed configuration property `config.service.ohip.contextId`
 * (`reservationOhipProperties.getContextId()`), never from the request or the reservation Opera
 * already holds.
 */
private const val OPERA_EXTERNAL_REFERENCE_ID_CONTEXT = "WB_DIGITAL"

/**
 * Requires exactly one external reference of the stated value under the deployed digital id
 * context [OPERA_EXTERNAL_REFERENCE_ID_CONTEXT]: the write always sends the configured context,
 * whatever context the reservation's existing reference carries.
 */
private fun externalReferencePatterns(externalReference: String): List<BodyPattern> =
    listOf(
        BodyPattern(
            matchesJsonPath =
                "$.reservations[*].externalReferences" +
                    "[?(@.id == ${literal(externalReference)} && " +
                    "@.idContext == ${literal(OPERA_EXTERNAL_REFERENCE_ID_CONTEXT)})]",
        ),
        absentJsonPath("$.reservations[*].externalReferences[1]"),
    )

/**
 * Requires exactly the stated preference collections, each requested value emitted as its own
 * `preference[].preferenceValue`, and no sibling room's reservation id in the body's reservation
 * identity: the write is per reservation even though every reservation receives the same
 * preference content.
 */
private fun preferenceMutationPatterns(
    booking: Booking,
    room: BookingRoom,
    preferences: List<ReservationPreferenceCollection>,
): List<BodyPattern> =
    buildList {
        add(BodyPattern(matchesJsonPath = "$.reservations[*].preferenceCollection"))
        preferences.forEach { collection ->
            val collectionFilter =
                "$.reservations[*].preferenceCollection[?(@.preferenceType == ${literal(collection.preferenceType)})]"
            add(BodyPattern(matchesJsonPath = collectionFilter))
            collection.values.forEach { value ->
                add(
                    BodyPattern(
                        matchesJsonPath =
                            "$collectionFilter.preference[?(@.preferenceValue == ${literal(value.value)})]",
                    ),
                )
            }
            add(
                BodyPattern(
                    not = BodyPattern(matchesJsonPath = "$collectionFilter.preference[${collection.values.size}]"),
                ),
            )
        }
        add(
            BodyPattern(
                not = BodyPattern(matchesJsonPath = "$.reservations[*].preferenceCollection[${preferences.size}]"),
            ),
        )
        booking.rooms
            .mapNotNull { sibling -> sibling.reservationId }
            .filter { reservationId -> reservationId != room.reservationId }
            .forEach { siblingReservationId ->
                add(
                    BodyPattern(
                        not =
                            BodyPattern(
                                matchesJsonPath =
                                    "$.reservations[*].reservationIdList" +
                                        "[?(@.id == ${literal(siblingReservationId)})]",
                            ),
                    ),
                )
            }
    }

/**
 * Requires exactly the stated alerts and no extra alert entry. An explicitly empty fact requires
 * a body whose alerts collection is present and empty, which is how every alert is removed.
 */
private fun alertMutationPatterns(alerts: List<ReservationAlert>): List<BodyPattern> =
    buildList {
        add(BodyPattern(matchesJsonPath = "$.reservations[*].alerts"))
        alerts.forEach { alert ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[*].alerts[?(@.id == ${literal(alert.id)} && " +
                            "@.code == ${literal(alert.code)} && " +
                            "@.description == ${literal(alert.description)} && " +
                            "@.area == ${literal(alert.area)} && " +
                            "@.screenNotification == ${alert.screenNotification} && " +
                            "@.printerNotification == ${alert.printerNotification})]",
                ),
            )
        }
        add(absentJsonPath("$.reservations[*].alerts[${alerts.size}]"))
    }

/**
 * Builds the two mappings that model the contact-centre agent write capability for [room]: one
 * body setting `UDFC08` to the stated agent id and one clearing it.
 *
 * The clearing body carries a `UDFC08` entry with no `value` member at all, because the Opera
 * client encodes `NON_NULL`, so a cleared value omits the member rather than sending JSON null.
 * The permissive any-body mapping is deliberately absent, so a wrong agent-id body 404s instead
 * of passing.
 */
private fun ccAgentUpdateMappings(
    booking: Booking,
    room: BookingRoom,
    ccAgentId: String,
): List<StubMapping> {
    val singleUdfEntry =
        BodyPattern(
            not = BodyPattern(matchesJsonPath = "$.reservations[*].userDefinedFields.characterUDFs[1]"),
        )
    val setBody =
        singleReservationInstructionPatterns(booking) +
            BodyPattern(
                matchesJsonPath =
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"$CC_AGENT_CHARACTER_UDF\" && @.value == ${literal(ccAgentId)})]",
            ) + singleUdfEntry
    val clearBody =
        singleReservationInstructionPatterns(booking) +
            BodyPattern(
                matchesJsonPath =
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"$CC_AGENT_CHARACTER_UDF\" && !(@.value))]",
            ) + singleUdfEntry
    return listOf(
        putReservationMapping(booking, room, setBody),
        putReservationMapping(booking, room, clearBody),
    )
}

/** Opera's character user-defined field holding the contact-centre agent identifier. */
private const val CC_AGENT_CHARACTER_UDF = "UDFC08"

/** Builds the scoped Opera reservation-lookup mapping for [room]. */
fun getReservation(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = getReservations(booking, listOf(room))

fun getReservations(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_GET_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.flatMap { room -> getReservationMapping(booking, room) },
    )

private fun getReservationMappings(
    booking: Booking,
    room: BookingRoom,
    scenario: ReservationUpdateScenario?,
): List<StubMapping> {
    val initial = getReservationMapping(booking, room)
    if (scenario == null) return initial

    val updatedPolicy = requireNotNull(room.depositPolicyCodeAfterUpdate)
    val updated = getReservationMapping(booking, room.copy(depositPolicyCode = updatedPolicy))
    return initial.map { mapping ->
        mapping.copy(
            scenarioName = scenario.name,
            requiredScenarioState = SCENARIO_STARTED,
        )
    } +
        updated.map { mapping ->
            mapping.copy(
                scenarioName = scenario.name,
                requiredScenarioState = DEPOSIT_POLICY_UPDATED,
            )
        }
}

/**
 * The exact `fetchInstructions` sets ohip-adapter-service sends when it reads one reservation by
 * id, one entry per real caller.
 *
 * Modelling the known variants instead of matching every query shape is what makes the read
 * capability provable: a caller that drops or renames an instruction stops matching and its
 * journey fails, rather than silently receiving a payload it never asked Opera for.
 */
internal val RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS: List<List<String>> =
    listOf(
        // getReservation: the full reservation read behind the reservation mutation endpoints.
        listOf(
            "Reservation",
            "InventoryItems",
            "ReservationPolicies",
            "Packages",
            "ReservationPaymentMethods",
            "RoutingInstructions",
            "Comments",
            "Preferences",
            "LinkedReservations",
            "Alerts",
        ),
        // sendGetReservationsByReservationId: the read behind reservation-by-id lookups.
        listOf(
            "Reservation",
            "ReservationPaymentMethods",
            "ReservationPolicies",
            "Attachments",
            "Alerts",
        ),
        // getReservationPaymentMethods.
        listOf("Reservation", "ReservationPaymentMethods"),
        // getReservationWithRoutingInstructions.
        listOf("Reservation", "RoutingInstructions", "Comments"),
        // getReservationWithPreferences.
        listOf("Reservation", "Preferences"),
        // getProfileIdByReservation: the profile-side read that resolves the reservation's guest
        // profile before a profile is created, updated, or attached.
        listOf("GuestLastStay"),
    )

/**
 * The headers every Opera call from the shared OHIP WebClient carries: the OAuth bearer token,
 * the application key, and the hotel the request is scoped to.
 *
 * The token and key are matched by shape rather than value so the capability stays independent of
 * the deployed credentials, while a request that sends neither stops matching.
 */
private fun operaReservationReadHeaders(hotelId: String): Map<String, StringValuePattern> =
    mapOf(
        "Authorization" to StringValuePattern(matches = "Bearer .+"),
        "x-app-key" to StringValuePattern(matches = ".+"),
    ) + hotelHeaders(hotelId)

/**
 * Builds one reservation-read mapping per known caller fetch-instruction variant, all answering
 * with the same reservation payload: the reservation Opera holds is the same world whichever
 * projection of it a caller asks for.
 */
private fun getReservationMapping(
    booking: Booking,
    room: BookingRoom,
): List<StubMapping> {
    val reservationId = room.requiredReservationId()
    val confirmationNumber = confirmationNumberFor(reservationId)
    val response =
        jsonResponse(
            jsonBody =
                if (room.reservationAbsentInOpera) {
                    absentReservationResponse()
                } else {
                    getReservationResponse(booking, room, reservationId, confirmationNumber)
                },
        )
    return RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS.map { fetchInstructions ->
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId",
                    queryParameters =
                        mapOf(
                            "fetchInstructions" to
                                StringValuePattern(
                                    hasExactly =
                                        fetchInstructions.map { instruction ->
                                            StringValuePattern(equalTo = instruction)
                                        },
                                ),
                        ),
                    headers = operaReservationReadHeaders(booking.hotel.hotelId),
                ),
            response = response,
        )
    }
}

/**
 * Builds the success envelope Opera answers with for a reservation the property does not hold:
 * a `reservations` object carrying no `reservation` member at all.
 *
 * This is the only Opera "nothing here" shape consumers map to a not-found reservation. An
 * entirely empty body and a present-but-empty `reservation` array are different worlds that fail
 * inside the consumer instead, so they are not modelled by this fact.
 */
private fun absentReservationResponse() = stubJsonObject("reservations" to stubJsonObject())

/**
 * Builds the Opera reservation-update mapping for [room].
 *
 * The mapping always matches the room's reservation URL; scenario ownership is stamped by the
 * installer. Optional [bodyPatterns] add semantic constraints for journeys that need to validate
 * the PUT body. An empty list preserves the permissive default and accepts any request body.
 */
fun putReservation(
    booking: Booking,
    room: BookingRoom,
    bodyPatterns: List<BodyPattern> = emptyList(),
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_PUT_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = listOf(putReservationMapping(booking, room, bodyPatterns)),
    )

fun putReservations(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_PUT_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> putReservationMapping(booking, room) },
    )

private fun putReservationMapping(
    booking: Booking,
    room: BookingRoom,
    bodyPatterns: List<BodyPattern> = emptyList(),
    scenario: ReservationUpdateScenario? = null,
): StubMapping {
    val reservationId = room.requiredReservationId()
    val confirmationNumber = confirmationNumberFor(reservationId)
    return StubMapping(
        request =
            RequestPattern(
                method = "PUT",
                urlPath = "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId",
                bodyPatterns = bodyPatterns.ifEmpty { null },
            ),
        response =
            jsonResponse(
                jsonBody = putReservationResponse(booking, room, reservationId, confirmationNumber),
            ),
        scenarioName = scenario?.name,
        requiredScenarioState = scenario?.let { SCENARIO_STARTED },
        newScenarioState = scenario?.let { DEPOSIT_POLICY_UPDATED },
    )
}

private fun reservationPackageUpdateMappings(
    booking: Booking,
    room: BookingRoom,
    addedPackages: List<SelectedPackage>,
    removedPackages: List<SelectedPackage>,
): List<StubMapping> {
    require(room.reservationId != null) {
        "Reservation package PUT planning requires BookingRoom.reservationId"
    }
    require(addedPackages.isNotEmpty() || removedPackages.isNotEmpty()) {
        "Reservation package PUT planning requires at least one added or removed package"
    }

    return buildList {
        add(
            putReservationMapping(
                booking = booking,
                room = room,
                bodyPatterns =
                    listOf(
                        BodyPattern(matchesJsonPath = "$.reservations[*].roomStay.roomRates"),
                    ),
            ),
        )
        if (removedPackages.isNotEmpty()) {
            add(
                putReservationMapping(
                    booking = booking,
                    room = room,
                    bodyPatterns = packageRemovalBodyPatterns(removedPackages),
                ),
            )
        }
        if (addedPackages.isNotEmpty()) {
            add(
                putReservationMapping(
                    booking = booking,
                    room = room,
                    bodyPatterns = packageAdditionBodyPatterns(booking, addedPackages),
                ),
            )
        } else if (removedPackages.isNotEmpty()) {
            add(
                putReservationMapping(
                    booking = booking,
                    room = room,
                    bodyPatterns = listOf(BodyPattern(contains = "\"reservationPackages\":[]")),
                ),
            )
        }
    }
}

private fun packageRemovalBodyPatterns(packages: List<SelectedPackage>): List<BodyPattern> =
    listOf(
        BodyPattern(matchesJsonPath = "$.reservations[*].reservationPackages"),
    ) +
        packages.map { selectedPackage ->
            val code = jsonPathStringLiteral(selectedPackage.code)
            BodyPattern(
                matchesJsonPath =
                    "$.reservations[*].reservationPackages" +
                        "[?(@.packageCode == $code && !(@.consumptionDetails))]",
            )
        }

private fun packageAdditionBodyPatterns(
    booking: Booking,
    packages: List<SelectedPackage>,
): List<BodyPattern> =
    buildList {
        packages.forEach { selectedPackage ->
            // Zero is a read-side fact (a package Opera holds with nothing scheduled against
            // it); the adapter always PUTs a positive nights-derived quantity when adding a
            // package, so a zero here could never match and would surface as an opaque 404.
            require(selectedPackage.quantity > 0) {
                "SelectedPackage ${selectedPackage.code} has quantity 0, which cannot be " +
                    "added to a reservation; zero-quantity packages are read-only facts"
            }
        }
        add(BodyPattern(matchesJsonPath = "$.reservations[*].reservationPackages"))
        packages.forEach { selectedPackage ->
            val code = jsonPathStringLiteral(selectedPackage.code)
            val quantity = selectedPackage.quantity
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.reservations[*].reservationPackages" +
                            "[?(@.packageCode == $code && " +
                            "@.consumptionDetails.defaultQuantity == $quantity && " +
                            "@.consumptionDetails.totalQuantity == $quantity)]",
                ),
            )

            when (selectedPackage.code) {
                EARLY_CHECK_IN_PACKAGE ->
                    add(expectedTimeBodyPattern("reservationExpectedArrivalTime", booking.arrival, "11:00:00"))

                LATE_CHECK_OUT_PACKAGE ->
                    add(expectedTimeBodyPattern("reservationExpectedDepartureTime", booking.departure, "14:00:00"))
            }

            if (isDonationPackage(selectedPackage.code)) {
                val arrival =
                    requireNotNull(booking.arrival) {
                        "Donation package PUT planning requires Booking.arrival"
                    }
                val firstNight = jsonPathStringLiteral(arrival.toString())
                add(
                    BodyPattern(
                        matchesJsonPath =
                            "$.reservations[*].reservationPackages" +
                                "[?(@.packageCode == $code && " +
                                "@.scheduleList[0].consumptionDate == $firstNight && " +
                                "!(@.scheduleList[1]))]",
                    ),
                )
            }
        }
    }

private fun expectedTimeBodyPattern(
    field: String,
    date: LocalDate?,
    time: String,
): BodyPattern {
    val expectedDate =
        requireNotNull(date) {
            "Special-time package PUT planning requires its Booking arrival or departure date"
        }
    val value = jsonPathStringLiteral("$expectedDate $time")
    return BodyPattern(
        matchesJsonPath = "$.reservations[?(@.roomStay.expectedTimes.$field == $value)]",
    )
}

private fun isDonationPackage(code: String): Boolean =
    code.contains("CHRTY") ||
        code.contains("ZCHRY") ||
        code.contains("ZR0705") ||
        code.contains("ZCHR10") ||
        code.contains("ZCHR11") ||
        code.contains("ZCHR12") ||
        code.contains("ZCHR13")

/** Builds the structured full reservation-lookup response. */
private fun getReservationResponse(
    booking: Booking,
    room: BookingRoom,
    reservationId: String,
    confirmationNumber: String,
) = stubJsonObject(
    "reservations" to
        mapOf(
            "reservation" to
                listOf(
                    mapOf(
                        "reservationIdList" to reservationIdList(reservationId, confirmationNumber),
                        "hotelId" to booking.hotel.hotelId,
                        "reservationStatus" to operaStatus(room.status),
                        "roomStay" to fullRoomStay(booking, room),
                        "reservationGuests" to reservationGuests(room, reservationId),
                        // Real Opera always reports this block (purposeOfStay may be absent);
                        // the confirm-amend mapper dereferences it unguarded on the original.
                        "additionalGuestInfo" to emptyMap<String, Any?>(),
                        // Real Opera always reports the reservation's attached profiles; the
                        // business-items and payment-routing mappers dereference this block
                        // unguarded, so a full reservation read must carry it.
                        "reservationProfiles" to
                            mapOf("reservationProfile" to reservationProfiles(room, reservationId)),
                        "reservationPolicies" to reservationPolicies(booking, room),
                        "reservationPackages" to reservationPackages(booking, room),
                        "reservationPaymentMethods" to listOf(reservationPaymentMethod(booking, room)),
                        "routingInstructions" to
                            room.routingInstructions.map { routingInstruction ->
                                mapOf(
                                    "folio" to routingInstructionFolio(booking, routingInstruction),
                                )
                            },
                        // Real Opera always reports the comments block (empty when the
                        // reservation has none); comment-removal flows iterate it unguarded.
                        "comments" to reservationComments(booking, room),
                        "cashiering" to cashiering(booking.hotel.vatRegion),
                        "createDateTime" to defaultCreateDateTime(),
                    ) + externalReferences(booking) + preRegistrationBlocks(room, reservationId) +
                        preferenceCollection(room),
                ),
        ),
    "links" to emptyList<Any>(),
)

/**
 * Builds the reservation's attached profiles as Opera reports them.
 *
 * Every reservation carries the Guest profile. A room whose guest is also the reservation's
 * contact carries a second entry of type ReservationContact for the same profile id: that is the
 * type consumers select on to find the booker, so without it no booker lookup resolves. The
 * Guest entry stays, because Opera never drops it and consumers read both types independently.
 * A room stating [BookingRoom.attachedCompanyProfileId] appends an entry typed Company carrying
 * that id typed Profile — the attachment consumers resolve a reservation's company profile from;
 * rooms without the fact stay byte-identical.
 */
private fun reservationProfiles(
    room: BookingRoom,
    reservationId: String,
): List<Map<String, Any?>> {
    val profileIdList =
        listOf(
            mapOf(
                "id" to guestProfileIdFor(room, reservationId),
                "type" to "Profile",
            ),
        )
    return buildList {
        add(
            mapOf(
                "reservationProfileType" to "Guest",
                "profileIdList" to profileIdList,
            ),
        )
        if (room.guestProfile?.reservationContact == true) {
            add(
                mapOf(
                    "reservationProfileType" to RESERVATION_CONTACT_PROFILE_TYPE,
                    "profileIdList" to profileIdList,
                ),
            )
        }
        room.attachedCompanyProfileId?.let { companyProfileId ->
            add(
                mapOf(
                    "reservationProfileType" to "Company",
                    "profileIdList" to listOf(mapOf("id" to companyProfileId, "type" to "Profile")),
                ),
            )
        }
    }
}

/**
 * Builds one routing-instruction folio as Opera reports it: destination window, payee
 * identity, and the charge-routing instructions inside the folio. Instructions are always
 * present (empty when the room fact carries none) because the routing-removal flow iterates
 * them unguarded; a daily instruction spans the booking stay and weekday flags are all true.
 */
private fun routingInstructionFolio(
    booking: Booking,
    routingInstruction: RoutingInstruction,
): Map<String, Any?> =
    mapOf(
        "folioWindowNo" to routingInstruction.folioWindowNumber,
        "payeeInfo" to
            mapOf(
                "payeeId" to
                    mapOf(
                        "id" to routingInstruction.payeeProfileId,
                        "type" to "Profile",
                    ),
            ),
        "instructions" to
            routingInstruction.instructions.map { instruction ->
                buildMap<String, Any?> {
                    put(
                        "duration",
                        buildMap<String, Any?> {
                            put("daily", instruction.daily)
                            put("sunday", true)
                            put("monday", true)
                            put("tuesday", true)
                            put("wednesday", true)
                            put("thursday", true)
                            put("friday", true)
                            put("saturday", true)
                            if (instruction.daily) {
                                // A stated offset moves only this instruction's start date, which
                                // is what makes an instruction routed from another date visible.
                                val startDate =
                                    booking.arrival?.plusDays(
                                        (instruction.startDateOffset ?: 0).toLong(),
                                    )
                                put(
                                    "timeSpan",
                                    mapOf(
                                        "startDate" to startDate.toString(),
                                        "endDate" to booking.departure.toString(),
                                    ),
                                )
                            }
                        },
                    )
                    instruction.creditLimit?.let { put("creditLimit", it) }
                    instruction.routingLinkId?.let {
                        put("routingLinkId", mapOf("id" to it, "type" to "RoutingLink"))
                    }
                    if (instruction.transactionCodes.isNotEmpty()) {
                        put(
                            "transactionCodes",
                            instruction.transactionCodes.map { code ->
                                mapOf("transactionCode" to code)
                            },
                        )
                    }
                    if (instruction.billingCodes.isNotEmpty()) {
                        put(
                            "billingInstructions",
                            instruction.billingCodes.map { code ->
                                mapOf("billingCode" to code)
                            },
                        )
                    }
                }
            },
    )

/**
 * Builds the always-present reservation comments block from the room's comment facts.
 *
 * Each comment carries the audit block real Opera always reports — creation and last-modification
 * stamp and author — because consumers that fold comments into memo data read all four unguarded.
 * A comment stating none of them takes deterministic stamps derived from the booking's arrival
 * date, distinct per comment and per room so a consumer ordering by last modification — or
 * folding one comment shared by two reservations into a single record — has a stable order.
 */
private fun reservationComments(
    booking: Booking,
    room: BookingRoom,
): List<Map<String, Any?>> =
    room.reservationComments.mapIndexed { index, comment ->
        mapOf(
            "id" to comment.commentId,
            "type" to comment.type,
            "comment" to
                mapOf(
                    "type" to comment.type,
                    "commentTitle" to comment.title,
                    "text" to mapOf("value" to comment.text),
                    "createDateTime" to
                        (comment.createdOn ?: derivedCommentTimestamp(booking, room, index, hoursBefore = 1)),
                    "creatorId" to (comment.createdBy ?: DEFAULT_COMMENT_AUTHOR),
                    // A derived modification stamp is clamped to an explicit createdOn: Opera
                    // never reports a comment modified before it was created, and the two stamps
                    // compare correctly as strings in the yyyy-MM-dd HH:mm:ss shape.
                    "lastModifyDateTime" to
                        (
                            comment.modifiedOn
                                ?: maxOf(
                                    derivedCommentTimestamp(booking, room, index, hoursBefore = 0),
                                    comment.createdOn.orEmpty(),
                                )
                        ),
                    "lastModifierId" to (comment.modifiedBy ?: DEFAULT_COMMENT_AUTHOR),
                ),
        )
    }

/**
 * Derives a comment audit stamp that is deterministic per Booking and distinct per comment.
 *
 * The room's own position in the Booking strides the stamp, so two rooms carrying the same
 * comment text still differ in modification order. The room is matched by reservation id because
 * reservation reads plan from copies of the Booking's rooms.
 */
private fun derivedCommentTimestamp(
    booking: Booking,
    room: BookingRoom,
    commentIndex: Int,
    hoursBefore: Long,
): String {
    val arrival =
        requireNotNull(booking.arrival) {
            "Booking.arrival must be configured to derive reservation comment audit stamps"
        }
    val roomIndex = booking.rooms.indexOfFirst { it.reservationId == room.reservationId }
    require(roomIndex >= 0) {
        "Room ${room.reservationId} is not in Booking.rooms; derived comment audit stamps " +
            "need the room's Booking position to stay distinct per reservation"
    }
    require(commentIndex < COMMENT_ROOM_MINUTE_STRIDE) {
        "Room ${room.reservationId} carries more than $COMMENT_ROOM_MINUTE_STRIDE unstamped " +
            "comments; the derived minute stride would collide with the next room's stamps - " +
            "state createdOn/modifiedOn on the extra comments instead"
    }
    return arrival
        .atStartOfDay()
        .plusHours(COMMENT_MODIFIED_HOUR - hoursBefore)
        .plusMinutes(roomIndex * COMMENT_ROOM_MINUTE_STRIDE + commentIndex)
        .format(COMMENT_TIMESTAMP_FORMAT)
}

/**
 * Builds the completed pre-registration state Opera reports: the preRegistered marker plus
 * the stored reg-card attachment and Pre-Check-In alert when the facts carry them. Absent
 * entirely for rooms without the fact, matching Opera reservations never pre-registered.
 */
private fun preRegistrationBlocks(
    room: BookingRoom,
    reservationId: String,
): Map<String, Any?> {
    val preRegistration = room.preRegistration ?: return emptyMap()
    return buildMap {
        put("preRegistered", true)
        preRegistration.regCardAttachmentId?.let { attachmentId ->
            put(
                "attachments",
                listOf(
                    mapOf(
                        "id" to attachmentId,
                        "fileName" to
                            (preRegistration.regCardFileName ?: "REG_RES_$attachmentId.pdf"),
                        "description" to "Registration card",
                    ),
                ),
            )
        }
        preCheckInAlert(preRegistration, reservationId)?.let { alert -> put("alerts", listOf(alert)) }
    }
}

/**
 * Builds the single Pre-Check-In reservation alert, or null when the room states neither
 * pre-check-in alert fact.
 *
 * The description carries the de-reg-card wording only when the room says the front desk
 * completed pre-check-in: it is the exact string the adapter equality-matches to derive
 * `deRegCardCompleted`. Either wording satisfies the alert-removal flow, which selects on code
 * Reservation and a description mentioning Pre-Check-In. A room stating only the completion
 * takes a reservation-derived alert id, so the alert stays addressable without inventing a fact.
 */
private fun preCheckInAlert(
    preRegistration: PreRegistration,
    reservationId: String,
): Map<String, Any?>? {
    if (preRegistration.preCheckInAlertId == null && !preRegistration.deRegCardCompleted) return null

    return mapOf(
        "id" to (preRegistration.preCheckInAlertId ?: "ALERT-DEREG-$reservationId"),
        // The alert-removal flow selects the alert whose code is
        // Reservation and whose description mentions Pre-Check-In.
        "code" to "Reservation",
        "description" to
            if (preRegistration.deRegCardCompleted) {
                DEREG_CARD_COMPLETED_ALERT_DESCRIPTION
            } else {
                "Pre-Check-In alert"
            },
    )
}

/**
 * Builds the preference collections Opera reports for a reservation whose preferences the room
 * states, each value on its own preference entry with the descriptions Opera reports when the
 * facts carry them.
 *
 * A room stating no preferences reports nothing preference-related at all, which keeps every
 * reservation read that predates the fact byte-identical and models the Opera reservation whose
 * preferences were never set.
 */
private fun preferenceCollection(room: BookingRoom): Map<String, Any?> {
    val preferences = room.reservationPreferences ?: return emptyMap()
    return mapOf(
        "preferenceCollection" to
            preferences.map { collection ->
                buildMap<String, Any?> {
                    put("preferenceType", collection.preferenceType)
                    collection.preferenceTypeDescription?.let { put("preferenceTypeDescription", it) }
                    put(
                        "preference",
                        collection.values.map { value ->
                            buildMap<String, Any?> {
                                put("preferenceValue", value.value)
                                value.description?.let { put("description", it) }
                            }
                        },
                    )
                }
            },
    )
}

/**
 * Builds the external references Opera holds for reservations that belong to a basket.
 *
 * One booking has one reference, shared by every room. Callers read the digital booking
 * reference from here, so a booking without one keeps the canonical no-basket world: the key
 * is absent and no basket lookup can resolve.
 */
private fun externalReferences(booking: Booking): Map<String, Any> =
    booking.bookingReference?.let { reference ->
        mapOf(
            "externalReferences" to
                listOf(
                    mapOf(
                        "id" to reference,
                        "idContext" to booking.bookingReferenceIdContext,
                    ),
                ),
        )
    } ?: emptyMap()

/** Builds the structured reservation-update response. */
private fun putReservationResponse(
    booking: Booking,
    room: BookingRoom,
    reservationId: String,
    confirmationNumber: String,
) = stubJsonObject(
    "reservations" to
        mapOf(
            "reservation" to
                listOf(
                    mapOf(
                        "reservationIdList" to reservationIdList(reservationId, confirmationNumber),
                        "hotelId" to booking.hotel.hotelId,
                        "reservationStatus" to operaStatus(room.status),
                        "roomStay" to basicRoomStay(booking, room),
                        "reservationGuests" to
                            listOf(
                                reservationGuest(
                                    guest = room.guestProfile,
                                    profileId = guestProfileIdFor(room, reservationId),
                                    fullRead = false,
                                ),
                            ),
                        "cashiering" to cashiering(booking.hotel.vatRegion),
                    ),
                ),
        ),
    "links" to
        listOf(
            mapOf(
                "href" to "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId",
                "rel" to "self",
                "method" to "GET",
                "operationId" to "getReservation",
            ),
        ),
)

/** Builds reservation policies, adding cancellationPolicies only when the room carries that fact. */
private fun reservationPolicies(
    booking: Booking,
    room: BookingRoom,
): Map<String, Any?> =
    buildMap {
        val total = booking.totalPrice(room)
        require(room.amountAlreadyPaid <= total) {
            "BookingRoom.amountAlreadyPaid must not exceed the reservation total"
        }
        put(
            "depositPolicies",
            listOf(
                mapOf(
                    "amountPaid" to
                        mapOf(
                            "amount" to room.amountAlreadyPaid,
                            "currencyCode" to booking.hotel.currency,
                        ),
                    "amountDue" to
                        mapOf(
                            "amount" to total - room.amountAlreadyPaid,
                            "currencyCode" to booking.hotel.currency,
                        ),
                    "policy" to mapOf("policyCode" to room.depositPolicyCode),
                ),
            ),
        )
        if (room.cancellationPolicies.isNotEmpty()) {
            put(
                "cancellationPolicies",
                room.cancellationPolicies.map { policy ->
                    mapOf(
                        "revenueType" to policy.revenueType,
                        "policy" to
                            mapOf(
                                "deadline" to mapOf("absoluteDeadline" to policy.deadline),
                                "amountPercent" to
                                    mapOf(
                                        "basisType" to policy.amountPercent.basisType,
                                        "nights" to policy.amountPercent.nights,
                                        "percent" to policy.amountPercent.percent,
                                        "amount" to policy.amountPercent.amount,
                                    ),
                                "policyCode" to policy.policyCode,
                                "manual" to policy.manual,
                                "effective" to policy.effective,
                            ),
                        "percentageDue" to policy.percentageDue,
                        "comments" to policy.comments,
                        "policyId" to
                            mapOf(
                                "id" to policy.policyId,
                                "type" to "PolicyScheduleId",
                            ),
                    )
                },
            )
        }
    }

/** Builds the reservation payment method, adding Opera card identity when the room carries one. */
private fun reservationPaymentMethod(
    booking: Booking,
    room: BookingRoom,
): Map<String, Any?> {
    val balance =
        mapOf(
            "amount" to booking.totalPrice(room),
            "currencyCode" to booking.hotel.currency,
        )
    val card = room.operaPaymentCard ?: return mapOf("paymentMethod" to DEFAULT_PAYMENT_METHOD, "balance" to balance)
    return mapOf(
        "paymentCard" to
            mapOf(
                "cardId" to mapOf("id" to card.cardId, "type" to "CreditCard"),
                "cardType" to card.cardType,
                // Real Opera always reports these; the service dereferences both unguarded.
                "processing" to "Manual",
                "cardOrToken" to "CardNumber",
                "cardNumberMasked" to maskedCardNumber(card.cardNumber),
                "expirationDateMasked" to "XX/XX",
                "cardHolderName" to card.cardHolderName,
            ),
        "paymentMethod" to card.cardType.uppercase(),
        "balance" to balance,
    )
}

private fun maskedCardNumber(cardNumber: String): String = "X".repeat(12) + cardNumber.takeLast(4)

/** Builds identifiers shared by reservation lookup and update responses. */
private fun reservationIdList(
    reservationId: String,
    confirmationNumber: String,
): List<Map<String, String>> =
    listOf(
        mapOf("id" to reservationId, "type" to "Reservation"),
        mapOf("id" to confirmationNumber, "type" to "Confirmation"),
    )

/** Builds the common room-stay fields used by lookup and update responses. */
private fun basicRoomStay(
    booking: Booking,
    room: BookingRoom,
): Map<String, Any?> =
    mapOf(
        "arrivalDate" to booking.arrival.toString(),
        "departureDate" to booking.departure.toString(),
        "guestCounts" to mapOf("adults" to room.adults, "children" to room.children),
        "roomRates" to nightlyRoomRates(booking, room),
        "total" to
            mapOf(
                "amountBeforeTax" to booking.totalPrice(room),
                "amountAfterTax" to booking.totalPrice(room),
                "currencyCode" to "GBP",
            ),
    )

/** Adds lookup-only guarantee and occupancy fields to the common room stay. */
private fun fullRoomStay(
    booking: Booking,
    room: BookingRoom,
): Map<String, Any?> =
    basicRoomStay(booking, room) +
        mapOf(
            // onHold is Opera's guarantee-level hold flag, driven by BookingRoom.heldInOpera; it
            // is independent of the reservation status ON_HOLD maps to.
            "guarantee" to mapOf("guaranteeCode" to DEFAULT_GUARANTEE_CODE, "onHold" to room.heldInOpera),
            "adultCount" to room.adults,
            "childCount" to room.children,
        )

/**
 * Builds the reservation guests the full reservation read reports: the room's primary guest,
 * plus a second non-primary entry when the room states [BookingRoom.accompanyingGuestProfile] —
 * a guest Opera already holds on the reservation without an attached reservation profile.
 * Every guest's profile carries profileType Guest, which real Opera always reports and
 * guest-profile-by-type consumers select on; rooms without the accompanying fact keep exactly
 * one guest entry.
 */
private fun reservationGuests(
    room: BookingRoom,
    reservationId: String,
): List<Map<String, Any?>> =
    buildList {
        add(
            reservationGuest(
                guest = room.guestProfile,
                profileId = guestProfileIdFor(room, reservationId),
                fullRead = true,
                primary = true,
            ),
        )
        room.accompanyingGuestProfile?.let { accompanying ->
            add(
                reservationGuest(
                    guest = accompanying,
                    profileId = accompanying.profileId,
                    fullRead = true,
                    primary = false,
                ),
            )
        }
    }

/**
 * Builds one reservation guest. A full reservation read carries the guest's email and Opera's
 * always-reported profileType Guest; the reservation-update acknowledgement body carries neither.
 */
private fun reservationGuest(
    guest: GuestProfile?,
    profileId: String,
    fullRead: Boolean,
    primary: Boolean = true,
): Map<String, Any?> {
    val profile =
        buildMap<String, Any?> {
            put(
                "customer",
                mapOf(
                    "personName" to
                        listOf(
                            mapOf(
                                "givenName" to (guest?.firstName ?: "TEMP"),
                                "surname" to (guest?.lastName ?: "GUEST"),
                                // Opera's PersonNameTypeType wire value; the confirm-amend
                                // mapper deserializes this enum and filters on "Primary".
                                "nameType" to "Primary",
                            ),
                        ),
                ),
            )
            if (fullRead) {
                // Opera's ProfileTypeType wire value; guest-by-type consumers equality-match it.
                put("profileType", "Guest")
            }
            if (fullRead) {
                guest?.let {
                    put(
                        "emails",
                        mapOf(
                            "emailInfo" to
                                listOf(
                                    mapOf(
                                        "email" to mapOf("emailAddress" to it.email),
                                        "type" to "EMAIL",
                                    ),
                                ),
                        ),
                    )
                }
            }
        }
    return mapOf(
        "profileInfo" to
            mapOf(
                "profileIdList" to listOf(mapOf("id" to profileId, "type" to "Profile")),
                "profile" to profile,
            ),
        "primary" to primary,
    )
}

/**
 * The price Opera posts for a selected package, at Opera's two-decimal money precision.
 *
 * Shared by the reservation read's schedule entries and the city-tax rate-info detail so both
 * default stubs report the same package price for the same Booking fact. Packages without a
 * [SelectedPackage.unitPrice] keep the historical unpriced schedule amount.
 */
internal fun operaPackagePrice(selectedPackage: SelectedPackage): BigDecimal =
    selectedPackage.unitPrice
        ?.let { price -> BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) }
        ?: UNPRICED_PACKAGE_SCHEDULE_AMOUNT

/** Schedule amount reported for a package that carries no `unitPrice` fact. */
private val UNPRICED_PACKAGE_SCHEDULE_AMOUNT = BigDecimal("12.00")

/**
 * Builds selected package entries for the reservation lookup response.
 *
 * A package Opera never adds to the rate and never prints on its own line is priced at zero by
 * ohip-adapter whatever the schedule says, so [SelectedPackage.unitPrice] drives both the posting
 * line and the price: set it and the package posts separately at that price, leave it null and the
 * package keeps the unpriced shape every existing Booking reports.
 */
private fun reservationPackages(
    booking: Booking,
    room: BookingRoom,
): List<Map<String, Any?>> =
    room.selectedPackages.map { selectedPackage ->
        val schedulePrice = operaPackagePrice(selectedPackage)
        mapOf(
            "packageCode" to selectedPackage.code,
            "packageGroup" to "ADDON",
            "packageHeaderType" to
                mapOf(
                    "primaryDetails" to mapOf("description" to "${selectedPackage.code} package"),
                    "postingAttributes" to
                        mapOf(
                            "addToRate" to false,
                            "printSeparateLine" to (selectedPackage.unitPrice != null),
                        ),
                ),
            "scheduleList" to
                listOf(
                    mapOf(
                        "unitPrice" to schedulePrice,
                        "computedResvPrice" to schedulePrice,
                        "totalQuantity" to selectedPackage.quantity,
                        "consumptionDate" to booking.arrival.toString(),
                    ),
                ),
            "consumptionDetails" to mapOf("totalQuantity" to selectedPackage.quantity),
            "startDate" to booking.arrival.toString(),
            "endDate" to booking.departure.toString(),
        )
    }

/** Builds the hotel-specific cashiering fragment used by reservation responses. */
private fun cashiering(vatRegion: String): Map<String, Any?> = mapOf("taxType" to mapOf("code" to vatRegion))

/** Builds structured nightly room-rate entries for the Booking stay. */
private fun nightlyRoomRates(
    booking: Booking,
    room: BookingRoom,
): List<Map<String, Any?>> {
    val rate = selectedRateOrNull(booking, room)
    val ratePlanCode = rate?.ratePlan ?: "UNKNOWN"
    val nightlyRate = rate?.nightlyRate ?: 0.0
    val discountAllowed = rate?.discountAllowed ?: false
    val nights = mutableListOf<Map<String, Any?>>()
    var date: LocalDate = booking.arrival!!
    while (date.isBefore(booking.departure)) {
        nights +=
            mapOf(
                "roomType" to room.roomType,
                "ratePlanCode" to ratePlanCode,
                "marketCode" to "DIRECT",
                "sourceCode" to room.sourceCode,
                "sourceCodeDescription" to "Web",
                "discountAllowed" to discountAllowed,
                "start" to date.toString(),
                "end" to date.plusDays(1).toString(),
                "rates" to
                    mapOf(
                        "rate" to
                            listOf(
                                mapOf(
                                    // Deposit-folio charge mapping reads the dates from this
                                    // nested rate, not from the enclosing room-rate row.
                                    "start" to date.toString(),
                                    "end" to date.plusDays(1).toString(),
                                    "base" to
                                        mapOf(
                                            "amountBeforeTax" to nightlyRate,
                                            "amountAfterTax" to nightlyRate,
                                            "currencyCode" to "GBP",
                                        ),
                                ),
                            ),
                    ),
            )
        date = date.plusDays(1)
    }
    return nights
}

/** Returns the required reservation ID or fails before mapping construction. */
private fun BookingRoom.requiredReservationId(): String =
    reservationId ?: error("BookingRoom.reservationId must be configured for reservation lookup stubs")

/** Calculates the total room price for the configured Booking stay. */
private fun Booking.totalPrice(room: BookingRoom): Double =
    (selectedRateOrNull(this, room)?.nightlyRate ?: 0.0) * ChronoUnit.DAYS.between(arrival, departure)

/** Produces the current Opera create timestamp in ISO instant format. */
private fun defaultCreateDateTime(): String = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)

/** Returns the configured guest profile ID or the existing temporary fallback. */
private fun guestProfileIdFor(
    room: BookingRoom,
    reservationId: String,
): String = room.guestProfile?.profileId ?: "TEMP-$reservationId"

/** Maps framework reservation status values to Opera response values. */
private fun operaStatus(status: ReservationStatus): String =
    when (status) {
        ReservationStatus.ON_HOLD -> "Reserved"
        ReservationStatus.RESERVED -> "Reserved"
        ReservationStatus.CONFIRMED -> "Reserved"
        ReservationStatus.CHECKED_IN -> "InHouse"
        ReservationStatus.CHECKED_OUT -> "CheckedOut"
        ReservationStatus.CANCELLED -> "Cancelled"
        ReservationStatus.NO_SHOW -> "NoShow"
    }
