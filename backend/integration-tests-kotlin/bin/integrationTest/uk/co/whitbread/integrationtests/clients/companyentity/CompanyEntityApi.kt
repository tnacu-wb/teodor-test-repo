package uk.co.whitbread.integrationtests.clients.companyentity

import io.ktor.client.request.parameter
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompaniesResponse
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompanyResponse
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient

class CompanyEntityApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.companyEntityBaseUrl),
) {
    suspend fun getCompaniesFromCdh(
        companyName: String,
        offset: Int,
        limit: Int,
        negotiatedRateCompanies: Boolean = false,
        testId: String,
    ): ApiResult<CompaniesResponse> =
        http.get("/v1/companies", testId) {
            parameter("companyName", companyName)
            parameter("offset", offset)
            parameter("limit", limit)
            parameter("negotiatedRateCompanies", negotiatedRateCompanies)
        }

    suspend fun getCompaniesProfile(
        hotelId: String,
        limit: Int,
        companyName: String? = null,
        arNumber: String? = null,
        testId: String,
    ): ApiResult<CompaniesResponse> =
        http.get("/v1/companies/profile", testId) {
            parameter("hotelId", hotelId)
            parameter("limit", limit)
            companyName?.let { parameter("companyName", it) }
            arNumber?.let { parameter("arNumber", it) }
        }

    suspend fun getCompany(
        corporateId: String,
        excludeNegotiatedRates: Boolean? = null,
        testId: String,
    ): ApiResult<CompanyResponse> =
        http.get("/v1/companies/$corporateId", testId) {
            excludeNegotiatedRates?.let { parameter("excludeNegotiatedRates", it) }
        }

    suspend fun getCompanyById(
        id: String,
        testId: String,
    ): ApiResult<CompanyResponse> = http.get("/v1/companies/id/$id", testId)

    suspend fun getCompanyByIdText(
        id: String,
        testId: String,
    ): ApiResult<String> = http.getText("/v1/companies/id/$id", testId)
}
