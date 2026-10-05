package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Cookie-consent content the mocked AEM cookie-policies endpoint serves. Presence gates the cookie-policies stub.")
data class AemCookiePolicies(
    val country: String,
    val language: String,
    val brand: String,
    val version: String,
    val config: AemCookieDurationConfig,
    val introView: AemCookieIntroView,
    val manageView: AemCookieManageView,
) {
    init {
        require(country.isNotBlank()) { "cookiePolicies country must not be blank" }
        require(language.isNotBlank()) { "cookiePolicies language must not be blank" }
        require(brand.isNotBlank()) { "cookiePolicies brand must not be blank" }
        require(version.isNotBlank()) { "cookiePolicies version must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Consent cookie expiry, in days, for opt-in and opt-out.")
data class AemCookieDurationConfig(
    val cookieOptOutExpiryDays: String,
    val cookieOptInExpiryDays: String,
) {
    init {
        require(cookieOptOutExpiryDays.isNotBlank()) {
            "cookiePolicies.config.cookieOptOutExpiryDays must not be blank"
        }
        require(cookieOptInExpiryDays.isNotBlank()) {
            "cookiePolicies.config.cookieOptInExpiryDays must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("First consent dialog: copy and button labels.")
data class AemCookieIntroView(
    val title: String,
    val description: String,
    val manageButtonText: String,
    val acceptAllButtonText: String,
    val necessaryOnlyButtonText: String,
) {
    init {
        require(title.isNotBlank()) { "cookiePolicies.introView.title must not be blank" }
        require(description.isNotBlank()) { "cookiePolicies.introView.description must not be blank" }
        require(manageButtonText.isNotBlank()) { "cookiePolicies.introView.manageButtonText must not be blank" }
        require(acceptAllButtonText.isNotBlank()) {
            "cookiePolicies.introView.acceptAllButtonText must not be blank"
        }
        require(necessaryOnlyButtonText.isNotBlank()) {
            "cookiePolicies.introView.necessaryOnlyButtonText must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("Manage-preferences dialog: copy and the cookie groups on offer.")
data class AemCookieManageView(
    val title: String,
    val description: String,
    val saveSettingsButtonText: String,
    val alwaysActiveText: String,
    val cookieGroup: List<AemCookieGroup>,
) {
    init {
        require(title.isNotBlank()) { "cookiePolicies.manageView.title must not be blank" }
        require(description.isNotBlank()) { "cookiePolicies.manageView.description must not be blank" }
        require(saveSettingsButtonText.isNotBlank()) {
            "cookiePolicies.manageView.saveSettingsButtonText must not be blank"
        }
        require(alwaysActiveText.isNotBlank()) { "cookiePolicies.manageView.alwaysActiveText must not be blank" }
        require(cookieGroup.isNotEmpty()) { "cookiePolicies.manageView.cookieGroup must not be empty" }
    }
}

@Serializable
@JsonSchema.Description("One toggleable cookie category.")
data class AemCookieGroup(
    val cookieName: String,
    val title: String,
    val description: String,
    val isAlwaysActive: Boolean,
    val toggleLabel: String,
) {
    init {
        require(cookieName.isNotBlank()) { "cookiePolicies.manageView.cookieGroup.cookieName must not be blank" }
        require(title.isNotBlank()) { "cookiePolicies.manageView.cookieGroup.title must not be blank" }
        require(description.isNotBlank()) { "cookiePolicies.manageView.cookieGroup.description must not be blank" }
        require(toggleLabel.isNotBlank()) { "cookiePolicies.manageView.cookieGroup.toggleLabel must not be blank" }
    }
}
