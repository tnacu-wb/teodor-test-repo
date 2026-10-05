package uk.co.whitbread.integrationtests.clients.spendingentity

import io.ktor.client.request.header
import io.ktor.client.request.parameter
import uk.co.whitbread.integrationtests.clients.spendingentity.model.AccountSpendingResponse
import uk.co.whitbread.integrationtests.clients.spendingentity.model.CompanySpendingResponse
import uk.co.whitbread.integrationtests.clients.spendingentity.model.EmployeeSpendResponse
import uk.co.whitbread.integrationtests.clients.spendingentity.model.PaymentInfoResponse
import uk.co.whitbread.integrationtests.clients.spendingentity.model.UpcomingSpendingResponse
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient

class SpendingApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.spendingEntityBaseUrl),
) {
    suspend fun getCompanySpending(
        fromMonthYear: String,
        toMonthYear: String,
        testId: String,
        wbAuthorization: String? = null,
    ): ApiResult<CompanySpendingResponse> =
        http.get("/v1/spending/companySpending", testId) {
            wbAuthorization?.let { header(WB_AUTHORIZATION_HEADER, it) }
            parameter("fromMonthYear", fromMonthYear)
            parameter("toMonthYear", toMonthYear)
        }

    suspend fun getAccountSpending(
        pibaAccountId: String,
        fromMonthYear: String,
        toMonthYear: String,
        testId: String,
        wbAuthorization: String? = null,
        tetheredUserGuid: String? = null,
    ): ApiResult<AccountSpendingResponse> =
        http.get("/v1/spending/accountSpending", testId) {
            wbAuthorization?.let { header(WB_AUTHORIZATION_HEADER, it) }
            parameter("pibaAccountId", pibaAccountId)
            parameter("fromMonthYear", fromMonthYear)
            parameter("toMonthYear", toMonthYear)
            tetheredUserGuid?.let { parameter("tetheredUserGuid", it) }
        }

    suspend fun getEmployeeSpend(
        fromMonthYear: String,
        toMonthYear: String,
        testId: String,
        wbAuthorization: String? = null,
    ): ApiResult<List<EmployeeSpendResponse>> =
        http.get("/v1/spending/employeeSpend", testId) {
            wbAuthorization?.let { header(WB_AUTHORIZATION_HEADER, it) }
            parameter("fromMonthYear", fromMonthYear)
            parameter("toMonthYear", toMonthYear)
        }

    suspend fun getUpcomingSpending(
        accountId: String,
        testId: String,
        wbAuthorization: String? = null,
        tetheredUserGuid: String? = null,
    ): ApiResult<UpcomingSpendingResponse> =
        http.get("/v1/spending/upcomingSpending", testId) {
            wbAuthorization?.let { header(WB_AUTHORIZATION_HEADER, it) }
            parameter("accountId", accountId)
            tetheredUserGuid?.let { parameter("tetheredUserGuid", it) }
        }

    suspend fun getPaymentInfo(
        accountId: String,
        page: Int,
        size: Int,
        nonInvoiceOnly: Boolean,
        testId: String,
        wbAuthorization: String? = null,
        tetheredUserGuid: String? = null,
    ): ApiResult<PaymentInfoResponse> =
        http.get("/v1/spending/paymentInfo", testId) {
            wbAuthorization?.let { header(WB_AUTHORIZATION_HEADER, it) }
            parameter("accountId", accountId)
            parameter("page", page)
            parameter("size", size)
            parameter("nonInvoiceOnly", nonInvoiceOnly)
            tetheredUserGuid?.let { parameter("tetheredUserGuid", it) }
        }

    private companion object {
        const val WB_AUTHORIZATION_HEADER = "WB-Authorization"
    }
}
