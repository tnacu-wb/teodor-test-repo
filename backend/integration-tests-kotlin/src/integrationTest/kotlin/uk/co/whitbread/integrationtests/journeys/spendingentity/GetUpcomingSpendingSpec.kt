package uk.co.whitbread.integrationtests.journeys.spendingentity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.spendingentity.SpendingApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.auth.AuthTokens
import uk.co.whitbread.integrationtests.testkit.model.AccountActivity
import uk.co.whitbread.integrationtests.testkit.model.BillingFrequency
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount
import uk.co.whitbread.integrationtests.testkit.model.TransactionAggregate
import uk.co.whitbread.integrationtests.testkit.model.WorldlineAccount
import java.time.LocalDate
import java.time.ZoneOffset

class GetUpcomingSpendingSpec :
    JourneySpec(
        "upcoming spending for authenticated user",
        {
            scenario("GET /v1/spending/upcomingSpending without WB-Authorization") {
                val booking = Booking()
                installFor(booking)

                val result =
                    SpendingApi().getUpcomingSpending(
                        accountId = PIBA_ACCOUNT_ID,
                        testId = testId,
                    )

                result.attachEvidence("Get Upcoming Spending")

                expect("returns 400") {
                    result.response.status.value shouldBe 400
                    result.bodyText shouldContain "Required request header 'WB-Authorization'"
                }
            }

            scenario("GET /v1/spending/upcomingSpending with authenticated user and tethered user") {
                val expectedSpend = monthlyUpcomingSpend()
                val booking = upcomingSpendingBooking(expectedSpend)
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                installFor(booking)

                val result =
                    SpendingApi().getUpcomingSpending(
                        accountId = tetheredAccount.pibaAccountId,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                        tetheredUserGuid = tetheredAccount.tetheredUserGuid,
                    )

                result.attachEvidence("Get Upcoming Spending")

                expect("returns upcoming spending") {
                    result.response.status.value shouldBe 200
                    result.body.accountStatus shouldBe expectedSpend.accountStatus
                    result.body.currency shouldBe expectedSpend.currency
                    result.body.expectedSpendToday shouldBe expectedSpend.spendToday
                    result.body.expectedNextBilling shouldBe expectedSpend.nextBilling
                    result.body.expectedNextPeriod shouldBe expectedSpend.nextPeriod
                }
            }

            scenario("GET /v1/spending/upcomingSpending with authenticated user and account lookup") {
                val expectedSpend = monthlyUpcomingSpend()
                val booking = upcomingSpendingBooking(expectedSpend)
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                installFor(booking)

                val result =
                    SpendingApi().getUpcomingSpending(
                        accountId = tetheredAccount.pibaAccountId,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                    )

                result.attachEvidence("Get Upcoming Spending")

                expect("returns upcoming spending") {
                    result.response.status.value shouldBe 200
                    result.body.accountStatus shouldBe expectedSpend.accountStatus
                    result.body.currency shouldBe expectedSpend.currency
                    result.body.expectedSpendToday shouldBe expectedSpend.spendToday
                    result.body.expectedNextBilling shouldBe expectedSpend.nextBilling
                    result.body.expectedNextPeriod shouldBe expectedSpend.nextPeriod
                }
            }
        },
    )

private fun upcomingSpendingBooking(expectedSpend: ExpectedUpcomingSpend): Booking =
    Booking(
        loggedUser =
            LoggedUser(
                accessLevel = "STANDARD",
                companyAccountId = "upcoming-company-account-123",
                companyId = "upcoming-company-123",
                employeeId = "upcoming-employee-123",
                email = "upcoming.test.user@premierinn.com",
                tetheredAccount =
                    TetheredAccount(
                        pibaAccountId = PIBA_ACCOUNT_ID,
                        tetheredUserGuid = TETHERED_USER_GUID,
                        accountActivity =
                            AccountActivity(
                                worldlineAccount =
                                    WorldlineAccount(
                                        billingFrequency = BillingFrequency.MONTHLY,
                                        status = expectedSpend.accountStatus,
                                        currency = expectedSpend.currency,
                                    ),
                                transactionAggregates = expectedSpend.transactionAggregates,
                            ),
                    ),
            ),
    )

private fun monthlyUpcomingSpend(today: LocalDate = LocalDate.now(ZoneOffset.UTC)): ExpectedUpcomingSpend {
    val spendToday = 12.34
    val nextPeriod = 90.12
    val nextBillingEnd =
        if (today.dayOfMonth > 2) {
            today.withDayOfMonth(2).plusMonths(1)
        } else {
            today.withDayOfMonth(2)
        }
    val nextPeriodStart = nextBillingEnd.plusDays(1)
    val nextPeriodEnd = nextPeriodStart.plusMonths(1).withDayOfMonth(2)
    val nextBilling = if (today == nextBillingEnd) spendToday else 56.78

    return ExpectedUpcomingSpend(
        accountStatus = "Open",
        currency = "GBP",
        spendToday = spendToday,
        nextBilling = nextBilling,
        nextPeriod = nextPeriod,
        transactionAggregates =
            buildList {
                add(
                    TransactionAggregate(
                        fromDate = today,
                        toDate = today,
                        totalBookingValue = spendToday,
                    ),
                )
                add(
                    TransactionAggregate(
                        fromDate = nextPeriodStart,
                        toDate = nextPeriodEnd,
                        totalBookingValue = nextPeriod,
                    ),
                )
                if (today != nextBillingEnd) {
                    add(
                        TransactionAggregate(
                            fromDate = today,
                            toDate = nextBillingEnd,
                            totalBookingValue = nextBilling,
                        ),
                    )
                }
            },
    )
}

private data class ExpectedUpcomingSpend(
    val accountStatus: String,
    val currency: String,
    val spendToday: Double,
    val nextBilling: Double,
    val nextPeriod: Double,
    val transactionAggregates: List<TransactionAggregate>,
)

private const val PIBA_ACCOUNT_ID = "PIBA-UPCOMING-ACCOUNT-123"
private const val TETHERED_USER_GUID = "tethered-upcoming-guid-123"
