package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemCookieGroup
import uk.co.whitbread.integrationtests.testkit.model.AemCookiePolicies

const val AEM_COOKIE_POLICIES_STUB_ID = "booking.aem.cookie-policies"

fun cookiePolicies(cookiePolicies: AemCookiePolicies): PlannedStub =
    PlannedStub(
        id = AEM_COOKIE_POLICIES_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(cookiePoliciesMapping(cookiePolicies)),
    )

private fun cookiePoliciesMapping(cookiePolicies: AemCookiePolicies): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url =
                    "/${cookiePolicies.country}/${cookiePolicies.language}" +
                        "/content-service.cookie-policies.detail/brand/${cookiePolicies.brand}.json",
            ),
        response =
            jsonResponse(
                body = cookiePoliciesBody(cookiePolicies),
            ),
    )

private fun cookiePoliciesBody(cookiePolicies: AemCookiePolicies): String {
    val body =
        mapOf(
            "cookiePolicies" to
                mapOf(
                    "version" to cookiePolicies.version,
                    "config" to
                        mapOf(
                            "cookieOptOutExpiryDays" to cookiePolicies.config.cookieOptOutExpiryDays,
                            "cookieOptInExpiryDays" to cookiePolicies.config.cookieOptInExpiryDays,
                        ),
                    "brand" to cookiePolicies.brand,
                    "introView" to
                        mapOf(
                            "title" to cookiePolicies.introView.title,
                            "description" to cookiePolicies.introView.description,
                            "manageButtonText" to cookiePolicies.introView.manageButtonText,
                            "acceptAllButtonText" to cookiePolicies.introView.acceptAllButtonText,
                            "necessaryOnlyButtonText" to cookiePolicies.introView.necessaryOnlyButtonText,
                        ),
                    "manageView" to
                        mapOf(
                            "title" to cookiePolicies.manageView.title,
                            "description" to cookiePolicies.manageView.description,
                            "saveSettingsButtonText" to cookiePolicies.manageView.saveSettingsButtonText,
                            "alwaysActiveText" to cookiePolicies.manageView.alwaysActiveText,
                            "cookieGroup" to cookiePolicies.manageView.cookieGroup.map(::cookieGroupBody),
                        ),
                ),
        )

    return Json.encodeToString(JsonElement.serializer(), toJsonElement(body))
}

private fun cookieGroupBody(cookieGroup: AemCookieGroup): Map<String, Any?> =
    mapOf(
        "cookieName" to cookieGroup.cookieName,
        "title" to cookieGroup.title,
        "description" to cookieGroup.description,
        "isAlwaysActive" to cookieGroup.isAlwaysActive,
        "toggleLabel" to cookieGroup.toggleLabel,
    )
