package uk.co.whitbread.integrationtests.clients.basket

import uk.co.whitbread.integrationtests.clients.basket.model.BasketResponse
import uk.co.whitbread.integrationtests.clients.basket.model.ChangeBasketStatusRequest
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient

/**
 * Typed access to basket-service, the deployed store of the booking a journey pays for.
 *
 * Journeys reach it for setup facts no other public response carries, most of all the booking
 * reference that other services hold as their external reference for the same booking.
 */
class BasketApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.basketBaseUrl),
) {
    /** Reads one basket by its basket reference, the value reservation creation returns. */
    suspend fun getBasket(
        basketReference: String,
        testId: String,
    ): ApiResult<BasketResponse> = http.get("/v1/baskets/$basketReference", testId)

    /** Moves a basket to another lifecycle state by its booking reference. */
    suspend fun changeStatus(
        bookingReference: String,
        request: ChangeBasketStatusRequest,
        testId: String,
    ): ApiResult<BasketResponse> = http.put("/v1/baskets/$bookingReference/changeStatus", request, testId)
}
