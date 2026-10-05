package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Response of `GET /ohip/v1/profile/company/id/{companyId}`. */
@Serializable
data class CompanyProfileResponse(
    val name: String? = null,
    val telephoneNumber: String? = null,
    val profileType: String? = null,
    val corpId: String? = null,
    val companyId: String? = null,
    val language: String? = null,
    val arNumber: String? = null,
    val active: Boolean = false,
    val restricted: Boolean = false,
)

@Serializable
data class CompaniesProfileResponse(
    val companies: List<CompanyProfileResponse> = emptyList(),
    val totalResults: Int = 0,
    val hasMore: Boolean = false,
    val limit: Int = 0,
    val offset: Int = 0,
)
