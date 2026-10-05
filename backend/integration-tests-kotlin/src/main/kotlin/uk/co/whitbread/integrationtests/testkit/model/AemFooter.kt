package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Locale and site variant the mocked AEM footer endpoint serves. Presence gates the footer stub.")
data class AemFooter(
    val country: String,
    val language: String,
    val site: AemSite,
) {
    init {
        require(country.isNotBlank()) { "footer country must not be blank" }
        require(language.isNotBlank()) { "footer language must not be blank" }
    }
}
