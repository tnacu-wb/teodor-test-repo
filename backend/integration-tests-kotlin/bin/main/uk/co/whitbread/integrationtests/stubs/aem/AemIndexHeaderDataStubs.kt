package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemSite

const val AEM_INDEX_HEADER_DATA_STUB_ID = "booking.aem.index-header-data"

fun indexHeaderData(indexHeaderData: AemIndexHeaderData): PlannedStub =
    PlannedStub(
        id = AEM_INDEX_HEADER_DATA_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(indexHeaderDataMapping(indexHeaderData)),
    )

private fun indexHeaderDataMapping(indexHeaderData: AemIndexHeaderData): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = indexHeaderDataPath(indexHeaderData),
            ),
        response =
            jsonResponse(
                body = indexHeaderDataBody(indexHeaderData),
            ),
    )

private fun indexHeaderDataPath(indexHeaderData: AemIndexHeaderData): String =
    when (indexHeaderData.site) {
        AemSite.BUSINESS_BOOKER ->
            "/${indexHeaderData.country}/${indexHeaderData.language}/business-booker/index.header.data"
        else -> "/${indexHeaderData.country}/${indexHeaderData.language}/index.header.data"
    }

private fun indexHeaderDataBody(indexHeaderData: AemIndexHeaderData): String {
    val content =
        mutableMapOf<String, Any?>(
            "favicon" to
                mapOf(
                    "faviconUrl" to indexHeaderData.favicon.faviconUrl,
                    "msIcons" to
                        indexHeaderData.favicon.msIcons.map { icon ->
                            mapOf(
                                "name" to icon.name,
                                "content" to icon.content,
                            )
                        },
                    "icons" to
                        indexHeaderData.favicon.icons.map { icon ->
                            mapOf(
                                "sizes" to icon.sizes,
                                "rel" to icon.rel,
                                "href" to icon.href,
                            )
                        },
                ),
            "seo" to
                mapOf(
                    "pageDescription" to indexHeaderData.seo.pageDescription,
                    "cardImageUrl" to indexHeaderData.seo.cardImageUrl,
                    "pageTitle" to indexHeaderData.seo.pageTitle,
                ),
        )

    indexHeaderData.announcement?.let { announcement ->
        content["announcement"] =
            mapOf(
                "text" to announcement.text,
                "type" to announcement.type,
                "browserCompatibilityMessage" to announcement.browserCompatibilityMessage,
            )
    }

    val body =
        mutableMapOf<String, Any?>(
            "content" to content,
        )

    indexHeaderData.features?.let { features ->
        body["config"] =
            mapOf(
                "features" to
                    mapOf(
                        "announcement" to features.announcement,
                    ),
            )
    }

    return Json.encodeToString(JsonElement.serializer(), toJsonElement(body))
}
