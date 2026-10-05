package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition

const val OPERA_PACKAGE_GROUP_STUB_ID = "booking.opera.package-group"

/** Builds all package-group mappings derived from [booking]. */
fun packageGroups(booking: Booking): PlannedStub {
    val hotelId = booking.hotel.hotelId
    val packages =
        booking.hotel.packageCatalogue
            ?.packages
            .orEmpty()
    val donationPackages =
        booking.hotel.packageCatalogue
            ?.donationPackages
            .orEmpty()
    val definedPackages = packages + donationPackages
    val packageCodes = definedPackages.map { it.code }.toSet()
    val selectedPackageCodes =
        booking.rooms
            .flatMap { room -> room.selectedPackages.map { it.code } }
            .distinct()
            .filterNot { it in packageCodes }
    val composablePackages = packages.filter { it.composition != null }
    val allGroupsStub = allPackageGroups(hotelId, composablePackages)
    val groupCodeStubs =
        definedPackages.map { item ->
            packageGroupByCode(hotelId, item.code, item)
        } +
            selectedPackageCodes.map { packageCode ->
                packageGroupByCode(hotelId, packageCode)
            }
    return PlannedStub(
        id = OPERA_PACKAGE_GROUP_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = listOf(allGroupsStub) + groupCodeStubs,
    )
}

/** Builds the exact-code package-group mapping and its structured response. */
private fun packageGroupByCode(
    hotelId: String,
    packageCode: String,
    item: PackageDefinition? = null,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rtp/v1/hotels/$hotelId/packageGroups",
                queryParameters =
                    mapOf(
                        "code" to StringValuePattern(equalTo = packageCode),
                        "limit" to StringValuePattern(equalTo = "50"),
                    ),
                headers = packageGroupsHeaders(hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    item
                        ?.takeIf { it.composition != null }
                        ?.let { packageGroupsBody(hotelId, listOf(it)) }
                        ?: noPackageGroupsBody(hotelId),
            ),
    )

/** Builds the no-code-filter package-group mapping with a literal hotel regex. */
private fun allPackageGroups(
    hotelId: String,
    packages: List<PackageDefinition>,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPattern =
                    "/rtp/v1/hotels/${Regex.escape(hotelId)}/packageGroups\\?" +
                        "(?=.*limit=50)(?!.*[?&]code=[^&]+).*",
                headers = packageGroupsHeaders(hotelId),
            ),
        response =
            jsonResponse(
                jsonBody = packageGroupsBody(hotelId, packages),
            ),
    )

private fun packageGroupsHeaders(hotelId: String): Map<String, StringValuePattern> =
    mapOf(
        "x-hotelid" to StringValuePattern(equalTo = hotelId),
    )

/** Builds a structured package-group response for [packages]. */
private fun packageGroupsBody(
    hotelId: String,
    packages: List<PackageDefinition>,
): kotlinx.serialization.json.JsonObject =
    stubJsonObject(
        "packageGroupList" to
            mapOf(
                "packageGroups" to
                    listOf(
                        mapOf(
                            "packageGroup" to
                                packages.map { item ->
                                    val composition = item.composition
                                    mapOf(
                                        "description" to (composition?.description ?: item.description),
                                        "shortDescription" to item.shortDescription,
                                        "code" to item.code,
                                        "sellSeparate" to true,
                                        "webBookable" to true,
                                        "membersList" to
                                            composition?.members.orEmpty().map { member ->
                                                mapOf(
                                                    "code" to member.code,
                                                    "description" to member.description,
                                                )
                                            },
                                    )
                                },
                            "hotelId" to hotelId,
                        ),
                    ),
                "allRowsFetched" to true,
                "totalRows" to packages.size,
            ),
        "links" to emptyList<Any>(),
    )

/** Builds the structured no-package-groups response. */
private fun noPackageGroupsBody(hotelId: String): kotlinx.serialization.json.JsonObject {
    val href =
        "https://whitbce1ua.whb.hospitality-api.eu-frankfurt-1.ocs.oc-test.com" +
            "/rtp/v1/hotels/$hotelId/packageGroups"
    return stubJsonObject(
        "packageGroupList" to mapOf("allRowsFetched" to false),
        "links" to
            listOf(
                mapOf(
                    "href" to href,
                    "rel" to "self",
                    "templated" to false,
                    "method" to "PUT",
                    "operationId" to "putPackageGroup",
                ),
                mapOf(
                    "href" to href,
                    "rel" to "self",
                    "templated" to false,
                    "method" to "POST",
                    "operationId" to "postPackageGroup",
                ),
            ),
    )
}
