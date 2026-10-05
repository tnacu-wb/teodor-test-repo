package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.rateInfo
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

/** Identifies one rate-info request by its room tuple and, when needed, interval start. */
data class RateInfoFailureSelector(
    val rate: Rate,
    val intervalStart: LocalDate? = null,
)

const val OPERA_RATE_INFO_REJECTED_STUB_ID = "opera.rate-info.rejected"

/**
 * Models Opera rejecting exactly one Booking-driven rate-info lookup while every other
 * requested mapping still prices successfully.
 *
 * Derived from the `booking.opera.rate-info` default - every request matcher and all
 * unselected successful mappings are retained - under its own stub ID, installed as an
 * explicit override (`excluded` + `installStub`).
 * [RateInfoFailureSelector.intervalStart] is optional for a tuple with one mapping and required to
 * disambiguate that tuple's mappings when OHIP splits a long stay into multiple Opera requests.
 */
fun rateInfoFailure(
    booking: Booking,
    rates: List<Rate>,
    selector: RateInfoFailureSelector,
): PlannedStub =
    rateInfo(booking, rates).let { default ->
        val selectedMappings = default.mappings.filter { mapping -> mapping.matches(selector) }
        // The default carries one mapping per summaryInfo variant (present / absent) for
        // each tuple and interval; the selector must resolve to exactly one interval and
        // the rejection is applied to both of its variants so it fails either caller shape.
        val selectedIntervals =
            selectedMappings
                .map { mapping ->
                    mapping.request.queryParameters
                        ?.get("criteriaStartDate")
                        ?.equalTo
                }.distinct()
        require(selectedMappings.isNotEmpty() && selectedIntervals.size == 1) {
            "Rate-info failure selector must match exactly one interval, but matched " +
                "${selectedIntervals.size} (${selectedMappings.size} mappings)"
        }

        default.copy(
            id = OPERA_RATE_INFO_REJECTED_STUB_ID,
            mappings =
                default.mappings.map { mapping ->
                    if (mapping in selectedMappings) {
                        mapping.copy(
                            response =
                                jsonResponse(
                                    status = 500,
                                    jsonBody = stubJsonObject("message" to "rate information lookup failed"),
                                ),
                        )
                    } else {
                        mapping
                    }
                },
        )
    }

private fun StubMapping.matches(selector: RateInfoFailureSelector): Boolean {
    val query = request.queryParameters.orEmpty()
    val rate = selector.rate

    return query["ratePlanCode"]?.equalTo == rate.ratePlan &&
        query["roomType"]?.equalTo == rate.roomType &&
        query["adults"]?.equalTo == rate.adults.toString() &&
        query["children"]?.equalTo == rate.children.toString() &&
        (
            selector.intervalStart == null ||
                query["criteriaStartDate"]?.equalTo == selector.intervalStart.toString()
        )
}
