package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "AEM content the scenario needs. Each non-null section gates exactly one AEM stub of the " +
        "same name; the hotel-directory and hotel-detail stubs are gated by booking.hotels instead.",
)
data class Aem(
    val footer: AemFooter? = null,
    val indexHeaderData: AemIndexHeaderData? = null,
    val searchResultsData: AemSearchResultsData? = null,
    val globalConfig: AemGlobalConfig? = null,
    val roomType: AemRoomType? = null,
    val cookiePolicies: AemCookiePolicies? = null,
)

@Serializable
@JsonSchema.Description("Whitbread site variant AEM content is served for.")
enum class AemSite(
    val value: String,
) {
    LEISURE("leisure"),
    BUSINESS_BOOKER("business-booker"),
    CCUI("ccui"),
    DISTRIBUTION("distribution"),
}
