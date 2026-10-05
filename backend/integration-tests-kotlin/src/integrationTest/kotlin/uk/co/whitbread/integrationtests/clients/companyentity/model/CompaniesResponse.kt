package uk.co.whitbread.integrationtests.clients.companyentity.model

import kotlinx.serialization.Serializable

@Serializable
data class CompaniesResponse(
    val companies: List<CompanyResponse> = emptyList(),
    val totalResults: Int = 0,
    val hasMore: Boolean = false,
    val limit: Int = 0,
    val offset: Int = 0,
    val page: Int = 0,
    val pageSize: Int = 0,
)

@Serializable
data class CompanyResponse(
    val name: String? = null,
    val telephoneNumber: String? = null,
    val profileType: String? = null,
    val corpId: String? = null,
    val companyId: String? = null,
    val language: String? = null,
    val arNumber: String? = null,
    val active: Boolean = false,
    val restricted: Boolean = false,
    val negotiatedRateEnabled: Boolean = false,
    val restrictedReason: String? = null,
    val address: CompanyAddressResponse? = null,
)

@Serializable
data class CompanyAddressResponse(
    val companyName: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val addressLine4: String? = null,
    val city: String? = null,
    val country: String? = null,
    val postalCode: String? = null,
)
