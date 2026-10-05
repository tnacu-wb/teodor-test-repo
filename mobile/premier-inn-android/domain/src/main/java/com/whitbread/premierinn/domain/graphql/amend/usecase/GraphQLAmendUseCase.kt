package com.whitbread.premierinn.domain.graphql.amend.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.UpdateReservationPackagesResponseDomain
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ChangeBookingDatesDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AddNewRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ChangeBookingDatesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CopyBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GraphQLAmendUseCase @Inject constructor(
    private val graphQlAmendRepository: GraphQLAmendRepository,
    private val amendedReservationRepository: AmendedReservationRepository,
    private val reviewBookingRepository: GraphQLReviewBookingRepository,
    private val graphQLPromotionsInformationRepository: GraphQLPromotionsInformationRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce
) {

    fun copyBookingAndBookingConfirmationWithSavingToDb(
        bookingRef: String,
        arrivalDate: String,
        departureDate: String,
        copyBookingRequestBody: CopyBookingRequestBody,
        originalBasketReference: String,
        country: String,
        deviceLanguage: String,
        hotelCode: String,
        token: String,
        dateTimeFormatString: String,
        bookingChannelDetails: BookingChannelDetails,
        isBusinessBooking: Boolean
    ): Single<PackagesAndAncillaryCloseoutDomain> {
        return graphQlAmendRepository.copyBooking(copyBookingRequestBody)
            .flatMap { copyBookingDetails ->
                if (copyBookingDetails.copyBasketReference.isEmpty()) {
                    return@flatMap Single.error(Throwable("500"))
                }
                bookingConfirmationCall(
                    bookingRef,
                    arrivalDate,
                    departureDate,
                    originalBasketReference,
                    country,
                    deviceLanguage,
                    hotelCode,
                    EMPTY_STRING_DOMAIN,
                    token,
                    dateTimeFormatString,
                    bookingChannelDetails,
                    copyBookingDetails.copyBasketReference,
                    false,
                    false,
                    isBusinessBooking
                )
            }
    }

    fun bookingConfirmationCall(bookingRef: String,
                                arrivalDate: String,
                                departureDate: String,
                                originalBasketReference: String,
                                country: String,
                                deviceLanguage: String,
                                hotelCode: String,
                                hotelName: String,
                                token: String,
                                dateTimeFormatString: String,
                                bookingChannelDetails: BookingChannelDetails,
                                tempBasketReference: String,
                                isAddRoom: Boolean,
                                isRemoveRoom: Boolean,
                                isBusinessBooking: Boolean) : Single<PackagesAndAncillaryCloseoutDomain> {
        return if (isBusinessBooking) {
            authenticationRepository.getIdToken()
                .toFlowable()
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .flatMapSingle { authToken ->
                    amendedReservationRepository.bookingConfirmationAndSaveBookingDetailsInDbAndSP(
                        tempBasketReference,
                        bookingRef,
                        country, deviceLanguage, hotelName,
                        CancelInformationRequestBody(
                            originalBasketReference, hotelCode,
                            dateTimeFormatString,
                            token, bookingChannelDetails
                        ),
                        arrivalDate,
                        departureDate,
                        isAddRoom,
                        isRemoveRoom,
                        isBusinessBooking,
                        authToken
                    )
                }.firstOrError()
        } else {
            amendedReservationRepository.bookingConfirmationAndSaveBookingDetailsInDbAndSP(
                tempBasketReference,
                bookingRef,
                country, deviceLanguage, hotelName,
                CancelInformationRequestBody(
                    originalBasketReference, hotelCode,
                    dateTimeFormatString,
                    token, bookingChannelDetails
                ),
                arrivalDate,
                departureDate,
                isAddRoom,
                isRemoveRoom,
                false,
                null
            )
        }
    }

    fun bookingConfirmationCallAndAmendSummaryCall(bookingRef: String,
                                arrivalDate: String,
                                departureDate: String,
                                originalBasketReference: String,
                                country: String,
                                deviceLanguage: String,
                                hotelCode: String,
                                hotelName: String,
                                token: String,
                                dateTimeFormatString: String,
                                bookingChannelDetails: BookingChannelDetails,
                                tempBasketReference: String,
                                isBusinessBooking: Boolean) : Single<Pair<AmendSummaryDomain, PackagesAndAncillaryCloseoutDomain>> {
        return if (isBusinessBooking) {
            authenticationRepository.getIdToken()
                .toFlowable()
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .flatMapSingle { authToken ->
                    amendedReservationRepository.bookingConfirmationAndManageBookingAmendFlowAndSaveBookingDetailsInDbAndSP(
                        originalBasketReference,
                        tempBasketReference,
                        bookingRef,
                        token,
                        bookingChannelDetails,
                        country, deviceLanguage, hotelName,
                        CancelInformationRequestBody(
                            originalBasketReference, hotelCode,
                            dateTimeFormatString,
                            token, bookingChannelDetails),
                        arrivalDate,
                        departureDate,
                        authToken
                    ).flatMap { packagesAndAncillaryCloseout ->
                        amendSummary(AmendSummaryRequestBody(
                            tempBasketReference = tempBasketReference,
                            originalBasketReference = originalBasketReference,
                            token = token,
                            bookingChannel = bookingChannelDetails,
                            country = country
                        )).map { amendSummary ->
                            Pair(amendSummary, packagesAndAncillaryCloseout)
                        }
                    }
                }
                .singleOrError()
        } else {
            amendedReservationRepository.bookingConfirmationAndManageBookingAmendFlowAndSaveBookingDetailsInDbAndSP(
                originalBasketReference,
                tempBasketReference,
                bookingRef,
                token,
                bookingChannelDetails,
                country, deviceLanguage, hotelName,
                CancelInformationRequestBody(
                    originalBasketReference, hotelCode,
                    dateTimeFormatString,
                    token, bookingChannelDetails),
                arrivalDate,
                departureDate,
                null
            ).flatMap { packagesAndAncillaryCloseout ->
                amendSummary(AmendSummaryRequestBody(
                    tempBasketReference = tempBasketReference,
                    originalBasketReference = originalBasketReference,
                    token = token,
                    bookingChannel = bookingChannelDetails,
                    country = country
                )).map { amendSummary ->
                    Pair(amendSummary, packagesAndAncillaryCloseout)
                }
            }
        }
    }

    fun removeRoom(removeRoomRequestBody: RemoveRoomRequestBody): Single<TempBookingRefDomain> {
        return graphQlAmendRepository.removeRoom(removeRoomRequestBody)
    }

    fun amendEditRoom(
        amendEditRoomRequestBody: AmendEditRoomRequestBody,
        isBusinessBooking: Boolean
    ): Single<TempBookingRefDomain> {
        return if (isBusinessBooking) {
            authenticationRepository.getIdToken()
                .toFlowable()
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .flatMapSingle { token ->
                    graphQlAmendRepository.amendEditRoom(amendEditRoomRequestBody, token = token)
                }
                .singleOrError()
        } else {
            graphQlAmendRepository.amendEditRoom(amendEditRoomRequestBody, token = null)
        }
    }

    fun addNewRoom(addNewRoomRequestBody: AddNewRoomRequestBody): Single<TempBookingRefDomain> {
        return graphQlAmendRepository.addNewRoom(addNewRoomRequestBody)
    }

    fun confirmAmendLogic(confirmAmendLogicRequestBody: ConfirmAmendLogicRequestBody): Single<ConfirmAmendLogicDomain> {
        return graphQlAmendRepository.confirmAmendLogic(confirmAmendLogicRequestBody)
    }

    fun getPromotionInformation(
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String? = null
    ): Observable<PromotionsInformationDomain> {
        return graphQLPromotionsInformationRepository.getPromotionsInformation(
            country,
            language,
            channel,
            brand,
            stayStartDate,
            stayEndDate,
            basketReference
        )
            .onErrorResumeNext { _: Throwable ->
                Single.just(PromotionsInformationDomain.createDefault())
            }
            .toObservable()
    }
    fun changeBookingDates(changeBookingDatesRequestBody: ChangeBookingDatesRequestBody) : Single<ChangeBookingDatesDomain> {
        return graphQlAmendRepository.changeBookingDates(changeBookingDatesRequestBody)
    }

    fun removeReservationIdLinkedAmendDetailsInDao(reservationId: String): Completable{
        return amendedReservationRepository.clearAmendReservationWithResId(reservationId)
    }

    fun updateReservationWithAncillaries(updateReservationPackagesRequest: UpdateReservationWithAncillariesRequestBody): Single<UpdateReservationPackagesResponseDomain> {
        return graphQlAmendRepository.updateReservationPackagesByReservation(updateReservationPackagesRequest)
    }

    fun amendSummary(amendSummaryRequestBody: AmendSummaryRequestBody): Single<AmendSummaryDomain> {
        return graphQlAmendRepository.amendSummary(amendSummaryRequestBody)
    }

    fun getBasketStatusAmend(basketReference: String): Single<BasketStatusRevisedPaymentsDomain> {
        return reviewBookingRepository.getBasketStatusRevisedPayments(basketReference)
    }
}
