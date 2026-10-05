package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition

const val OPERA_PACKAGES_LIST_STUB_ID = "booking.opera.packages-list"
const val OPERA_DONATION_PACKAGES_STUB_ID = "booking.opera.donation-packages"

/** Builds the packages-list mapping for one Booking room. */
fun packagesList(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = packagesList(booking, listOf(room))

fun packagesList(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_PACKAGES_LIST_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> packagesListMapping(booking, room) },
    )

private fun packagesListMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val packageCodeInfoItems =
        booking.hotel.packageCatalogue
            ?.packages
            .orEmpty()
            .map { item -> packageInfo(hotelId, item) }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPattern =
                    "/rtp/v1/packages\\?(?=.*hotelId=${Regex.escape(hotelId)})" +
                        "(?=.*startDate=${Regex.escape(booking.arrival!!.toString())})" +
                        "(?=.*endDate=${Regex.escape(booking.departure!!.toString())})" +
                        "(?=.*adults=${Regex.escape("1")})" +
                        "(?=.*children=${Regex.escape(room.children.toString())})" +
                        "(?=.*fetchInstructions=Header)(?=.*fetchInstructions=CalculatedPrice)" +
                        "(?=.*fetchInstructions=Items)(?=.*fetchInstructions=PostingRules).*",
            ),
        response =
            jsonResponse(
                jsonBody = packageCodesResponse(packageCodeInfoItems),
            ),
    )
}

/** Builds one mapping for every non-empty donation-package subset. */
fun donationPackagesDetails(hotel: Hotel): PlannedStub {
    val allDonationPackages = hotel.packageCatalogue?.donationPackages.orEmpty()
    return PlannedStub(
        id = OPERA_DONATION_PACKAGES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            allDonationPackages.nonEmptySubsets().map { requestedDonationPackages ->
                donationPackagesDetails(hotel, requestedDonationPackages, allDonationPackages)
            },
    )
}

/** Builds one exact donation-subset mapping and structured package response. */
private fun donationPackagesDetails(
    hotel: Hotel,
    donationPackages: List<PackageDefinition>,
    allDonationPackages: List<PackageDefinition>,
): StubMapping {
    val requiredPackageCodeParams =
        donationPackages.joinToString("") { item ->
            "(?=.*packageCode=${Regex.escape(item.code)})"
        }
    val excludedPackageCodeParams =
        allDonationPackages
            .filterNot { item -> donationPackages.any { it.code == item.code } }
            .joinToString("") { item ->
                "(?!.*packageCode=${Regex.escape(item.code)}(?:&|$))"
            }
    val packageCodeInfoItems = donationPackages.map { item -> donationPackageInfo(hotel.hotelId, item) }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPattern =
                    "/rtp/v1/packages\\?(?=.*hotelId=${Regex.escape(hotel.hotelId)})" +
                        requiredPackageCodeParams +
                        excludedPackageCodeParams +
                        "(?=.*fetchInstructions=Header)(?=.*fetchInstructions=CalculatedPrice)" +
                        "(?=.*fetchInstructions=Items)(?=.*fetchInstructions=PostingRules)" +
                        "(?=.*fetchInstructions=Details).*",
                headers =
                    mapOf(
                        "x-hotelid" to StringValuePattern(equalTo = hotel.hotelId),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = packageCodesResponse(packageCodeInfoItems),
            ),
    )
}

/** Builds one structured Opera donation-package item. */
private fun donationPackageInfo(
    hotelId: String,
    item: PackageDefinition,
): Map<String, Any?> =
    mapOf(
        "header" to
            mapOf(
                "primaryDetails" to
                    mapOf(
                        "description" to item.description,
                        "shortDescription" to item.shortDescription,
                    ),
                "transactionDetails" to transactionDetails(item, transactionCode = "145"),
                "postingAttributes" to
                    mapOf(
                        "addToRate" to false,
                        "printSeparateLine" to true,
                        "sellSeparate" to false,
                        "postNextDay" to false,
                        "forecastNextDay" to false,
                        "webBookable" to false,
                        "formulaFunctionName" to "",
                        "catering" to false,
                        "postingRhythm" to mapOf("type" to operaPostingRhythm(item.postingRhythm)),
                        "priceCalculationRule" to operaDonationPriceCalculationRule(item.calculationRule),
                        "ticket" to false,
                        "inventoryItems" to emptyList<Any>(),
                    ),
            ),
        "schedules" to
            listOf(
                mapOf(
                    "schedulePrices" to listOf(mapOf("unitPrice" to item.price, "bucket" to "Bucket1")),
                    "start" to "2022-12-01",
                    "end" to "2045-12-31",
                ),
            ),
        "hotelId" to hotelId,
        "code" to item.code,
        "group" to false,
    )

/** Returns every non-empty subset while preserving catalogue order. */
private fun <T> List<T>.nonEmptySubsets(): List<List<T>> =
    fold(listOf(emptyList<T>())) { subsets, item ->
        subsets + subsets.map { subset -> subset + item }
    }.filter { it.isNotEmpty() }

/** Builds one structured standard Opera package item. */
private fun packageInfo(
    hotelId: String,
    item: PackageDefinition,
): Map<String, Any?> =
    mapOf(
        "header" to
            mapOf(
                "primaryDetails" to
                    mapOf(
                        "description" to item.description,
                        "shortDescription" to item.shortDescription,
                    ),
                "transactionDetails" to transactionDetails(item, transactionCode = "11"),
                "postingAttributes" to
                    mapOf(
                        "addToRate" to false,
                        "printSeparateLine" to true,
                        "sellSeparate" to true,
                        "postNextDay" to false,
                        "forecastNextDay" to true,
                        "webBookable" to false,
                        "formulaFunctionName" to if (item.cityTaxPurposes.isEmpty()) "" else "CITY_TAX",
                        "formulaFunctionArguments" to formulaFunctionArguments(item),
                        "catering" to false,
                        "postingRhythm" to mapOf("type" to operaPostingRhythm(item.postingRhythm)),
                        "priceCalculationRule" to operaPriceCalculationRule(item.calculationRule),
                        "ticket" to false,
                        "inventoryItems" to inventoryItems(item),
                        "calculatedPrice" to item.price,
                    ),
            ),
        "hotelId" to hotelId,
        "code" to item.code,
        "group" to false,
    )

/** Builds the transaction metadata shared by standard and donation packages. */
private fun transactionDetails(
    item: PackageDefinition,
    transactionCode: String,
): Map<String, Any?> =
    mapOf(
        "allowance" to false,
        "currency" to item.currency,
        "packagePostingRules" to
            mapOf(
                "transactionCode" to
                    mapOf(
                        "description" to item.description,
                        "code" to transactionCode,
                        "type" to "Inclusive",
                    ),
                "overageCode" to mapOf("type" to "Inclusive"),
                "profitCode" to mapOf("type" to "Inclusive"),
                "lossCode" to mapOf("type" to "Inclusive"),
            ),
    )

/** Wraps package items in the Opera package-code response envelope. */
private fun packageCodesResponse(items: List<Map<String, Any?>>) =
    stubJsonObject(
        "packageCodesList" to
            mapOf(
                "packageCodes" to listOf(mapOf("packageCodeInfo" to items)),
            ),
    )

private fun operaPriceCalculationRule(rule: String): String =
    when (rule) {
        "FLAT_RATE", "Flat", "FlatRate" -> "Flat"
        "PER_ADULT", "PerAdult" -> "PerAdult"
        "PER_PERSON", "PerPerson" -> "PerPerson"
        "PER_ROOM", "PerRoom" -> "PerRoom"
        else -> rule
    }

private fun operaDonationPriceCalculationRule(rule: String): String =
    when (rule) {
        "FLAT_RATE", "Flat", "FlatRate" -> "FlatRate"
        else -> operaPriceCalculationRule(rule)
    }

private fun operaPostingRhythm(rhythm: String): String =
    when (rhythm) {
        "EVERY_NIGHT", "EveryNight" -> "EveryNight"
        "ARRIVAL", "Arrival" -> "Arrival"
        "ARRIVAL_NIGHT", "ArrivalNight" -> "ArrivalNight"
        "DEPARTURE", "Departure" -> "Departure"
        "LAST_NIGHT", "LastNight" -> "LastNight"
        else -> rhythm
    }

/** Builds the optional inventory item list without string interpolation. */
private fun inventoryItems(item: PackageDefinition): List<Map<String, String>> =
    item.inventoryArticleNumber
        ?.let { listOf(mapOf("articleNumber" to it)) }
        .orEmpty()

/** Builds the optional city-tax formula arguments without string interpolation. */
private fun formulaFunctionArguments(item: PackageDefinition): List<Map<String, String>> {
    if (item.cityTaxPurposes.isEmpty()) return emptyList()

    val purposeValue = item.cityTaxPurposes.joinToString(",")
    return listOf(
        mapOf(
            "name" to "IN_PURPOSEOFSTAY_STAY_LIST",
            "value" to "'$purposeValue'",
        ),
    )
}
