package uk.co.whitbread.integrationtests.clients.ohip

import io.ktor.client.request.parameter
import uk.co.whitbread.integrationtests.clients.ohip.model.AmendSummaryResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.AttachReservationProfileRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsV2Request
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsV2Response
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsV3Request
import uk.co.whitbread.integrationtests.clients.ohip.model.BookingAllowancesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.BusinessItemsUpdateRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CancelInformationResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CancelReservationResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CancellationPoliciesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CancellationReasonsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ChangeLogResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CompaniesProfileResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CompanyProfileResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmAmendRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmAmendSingleCallRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmReservationRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmReservationResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateMemoRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfileKioskRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateReservationGuestRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateReservationGuestResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.DepositsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ExternalReservationResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelAvailabilityRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelAvailabilityResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelDonationPackagesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelInfoResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelInventoryResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageGroupsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackageGroupsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPackagesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelPreferencesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelRoomTypesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HotelStatusResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.HousekeepingStatusResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ItemInventoryRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ItemInventoryResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.KioskCheckInRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.KioskCheckInResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.LinkReservationToLeisureCustomerRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.MemosResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityV2Request
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityV2Response
import uk.co.whitbread.integrationtests.clients.ohip.model.NegotiatedRatesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.PreCheckInRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.PreCheckInResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.PromotionResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.QuestionsAndAnswersRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.RateCodePricingRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.RateCodePricingResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.RatePlanInfoResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.RatePlansResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationAmountsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationByBasketRefResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationFileAttachmentRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationIdDetailsResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationLightweightResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationPaymentTypeEntry
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationPreferencesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationsPackagesResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.RestrictionsByDateRangeResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomAllocationRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomAllocationResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomPriceBreakdownRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomPriceBreakdownResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.SaveDepositFoliosRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.SpecialRequestsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBillingAddressRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookerDetailsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookerEmailRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCancellationPoliciesRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCancellationPolicyRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCustomReferenceNumberRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateDiscountRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdatePreferencesRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReasonForStayRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReasonForStayResponse
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationAlertsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationCcAgentIdRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationOverrideReasonsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationsRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.VacantRoomsResponse
import uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig
import uk.co.whitbread.integrationtests.framework.http.ApiResult
import uk.co.whitbread.integrationtests.framework.http.ServiceApiClient
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class OhipApi(
    private val http: ServiceApiClient =
        IntegrationTestConfig.serviceApiClient(IntegrationTestConfig.config.ohipInterfaceBaseUrl),
) {
    suspend fun confirmReservation(
        request: ConfirmReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ConfirmReservationResponse> =
        http.post(
            "/ohip/v1/reservations/confirm",
            request,
            testId,
            featureFlagOverrides,
        )

    suspend fun cancelReservation(
        request: CancelReservationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancelReservationResponse> = http.post("/ohip/v1/reservations/cancellations", request, testId, featureFlagOverrides)

    suspend fun updateCancellationPolicy(
        request: UpdateCancellationPolicyRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/cancel", request, testId, featureFlagOverrides)

    suspend fun updateCancellationPolicies(
        request: UpdateCancellationPoliciesRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/cancel-policies", request, testId, featureFlagOverrides)

    suspend fun getCancelInformation(
        hotelId: String,
        reservationIds: List<String>,
        userDateTime: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancelInformationResponse> =
        http.get("/ohip/v1/reservations/cancel", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
            parameter("userDateTime", userDateTime)
        }

    suspend fun getPromotions(
        promotionCodes: List<String>,
        hotelId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<List<PromotionResponse>> =
        http.get("/ohip/promotions", testId, featureFlagOverrides) {
            parameter("promotionCodes", promotionCodes.joinToString(","))
            parameter("hotelId", hotelId)
        }

    suspend fun getRatePlanInfo(
        ratePlanCode: String,
        hotelId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<RatePlanInfoResponse> =
        http.get("/ohip/ratePlanInfo", testId, featureFlagOverrides) {
            parameter("ratePlanCode", ratePlanCode)
            parameter("hotelId", hotelId)
        }

    suspend fun getNegotiatedRates(
        profileId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<NegotiatedRatesResponse> = http.get("/ohip/$profileId/negotiatedRates", testId, featureFlagOverrides)

    suspend fun getHotelsStatus(
        hotelIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<List<HotelStatusResponse>> =
        http.get("/ohip/hotels/status", testId, featureFlagOverrides) {
            parameter("hotelIds", hotelIds.joinToString(","))
        }

    suspend fun getPreferencesForGroup(
        hotelId: String,
        preferenceGroupsCodes: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<HotelPreferencesResponse> =
        http.get("/ohip/v1/preference/hotels/$hotelId", testId, featureFlagOverrides) {
            parameter("preferenceGroupsCodes", preferenceGroupsCodes)
        }

    suspend fun getChangeLog(
        hotelId: String,
        reservationId: String,
        testId: String,
        limit: Int? = null,
        offset: Int? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ChangeLogResponse> =
        http.get("/ohip/v1/hotels/$hotelId/reservations/changeLog", testId, featureFlagOverrides) {
            parameter("reservationId", reservationId)
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
        }

    suspend fun getRestrictions(
        hotelId: String,
        startDate: String,
        endDate: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<RestrictionsByDateRangeResponse> =
        http.get("/ohip/hotels/$hotelId/restrictions", testId, featureFlagOverrides) {
            parameter("startDate", startDate)
            parameter("endDate", endDate)
        }

    suspend fun getMultiHotelRestrictions(
        hotelIds: List<String>,
        startDate: String,
        endDate: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<List<RestrictionsByDateRangeResponse>> =
        http.get("/ohip/hotels/restrictions", testId, featureFlagOverrides) {
            parameter("hotelIds", hotelIds.joinToString(","))
            parameter("startDate", startDate)
            parameter("endDate", endDate)
        }

    suspend fun getCompanyProfileByCompanyId(
        companyId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CompanyProfileResponse> = http.get("/ohip/v1/profile/company/id/$companyId", testId, featureFlagOverrides)

    suspend fun getCompanyProfileByCorporateId(
        corporateId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CompanyProfileResponse> = http.get("/ohip/v1/profile/company/$corporateId", testId, featureFlagOverrides)

    suspend fun getCompaniesProfile(
        hotelId: String,
        limit: Int,
        testId: String,
        companyName: String? = null,
        arNumber: String? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CompaniesProfileResponse> =
        http.get("/ohip/v1/profile/companies", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("limit", limit)
            companyName?.let { parameter("companyName", it) }
            arNumber?.let { parameter("arNumber", it) }
        }

    suspend fun getPaymentTypeReservationsByReservationIds(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<List<ReservationPaymentTypeEntry>> =
        http.get("/ohip/v1/reservations/paymentType", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    suspend fun getCancellationPolicies(
        reservationIds: List<String>,
        hotelId: String,
        ratePlanCode: String,
        arrivalDate: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancellationPoliciesResponse> =
        http.get("/ohip/v1/reservations/cancellationPolicies", testId, featureFlagOverrides) {
            parameter("reservationIds", reservationIds.joinToString(","))
            parameter("hotelId", hotelId)
            parameter("ratePlanCode", ratePlanCode)
            parameter("arrivalDate", arrivalDate)
        }

    suspend fun getLightweightReservationsByIds(
        reservationId: String,
        hotelId: String,
        testId: String,
    ): ApiResult<ReservationLightweightResponse> = getLightweightReservationsByIds(listOf(reservationId), hotelId, testId)

    suspend fun getLightweightReservationsByIds(
        reservationIds: List<String>,
        hotelId: String,
        testId: String,
    ): ApiResult<ReservationLightweightResponse> =
        http.get("/ohip/v1/reservations/ids", testId) {
            parameter("reservationIds", reservationIds.joinToString(","))
            parameter("hotelId", hotelId)
        }

    suspend fun getReservationsByIds(
        reservationId: String,
        hotelId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationByBasketRefResponse> = getReservationsByIds(listOf(reservationId), hotelId, testId, featureFlagOverrides)

    suspend fun getReservationsByIds(
        reservationIds: List<String>,
        hotelId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationByBasketRefResponse> =
        http.get("/ohip/v1/reservations/basket", testId, featureFlagOverrides) {
            parameter("reservationIds", reservationIds.joinToString(","))
            parameter("hotelId", hotelId)
        }

    suspend fun getReservationsByExternalId(
        externalReferenceId: String,
        testId: String,
    ): ApiResult<ExternalReservationResponse> =
        http.get("/ohip/v1/reservations/external", testId) {
            parameter("externalReferenceId", externalReferenceId)
        }

    suspend fun getDepositsForReservationId(
        hotelId: String,
        reservationId: String,
        testId: String,
    ): ApiResult<DepositsResponse> =
        http.get("/ohip/v1/reservations/deposits", testId) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    suspend fun getRatePlans(
        hotelId: String,
        testId: String,
    ): ApiResult<RatePlansResponse> =
        http.get("/ohip/ratePlans", testId) {
            parameter("hotelId", hotelId)
        }

    suspend fun getHotelInfo(
        hotelId: String,
        testId: String,
    ): ApiResult<HotelInfoResponse> = http.get("/ohip/hotels/$hotelId/info", testId)

    suspend fun getCancellationReasons(
        hotelId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CancellationReasonsResponse> = http.get("/ohip/hotels/$hotelId/cancellationReasons", testId, featureFlagOverrides)

    suspend fun getHotelRoomTypes(
        hotelId: String,
        testId: String,
    ): ApiResult<HotelRoomTypesResponse> = http.get("/ohip/hotels/$hotelId/roomTypes", testId)

    suspend fun getHotelRoomsInventory(
        hotelId: String,
        dateRangeStart: LocalDate,
        dateRangeEnd: LocalDate,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<HotelInventoryResponse> =
        http.get("/ohip/hotels/$hotelId/hotelInventory", testId, featureFlagOverrides) {
            parameter("dateRangeStart", dateRangeStart.toString())
            parameter("dateRangeEnd", dateRangeEnd.toString())
        }

    suspend fun getItemInventory(
        request: ItemInventoryRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ItemInventoryResponse> =
        http.get("/ohip/hotels/${request.hotelId}/itemInventory", testId, featureFlagOverrides) {
            parameter("startDate", request.startDate.toString())
            parameter("endDate", request.endDate.toString())
            request.itemCodes?.let { itemCodes -> parameter("itemCodes", itemCodes.joinToString(",")) }
        }

    suspend fun getRoomPriceBreakdown(
        request: RoomPriceBreakdownRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<RoomPriceBreakdownResponse> =
        http.get("/ohip/hotels/${request.hotelId}/price-breakdown", testId, featureFlagOverrides) {
            parameter("arrivalDate", request.arrivalDate.toString())
            parameter("departureDate", request.departureDate.toString())
            parameter("ratePlanCode", request.ratePlanCode)
            parameter("roomTypes", request.roomTypes.joinToString(","))
            parameter("adultsNo", request.adultsNo.joinToString(","))
            parameter("childrenNo", request.childrenNo.joinToString(","))
        }

    suspend fun getRateCodePricing(
        request: RateCodePricingRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<RateCodePricingResponse> =
        http.get("/ohip/hotels/${request.hotelId}/rate-code-pricing", testId, featureFlagOverrides) {
            parameter("arrivalDate", request.arrivalDate.toString())
            parameter("departureDate", request.departureDate.toString())
            parameter("ratePlanCode", request.ratePlanCode)
            parameter("roomTypes", request.roomTypes.joinToString(","))
            parameter("adultsNo", request.adultsNo.joinToString(","))
            parameter("childrenNo", request.childrenNo.joinToString(","))
        }

    suspend fun getHotelAvailability(
        request: HotelAvailabilityRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<HotelAvailabilityResponse> =
        http.get("/ohip/hotels/${request.hotelId}/availabilities", testId, featureFlagOverrides) {
            parameter("arrivalDate", request.arrivalDate.toString())
            parameter("departureDate", request.departureDate.toString())
            parameter("roomTypes", request.roomTypes.joinToString(","))
            parameter("adults", request.adults.joinToString(","))
            parameter("children", request.children.joinToString(","))
            parameter("cotsRequired", request.cotsRequired.joinToString(","))
            parameter("channel", request.channel)
            parameter("subchannel", request.subchannel)
            request.language?.let { parameter("language", it) }
            request.companyId?.let { parameter("companyId", it) }
            request.promotionCode?.let { parameter("promotionCode", it) }
        }

    suspend fun getHotelPackages(
        hotelId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        adults: Int,
        children: Int,
        testId: String,
        ratePlanCode: String? = null,
        mealInclusiveRate: Boolean? = null,
    ): ApiResult<HotelPackagesResponse> =
        http.get("/ohip/hotels/$hotelId/packages", testId) {
            parameter("startDate", startDate.toString())
            parameter("endDate", endDate.toString())
            parameter("adults", adults)
            parameter("children", children)
            parameter("nrNights", ChronoUnit.DAYS.between(startDate, endDate).toInt())
            ratePlanCode?.let { parameter("ratePlanCode", it) }
            mealInclusiveRate?.let { parameter("mealInclusiveRate", it) }
        }

    suspend fun getHotelDonationPackages(
        hotelId: String,
        packageCodes: List<String>,
        testId: String,
    ): ApiResult<HotelDonationPackagesResponse> =
        http.get("/ohip/hotels/$hotelId/packages/donations", testId) {
            packageCodes.forEach { packageCode ->
                parameter("packageCodes", packageCode)
            }
        }

    /**
     * Creates one Opera guest profile per staying guest and attaches all profiles to the
     * reservation. The handler is a void method, so the runtime success status is 200 with an
     * empty body (the OpenAPI annotation's 204 is not what the service returns).
     */
    suspend fun createProfileKiosk(
        hotelId: String,
        reservationId: String,
        request: CreateProfileKioskRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.post("/ohip/v1/profile/createProfile", request, testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    suspend fun createProfiles(
        request: CreateProfilesRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CreateProfilesResponse> = http.post("/ohip/v1/reservations/create/profile", request, testId, featureFlagOverrides)

    suspend fun preCheckInReservation(
        request: PreCheckInRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<PreCheckInResponse> = http.post("/ohip/v1/reservations/pre-checkin", request, testId, featureFlagOverrides)

    suspend fun preRegisterReservation(
        request: PreCheckInRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<PreCheckInResponse> = http.post("/ohip/v1/reservations/pre-register", request, testId, featureFlagOverrides)

    suspend fun addAttachmentToReservation(
        request: ReservationFileAttachmentRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<PreCheckInResponse> = http.post("/ohip/v1/reservations/attachments", request, testId, featureFlagOverrides)

    suspend fun deleteReservation(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.delete("/ohip/v1/reservations", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    suspend fun deleteRegCardAttachment(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.delete("/ohip/v1/reservations/attachments", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    suspend fun deleteRoutingInstructions(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.delete("/ohip/v1/reservations/routingInstructions", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    suspend fun getHotelPackageGroups(
        request: HotelPackageGroupsRequest,
        testId: String,
    ): ApiResult<HotelPackageGroupsResponse> = http.post("/ohip/hotels/packages/groups", request, testId)

    suspend fun getMultiHotelAvailability(
        request: MultiHotelAvailabilityRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<MultiHotelAvailabilityResponse> =
        http.get("/ohip/hotels/availabilities", testId, featureFlagOverrides) {
            request.hotelIds.forEach { hotelId -> parameter("hotelIds", hotelId) }
            parameter("arrivalDate", request.arrivalDate.toString())
            parameter("departureDate", request.departureDate.toString())
            parameter("numberOfRooms", request.numberOfRooms.joinToString(","))
            parameter("roomTypes", request.roomTypes.joinToString(","))
            parameter("adults", request.adults.joinToString(","))
            parameter("children", request.children.joinToString(","))
            parameter("cotsRequired", request.cotsRequired.joinToString(","))
            parameter("channel", request.channel)
            request.companyId?.let { companyId -> parameter("companyId", companyId) }
        }

    suspend fun getHotelAvailabilityByIds(
        request: AvailabilityByIdsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<AvailabilityByIdsResponse> =
        http.get("/ohip/hotels/availabilities/distr", testId, featureFlagOverrides) {
            request.hotelIds.forEach { hotelId -> parameter("hotelIds", hotelId) }
            parameter("arrivalDate", request.arrivalDate.toString())
            parameter("departureDate", request.departureDate.toString())
            parameter("roomTypes", request.roomTypes.joinToString(","))
            parameter("adults", request.adults.joinToString(","))
            parameter("children", request.children.joinToString(","))
            parameter("cotsRequired", request.cotsRequired.joinToString(","))
            parameter("channel", request.channel)
            parameter("subchannel", request.subchannel)
            parameter("language", request.language)
            if (request.ratePlanCodes.isNotEmpty()) {
                parameter("ratePlanCodes", request.ratePlanCodes.joinToString(","))
            }
            request.globalCompanyId?.let { parameter("globalCompanyId", it) }
            if (request.negotiatedRateDisplaySets.isNotEmpty()) {
                parameter("negotiatedRateDisplaySets", request.negotiatedRateDisplaySets.joinToString(","))
            }
            if (request.pmsRoomTypes.isNotEmpty()) {
                parameter("pmsRoomTypes", request.pmsRoomTypes.joinToString(","))
            }
        }

    suspend fun getMultiHotelAvailabilityV2(
        request: MultiHotelAvailabilityV2Request,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<MultiHotelAvailabilityV2Response> = http.post("/ohip/v2/hotels/availabilities", request, testId, featureFlagOverrides)

    suspend fun getHotelAvailabilitiesByIdsV2(
        request: AvailabilityByIdsV2Request,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<AvailabilityByIdsV2Response> = http.post("/ohip/v2/hotels/availabilities/distr", request, testId, featureFlagOverrides)

    /**
     * Lists the clean, vacant rooms of one room type in one hotel. Despite the POST method the
     * endpoint takes no body — both inputs are query parameters — so an empty JSON object is
     * sent as the body.
     */
    suspend fun getVacantRooms(
        hotelId: String,
        roomType: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<VacantRoomsResponse> =
        http.post("/ohip/v1/rooms/getVacant", emptyMap<String, String>(), testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("roomType", roomType)
        }

    suspend fun allocateRooms(
        request: RoomAllocationRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<RoomAllocationResponse> = http.post("/ohip/v1/rooms/allocate", request, testId, featureFlagOverrides)

    suspend fun kioskCheckIn(
        request: KioskCheckInRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<KioskCheckInResponse> = http.post("/ohip/v1/kiosk/checkIn", request, testId, featureFlagOverrides)

    suspend fun fetchHouseKeepingStatus(
        hotelId: String,
        roomId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<HousekeepingStatusResponse> =
        http.get("/ohip/v1/rooms/fetchHouseKeepingStatus", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("roomId", roomId)
        }

    suspend fun fetchReservationWithPreferences(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationPreferencesResponse> =
        http.get("/ohip/v1/rooms/fetchReservationWithPreferences", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    /**
     * Confirms an amend in one merged Opera change-reservation PUT per reservation and
     * returns the refreshed reservations as a basket response.
     */
    suspend fun confirmAmendSingleCall(
        request: ConfirmAmendSingleCallRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationByBasketRefResponse> =
        http.put("/ohip/v1/reservations/confirmAmendSingleCall", request, testId, featureFlagOverrides)

    /**
     * Confirms an amend by copying each linked temp reservation's state onto its original
     * reservation and returns the refreshed originals as a basket response.
     */
    suspend fun confirmAmend(
        request: ConfirmAmendRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationByBasketRefResponse> = http.put("/ohip/v1/reservations/confirmAmend", request, testId, featureFlagOverrides)

    /**
     * Applies amend-stay-dates or edit-room changes to reservations from caller-supplied
     * temp-reservation state. The handler returns `200 OK` with no body on success.
     */
    suspend fun updateReservation(
        request: UpdateReservationsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations", request, testId, featureFlagOverrides)

    /**
     * Moves each carded reservation's payment details to folio window 2, optionally raising
     * CNP check-in alerts. The handler returns `200 OK` with no body on success.
     */
    suspend fun movePaymentDetails(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.put("/ohip/v1/reservations/movePaymentDetails", emptyMap<String, String>(), testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    /**
     * Applies business items (allowances, company routing, or prepaid card routing) to the
     * given reservations. The handler returns `200 OK` with no body on success.
     */
    suspend fun updateBusinessItems(
        request: BusinessItemsUpdateRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/business", request, testId, featureFlagOverrides)

    /**
     * Replaces the special-request preferences and `SPECIAL REQUESTS` booking notes on the
     * given reservations, removing any pre-existing `SPECIAL REQUESTS` comment first. The
     * handler returns `200 OK` with no body on success.
     */
    suspend fun updateSpecialRequests(
        request: SpecialRequestsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/special-requests", request, testId, featureFlagOverrides)

    /**
     * Adds purchase-order, customer-reference, and user-defined questions and answers to the
     * given reservations, unless the first reservation Opera returns already carries a
     * protected Q&A comment. The handler returns `200 OK` with no body.
     */
    suspend fun updateQuestionsAndAnswers(
        request: QuestionsAndAnswersRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/questions-and-answers", request, testId, featureFlagOverrides)

    /**
     * Sets the same custom reference on every distinct requested reservation of one hotel, one
     * independently mapped Opera reservation update per id and no read before the write. The
     * handler returns `200 OK` with no body.
     */
    suspend fun updateCustomReferenceNumber(
        request: UpdateCustomReferenceNumberRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/customReferenceNumber", request, testId, featureFlagOverrides)

    /**
     * Sets the same purpose of stay on every requested reservation id of one hotel, one
     * independently mapped Opera reservation update per list entry — duplicates included — and no
     * read before the write. The handler returns `200 OK` with the hotel and the `Reservation`
     * typed ids Opera reported.
     */
    suspend fun updateReasonForStay(
        request: UpdateReasonForStayRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<UpdateReasonForStayResponse> = http.put("/ohip/v1/reservations/reasonForStay", request, testId, featureFlagOverrides)

    /**
     * Writes the same override-reason audit value to character UDF `UDFC08` on every distinct
     * requested reservation of one hotel: one Opera reservation update per distinct id carrying
     * one shared body, with the reservation identity supplied only by the Opera URL and no read
     * before the write. The handler returns `200 OK` with no body.
     */
    suspend fun updateReservationOverrideReasons(
        request: UpdateReservationOverrideReasonsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/overrideReasons", request, testId, featureFlagOverrides)

    /**
     * Links every distinct requested reservation of one hotel to one CDH leisure customer
     * account: one Opera reservation update per distinct id carrying one body mapped before the
     * fan-out, which holds the account id in character UDF `UDFC35` and the fixed `PI`
     * booking-channel marker in `UDFC09`, with the reservation identity supplied only by the
     * Opera URL and no read before the write. The handler returns `200 OK` with no body.
     */
    suspend fun linkReservationToLeisureCustomer(
        request: LinkReservationToLeisureCustomerRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/link-leisure-customer", request, testId, featureFlagOverrides)

    /**
     * Writes the same preference collections onto every requested reservation id of one hotel:
     * one independently mapped Opera reservation update per list entry — duplicates included —
     * whose body carries the request hotel, the entry's reservation id typed `Reservation`, and
     * every requested preference value as its own Opera preference entry, with no read before
     * the write. The handler returns `204 No Content` with no body.
     *
     * The service maps each body by filtering the full requested id list, so a duplicated id
     * still produces one PUT per entry but repeats that id in the body's `reservationIdList`.
     * The semantically pinned reservation-update stub requires exactly one identity entry, so it
     * supports distinct-id worlds only and duplicate-id requests are out of its scope.
     */
    suspend fun updatePreferences(
        request: UpdatePreferencesRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/preferences", request, testId, featureFlagOverrides)

    /**
     * Writes the same alert list onto every distinct requested reservation of one hotel: one
     * independently mapped Opera reservation update per distinct id, whose body carries the request
     * hotel, that reservation's own id typed `Reservation`, and the mapped alerts, with no read
     * before the write. The Opera response body is never inspected, so an empty success body is
     * accepted. The handler returns `204 No Content` with no body.
     */
    suspend fun updateReservationAlerts(
        request: UpdateReservationAlertsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/alerts", request, testId, featureFlagOverrides)

    /**
     * Attaches one Opera CRM profile as the `Company` reservation profile to every distinct
     * requested reservation of one hotel: exactly one Opera profile read for the requested
     * `profileId`, then one Opera reservation update per distinct id carrying the first entry of
     * Opera's returned `profileIdList`, with no read of the reservation before the write. The
     * handler returns `200 OK` with no body.
     */
    suspend fun attachProfileToReservations(
        request: AttachReservationProfileRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.post("/ohip/v1/reservations/profiles", request, testId, featureFlagOverrides)

    /**
     * Attaches staying-guest, booker, and optional company identities to reservations Opera
     * already holds, in one of two mutually exclusive orchestrations selected by `preCheckIn`.
     * The default path optionally creates a `Company` profile, resolves the booker language
     * (reading the reservation and the real rules-agent when it is missing), creates or reuses
     * the booker and per-guest CRM profiles, and fans out one unordered reservation update per
     * staying-guest entry. The pre-check-in path reads every distinct reservation, validates the
     * group against Opera's guest counts, updates or creates one `Guest` profile per flagged
     * entry, and sends one sequential reservation update per distinct id. The handler returns
     * `201 Created` with the hotel id and the `Reservation`-typed ids from the update responses.
     */
    suspend fun createReservationGuest(
        request: CreateReservationGuestRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<CreateReservationGuestResponse> = http.post("/ohip/v1/reservations/guests", request, testId, featureFlagOverrides)

    /**
     * Replaces the email address on the primary reservation guest's Opera CRM profile and
     * restamps it on every distinct requested reservation of one hotel: one full Opera
     * reservation read per distinct id, then exactly one CRM profile read and one emails-only
     * CRM profile update for the first primary guest's profile id resolved across all reads,
     * then one reservation update per distinct id carrying a `ReservationContact` profile with
     * that id and the new address. The handler returns `200 OK` with no body.
     */
    suspend fun updateBookerEmail(
        request: UpdateBookerEmailRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/email", request, testId, featureFlagOverrides)

    /**
     * Amends the booker (reservation-contact) CRM profile of the requested reservations of one
     * hotel and applies the request's three-way company decision: one full Opera reservation
     * read per distinct id, then one CRM profile read plus merged profile update for the first
     * `ReservationContact` profile id, then — from the attached company profile and
     * `booker.companyName` — a company rename in place (profile GET+PUT, no reservation write),
     * a detach (one reservation update per requested id), or a create-and-attach (company
     * profile POST then one reservation update per requested id). The handler returns `200 OK`
     * with no body.
     */
    suspend fun updateBookerDetails(
        request: UpdateBookerDetailsRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/booker", request, testId, featureFlagOverrides)

    /**
     * Writes the booker's billing address onto the Opera profiles attached to the requested
     * reservations of one hotel: one full Opera reservation read per distinct id, then one
     * sequential CRM profile read plus `BILLING`-address profile update per selected profile —
     * selected by the request's update indicators when the capture-billing-address flag is on and
     * the channel is `BB`, and by `booker.address.addressType` otherwise (including the
     * `ACCOUNT_COMPANY` CCUI variant). No reservation is written. The handler returns
     * `204 No Content` with no body.
     */
    suspend fun updateBillingAddress(
        request: UpdateBillingAddressRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservation/updateBillingAddress", request, testId, featureFlagOverrides)

    /**
     * Re-points the first routing-instruction folio of every distinct requested reservation at
     * that reservation's attached Opera `Company` profile: one full Opera reservation read per
     * distinct id, then one reservation update per id whose body carries the company payee and
     * the read folio's own `instructions` and `folioWindowNo` — or the bare change envelope when
     * no company is attached. The endpoint carries no body: both inputs are query parameters,
     * and [reservationIds] binds into a `Set`, so duplicates collapse before the fan-out. The
     * handler returns `200 OK` with no body.
     */
    suspend fun updateRoutingInstructionsWithPayeeInfo(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.put(
            "/ohip/v1/reservations/instructions/payeeInfo",
            emptyMap<String, String>(),
            testId,
            featureFlagOverrides,
        ) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    /**
     * Writes the contact-centre agent identifier to character UDF `UDFC08` on every distinct
     * requested reservation of one hotel: one Opera reservation update per distinct id carrying
     * one shared body, with the reservation identity supplied only by the Opera URL and no read
     * before the write. `clearFirst` adds a completed clearing pass over the same ids first, so
     * each reservation receives two updates. The handler returns `200 OK` with no body.
     */
    suspend fun updateReservationCcAgentId(
        request: UpdateReservationCcAgentIdRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/ccAgentId", request, testId, featureFlagOverrides)

    /**
     * Tags every distinct requested reservation of one hotel with the same external reference
     * under the configured Opera id context. The endpoint carries no body: all three inputs are
     * query parameters, and [reservationIds] binds into a `Set`, so duplicates collapse before
     * the fan-out. The handler returns `200 OK` with no body.
     */
    suspend fun updateReservationsWithExternalRef(
        hotelId: String,
        reservationIds: List<String>,
        externalReference: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> =
        http.put("/ohip/v1/reservations/externalRef", emptyMap<String, String>(), testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
            parameter("externalReference", externalReference)
        }

    /**
     * Distributes one discount amount across the discountable nightly rates of the given
     * reservations. Every requested reservation is read first; an incomplete read set or a
     * discount above the aggregate nightly-rate total is rejected before any Opera update. The
     * handler returns `200 OK` with no body on success.
     */
    suspend fun updateDiscount(
        request: UpdateDiscountRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.put("/ohip/v1/reservations/discount", request, testId, featureFlagOverrides)

    /**
     * Posts one deposit folio per reservation to Opera cashiering. The handler returns
     * `201 Created` with no body on success.
     */
    suspend fun saveDepositFolios(
        request: SaveDepositFoliosRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<Unit> = http.post("/ohip/v1/reservations/deposit-folios", request, testId, featureFlagOverrides)

    /**
     * Reads the packages (ancillaries) attached to the given Opera reservations, one Opera
     * reservation read per id. `mealInclusiveRate = true` skips the zero-quantity package
     * filter; it changes no downstream call.
     */
    suspend fun getReservationAncillaries(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        mealInclusiveRate: Boolean? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationsPackagesResponse> =
        http.get("/ohip/v1/reservations/ancillaries", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
            mealInclusiveRate?.let { parameter("mealInclusiveRate", it) }
        }

    /**
     * Reads the memos (Opera reservation comments) held against [reservationIds] at [hotelId].
     * The ids bind to a `Set<String>` server-side and drive one Opera reservation read per id.
     */
    suspend fun getReservationMemos(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<MemosResponse> =
        http.get("/ohip/v1/reservations/memos", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    /**
     * Writes one `AGENT NOTES` memo (an Opera reservation comment) to every reservation in
     * [request], then re-reads those reservations. The handler answers `201 Created` with the
     * post-write memo state, so the response reflects what Opera reports after the write, not an
     * echo of the request.
     */
    suspend fun createReservationMemos(
        request: CreateMemoRequest,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<MemosResponse> = http.post("/ohip/v1/reservations/memos", request, testId, featureFlagOverrides)

    suspend fun getBookingAllowances(
        hotelId: String,
        reservationId: String,
        testId: String,
        bookingAllowanceIds: List<String>? = null,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<BookingAllowancesResponse> =
        http.get("/ohip/v1/reservations/bookingAllowances", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
            bookingAllowanceIds?.forEach { parameter("bookingAllowanceIds", it) }
        }

    /**
     * Reads the marketing preferences of the reservation-contact (booker) profile attached to
     * [reservationId] at [hotelId].
     *
     * Kept as raw text on purpose: the endpoint answers HTTP 200 with an **empty body** whenever
     * the reservation carries no `ReservationContact` profile, which is the only body reachable
     * on default-stub worlds (see data_model_issues/ohip-adapter-data-model.md). A JSON DTO
     * would fail to decode that empty body instead of letting the scenario assert it.
     */
    suspend fun getMarketingPreferences(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<String> =
        http.getText("/ohip/v1/reservations/marketingPreferences", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    /**
     * Reads the full detail of one Opera reservation together with its money summary
     * (`totalCost`, `amountPaid`, `balanceOutstanding`) from the reservation rate-info summary.
     * Answers `404` with an empty body when Opera holds no reservation for [reservationId].
     */
    suspend fun getReservationByReservationId(
        hotelId: String,
        reservationId: String,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationIdDetailsResponse> =
        http.get("/ohip/v1/reservation/reservationId", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationId", reservationId)
        }

    /**
     * Reads one aggregated money summary for a set of Opera reservations at one hotel: the
     * reservation rate-info summaries reduced together, with the deposit corrected by whatever has
     * actually been posted on each reservation's cashiering folio.
     */
    suspend fun getReservationAmounts(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<ReservationAmountsResponse> =
        http.get("/ohip/v1/reservations/amounts", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    suspend fun getDetailsForAmend(
        hotelId: String,
        reservationIds: List<String>,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<AmendSummaryResponse> =
        http.get("/ohip/v1/reservations/amend/getDetailsForAmend", testId, featureFlagOverrides) {
            parameter("hotelId", hotelId)
            parameter("reservationIds", reservationIds.joinToString(","))
        }

    suspend fun getHotelAvailabilitiesByIdsV3(
        request: AvailabilityByIdsV3Request,
        testId: String,
        featureFlagOverrides: Map<FeatureFlag, Boolean> = emptyMap(),
    ): ApiResult<AvailabilityByIdsV2Response> = http.post("/ohip/v3/hotels/availabilities/distr", request, testId, featureFlagOverrides)
}
