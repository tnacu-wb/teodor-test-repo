package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class IndexHeaderDataResponse(
    val content: IndexHeaderContentResponse? = null,
    val announcement: IndexHeaderAnnouncementResponse? = null,
)

@Serializable
data class IndexHeaderContentResponse(
    val seo: IndexHeaderSeoResponse? = null,
    val favicon: IndexHeaderFaviconResponse? = null,
)

@Serializable
data class IndexHeaderSeoResponse(
    val pageTitle: String? = null,
    val pageDescription: String? = null,
    val cardImageUrl: String? = null,
)

@Serializable
data class IndexHeaderFaviconResponse(
    val faviconUrl: String? = null,
    val icons: List<IndexHeaderIconResponse> = emptyList(),
    val msIcons: List<IndexHeaderMsIconResponse> = emptyList(),
)

@Serializable
data class IndexHeaderIconResponse(
    val rel: String? = null,
    val sizes: String? = null,
    val href: String? = null,
)

@Serializable
data class IndexHeaderMsIconResponse(
    val name: String? = null,
    val content: String? = null,
)

@Serializable
data class IndexHeaderAnnouncementResponse(
    val text: String? = null,
    val type: String? = null,
    val browserCompatibilityMessage: String? = null,
)
