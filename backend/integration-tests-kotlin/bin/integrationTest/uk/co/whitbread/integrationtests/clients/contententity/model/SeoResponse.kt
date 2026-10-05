package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class SeoResponse(
    val pageTitle: String? = null,
    val pageDescription: String? = null,
    val cardImageUrl: String? = null,
    val faviconUrl: String? = null,
    val icons: List<SeoIcon> = emptyList(),
    val msIcons: List<SeoMsIcon> = emptyList(),
    val hreflangs: List<SeoHreflang>? = null,
)

@Serializable
data class SeoIcon(
    val rel: String? = null,
    val sizes: String? = null,
    val href: String? = null,
)

@Serializable
data class SeoMsIcon(
    val name: String? = null,
    val content: String? = null,
)

@Serializable
data class SeoHreflang(
    val hreflang: String? = null,
    val href: String? = null,
)
