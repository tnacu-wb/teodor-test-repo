package com.whitbread.premierinn.domain.graphql.bookingDetails.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.ciol.usecase.GetCountryNameFromIsoCodeUseCase
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ResendInvoiceDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.repository.GraphQLFindBookingRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ResendInvoiceRequestBody
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GraphQLBookingDetailsUseCase @Inject constructor(
    private val getCountryNameFromIsoCodeUseCase: GetCountryNameFromIsoCodeUseCase,
    private val bookingDetailsRepository: GraphQLBookingDetailsRepository,
    private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository,
    private val graphQLPromotionsInformationRepository: GraphQLPromotionsInformationRepository,
    private val graphQLFindBookingRepository: GraphQLFindBookingRepository,
    private val bookingRepository: BookingRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
    private val isCustomerLoggedIn: IsCustomerLoggedIn) {

    fun bookingConfirmationAndManageBooking(
        basketReference: String,
        country: String,
        language: String,
        bookingChannel: String,
        hotelName: String? = EMPTY_STRING_DOMAIN,
        cancelInformationRequestBody: CancelInformationRequestBody
    ): Observable<Booking> {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            bookingDetailsRepository.bookingConfirmationAndManageBooking(
                basketReference,
                country,
                language,
                bookingChannel,
                hotelName,
                cancelInformationRequestBody,
                token = authenticationRepository.getIdToken().blockingGet()
            )
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .toObservable()
                .subscribeOn(Schedulers.io())
        } else {
            bookingDetailsRepository.bookingConfirmationAndManageBooking(
                basketReference,
                country,
                language,
                bookingChannel,
                hotelName,
                cancelInformationRequestBody,
                token = null
            ).toObservable()
                .subscribeOn(Schedulers.io())
        }
    }

fun bookingConfirmationAndManageBookingWithHotelInfo(
        uuidBasketReference: String, country: String, language: String,
        bookingChannel: String, hotelName: String? = EMPTY_STRING_DOMAIN,
        cancelInformationRequestBody: CancelInformationRequestBody
    ): Observable<Booking> {
        return bookingConfirmationAndManageBookingWithHotelInfoPair(
            uuidBasketReference,
            country,
            language,
            bookingChannel,
            hotelName,
            cancelInformationRequestBody
        ).map {
            it.first.copy(hotelName = it.second.name)
        }
    }

    fun bookingConfirmationAndManageBookingWithHotelInfoPair(
        uuidBasketReference: String,
        country: String,
        language: String,
        bookingChannel: String,
        hotelName: String? = EMPTY_STRING_DOMAIN,
        cancelInformationRequestBody: CancelInformationRequestBody
    ): Observable<Pair<Booking,HotelInformationDomain>> {
        return if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
            authenticationRepository.getIdToken()
                .flatMap { token ->
                    bookingDetailsRepository.bookingConfirmationAndManageBooking(
                        uuidBasketReference,
                        country,
                        language,
                        bookingChannel,
                        hotelName,
                        cancelInformationRequestBody,
                        token = token
                    )
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .toObservable()
        } else {
            bookingDetailsRepository.bookingConfirmationAndManageBooking(
                uuidBasketReference,
                country,
                language,
                bookingChannel,
                hotelName,
                cancelInformationRequestBody,
                token = null
            ).toObservable()
        }.flatMap { booking ->
            Observable.zip(
                Observable.just(
                    booking.copy(
                        isBusinessBooking = bookingChannel != Channel.PI.name,
                        preStayDetails = booking.preStayDetails?.copy(
                            guestRooms = booking.preStayDetails.guestRooms.map { room ->
                                room.copy(
                                    leadGuestNationality = getCountryNameFromIsoCodeUseCase(room.leadGuestNationality),
                                    accompanyingGuestNationality = getCountryNameFromIsoCodeUseCase(
                                        room.accompanyingGuestNationality
                                    )
                                )
                            }
                        )
                    )
                ),
                getHotelInfo(country, language, booking.hotelCode)
            ) { bookingInfo, hotel -> Pair(bookingInfo, hotel) }
        }.subscribeOn(Schedulers.io())
    }

    fun getRoomKeyInstructions(categoryLabelsRequestBody: CategoryLabelsRequestBody): Single<RoomKeyInstructionsDomain> {
        return bookingDetailsRepository.getRoomKeyInstructions(categoryLabelsRequestBody)
    }

    fun cancelReservation(cancelReservationRequestBody: CancelReservationRequestBody, reference: String): Single<CancelReservationDomain> {
        return bookingDetailsRepository.cancelReservation(cancelReservationRequestBody, reference)
    }

    fun saveUpdatedBookingInDb(booking: Booking) {
        bookingRepository.store(booking)
    }

    fun getPackages(packagesRequestBody: HotelPackagesRequestBody): Single<DataPackagesDomain> {
        return bookingDetailsRepository.packages(packagesRequestBody)
    }

    fun resendInvoice(body: ResendInvoiceRequestBody): Single<ResendInvoiceDomain> =
        bookingDetailsRepository.resendInvoiceEmail(body)

    fun getHotelInfo(
        country: String,
        language: String,
        hotelId: String
    ): Observable<HotelInformationDomain> {
        return graphQLHotelDetailsRepository.getHotelInfo(country, hotelId, language)
            .toObservable()
    }

    fun findBookingAndPromoInfoCall(
        findBookingRequestBody: FindBookingRequestBody,
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String? = null
    ): Single<Pair<FindBookingDomain, PromotionsInformationDomain>> {
        return graphQLFindBookingRepository.findBooking(findBookingRequestBody)
            .flatMap { findBookingDomain ->
                getPromotionInformation(
                    country = country,
                    language = language,
                    channel = channel,
                    brand = brand,
                    stayStartDate = stayStartDate,
                    stayEndDate = stayEndDate,
                    basketReference = basketReference
                )
                .firstOrError()
                .map { promoInfo -> Pair(findBookingDomain, promoInfo) }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun getPromotionInformation(
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
}
