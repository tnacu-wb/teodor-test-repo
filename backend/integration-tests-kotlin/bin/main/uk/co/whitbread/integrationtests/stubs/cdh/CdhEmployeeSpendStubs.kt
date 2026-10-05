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
import uk.co.whitbread.integrationtests.testkit.model.EmployeeSpend
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser

const val CDH_EMPLOYEE_SPEND_STUB_ID = "booking.cdh.employee-spend"

fun employeeSpend(
    loggedUser: LoggedUser,
    employeeSpend: EmployeeSpend,
): PlannedStub =
    PlannedStub(
        id = CDH_EMPLOYEE_SPEND_STUB_ID,
        target = WireMockTarget.CDH,
        mappings = listOf(employeeSpendMapping(loggedUser, employeeSpend)),
    )

private fun employeeSpendMapping(
    loggedUser: LoggedUser,
    employeeSpend: EmployeeSpend,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/IB/V1/Report/EmployeeSpend/${loggedUser.companyAccountId}/${loggedUser.employeeId}",
                queryParameters =
                    mapOf(
                        "fromMonthYear" to StringValuePattern(equalTo = employeeSpend.period.fromMonthYear),
                        "toMonthYear" to StringValuePattern(equalTo = employeeSpend.period.toMonthYear),
                    ),
                headers =
                    mapOf(
                        "AccessContext" to StringValuePattern(equalTo = "InnB"),
                        "AccessedBy" to StringValuePattern(equalTo = loggedUser.email),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = employeeSpendResponse(loggedUser, employeeSpend),
            ),
    )

private fun employeeSpendResponse(
    loggedUser: LoggedUser,
    employeeSpend: EmployeeSpend,
): JsonArray =
    JsonArray(
        employeeSpend.items.map { item ->
            JsonObject(
                mapOf(
                    "CompanyAccountId" to JsonPrimitive(loggedUser.companyAccountId),
                    "EmployeeAccountId" to JsonPrimitive(loggedUser.employeeId),
                    "Year" to JsonPrimitive(item.year),
                    "Month" to JsonPrimitive(item.month),
                    "NoOfBookings" to JsonPrimitive(item.noOfBookings),
                    "BookingValue" to JsonPrimitive(item.bookingValue),
                    "BookingCurrency" to JsonPrimitive(item.bookingCurrency),
                ),
            )
        },
    )
