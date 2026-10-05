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
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

const val CDH_REGISTRATION_DETAILS_STUB_ID = "booking.cdh.registration-details"

fun cdhRegistrationDetails(
    loggedUser: LoggedUser,
    tetheredAccount: TetheredAccount,
): PlannedStub =
    PlannedStub(
        id = CDH_REGISTRATION_DETAILS_STUB_ID,
        target = WireMockTarget.CDH,
        mappings = listOf(cdhRegistrationDetailsMapping(loggedUser, tetheredAccount)),
    )

private fun cdhRegistrationDetailsMapping(
    loggedUser: LoggedUser,
    tetheredAccount: TetheredAccount,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/IBPay/V1/Registration",
                queryParameters =
                    mapOf(
                        "companyId" to StringValuePattern(equalTo = loggedUser.companyId),
                        "employeeId" to StringValuePattern(equalTo = loggedUser.employeeId),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    JsonArray(
                        listOf(
                            JsonObject(
                                mapOf(
                                    "CompanyId" to JsonPrimitive(123),
                                    "EmployeeId" to JsonPrimitive(456),
                                    "Scheme" to JsonPrimitive(tetheredAccount.scheme),
                                    "TetheredGuid" to JsonPrimitive(tetheredAccount.tetheredUserGuid),
                                ),
                            ),
                        ),
                    ),
            ),
    )
