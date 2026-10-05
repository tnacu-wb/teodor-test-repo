package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemSearchResultsData

const val AEM_SEARCH_RESULTS_DATA_STUB_ID = "booking.aem.search-results-data"

fun searchResultsData(searchResultsData: AemSearchResultsData): PlannedStub =
    PlannedStub(
        id = AEM_SEARCH_RESULTS_DATA_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(searchResultsDataMapping(searchResultsData)),
    )

private fun searchResultsDataMapping(searchResultsData: AemSearchResultsData): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url = "/${searchResultsData.country}/${searchResultsData.language}/search.searchresults.data",
            ),
        response =
            jsonResponse(
                body = searchResultsDataBody(searchResultsData),
            ),
    )

private fun searchResultsDataBody(searchResultsData: AemSearchResultsData): String {
    val body =
        mapOf(
            "content" to
                mapOf(
                    "seo" to
                        mapOf(
                            "pageDescription" to searchResultsData.seo.pageDescription,
                            "cardImageUrl" to searchResultsData.seo.cardImageUrl,
                            "pageTitle" to searchResultsData.seo.pageTitle,
                            "hreflangs" to
                                searchResultsData.seo.hreflangs.map { hreflang ->
                                    mapOf(
                                        "hreflang" to hreflang.hreflang,
                                        "href" to hreflang.href,
                                    )
                                },
                        ),
                ),
        )

    return Json.encodeToString(JsonElement.serializer(), toJsonElement(body))
}
