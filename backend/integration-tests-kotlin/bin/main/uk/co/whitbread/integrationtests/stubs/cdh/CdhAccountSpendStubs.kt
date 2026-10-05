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
import uk.co.whitbread.integrationtests.testkit.model.AccountSpend
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

const val CDH_ACCOUNT_SPEND_STUB_ID = "booking.cdh.account-spend"

fun accountSpend(
    tetheredAccount: TetheredAccount,
    accountSpend: AccountSpend,
): PlannedStub =
    PlannedStub(
        id = CDH_ACCOUNT_SPEND_STUB_ID,
        target = WireMockTarget.CDH,
        mappings = listOf(accountSpendMapping(tetheredAccount, accountSpend)),
    )

private fun accountSpendMapping(
    tetheredAccount: TetheredAccount,
    accountSpend: AccountSpend,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/IB/V1/Report/PIBAAccountSpend/${tetheredAccount.pibaAccountId}",
                queryParameters =
                    mapOf(
                        "fromMonthYear" to StringValuePattern(equalTo = accountSpend.period.fromMonthYear),
                        "toMonthYear" to StringValuePattern(equalTo = accountSpend.period.toMonthYear),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = accountSpendResponse(tetheredAccount, accountSpend),
            ),
    )

private fun accountSpendResponse(
    tetheredAccount: TetheredAccount,
    accountSpend: AccountSpend,
): JsonArray =
    JsonArray(
        accountSpend.items.map { item ->
            JsonObject(
                mapOf(
                    "PIBAAccountId" to JsonPrimitive(tetheredAccount.pibaAccountId),
                    "Year" to JsonPrimitive(item.year),
                    "Month" to JsonPrimitive(item.month),
                    "NoOfBookings" to JsonPrimitive(item.noOfBookings),
                    "BookingValue" to JsonPrimitive(item.bookingValue),
                ),
            )
        },
    )
