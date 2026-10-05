package uk.co.whitbread.integrationtests.journeys.spendingentity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.spendingentity.SpendingApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.auth.AuthTokens
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.EmployeeSpend
import uk.co.whitbread.integrationtests.testkit.model.EmployeeSpendItem
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.ReportingPeriod

class GetEmployeeSpendSpec :
    JourneySpec(
        "employee spend for authenticated user",
        {
            scenario("GET /v1/spending/employeeSpend without WB-Authorization") {
                val booking = Booking()
                installFor(booking)

                val result =
                    SpendingApi().getEmployeeSpend(
                        fromMonthYear = FROM_MONTH_YEAR,
                        toMonthYear = TO_MONTH_YEAR,
                        testId = testId,
                    )

                result.attachEvidence("Get Employee Spend")

                expect("returns 400") {
                    result.response.status.value shouldBe 400
                    result.bodyText shouldContain "Required request header 'WB-Authorization'"
                }
            }

            scenario("GET /v1/spending/employeeSpend with authenticated user") {
                val booking = employeeSpendBooking()
                val employeeSpend = booking.loggedUser!!.employeeSpend!!
                installFor(booking)

                val result =
                    SpendingApi().getEmployeeSpend(
                        fromMonthYear = employeeSpend.period.fromMonthYear,
                        toMonthYear = employeeSpend.period.toMonthYear,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                    )

                result.attachEvidence("Get Employee Spend")

                expect("returns employee spend") {
                    result.response.status.value shouldBe 200
                    val expectedEmployeeSpend = employeeSpend.items.single()
                    val actualEmployeeSpend = result.body.single()
                    actualEmployeeSpend.companyAccountId shouldBe booking.loggedUser.companyAccountId
                    actualEmployeeSpend.employeeAccountId shouldBe booking.loggedUser.employeeId
                    actualEmployeeSpend.year shouldBe expectedEmployeeSpend.year
                    actualEmployeeSpend.month shouldBe expectedEmployeeSpend.month
                    actualEmployeeSpend.noOfBookings shouldBe expectedEmployeeSpend.noOfBookings
                    actualEmployeeSpend.bookingValue shouldBe expectedEmployeeSpend.bookingValue
                    actualEmployeeSpend.bookingCurrency shouldBe expectedEmployeeSpend.bookingCurrency
                }
            }
        },
    )

private fun employeeSpendBooking(): Booking =
    Booking(
        loggedUser =
            LoggedUser(
                accessLevel = "STANDARD",
                companyAccountId = "employee-spend-company-account-123",
                companyId = "employee-spend-company-123",
                employeeId = "employee-spend-employee-123",
                email = "employee.spend.test.user@premierinn.com",
                employeeSpend =
                    EmployeeSpend(
                        period =
                            ReportingPeriod(
                                fromMonthYear = FROM_MONTH_YEAR,
                                toMonthYear = TO_MONTH_YEAR,
                            ),
                        items =
                            listOf(
                                EmployeeSpendItem(
                                    year = 2024,
                                    month = 11,
                                    noOfBookings = 3,
                                    bookingValue = 234.56,
                                    bookingCurrency = "GBP",
                                ),
                            ),
                    ),
            ),
    )

private const val FROM_MONTH_YEAR = "11-2024"
private const val TO_MONTH_YEAR = "09-2025"
