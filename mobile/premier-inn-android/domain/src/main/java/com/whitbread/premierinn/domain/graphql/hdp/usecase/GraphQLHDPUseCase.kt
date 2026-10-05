package com.whitbread.premierinn.domain.graphql.hdp.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.CancelOnHoldReservationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelInformationSlugDomain
import com.whitbread.premierinn.domain.graphql.hdp.repository.GraphQLHDPRepository
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain.Companion.PROMO_BOX_STATUS_SUCCESS
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain.Companion.PROMO_KIND_SITE_WIDE
import com.whitbread.premierinn.domain.graphql.promotions.usecase.GraphQLPromotionsInformationUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GraphQLHDPUseCase @Inject constructor(
    private val graphQLHDPRepository: GraphQLHDPRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val graphQLPromotionsInformationUseCase: GraphQLPromotionsInformationUseCase
) {

    fun fetchHotelInfoBySlug(slug: String, country: String, language: String): Single<HotelInformationSlugDomain> {
        return graphQLHDPRepository.getHotelInfoBySlug(slug, country, language)
    }

    fun fetchHotelAvailabilityAndRatesInfoAndRoomType(
        hotelAvailabilityRequestBody: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        promoCode: String?,
        promoKind: String?
    ): Observable<HotelBookingAvailabilityState> {

        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            authenticationRepository.getIdToken()
                .flatMap { token ->
                    graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                        hotelAvailabilityRequestBody,
                        language, country, hotelId, channel, token,
                        promoCode, promoKind
                    )
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())

                .flatMap { availability ->
                    val ratePlanCodes = availability.roomRateDomainList?.map { it.ratePlanCode } ?: emptyList()
                    val rateClassifications = availability.listOfRatesClassification.map { it.rateClassification }
                    val missingRates = ratePlanCodes.filterNot { it in rateClassifications }
                    if (missingRates.isNotEmpty()) {
                        graphQLHDPRepository.getRatesInformation(hotelAvailabilityRequestBody.brand,
                            channel, missingRates,
                            language, country, hotelId)
                                .map { updatedRates ->
                                    availability.copy(
                                        listOfRatesClassification = availability.listOfRatesClassification + updatedRates.listOfRatesClassification
                                    )
                                }
                    } else {
                        Single.just(availability)
                    }
                }
                .toObservable()
        } else {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                hotelAvailabilityRequestBody, language,
                country, hotelId, channel, null, promoCode, promoKind
            ).toObservable()
        }.map {
            HotelBookingAvailabilityState().copy(
                action = AvailabilityAction.AvailabilitySuccess,
                hotelAvailability = it,
                ratesInformation = it.listOfRatesClassification,
                listOfRoomTypeInformation = it.listOfRoomTypeInfo,
                promoCode = promoCode ?: EMPTY_STRING_DOMAIN,
                promoKind = it.promoKind ?: EMPTY_STRING_DOMAIN
            )
        }
            .onErrorReturn {
                HotelBookingAvailabilityState().copy(
                    action = AvailabilityAction.AvailabilityError(it, it.message)
                )
            }
            .subscribeOn(Schedulers.io())
            .startWith(HotelBookingAvailabilityState()
                .copy(action = AvailabilityAction.AvailabilityLoading))
    }

    fun fetchPromotionsAndHotelAvailability(
        hotelAvailabilityRequestBody: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        promoCode: String?,
        skipPromotionsCheck: Boolean = false
    ): Observable<HotelBookingAvailabilityState> {
        // If app incentive or free breakfast is active, skip promotions information API call
        // and directly fetch availability without checking for sitewide promotions
        if (skipPromotionsCheck) {
            return fetchAvailability(
                hotelAvailabilityRequestBody, language, country, hotelId, channel,
                promoCode, null, sitewidePromoActive = false
            )
        }

        // Check for sitewide promotions (without discount code)
        return getPromoInfo(
            brand = hotelAvailabilityRequestBody.brand,
            arrival = hotelAvailabilityRequestBody.arrival,
            departure = hotelAvailabilityRequestBody.departure,
            language = language,
            country = country,
            channel = channel
        )
            .flatMapObservable { sitewidePromo: PromotionsInformationDomain ->
                // Only use sitewide promo if ALL conditions are met:
                // 1. showPromo is explicitly true
                // 2. isWithinPromoWindow is explicitly true (promo is active for these dates)
                // 3. promoKind is SITE_WIDE
                // 4. promotionCode is not null/blank
                val isSitewidePromoValid = sitewidePromo.showPromo == true
                        && sitewidePromo.isWithinPromoWindow == true
                        && sitewidePromo.promoKind == PROMO_KIND_SITE_WIDE
                        && !sitewidePromo.promotionCode.isNullOrBlank()

                when {
                    // Case 1: Valid sitewide promo found - use it
                    isSitewidePromoValid -> {
                        fetchAvailability(
                            hotelAvailabilityRequestBody, language, country, hotelId, channel,
                            sitewidePromo.promotionCode, sitewidePromo.promoKind, sitewidePromoActive = true
                        )
                    }
                    // Case 2: No sitewide promo, but user has entered a discount code - validate it
                    !promoCode.isNullOrBlank() -> {
                        validateAndFetchWithDiscountCode(
                            hotelAvailabilityRequestBody, language, country, hotelId, channel, promoCode
                        )
                    }
                    // Case 3: No sitewide promo and no discount code - fetch without any promo
                    else -> {
                        fetchAvailability(
                            hotelAvailabilityRequestBody, language, country, hotelId, channel,
                            null, null, sitewidePromoActive = false
                        )
                    }
                }
            }
            .onErrorResumeNext { _: Throwable ->
                // If sitewide promo check fails, try discount validation if we have a code
                if (!promoCode.isNullOrBlank()) {
                    validateAndFetchWithDiscountCode(
                        hotelAvailabilityRequestBody, language, country, hotelId, channel, promoCode
                    )
                } else {
                    fetchAvailability(
                        hotelAvailabilityRequestBody, language, country, hotelId, channel,
                        null, null, sitewidePromoActive = false
                    )
                }
            }
    }

    private fun getPromoInfo(
        brand: String,
        arrival: String,
        departure: String,
        language: String,
        country: String,
        channel: String,
        promotionCode: String? = null,
        isPromoBox: Boolean = false
    ): Single<PromotionsInformationDomain> {
        return graphQLPromotionsInformationUseCase.execute(
            country = country,
            language = language,
            channel = channel,
            brand = brand,
            stayStartDate = arrival,
            stayEndDate = departure,
            basketReference = if (isPromoBox) EMPTY_STRING_DOMAIN else null,
            promotionCode = promotionCode,
            isPromoBox = isPromoBox
        )
    }

    private fun validateAndFetchWithDiscountCode(
        hotelAvailabilityRequestBody: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        promoCode: String
    ): Observable<HotelBookingAvailabilityState> {
        return getPromoInfo(
            brand = hotelAvailabilityRequestBody.brand,
            arrival = hotelAvailabilityRequestBody.arrival,
            departure = hotelAvailabilityRequestBody.departure,
            language = language,
            country = country,
            channel = channel,
            promotionCode = promoCode,
            isPromoBox = true
        )
            .flatMapObservable { discountPromo: PromotionsInformationDomain ->
                if (discountPromo.promoBoxStatus == PROMO_BOX_STATUS_SUCCESS) {
                    // Use discount code if valid
                    fetchAvailability(
                        hotelAvailabilityRequestBody, language, country, hotelId, channel,
                        promoCode, discountPromo.promoKind, sitewidePromoActive = false
                    )
                } else {
                    // Discount code is invalid for these dates, fetch without it but show error
                    val invalidMessage = PromotionsInformationDomain.resolveMessageForStatus(
                        discountPromo.promoBoxStatus,
                        discountPromo.promoBox
                    ) ?: discountPromo.appPromoInvalidMessage
                    fetchAvailability(
                        hotelAvailabilityRequestBody, language, country, hotelId, channel,
                        null, null, sitewidePromoActive = false, invalidDiscountCodeMessage = invalidMessage
                    )
                }
            }
            .onErrorResumeNext { _: Throwable ->
                // If validation fails, fetch availability without promo code
                fetchAvailability(
                    hotelAvailabilityRequestBody, language, country, hotelId, channel,
                    null, null, sitewidePromoActive = false
                )
            }
    }

    private fun fetchAvailability(
        hotelAvailabilityRequestBody: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        promoCode: String?,
        promoKind: String?,
        sitewidePromoActive: Boolean,
        invalidDiscountCodeMessage: String? = null
    ): Observable<HotelBookingAvailabilityState> {
        return fetchHotelAvailabilityAndRatesInfoAndRoomType(
            hotelAvailabilityRequestBody,
            language,
            country,
            hotelId,
            channel,
            promoCode,
            promoKind
        ).map { state ->
            state.copy(
                sitewidePromoActive = sitewidePromoActive,
                invalidDiscountCodeMessage = invalidDiscountCodeMessage
            )
        }
    }

    fun cancelOnHoldReservation(basketReference: String, hotelCode: String): Single<CancelOnHoldReservationDomain> {
        return graphQLHDPRepository.cancelOnHoldReservation(basketReference, hotelCode)
    }

    sealed class AvailabilityAction {
        object AvailabilityLoading: AvailabilityAction()
        object AvailabilitySuccess: AvailabilityAction()
        object AvailabilityFullyBooked: AvailabilityAction()
        data class AvailabilityError(val exception: Throwable? = null, val msg: String? = null): AvailabilityAction()
    }
}