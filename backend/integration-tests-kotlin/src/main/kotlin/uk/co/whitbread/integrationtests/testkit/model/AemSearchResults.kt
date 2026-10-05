package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "Search-results page content the mocked AEM search-results endpoint serves. Presence " +
        "gates the search-results-data stub.",
)
data class AemSearchResultsData(
    val country: String,
    val language: String,
    val seo: AemSearchResultsSeo,
) {
    init {
        require(country.isNotBlank()) { "searchResultsData country must not be blank" }
        require(language.isNotBlank()) { "searchResultsData language must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("SEO metadata for the search-results page.")
data class AemSearchResultsSeo(
    val pageTitle: String,
    val pageDescription: String,
    val cardImageUrl: String,
    val hreflangs: List<AemHreflang> = emptyList(),
) {
    init {
        require(pageTitle.isNotBlank()) { "searchResultsData.seo.pageTitle must not be blank" }
        require(pageDescription.isNotBlank()) { "searchResultsData.seo.pageDescription must not be blank" }
        require(cardImageUrl.isNotBlank()) { "searchResultsData.seo.cardImageUrl must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One hreflang alternate-language link.")
data class AemHreflang(
    val hreflang: String,
    val href: String,
) {
    init {
        require(hreflang.isNotBlank()) { "aem.hreflang must not be blank" }
        require(href.isNotBlank()) { "aem.hreflang href must not be blank" }
    }
}
