package uk.co.whitbread.integrationtests.clients.contententity

import io.ktor.client.request.parameter
import uk.co.whitbread.integrationtests.clients.contententity.model.CookiePoliciesResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.FooterResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.GlobalConfigResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.HotelInformationResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.IndexHeaderDataResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.RoomTypeResponse
import uk.co.whitbread.integrationtests.clients.contententity.model.SeoResponse
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient
import uk.co.whitbread.integrationtests.testkit.model.AemFooter

class ContentEntityApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.contentBaseUrl),
) {
    suspend fun getFooter(
        footer: AemFooter,
        testId: String,
    ): ApiResult<FooterResponse> =
        http.get("/v1/content/footer", testId) {
            parameter("country", footer.country)
            parameter("language", footer.language)
            parameter("site", footer.site.value)
        }

    suspend fun getHotelBySlug(
        slug: String,
        testId: String,
        country: String = "gb",
        language: String = "en",
    ): ApiResult<HotelInformationResponse> =
        http.get("/v1/content/hotels", testId) {
            parameter("slug", slug)
            parameter("country", country)
            parameter("language", language)
        }

    suspend fun getHotelInformation(
        hotelId: String,
        testId: String,
        country: String? = "gb",
        language: String? = "en",
        channel: String? = null,
        subchannel: String? = null,
    ): ApiResult<HotelInformationResponse> =
        http.get("/v1/content/hotels/$hotelId/information", testId) {
            country?.let { parameter("country", it) }
            language?.let { parameter("language", it) }
            channel?.let { parameter("channel", it) }
            subchannel?.let { parameter("subchannel", it) }
        }

    suspend fun getSeo(
        page: String,
        testId: String,
        country: String = "gb",
        language: String = "en",
        hotelId: String? = null,
        bookingFlowId: String? = null,
    ): ApiResult<SeoResponse> =
        http.get("/v1/content/seo", testId) {
            parameter("page", page)
            parameter("country", country)
            parameter("language", language)
            hotelId?.let { parameter("hotelId", it) }
            bookingFlowId?.let { parameter("bookingFlowId", it) }
        }

    suspend fun getIndexHeaderData(
        testId: String,
        country: String? = "gb",
        language: String? = "en",
        businessBooker: Boolean? = null,
    ): ApiResult<IndexHeaderDataResponse> =
        http.get("/v1/content/header/data", testId) {
            country?.let { parameter("country", it) }
            language?.let { parameter("language", it) }
            businessBooker?.let { parameter("businessBooker", it) }
        }

    suspend fun getGlobalConfig(
        channelId: String? = null,
        testId: String,
        country: String? = "gb",
        language: String? = "en",
        brand: String? = "PI",
    ): ApiResult<GlobalConfigResponse> =
        http.get("/v1/content/global-config", testId) {
            country?.let { parameter("country", it) }
            language?.let { parameter("language", it) }
            channelId?.let { parameter("channelId", it) }
            brand?.let { parameter("brand", it) }
        }

    suspend fun getRoomType(
        country: String? = "gb",
        language: String? = "en",
        brand: String? = "PI",
        hotelId: String? = null,
        testId: String,
    ): ApiResult<RoomTypeResponse> =
        http.get("/v1/content/room-type", testId) {
            country?.let { parameter("country", it) }
            language?.let { parameter("language", it) }
            brand?.let { parameter("brand", it) }
            hotelId?.let { parameter("hotelId", it) }
        }

    suspend fun getCookiePolicies(
        country: String?,
        language: String?,
        brand: String?,
        testId: String,
    ): ApiResult<CookiePoliciesResponse> =
        http.get("/v1/content/cookie-policies", testId) {
            country?.let { parameter("country", it) }
            language?.let { parameter("language", it) }
            brand?.let { parameter("brand", it) }
        }
}
