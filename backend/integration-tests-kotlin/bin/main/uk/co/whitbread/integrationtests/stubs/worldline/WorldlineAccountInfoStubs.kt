package uk.co.whitbread.integrationtests.stubs.worldline

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount
import uk.co.whitbread.integrationtests.testkit.model.WorldlineAccount

const val WORLDLINE_ACCOUNT_INFO_STUB_ID = "booking.worldline.account-info"

fun accountInfo(
    tetheredAccount: TetheredAccount,
    worldlineAccount: WorldlineAccount,
): PlannedStub =
    PlannedStub(
        id = WORLDLINE_ACCOUNT_INFO_STUB_ID,
        target = WireMockTarget.WORLDLINE,
        mappings = listOf(accountInfoMapping(tetheredAccount, worldlineAccount)),
    )

private fun accountInfoMapping(
    tetheredAccount: TetheredAccount,
    worldlineAccount: WorldlineAccount,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/PIRestAPI/api/v1/account/info",
                headers =
                    mapOf(
                        "TetheredUserGuid" to StringValuePattern(equalTo = tetheredAccount.tetheredUserGuid),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = accountInfoResponse(worldlineAccount),
            ),
    )

private fun accountInfoResponse(worldlineAccount: WorldlineAccount): JsonObject =
    JsonObject(
        mapOf(
            "responseCode" to JsonPrimitive("OK"),
            "data" to
                JsonObject(
                    mapOf(
                        "billingFrequency" to JsonPrimitive(worldlineAccount.billingFrequency.worldlineValue),
                        "daysToPay" to JsonPrimitive(0),
                        "status" to JsonPrimitive(worldlineAccount.status),
                        "statementValue" to accountValue(worldlineAccount.currency),
                        "outStandingBalance" to accountValue(worldlineAccount.currency),
                    ),
                ),
            "errors" to JsonArray(emptyList()),
        ),
    )

private fun accountValue(currency: String): JsonObject =
    JsonObject(
        mapOf(
            "value" to JsonPrimitive(0),
            "currencyCode" to JsonPrimitive(currency),
        ),
    )
