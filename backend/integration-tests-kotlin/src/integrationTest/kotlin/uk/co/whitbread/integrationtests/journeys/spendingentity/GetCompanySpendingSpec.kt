package uk.co.whitbread.integrationtests.journeys.spendingentity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.spendingentity.SpendingApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.auth.AuthTokens
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.CompanySpend
import uk.co.whitbread.integrationtests.testkit.model.CompanySpendItem
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.ReportingPeriod

class GetCompanySpendingSpec :
    JourneySpec(
        "company spending for authenticated super user",
        {
            scenario("GET /v1/spending/companySpending without WB-Authorization") {
                val booking = Booking()
                installFor(booking)

                val result =
                    SpendingApi().getCompanySpending(
                        fromMonthYear = "11-2024",
                        toMonthYear = "09-2025",
                        testId = testId,
                    )

                result.attachEvidence("Get Company Spending")

                expect("returns 400") {
                    result.response.status.value shouldBe 400
                    result.bodyText shouldContain "Required request header 'WB-Authorization'"
                }
            }

            scenario("GET /v1/spending/companySpending with authenticated super user") {
                val booking = companySpendingBooking()
                val companySpend = booking.loggedUser!!.companySpend!!
                installFor(booking)

                val result =
                    SpendingApi().getCompanySpending(
                        fromMonthYear = companySpend.period.fromMonthYear,
                        toMonthYear = companySpend.period.toMonthYear,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                    )

                result.attachEvidence("Get Company Spending")

                expect("returns company spending") {
                    result.response.status.value shouldBe 200
                    val expectedCompanySpending = companySpend.items.single()
                    val companySpending = result.body.companySpendingDtoList.single()
                    companySpending.companyAccountId shouldBe booking.loggedUser.companyAccountId
                    companySpending.year shouldBe expectedCompanySpending.year
                    companySpending.month shouldBe expectedCompanySpending.month
                    companySpending.noOfBookings shouldBe expectedCompanySpending.noOfBookings
                    companySpending.bookingValue shouldBe expectedCompanySpending.bookingValue
                    companySpending.bookingCurrency shouldBe expectedCompanySpending.bookingCurrency
                }
            }

            scenario("GET /v1/spending/companySpending with authenticated non-super user") {
                val booking = companySpendingBooking(accessLevel = "STANDARD")
                installFor(booking)

                val result =
                    SpendingApi().getCompanySpending(
                        fromMonthYear = FROM_MONTH_YEAR,
                        toMonthYear = TO_MONTH_YEAR,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser!!),
                    )

                result.attachEvidence("Get Company Spending")

                expect("returns 403 before calling CDH") {
                    result.response.status.value shouldBe 403
                }
            }
        },
    )

private fun companySpendingBooking(accessLevel: String = "SUPER"): Booking =
    Booking(
        loggedUser =
            LoggedUser(
                accessLevel = accessLevel,
                companyAccountId = "123456",
                companyId = "company-123",
                employeeId = "employee-123",
                email = "test.user@premierinn.com",
                companySpend = if (accessLevel == "SUPER") companySpendFixture() else null,
            ),
    )

private fun companySpendFixture(): CompanySpend =
    CompanySpend(
        period =
            ReportingPeriod(
                fromMonthYear = FROM_MONTH_YEAR,
                toMonthYear = TO_MONTH_YEAR,
            ),
        items =
            listOf(
                CompanySpendItem(
                    year = 2024,
                    month = 11,
                    noOfBookings = 2,
                    bookingValue = 123.45,
                    bookingCurrency = "GBP",
                ),
            ),
    )

private const val FROM_MONTH_YEAR = "11-2024"
private const val TO_MONTH_YEAR = "09-2025"
