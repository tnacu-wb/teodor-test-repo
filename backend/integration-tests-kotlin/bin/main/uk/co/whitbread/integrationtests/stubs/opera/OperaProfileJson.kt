package uk.co.whitbread.integrationtests.stubs.opera

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

// Fragments shared by the Opera profile endpoints.
//
// Opera serves guest profiles and company profiles from the same two paths, so those builders sit
// together in the two files named for those paths. These three helpers are used from both files and
// are therefore internal rather than private. None is part of any author-facing vocabulary.

/** Returns the configured profile or fails before constructing an invalid mapping. */
internal fun BookingRoom.guestProfile() = guestProfile ?: error("BookingRoom.guestProfile must be configured for profile stubs")

/** Builds the shared structured self-link response for profile create and update. */
internal fun profileLinkResponse(profileId: String) =
    stubJsonObject(
        "links" to
            listOf(
                mapOf(
                    "href" to "/crm/v1/profiles/$profileId",
                    "rel" to "self",
                    "method" to "GET",
                ),
            ),
    )

/** Builds one typed Opera profile identifier. */
internal fun uniqueId(
    id: String,
    type: String,
): JsonObject =
    JsonObject(
        mapOf(
            "id" to JsonPrimitive(id),
            "type" to JsonPrimitive(type),
        ),
    )
