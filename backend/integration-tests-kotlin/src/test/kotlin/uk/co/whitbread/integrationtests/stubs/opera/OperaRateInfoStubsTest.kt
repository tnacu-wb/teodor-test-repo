package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Pins the two reservation-scoped rate-info defaults - the amounts summary and the per-date
 * city-tax detail: their request matchers, the amounts they derive from the driving `BookingRoom`
 * facts, and their `DefaultStubs` gates.
 *
 * The zero-deposit tests assert the rendered JSON *text*, not a numeric comparison: the whole
 * point of the scale-0 zero is that `0` and `0.0` compare equal but are not equal downstream.
 */
class OperaRateInfoStubsTest :
    FunSpec({
        test("reservation amounts pin both id-context caller shapes exactly") {
            val booking = amountsBooking()
            val requests =
                reservationAmounts(booking, listOf(booking.room))
                    .mappings
                    .map { it.request }
            requests.size shouldBe 2

            requests.forEach { request ->
                val queryParameters = requireNotNull(request.queryParameters)
                request.method shouldBe "GET"
                request.urlPath shouldBe "/rsv/v1/hotels/AMT01/reservations/rateInfo"
                queryParameters.keys shouldContainExactly setOf("idContext", "id", "summaryInfo", "type")
                queryParameters.getValue("id").equalTo shouldBe "RSV-AMT-1"
                queryParameters.getValue("summaryInfo").equalTo shouldBe "true"
                queryParameters.getValue("type").equalTo shouldBe "Reservation"
            }
            // One variant per caller: basket flows send idContext=OPERA, the amend detail
            // read sends no idContext; a caller regressing the parameter matches neither.
            requests.map { it.queryParameters!!.getValue("idContext") } shouldContainExactly
                listOf(
                    StringValuePattern(equalTo = "OPERA"),
                    StringValuePattern(absent = true),
                )
        }

        test("criteria rate-info pins both summaryInfo caller shapes exactly") {
            val booking = rateInfoBooking()
            val rate = booking.hotel.availableRates.single()
            val requests = rateInfo(booking, rate).mappings.map { it.request }
            requests.size shouldBe 2

            requests.forEach { request ->
                requireNotNull(request.queryParameters).keys shouldContainExactly
                    setOf(
                        "criteriaStartDate",
                        "criteriaEndDate",
                        "adults",
                        "children",
                        "ratePlanCode",
                        "roomType",
                        "summaryInfo",
                    )
            }
            // One variant per caller: price-breakdown sends summaryInfo=true,
            // rate-code-pricing sends no summaryInfo; a caller regressing the parameter
            // matches neither.
            requests.map { it.queryParameters!!.getValue("summaryInfo") } shouldContainExactly
                listOf(
                    StringValuePattern(equalTo = "true"),
                    StringValuePattern(absent = true),
                )
        }

        test("the three rate-info mappings on the shared Opera URL stay mutually disjoint") {
            val criteriaKeys =
                rateInfoBooking().let { booking ->
                    rateInfo(booking, booking.hotel.availableRates.single())
                        .mappings
                        .first()
                        .request.queryParameters!!
                        .keys
                }
            val amountsBooking = amountsBooking()
            val amountsKeys =
                reservationAmounts(amountsBooking, listOf(amountsBooking.room))
                    .mappings
                    .first()
                    .request.queryParameters!!
            val cityTaxBooking = cityTaxBooking(SelectedPackage(code = "CITYTAX"))
            val cityTaxKeys =
                cityTaxRateInfo(cityTaxBooking, cityTaxBooking.rooms)
                    .mappings
                    .single()
                    .request.queryParameters!!

            // The criteria call sends no id, so it can never satisfy the two id-based mappings.
            criteriaKeys shouldNotContain "id"
            // The id-based calls send no stay criteria, so they never satisfy the criteria mapping.
            amountsKeys.keys shouldNotContain "criteriaStartDate"
            cityTaxKeys.keys shouldNotContain "criteriaStartDate"
            // summaryInfo separates the two id-based mappings from each other.
            amountsKeys.getValue("summaryInfo").equalTo shouldBe "true"
            cityTaxKeys.getValue("summaryInfo").equalTo shouldBe "false"
        }

        test("a nothing-paid room emits a scale-0 zero deposit") {
            val summary = amountSummary(amountsBooking())

            // Rendered as `0`, not `0.0`: the deposit survives ohip-adapter's
            // BigDecimal.ZERO.add(deposit.negate()) reduction and only a scale-0 zero satisfies
            // hotel-reservation-entity-service's BigDecimal.ZERO.equals(amountPaid) check.
            summary.getValue("deposit").jsonPrimitive.content shouldBe "0"
            summary.getValue("outStandingCostOfStay").jsonPrimitive.content shouldBe
                summary.getValue("totalCostOfStay").jsonPrimitive.content
        }

        test("a part-paid room still emits the negated payment and the reduced outstanding cost") {
            val summary = amountSummary(amountsBooking(amountAlreadyPaid = 25.0))

            BigDecimal(summary.getValue("deposit").jsonPrimitive.content)
                .compareTo(BigDecimal("-25")) shouldBe 0
            // Two nights at 60.00 gross, less the 25.00 already paid.
            BigDecimal(summary.getValue("totalCostOfStay").jsonPrimitive.content)
                .compareTo(BigDecimal("120")) shouldBe 0
            BigDecimal(summary.getValue("outStandingCostOfStay").jsonPrimitive.content)
                .compareTo(BigDecimal("95")) shouldBe 0
        }

        test("the city-tax detail read matches the per-consumption-date rate-info request") {
            val booking = cityTaxBooking(SelectedPackage(code = "CITYTAX"))
            val request =
                cityTaxRateInfo(booking, booking.rooms)
                    .mappings
                    .single()
                    .request
            val queryParameters = requireNotNull(request.queryParameters)

            request.method shouldBe "GET"
            request.urlPath shouldBe "/rsv/v1/hotels/AMT01/reservations/rateInfo"
            queryParameters.keys shouldContainExactly setOf("id", "detailDate", "summaryInfo", "type")
            queryParameters.getValue("id").equalTo shouldBe "RSV-AMT-1"
            queryParameters.getValue("detailDate").equalTo shouldBe "2026-09-01"
            queryParameters.getValue("summaryInfo").equalTo shouldBe "false"
            queryParameters.getValue("type").equalTo shouldBe "Reservation"
        }

        test("an unpriced CITYTAX package keeps the historical city-tax detail amounts") {
            val cityTax = cityTaxDetail(cityTaxBooking(SelectedPackage(code = "CITYTAX")))

            cityTax.getValue("code").jsonPrimitive.content shouldBe "CITYTAX"
            cityTax.getValue("amountBeforeTax").jsonPrimitive.content shouldBe "12.00"
            cityTaxVatOf(cityTax) shouldBe "2.40"
        }

        test("a priced CITYTAX package drives the city-tax detail amount and its VAT") {
            val cityTax =
                cityTaxDetail(cityTaxBooking(SelectedPackage(code = "CITYTAX", unitPrice = 15.5)))

            // The reservation read posts the same 15.50, so ohip-adapter compares one Booking fact
            // against itself plus the 20% VAT share rather than two unrelated stub constants.
            cityTax.getValue("amountBeforeTax").jsonPrimitive.content shouldBe "15.50"
            cityTaxVatOf(cityTax) shouldBe "3.10"
        }

        test("the city-tax rate-info default is gated on a room carrying a CITYTAX package") {
            defaultStubsFor(cityTaxBooking(SelectedPackage(code = "CITYTAX")))
                .map { it.id } shouldContain OPERA_CITY_TAX_RATE_INFO_STUB_ID
            defaultStubsFor(cityTaxBooking(SelectedPackage(code = "BREAKFAST")))
                .map { it.id } shouldNotContain OPERA_CITY_TAX_RATE_INFO_STUB_ID
        }

        test("the reservation-amounts default is gated on a room carrying a reservation id") {
            val booking = amountsBooking()

            defaultStubsFor(booking).map { it.id } shouldContain OPERA_RESERVATION_AMOUNTS_STUB_ID
            defaultStubsFor(
                booking.copy(rooms = listOf(booking.room.copy(reservationId = null))),
            ).map { it.id } shouldNotContain OPERA_RESERVATION_AMOUNTS_STUB_ID
        }
    })

private fun cityTaxDetail(booking: Booking): JsonObject =
    cityTaxRateInfo(booking, booking.rooms)
        .mappings
        .single()
        .response
        .jsonBody!!
        .jsonObject
        .getValue("detail")
        .jsonObject
        .getValue("packages")
        .jsonArray
        .single()
        .jsonObject

private fun cityTaxVatOf(cityTax: JsonObject): String =
    cityTax
        .getValue("taxes")
        .jsonObject
        .getValue("tax")
        .jsonArray
        .single()
        .jsonObject
        .getValue("amount")
        .jsonPrimitive
        .content

private fun cityTaxBooking(selectedPackage: SelectedPackage): Booking =
    amountsBooking().let { booking ->
        booking.copy(rooms = listOf(booking.room.copy(selectedPackages = listOf(selectedPackage))))
    }

private fun amountSummary(booking: Booking): JsonObject = summaryOf(reservationAmounts(booking, listOf(booking.room)))

private fun summaryOf(stub: PlannedStub): JsonObject =
    stub.mappings
        .first()
        .response
        .jsonBody!!
        .jsonObject
        .getValue("summary")
        .jsonObject

/** An availability-shaped Booking (no reservation ids) that gates the criteria rate-info stub. */
private fun rateInfoBooking(): Booking =
    amountsBooking().let { booking ->
        booking.copy(rooms = listOf(booking.room.copy(reservationId = null)))
    }

private fun amountsBooking(amountAlreadyPaid: Double = 0.0): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "AMT01",
                    shortId = "amt-hotel",
                    name = "Amounts Hotel",
                    addressLine = "1 Amounts Street",
                    city = "London",
                    postcode = "SW1A 1AA",
                    phone = "02079460000",
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "FLEX",
                                // Present only so the reservation-id-less gate variant, which
                                // resolves to availability data, still builds.
                                ratePlanSet = "BAR",
                                roomType = "DOUBLE",
                                adults = 2,
                                nightlyRate = 60.0,
                            ),
                        ),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 1),
        departure = LocalDate.of(2026, 9, 3),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-AMT-1",
                    roomType = "DOUBLE",
                    adults = 2,
                    amountAlreadyPaid = amountAlreadyPaid,
                ),
            ),
    )
