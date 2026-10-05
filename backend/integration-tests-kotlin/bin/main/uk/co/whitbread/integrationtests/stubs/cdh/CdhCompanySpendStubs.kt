package uk.co.whitbread.integrationtests.stubs.cdh

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.CompanySpend
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser

const val CDH_COMPANY_SPEND_STUB_ID = "booking.cdh.company-spend"

fun companySpend(
    loggedUser: LoggedUser,
    companySpend: CompanySpend,
): PlannedStub =
    PlannedStub(
        id = CDH_COMPANY_SPEND_STUB_ID,
        target = WireMockTarget.CDH,
        mappings = listOf(companySpendMapping(loggedUser, companySpend)),
    )

private fun companySpendMapping(
    loggedUser: LoggedUser,
    companySpend: CompanySpend,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/IB/V1/Report/CompanySpend/${loggedUser.companyAccountId}",
                queryParameters =
                    mapOf(
                        "fromMonthYear" to StringValuePattern(equalTo = companySpend.period.fromMonthYear),
                        "toMonthYear" to StringValuePattern(equalTo = companySpend.period.toMonthYear),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = companySpendResponse(loggedUser, companySpend),
            ),
    )

private fun companySpendResponse(
    loggedUser: LoggedUser,
    companySpend: CompanySpend,
): JsonArray =
    JsonArray(
        companySpend.items.map { item ->
            JsonObject(
                mapOf(
                    "CompanyAccountId" to JsonPrimitive(loggedUser.companyAccountId),
                    "Year" to JsonPrimitive(item.year),
                    "Month" to JsonPrimitive(item.month),
                    "NoOfBookings" to JsonPrimitive(item.noOfBookings),
                    "BookingValue" to JsonPrimitive(item.bookingValue),
                    "BookingCurrency" to JsonPrimitive(item.bookingCurrency),
                ),
            )
        },
    )
