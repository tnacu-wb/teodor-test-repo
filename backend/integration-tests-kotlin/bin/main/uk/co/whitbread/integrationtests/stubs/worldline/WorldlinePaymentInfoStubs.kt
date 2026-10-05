package uk.co.whitbread.integrationtests.stubs.worldline

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.PaymentInfoItem
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount

const val WORLDLINE_PAYMENT_INFO_STUB_ID = "booking.worldline.payment-info"

fun paymentInfo(
    tetheredAccount: TetheredAccount,
    payments: List<PaymentInfoItem>,
    page: Int = 1,
    size: Int = 10,
    nonInvoiceOnly: Boolean = false,
): PlannedStub =
    PlannedStub(
        id = WORLDLINE_PAYMENT_INFO_STUB_ID,
        target = WireMockTarget.WORLDLINE,
        mappings =
            listOf(
                paymentInfoMapping(
                    tetheredAccount = tetheredAccount,
                    payments = payments,
                    page = page,
                    size = size,
                    nonInvoiceOnly = nonInvoiceOnly,
                ),
            ),
    )

private fun paymentInfoMapping(
    tetheredAccount: TetheredAccount,
    payments: List<PaymentInfoItem>,
    page: Int,
    size: Int,
    nonInvoiceOnly: Boolean,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/PIRestAPI/api/v1/account/paymentInfo",
                queryParameters =
                    mapOf(
                        "page" to StringValuePattern(equalTo = page.toString()),
                        "maxDisplayRows" to StringValuePattern(equalTo = size.toString()),
                        "nonInvoicedOnly" to StringValuePattern(equalTo = nonInvoiceOnly.toString()),
                    ),
                headers =
                    mapOf(
                        "TetheredUserGuid" to StringValuePattern(equalTo = tetheredAccount.tetheredUserGuid),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = paymentInfoResponse(payments),
            ),
    )

private fun paymentInfoResponse(payments: List<PaymentInfoItem>): JsonObject =
    JsonObject(
        mapOf(
            "responseCode" to JsonPrimitive("OK"),
            "data" to JsonArray(payments.map(::paymentInfoItem)),
            "errors" to JsonNull,
        ),
    )

private fun paymentInfoItem(item: PaymentInfoItem): JsonObject =
    JsonObject(
        mapOf(
            "paymentDate" to JsonPrimitive(item.paymentDate),
            "paymentDescription" to JsonPrimitive(item.paymentDescription),
            "failureReason" to JsonPrimitive(item.failureReason),
            "paymentValue" to
                JsonObject(
                    mapOf(
                        "value" to JsonPrimitive(item.value),
                        "currencyCode" to JsonPrimitive(item.currencyCode),
                    ),
                ),
        ),
    )
