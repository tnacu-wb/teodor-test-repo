package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Page-header content the mocked AEM index-header endpoint serves. Presence gates the index-header-data stub.")
data class AemIndexHeaderData(
    val country: String,
    val language: String,
    val seo: AemIndexHeaderSeo,
    val favicon: AemIndexHeaderFavicon,
    val site: AemSite = AemSite.LEISURE,
    val announcement: AemIndexHeaderAnnouncement? = null,
    val features: AemIndexHeaderFeatures? = null,
) {
    init {
        require(country.isNotBlank()) { "indexHeaderData country must not be blank" }
        require(language.isNotBlank()) { "indexHeaderData language must not be blank" }
        require(announcement == null || features != null) {
            "indexHeaderData.features must be provided when indexHeaderData.announcement is set"
        }
    }
}

@Serializable
@JsonSchema.Description("SEO metadata for the index page.")
data class AemIndexHeaderSeo(
    val pageTitle: String,
    val pageDescription: String,
    val cardImageUrl: String,
) {
    init {
        require(pageTitle.isNotBlank()) { "indexHeaderData.seo.pageTitle must not be blank" }
        require(pageDescription.isNotBlank()) { "indexHeaderData.seo.pageDescription must not be blank" }
        require(cardImageUrl.isNotBlank()) { "indexHeaderData.seo.cardImageUrl must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Favicon and icon links for the index page.")
data class AemIndexHeaderFavicon(
    val faviconUrl: String,
    val icons: List<AemIndexHeaderIcon> = emptyList(),
    val msIcons: List<AemIndexHeaderMsIcon> = emptyList(),
) {
    init {
        require(faviconUrl.isNotBlank()) { "indexHeaderData.favicon.faviconUrl must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One favicon link element.")
data class AemIndexHeaderIcon(
    val rel: String,
    val href: String,
    val sizes: String? = null,
) {
    init {
        require(rel.isNotBlank()) { "indexHeaderData.favicon.icons.rel must not be blank" }
        require(href.isNotBlank()) { "indexHeaderData.favicon.icons.href must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One Microsoft tile icon meta element.")
data class AemIndexHeaderMsIcon(
    val name: String,
    val content: String,
) {
    init {
        require(name.isNotBlank()) { "indexHeaderData.favicon.msIcons.name must not be blank" }
        require(content.isNotBlank()) { "indexHeaderData.favicon.msIcons.content must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Site-wide announcement banner content; requires features to be set.")
data class AemIndexHeaderAnnouncement(
    val text: String,
    val type: String,
    val browserCompatibilityMessage: String,
) {
    init {
        require(text.isNotBlank()) { "indexHeaderData.announcement.text must not be blank" }
        require(type.isNotBlank()) { "indexHeaderData.announcement.type must not be blank" }
        require(
            browserCompatibilityMessage.isNotBlank(),
        ) { "indexHeaderData.announcement.browserCompatibilityMessage must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Feature toggles for the index header.")
data class AemIndexHeaderFeatures(
    val announcement: Boolean? = null,
)
