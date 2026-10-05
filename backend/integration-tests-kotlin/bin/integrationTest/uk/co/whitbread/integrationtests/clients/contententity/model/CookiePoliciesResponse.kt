package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class CookiePoliciesResponse(
    val cookiePolicies: CookiePoliciesInformationResponse? = null,
)

@Serializable
data class CookiePoliciesInformationResponse(
    val version: String? = null,
    val brand: String? = null,
    val config: CookieDurationConfigResponse? = null,
    val introView: CookieIntroViewResponse? = null,
    val manageView: CookieManageViewResponse? = null,
)

@Serializable
data class CookieDurationConfigResponse(
    val cookieOptOutExpiryDays: String? = null,
    val cookieOptInExpiryDays: String? = null,
)

@Serializable
data class CookieIntroViewResponse(
    val title: String? = null,
    val description: String? = null,
    val manageButtonText: String? = null,
    val acceptAllButtonText: String? = null,
    val necessaryOnlyButtonText: String? = null,
)

@Serializable
data class CookieManageViewResponse(
    val title: String? = null,
    val description: String? = null,
    val saveSettingsButtonText: String? = null,
    val alwaysActiveText: String? = null,
    val cookieGroup: List<CookieGroupResponse> = emptyList(),
)

@Serializable
data class CookieGroupResponse(
    val cookieName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isAlwaysActive: Boolean? = null,
    val toggleLabel: String? = null,
)
