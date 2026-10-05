package uk.co.whitbread.integrationtests.stubs.cdh

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.TetheredAccount
import uk.co.whitbread.integrationtests.testkit.model.TransactionAggregate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val CDH_TRANSACTION_DETAILS_STUB_ID = "booking.cdh.transaction-details"

/** Builds scoped CDH transaction mappings for every configured aggregate period. */
fun transactionAggregates(
    tetheredAccount: TetheredAccount,
    transactionAggregates: List<TransactionAggregate>,
): PlannedStub =
    PlannedStub(
        id = CDH_TRANSACTION_DETAILS_STUB_ID,
        target = WireMockTarget.CDH,
        mappings =
            transactionAggregates.map { aggregate ->
                transactionDetails(
                    tetheredAccount = tetheredAccount,
                    fromDate = aggregate.fromDate,
                    toDate = aggregate.toDate,
                    totalBookingValue = aggregate.totalBookingValue,
                )
            },
    )

/** Builds one CDH transaction mapping with format-safe JSONPath operands. */
private fun transactionDetails(
    tetheredAccount: TetheredAccount,
    fromDate: LocalDate,
    toDate: LocalDate,
    totalBookingValue: Double,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/IB/V1/Report/transactions",
                bodyPatterns =
                    listOf(
                        BodyPattern(
                            matchesJsonPath = "$[?(@.PIBAAccountNo == ${jsonPathStringLiteral(tetheredAccount.pibaAccountId)})]",
                        ),
                        BodyPattern(matchesJsonPath = "$[?(@.fromdate == ${jsonPathStringLiteral(fromDate.toCdhDate())})]"),
                        BodyPattern(matchesJsonPath = "$[?(@.todate == ${jsonPathStringLiteral(toDate.toCdhDate())})]"),
                        BodyPattern(matchesJsonPath = "$[?(@.PageSize == 1)]"),
                        BodyPattern(matchesJsonPath = "$[?(@.PageNumber == 1)]"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = transactionDetailsResponse(totalBookingValue),
            ),
    )

private fun transactionDetailsResponse(totalBookingValue: Double): JsonObject =
    JsonObject(
        mapOf(
            "TotalBookingValue" to JsonPrimitive(totalBookingValue),
            "Transactions" to JsonArray(emptyList()),
            "Paging" to
                JsonObject(
                    mapOf(
                        "TotalResults" to JsonPrimitive(0),
                        "CurrentPage" to JsonPrimitive(1),
                        "PageSize" to JsonPrimitive(1),
                    ),
                ),
        ),
    )

private fun LocalDate.toCdhDate(): String = format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
