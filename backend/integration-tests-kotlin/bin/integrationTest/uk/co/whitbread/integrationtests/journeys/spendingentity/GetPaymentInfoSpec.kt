package uk.co.whitbread.integrationtests.journeys.spendingentity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.clients.spendingentity.SpendingApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.auth.AuthTokens
import uk.co.whitbread.integrationtests.testkit.model.AccountActivity
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import uk.co.whitbread.integrationtests.testkit.model.PaymentHistory
import uk.co.whitbread.integrationtests.testkit.model.PaymentInfoItem
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

class GetPaymentInfoSpec :
    JourneySpec(
        "payment info for authenticated user",
        {
            scenario("GET /v1/spending/paymentInfo without WB-Authorization") {
                val booking = Booking()
                installFor(booking)

                val result =
                    SpendingApi().getPaymentInfo(
                        accountId = PIBA_ACCOUNT_ID,
                        page = PAGE,
                        size = SIZE,
                        nonInvoiceOnly = NON_INVOICE_ONLY,
                        testId = testId,
                    )

                result.attachEvidence("Get Payment Info")

                expect("returns 400") {
                    result.response.status.value shouldBe 400
                    result.bodyText shouldContain "Required request header 'WB-Authorization'"
                }
            }

            scenario("GET /v1/spending/paymentInfo with authenticated user and tethered user") {
                val booking = paymentInfoBooking()
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                val paymentHistory = tetheredAccount.accountActivity!!.paymentHistory!!
                installFor(booking)

                val result =
                    SpendingApi().getPaymentInfo(
                        accountId = tetheredAccount.pibaAccountId,
                        page = PAGE,
                        size = SIZE,
                        nonInvoiceOnly = NON_INVOICE_ONLY,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                        tetheredUserGuid = tetheredAccount.tetheredUserGuid,
                    )

                result.attachEvidence("Get Payment Info")

                expect("returns payment info") {
                    result.response.status.value shouldBe 200
                    val expectedPayment = paymentHistory.payments.single()
                    val payment = result.body.payments.single()
                    payment.paymentDate shouldBe expectedPayment.paymentDate
                    payment.paymentDescription shouldBe expectedPayment.paymentDescription
                    payment.failureReason shouldBe expectedPayment.failureReason
                    payment.paymentFailed shouldBe false
                    payment.paymentValue!!.value shouldBe expectedPayment.value
                    payment.paymentValue.currencyCode shouldBe expectedPayment.currencyCode
                    payment.paymentValue.currencySymbol shouldBe "£"
                }
            }

            scenario("GET /v1/spending/paymentInfo with authenticated user and account lookup") {
                val booking = paymentInfoBooking()
                val tetheredAccount = booking.loggedUser!!.tetheredAccount!!
                val paymentHistory = tetheredAccount.accountActivity!!.paymentHistory!!
                installFor(booking)

                val result =
                    SpendingApi().getPaymentInfo(
                        accountId = tetheredAccount.pibaAccountId,
                        page = PAGE,
                        size = SIZE,
                        nonInvoiceOnly = NON_INVOICE_ONLY,
                        testId = testId,
                        wbAuthorization = AuthTokens.bearer(booking.loggedUser),
                    )

                result.attachEvidence("Get Payment Info")

                expect("returns payment info") {
                    result.response.status.value shouldBe 200
                    val expectedPayment = paymentHistory.payments.single()
                    val payment = result.body.payments.single()
                    payment.paymentDate shouldBe expectedPayment.paymentDate
                    payment.paymentDescription shouldBe expectedPayment.paymentDescription
                    payment.failureReason shouldBe expectedPayment.failureReason
                    payment.paymentFailed shouldBe false
                    payment.paymentValue!!.value shouldBe expectedPayment.value
                    payment.paymentValue.currencyCode shouldBe expectedPayment.currencyCode
                    payment.paymentValue.currencySymbol shouldBe "£"
                }
            }
        },
    )

private fun paymentInfoBooking(): Booking =
    Booking(
        loggedUser =
            LoggedUser(
                accessLevel = "STANDARD",
                companyAccountId = "payment-company-account-123",
                companyId = "payment-company-123",
                employeeId = "payment-employee-123",
                email = "payment.test.user@premierinn.com",
                tetheredAccount =
                    TetheredAccount(
                        pibaAccountId = PIBA_ACCOUNT_ID,
                        tetheredUserGuid = TETHERED_USER_GUID,
                        accountActivity =
                            AccountActivity(
                                paymentHistory =
                                    PaymentHistory(
                                        payments =
                                            listOf(
                                                PaymentInfoItem(
                                                    paymentDate = "2026-06-29",
                                                    paymentDescription = "Payment received",
                                                    failureReason = "N/A",
                                                    value = "12.34",
                                                    currencyCode = "826",
                                                ),
                                            ),
                                    ),
                            ),
                    ),
            ),
    )

private const val PIBA_ACCOUNT_ID = "PIBA-PAYMENT-ACCOUNT-123"
private const val TETHERED_USER_GUID = "tethered-payment-guid-123"
private const val PAGE = 1
private const val SIZE = 10
private const val NON_INVOICE_ONLY = false
