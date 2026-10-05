package uk.co.whitbread.integrationtests.clients.hotelreservation

import io.ktor.client.request.parameter
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancellationPoliciesResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CdhSearchBookingsResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ChangeLogResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ConfirmReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ConfirmReservationResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.DepositsResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.EditRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.FindBookingRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.FindBookingResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.PreviewDepositsResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationsByBasketResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.SaveDepositFoliosRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.TempBookingRefResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationAlertsRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationDiscountRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationPackagesRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationPackagesResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationPreferencesRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationQuestionsAndAnswersRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationSpecialRequestsRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateRoomTypeRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateRoomTypeResponse
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateUdfc20Request
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag

class HotelReservationApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.hotelReservationBaseUrl),
) {
    suspend fun confirmReservation(
        request: ConfirmReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ConfirmReservationResponse> = http.post("/v1/reservations/confirm", request, testId, featureFlagOverrides)

    /**
     * Cancels every reservation the basket holds. Unauthenticated callers authorize with the
     * encrypted `basketReference|epochSeconds` token that `GET /v1/reservations/find` returns.
     */
    suspend fun cancelReservation(
        request: CancelReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancelReservationResponse> = http.post("/v1/reservations/cancellations", request, testId, featureFlagOverrides)

    /**
     * Cancels the reservation ids the request names, without reading or touching any basket. The
     * request's `paymentOption` is forwarded verbatim and is what selects the deposit, folio and
     * card work inside ohip-adapter; `token` is accepted but never validated on this path.
     */
    suspend fun rollbackReservationCancellations(
        request: CancelReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancelReservationResponse> = http.post("/v1/reservations/cancellations/rollback", request, testId, featureFlagOverrides)

    /**
     * Cancels the Opera reservations an OPEN basket holds, but only when every one of them is
     * still on hold in Opera, and closes the basket. No token: this in-port reads none.
     */
    suspend fun cancelOnHoldReservation(
        request: CancelReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancelReservationResponse> = http.put("/v1/reservations/cancellations/on-hold", request, testId, featureFlagOverrides)

    /**
     * The on-hold cancellation with its success body left undecoded.
     *
     * The silent no-op branch — a basket whose reservations are not all on hold — answers HTTP
     * 200 with no body at all, which [cancelOnHoldReservation] cannot decode into
     * [CancelReservationResponse]. Use this variant only for that outcome; every other on-hold
     * journey uses the typed method.
     */
    suspend fun cancelOnHoldReservationNoContent(
        request: CancelReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/cancellations/on-hold", request, testId, featureFlagOverrides)

    /**
     * Applies a room-level edit (room type, occupancy, lead guest) to one Opera reservation held by
     * the basket named in `tempBookingRef`, and answers with that same reference. Unauthenticated
     * callers are only admitted when `release_amend_distribution_single_call` is on AND
     * `bookingChannel.channel` is `DISTR`; every other shape validates `token` against the basket's
     * `originalBasketId`, which a created (non-copy) basket does not have.
     */
    suspend fun amendEditRoom(
        request: EditRoomRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<TempBookingRefResponse> = http.put("/v1/reservations/amend/editRoom", request, testId, featureFlagOverrides)

    suspend fun createReservation(
        request: CreateReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CreateReservationResponse> = http.post("/v1/reservations", request, testId, featureFlagOverrides)

    suspend fun createReservationGuest(
        request: ReservationGuestRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationGuestResponse> = http.post("/v1/reservations/guests", request, testId, featureFlagOverrides)

    /**
     * Posts each folio in the body to Opera cashiering through ohip-adapter-service. The endpoint
     * evaluates no feature flags; `featureFlagOverrides` is carried for signature consistency
     * only. The success response is `201 Created` with an empty body.
     */
    suspend fun saveDepositFolios(
        request: SaveDepositFoliosRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.post("/v1/reservations/save-deposit-folios", request, testId, featureFlagOverrides)

    suspend fun updateReservationPackages(
        request: UpdateReservationPackagesRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<UpdateReservationPackagesResponse> = http.put("/v1/reservations/ancillaries", request, testId, featureFlagOverrides)

    /** Applies the supplied Opera alert collection to every unique reservation id. */
    suspend fun updateReservationAlerts(
        request: UpdateReservationAlertsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/alerts", request, testId, featureFlagOverrides)

    /** Distributes one discount across the eligible rates of every unique reservation id. */
    suspend fun updateReservationDiscount(
        request: UpdateReservationDiscountRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/discount", request, testId, featureFlagOverrides)

    /** Applies each preference collection to every reservation-id list entry. */
    suspend fun updateReservationPreferences(
        request: UpdateReservationPreferencesRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/preferences", request, testId, featureFlagOverrides)

    /** Stores the requested CIOL status as Opera character UDF `UDFC20`. */
    suspend fun updateUdfc20(
        request: UpdateUdfc20Request,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/updateUdfc20", request, testId, featureFlagOverrides)

    /** Adds company and user-defined Q&A details when the reservation has no protected Q&A. */
    suspend fun updateReservationQuestionsAndAnswers(
        request: UpdateReservationQuestionsAndAnswersRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/questions-and-answers", request, testId, featureFlagOverrides)

    /** Replaces reservation special-request preferences and booking-note comments. */
    suspend fun updateReservationSpecialRequests(
        request: UpdateReservationSpecialRequestsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/v1/reservations/special-requests", request, testId, featureFlagOverrides)

    /** Prices and applies fixed nightly rates for the requested target room types. */
    suspend fun updateRoomType(
        request: UpdateRoomTypeRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<UpdateRoomTypeResponse> = http.put("/v1/reservations/roomTypeUpdate", request, testId, featureFlagOverrides)

    suspend fun deleteRoutingInstructions(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.delete("/v1/reservations/routingInstructions", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    suspend fun getChangeLog(
        hotelId: String,
        reservationId: String,
        testId: String,
        limit: Int? = null,
        offset: Int? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ChangeLogResponse> =
        http.get("/v1/reservations/changeLog", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
        }

    /**
     * `basketReference` is always emitted, empty by default: the controller requires all four
     * parameters, and an empty (rather than absent) reference is what selects the rate-plan
     * branch instead of the basket-service hop.
     */
    suspend fun getCancellationPolicies(
        hotelId: String,
        ratePlanCode: String,
        arrivalDate: String,
        testId: String,
        basketReference: String = "",
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancellationPoliciesResponse> =
        http.get("/v1/reservations/cancellationPolicies", testId, featureFlagOverrides) {
            parameter("basketReference", basketReference)
            parameter("hotelId", hotelId)
            parameter("ratePlanCode", ratePlanCode)
            parameter("arrivalDate", arrivalDate)
        }

    /**
     * Reads back every reservation a basket holds. The public path carries no context path and no
     * authentication: the anonymous sibling of the `/authenticated` variant. `priceBreakdownNeeded`
     * is forwarded to ohip-adapter-service, where it only adds nightly rate-info calls once the
     * rules-agent lookup resolves the basket's channel as Distribution.
     */
    suspend fun getReservationsByBasket(
        basketReference: String,
        testId: String,
        priceBreakdownNeeded: Boolean = false,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationsByBasketResponse> =
        http.get("/v1/reservations/basket/$basketReference", testId, featureFlagOverrides) {
            parameter("priceBreakdownNeeded", priceBreakdownNeeded)
        }

    suspend fun getReservationDeposits(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<DepositsResponse> =
        http.get("/v1/reservations/deposits", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    suspend fun getPreviewDeposits(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<PreviewDepositsResponse> =
        http.get("/v1/reservations/preview-deposits", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    /**
     * `bookingsDatabaseSearch` defaults to true: the CDH reservation-search default stub pins
     * `BookingsDatabaseSearch == true`, and the service's own default of false leaves the CDH call
     * unmatched. `pageSize` and `pageNumber` are non-nullable because the out-port unboxes both
     * without a null check.
     */
    suspend fun searchBookingFromCdh(
        bookingReference: String,
        pageSize: Int,
        pageNumber: Int,
        testId: String,
        bookingsDatabaseSearch: Boolean = true,
        guestLastName: String? = null,
        bookerLastName: String? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CdhSearchBookingsResponse> =
        http.get("/v1/reservations/search/booking/cdh", testId, featureFlagOverrides) {
            parameter("bookingReference", bookingReference)
            parameter("bookingsDatabaseSearch", bookingsDatabaseSearch)
            parameter("pageSize", pageSize)
            parameter("pageNumber", pageNumber)
            guestLastName?.let { parameter("guestLastName", it) }
            bookerLastName?.let { parameter("bookerLastName", it) }
        }

    suspend fun findBooking(
        request: FindBookingRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<FindBookingResponse> =
        http.get("/v1/reservations/find", testId, featureFlagOverrides) {
            parameter("resNo", request.resNo)
            parameter("arrivalDate", request.arrivalDate)
            parameter("lastName", request.lastName)
            parameter("country", request.country)
            parameter("language", request.language)
            parameter("isOldBooking", request.isOldBooking)
            parameter("channel", request.channel)
            parameter("subchannel", request.subchannel)
        }

    suspend fun findBookingForKiosk(
        resNo: String,
        testId: String,
        country: String = "gb",
        language: String = "en",
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<FindBookingResponse> =
        http.get("/v1/reservations/find/kiosk", testId, featureFlagOverrides) {
            parameter("resNo", resNo)
            parameter("country", country)
            parameter("language", language)
        }

    /**
     * The kiosk lookup with its success body kept as raw text.
     *
     * A kiosk lookup that matches nothing answers HTTP 200 with no body at all, which
     * [findBookingForKiosk] cannot decode into [FindBookingResponse]. Use this variant only for
     * the no-match outcome; every other kiosk journey uses the typed method.
     */
    suspend fun findBookingForKioskText(
        resNo: String,
        testId: String,
        country: String = "gb",
        language: String = "en",
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<String> =
        http.getText("/v1/reservations/find/kiosk", testId, featureFlagOverrides) {
            parameter("resNo", resNo)
            parameter("country", country)
            parameter("language", language)
        }
}
