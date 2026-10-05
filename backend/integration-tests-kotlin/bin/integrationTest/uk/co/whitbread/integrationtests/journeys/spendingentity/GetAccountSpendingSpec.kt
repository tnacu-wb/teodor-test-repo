package uk.co.whitbread.integrationtests.journeys.spendingentity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.spendingentity.SpendingApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.auth.AuthTokens
import uk.co.whitbread.integrationtests.testkit.model.AccountSpend
import uk.co.whitbread.integrationtests.testkit.model.AccountSpendItem
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.ReportingPeriod
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

class GetAccountSpendingSpec :
    JourneySpec(
        "account spending for authenticated user",
        {
            scenario("GET /v1/spending/accountSpending without WB-Authorization") {
                val booking = Booking()
                installFor(booking)

                val result =
                    SpendingApi().getAccountSpending(
                        pibaAccountId = PIBA_ACCOUNT_ID,
                        fromMonthYear = FROM_MONTH_YEAR,
                        toMonthYear = TO_MONTH_YEAR,
                        testId = testId,
                    )

                result.attachEvidence("Get Account Spending")

                expect("returns 400") {
                    result.response.status.value shouldBe 400
                    result.bodyText shouldContain "Required request header 'WB-Authorization'"
                }
            }

            scenario("GET /v1/spending/accountSpending with authenticated user and tethered user") {
                val booking = accountSpendingBooking()
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                val accountSpend = tetheredAccount.accountSpend!!
                installFor(booking)

                val result =
                    SpendingApi().getAccountSpending(
                        pibaAccountId = tetheredAccount.pibaAccountId,
                        fromMonthYear = accountSpend.period.fromMonthYear,
                        toMonthYear = accountSpend.period.toMonthYear,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                        tetheredUserGuid = tetheredAccount.tetheredUserGuid,
                    )

                result.attachEvidence("Get Account Spending")

                expect("returns account spending") {
                    result.response.status.value shouldBe 200
                    val expectedAccountSpending = accountSpend.items.single()
                    val accountSpending = result.body.accountSpendingDtoList.single()
                    accountSpending.pibaAccountId shouldBe tetheredAccount.pibaAccountId
                    accountSpending.year shouldBe expectedAccountSpending.year
                    accountSpending.month shouldBe expectedAccountSpending.month
                    accountSpending.noOfBookings shouldBe expectedAccountSpending.noOfBookings
                    accountSpending.bookingValue shouldBe expectedAccountSpending.bookingValue
                }
            }

            scenario("GET /v1/spending/accountSpending with authenticated user and account lookup") {
                val booking = accountSpendingBooking()
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                val accountSpend = tetheredAccount.accountSpend!!
                installFor(booking)

                val result =
                    SpendingApi().getAccountSpending(
                        pibaAccountId = tetheredAccount.pibaAccountId,
                        fromMonthYear = accountSpend.period.fromMonthYear,
                        toMonthYear = accountSpend.period.toMonthYear,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                    )

                result.attachEvidence("Get Account Spending")

                expect("returns account spending") {
                    result.response.status.value shouldBe 200
                    val expectedAccountSpending = accountSpend.items.single()
                    val accountSpending = result.body.accountSpendingDtoList.single()
                    accountSpending.pibaAccountId shouldBe tetheredAccount.pibaAccountId
                    accountSpending.year shouldBe expectedAccountSpending.year
                    accountSpending.month shouldBe expectedAccountSpending.month
                    accountSpending.noOfBookings shouldBe expectedAccountSpending.noOfBookings
                    accountSpending.bookingValue shouldBe expectedAccountSpending.bookingValue
                }
            }
        },
    )

private fun accountSpendingBooking(): Booking =
    Booking(
        loggedUser =
            LoggedUser(
                accessLevel = "STANDARD",
                companyAccountId = "123456",
                companyId = "company-123",
                employeeId = "employee-123",
                email = "test.user@premierinn.com",
                tetheredAccount =
                    TetheredAccount(
                        pibaAccountId = PIBA_ACCOUNT_ID,
                        tetheredUserGuid = TETHERED_USER_GUID,
                        accountSpend =
                            AccountSpend(
                                period =
                                    ReportingPeriod(
                                        fromMonthYear = FROM_MONTH_YEAR,
                                        toMonthYear = TO_MONTH_YEAR,
                                    ),
                                items =
                                    listOf(
                                        AccountSpendItem(
                                            year = 2024,
                                            month = 11,
                                            noOfBookings = 3,
                                            bookingValue = 234.56,
                                        ),
                                    ),
                            ),
                    ),
            ),
    )

private const val PIBA_ACCOUNT_ID = "PIBA-ACCOUNT-123"
private const val TETHERED_USER_GUID = "tethered-guid-123"
private const val FROM_MONTH_YEAR = "11-2024"
private const val TO_MONTH_YEAR = "09-2025"
