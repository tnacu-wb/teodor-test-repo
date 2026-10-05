package uk.co.whitbread.integrationtests.stubs.opera

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate

const val OPERA_RATE_PLANS_STUB_ID = "booking.opera.rate-plans"

fun ratePlans(hotel: Hotel): PlannedStub = ratePlans(listOf(hotel))

/** Builds one rate-plan summary mapping per hotel so multi-hotel searches read each hotel's catalogue. */
fun ratePlans(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_RATE_PLANS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::ratePlansMapping),
    )

private fun ratePlansMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rtp/v1/ratePlans",
                headers =
                    mapOf(
                        "x-hotelid" to StringValuePattern(equalTo = hotel.hotelId),
                    ),
            ),
        response =
            jsonResponse(
                body = ratePlansBody(hotel),
            ),
    )

private val responseJson = Json { prettyPrint = true }

private fun ratePlansBody(hotel: Hotel): String {
    val rates = hotel.availableRates.distinctBy { it.ratePlan }
    val body =
        buildJsonObject {
            put(
                "ratePlanShortInfoList",
                buildJsonObject {
                    put("hasMore", false)
                    put("totalResults", rates.size)
                    put("offset", 0)
                    put("limit", rates.size)
                    put("totalPages", if (rates.isEmpty()) 0 else 1)
                    put(
                        "ratePlanShortInfo",
                        buildJsonArray {
                            rates.forEach { rate ->
                                add(ratePlanJson(hotel, rate))
                            }
                        },
                    )
                },
            )
        }
    return responseJson.encodeToString(JsonObject.serializer(), body)
}

private fun ratePlanJson(
    hotel: Hotel,
    rate: Rate,
): JsonObject {
    val metadata = metadataFor(rate.ratePlan)
    return buildJsonObject {
        put("ratePlanCode", rate.ratePlan)
        put("hotelId", hotel.hotelId)
        put(
            "primaryDetails",
            buildJsonObject {
                put(
                    "description",
                    buildJsonObject {
                        put("defaultText", metadata.description)
                    },
                )
            },
        )
        put(
            "classifications",
            buildJsonObject {
                put("rateCategory", metadata.rateCategory)
                // A rate's own displaySet fact wins over the catalogue default.
                (rate.displaySet ?: metadata.displaySet)?.let { put("displaySet", it) }
                metadata.marketCode?.let { put("marketCode", it) }
            },
        )
    }
}

private data class RatePlanMetadata(
    val description: String,
    val rateCategory: String,
    val displaySet: String? = "PBN",
    val marketCode: String? = "OTH",
)

private fun metadataFor(ratePlan: String): RatePlanMetadata =
    defaultRatePlanMetadata[ratePlan] ?: RatePlanMetadata(
        description = ratePlan,
        rateCategory = "A",
    )

private val defaultRatePlanMetadata =
    mapOf(
        "FLEXRATE" to RatePlanMetadata("Flex Rate", "A", "PBF"),
        "SEMIFLEX" to RatePlanMetadata("Semi-Flex Rate", "C"),
        "ADVANCE" to RatePlanMetadata("Advance Rate", "S"),
        "STANDARD" to RatePlanMetadata("Standard Rate", "U"),
        "NONFLEX" to RatePlanMetadata("Non-Flex Rate", "O"),
        "NONFLEXD" to RatePlanMetadata("Non-Flex Rate - Direct Connect", "W", "DCN"),
        "ADVNCEBB" to RatePlanMetadata("Advance B&B Rate", "D", "BDB"),
        "BADUTYBB" to RatePlanMetadata("BA B&B Rate", "D", "BDB"),
        "BUSIFLEX" to RatePlanMetadata("Business Flex", "F", "BFL"),
        "FLEXBEDB" to RatePlanMetadata("B&B Flex", "D", "BDB"),
        "MIFIXB03" to RatePlanMetadata("3% B&B Fixed Corporate Discount (Migration)", "D", "BDB"),
    )
