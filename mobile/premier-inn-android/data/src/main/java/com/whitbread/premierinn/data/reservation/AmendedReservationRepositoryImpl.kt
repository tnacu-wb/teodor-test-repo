package com.whitbread.premierinn.data.reservation

import com.google.gson.Gson
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.common.DatabaseTransactionRunner
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.commaSeparatedAddress
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.throwApiExceptionIfNullable
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingDomain
import com.whitbread.premierinn.data.remote.AmendReservationRequest.CompletePendingAmendRequest
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.data.remote.ReservationApi
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationAndAmendSummaryGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownDao
import com.whitbread.premierinn.data.roombreakdown.toAmendedRoomBreakdownEntity
import com.whitbread.premierinn.data.roombreakdown.toDomain
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomcriteria.toAmendRoomCriteriaEntity
import com.whitbread.premierinn.data.roomcriteria.toDomain
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomguest.toAmendedGuestEntity
import com.whitbread.premierinn.data.roomguest.toDomain
import com.whitbread.premierinn.data.roomupsell.RoomUpsellDao
import com.whitbread.premierinn.data.roomupsell.RoomUpsellEntity
import com.whitbread.premierinn.data.roomupsell.toReservationDomain
import com.whitbread.premierinn.data.roomupsell.toRoomUpsellEntity
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableDao
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableEntity
import com.whitbread.premierinn.data.upsellavailable.toAmendedUpsellItemsAvailableEntity
import com.whitbread.premierinn.data.upsellavailable.toUpsellsAvailable
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.DONATION_LIST
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.HAS_TWIN_PACKAGE_CODE
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.common.toListOfUpsellsAvailable
import com.whitbread.premierinn.domain.common.toUpsell
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.toChannelEnum
import com.whitbread.premierinn.domain.payment.AmendReservationDomainDetails
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import javax.inject.Inject

class AmendedReservationRepositoryImpl @Inject constructor(
    private val simplePersistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val amendedReservationDao: AmendedReservationDao,
    private val roomCriteriaDao: RoomCriteriaDao,
    private val upsellItemAvailableDao: UpsellItemAvailableDao,
    private val roomGuestDao: RoomGuestDao,
    private val roomUpsellDao: RoomUpsellDao,
    private val bookingDao: BookingDao,
    private val roomBreakdownDao: RoomBreakdownDao,
    private val transactionRunner: DatabaseTransactionRunner,
    private val remote: ReservationApi,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val errorLogger: ErrorLogger,
    private val graphQlAmendRepository: GraphQLAmendRepository,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonVariableForBookingConf: JSONObject,
    private val jsonVariableForBookingConfAmendSummary: JSONObject,
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val bookingRepository: BookingRepository,
    private val countriesRepository: CountriesRepository,
) : AmendedReservationRepository {

//    TODO: Dashboard Not migrated to opera
//    override fun trigger(reservationReference: String, surname: String, arrivalDate: LocalDate): Completable {
//        return remote.getReservation(reservationId = reservationReference, surname = surname,
//                arrivalDate = arrivalDate, deviceLocaleProvider.getBookingChannel(),
//                              deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()),
//                              deviceLocaleProvider.getDeviceLanguage())
//                .toObservable()
//                .doOnNext {
//                    throwApiExceptionIfNullable(it)
//                    simplePersistenceManager.storeAmendReservationID(it.sessionId)
//                    if (it.details?.paymentDetails != null) {
//                        if (it.details.paymentDetails.billingAddress == null) {
//                            val bookerAddress = it.details.booker.address
//                            val detailModified = it.details.copy(
//                                paymentDetails = it.details.paymentDetails.copy(billingAddress = bookerAddress))
//                            simplePersistenceManager.storePaymentDetails(detailModified.paymentDetails.toDomain()!!)
//                        } else {
//                            simplePersistenceManager.storePaymentDetails(it.details.paymentDetails.toDomain()!!)
//                        }
//                    }
//                }.doAfterNext {
//                throwApiExceptionIfNullable(it)
//                //saveUpsellAvailableItems(it.upsellItemsAvailable) This method is opera but the rest is api so uncomment when migrated
//            }.ignoreElements()
//    }

    override fun bookingConfirmationAndSaveBookingDetailsInDbAndSP(
        tempBasketReference: String,
        bookingRef: String,
        country: String, language: String, hotelName: String?,
        cancelInformationRequestBody: CancelInformationRequestBody,
        arrivalDate: String,
        departureDate: String,
        isAddRoom: Boolean,
        isRemoveRoom: Boolean,
        isBusinessBooking: Boolean,
        authToken: String?
    ): Single<PackagesAndAncillaryCloseoutDomain> {
        val useManageBooking = !isAddRoom && !isRemoveRoom
        val query: String = if (!useManageBooking) {
            fileDataProvider.loadFileFromAssetGQL("graphql/BookingConfirmationQueryGQL.txt")
        } else {
            fileDataProvider.loadFileFromAssetGQL("graphql/BookingConfirmationAndManageBookingQueryGQL.txt")
        }
        val constructVariablesJsonObject = constructVariables(jsonVariableForBookingConf,
            tempBasketReference, country, language, cancelInformationRequestBody)
        var bookingFlowId = EMPTY_STRING
        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.bookingConfirmationGraphQL(
            bearerToken = if (useManageBooking) authToken?.let { "$AUTHORIZATION_BEARER $it" } else null,
            query = jsonObject.toString())
            .onGraphQLError()
            .toObservable()
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }.doOnNext { bookingConfirmationResp ->
                bookingFlowId = bookingConfirmationResp.data.bookingConfirmation?.bookingFlowID ?: EMPTY_STRING
                simplePersistenceManager.storeTempBookingRef(tempBasketReference)
                simplePersistenceManager.storeBillingAddress(
                    bookingConfirmationResp.data.bookingConfirmation?.reservationByIdList?.first()?.billing?.commaSeparatedAddress() ?: EMPTY_STRING)
            }.flatMapSingle { booking ->
                if (booking.data.bookingConfirmation !=  null){
                    val updatedBookingConf = booking.data.bookingConfirmation.copy(bookingReference = bookingRef)
                    val updatedBookingData = booking.data.copy(bookingConfirmation = updatedBookingConf)
                    bookingRepository.storeUpdatedAmendedRoom(
                        booking.copy(data = updatedBookingData).mapToBookingDomain(
                            hotelName = hotelName,
                            countries = countriesRepository.getCountriesFromSharedPref()
                        )
                    )
                    graphQlAmendRepository.packagesAndAncillariesCloseoutInfo(HotelPackagesRequestBody(
                        hotelId = booking.data.bookingConfirmation.hotelId,
                        startDate = arrivalDate,
                        endDate = departureDate,
                        adultsNumber = booking.data.bookingConfirmation.totalNumberOfAdults,
                        childrenNumber = booking.data.bookingConfirmation.totalNumberOfChildren,
                        nightsNumber = booking.data.bookingConfirmation.reservationByIdList.first().roomStay.numberOfNights,
                        language = language,
                        country = country,
                        bookingFlowId = bookingFlowId,
                        basketReferenceId = tempBasketReference,
                        channel = if (isBusinessBooking) Channel.BB else Channel.PI,
                    )).doOnSuccess{ packagesAndAncillaryCloseout ->
                        storeReservationAsAmended(booking, tempBasketReference, bookingRef,
                            packagesAndAncillaryCloseout.packages.packages.roomSelection,
                            packagesAndAncillaryCloseout.packages, departureDate)
                        if (isAddRoom) {
                            saveOriginalRoomSelectionInSP(packagesAndAncillaryCloseout.packages.packages.roomSelection)
                        } else if (isRemoveRoom) {
                            saveUpdatedRoomSelectionAfterRemoveInSP(packagesAndAncillaryCloseout.packages.packages.roomSelection)

                        } else {
                            storeOriginalBookingInSP(booking, tempBasketReference, bookingRef, packagesAndAncillaryCloseout.packages.packages, departureDate)
                            saveOriginalRoomSelectionInSP(packagesAndAncillaryCloseout.packages.packages.roomSelection)
                        }

                        val upsellItemsAllowed = if (isBusinessBooking) {
                            businessPersistenceManager
                                .getCompany()
                                ?.requestedCompany
                                ?.bookingAllowances
                                ?.upsellItemsAllowed
                        } else null

                        val filteredUpsellItems = packagesAndAncillaryCloseout.packages.packages.meals
                            .filter { meal ->
                                !isBusinessBooking || (meal.bartId != null && upsellItemsAllowed?.contains(meal.bartId) == true)
                            }
                            .toListOfUpsellsAvailable()
                        saveUpsellAvailableItems(filteredUpsellItems)
                    }
                } else {
                    Single.error(Throwable("500"))
                }
            }.firstOrError()
    }

    private fun constructVariables(
        jsonObject: JSONObject,
        basketReference: String,
        country: String,
        language: String,
        cancelInformationRequestBody: CancelInformationRequestBody
    ) : JSONObject {
        return jsonObject.apply {
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)

            put("cancelInformationCriteria", JSONObject().apply {
                put("basketReference", cancelInformationRequestBody.basketReference)
                put("hotelId", cancelInformationRequestBody.hotelId)
                put("userDateTime", cancelInformationRequestBody.userDateTime)
                put("token", cancelInformationRequestBody.token)
                put("bookingChannel", JSONObject(Gson().toJson(cancelInformationRequestBody.bookingChannel)))
            })
        }
    }

    override fun bookingConfirmationAndManageBookingAmendFlowAndSaveBookingDetailsInDbAndSP(
        originalBasketReference: String,
        tempBasketReference: String,
        bookingRef: String,
        token: String,
        bookingChannel: BookingChannelDetails,
        country: String,
        language: String,
        hotelName: String?,
        cancelInformationRequestBody: CancelInformationRequestBody,
        arrivalDate: String,
        departureDate: String,
        authToken: String?
    ): Single<PackagesAndAncillaryCloseoutDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BookingConfirmationAndManageBookingQueryGQL.txt")
        val constructVariablesJsonObject = constructVariables(jsonVariableForBookingConfAmendSummary,
            originalBasketReference, tempBasketReference, token, bookingChannel.channel, country, language, cancelInformationRequestBody)
        var bookingFlowId = EMPTY_STRING
        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.bookingConfirmationAndAmendSummaryGraphQL(
            bearerToken = authToken?.let { "$AUTHORIZATION_BEARER $it" },
            query = jsonObject.toString())
            .onGraphQLError()
            .toObservable()
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }.doOnNext {
                bookingFlowId = it.data.bookingConfirmation?.bookingFlowID ?: EMPTY_STRING
                simplePersistenceManager.storeTempBookingRef(tempBasketReference)
                simplePersistenceManager.storeBillingAddress(
                    it.data.bookingConfirmation?.reservationByIdList?.first()?.billing?.commaSeparatedAddress() ?: EMPTY_STRING)
            }.flatMapSingle { booking ->
                if (booking.data.bookingConfirmation !=  null){
                    val updatedBookingConf = booking.data.bookingConfirmation.copy(bookingReference = bookingRef)
                    val updatedBookingData = booking.data.copy(bookingConfirmation = updatedBookingConf)
                    bookingRepository.storeUpdatedAmendedRoom(booking.copy(data = updatedBookingData).mapToBookingDomain(hotelName = hotelName))
                    graphQlAmendRepository.packagesAndAncillariesCloseoutInfo(HotelPackagesRequestBody(
                        hotelId = booking.data.bookingConfirmation.hotelId,
                        startDate = arrivalDate,
                        endDate = departureDate,
                        adultsNumber = booking.data.bookingConfirmation.totalNumberOfAdults,
                        childrenNumber = booking.data.bookingConfirmation.totalNumberOfChildren,
                        nightsNumber = booking.data.bookingConfirmation.reservationByIdList.first().roomStay.numberOfNights,
                        language = language,
                        country = country,
                        bookingFlowId = bookingFlowId,
                        basketReferenceId = tempBasketReference,
                        channel = bookingChannel.toChannelEnum()
                    )).doOnSuccess{ packagesAndAncillaries ->
                        storeReservationAsAmendedWithAmendSummaryResponse(booking, tempBasketReference, bookingRef,
                            packagesAndAncillaries.packages.packages.roomSelection, packagesAndAncillaries.packages, departureDate)
                        saveOriginalRoomSelectionInSP(packagesAndAncillaries.packages.packages.roomSelection)
                        saveUpsellAvailableItems(packagesAndAncillaries.packages.packages.meals.toListOfUpsellsAvailable())
                    }
                } else {
                    Single.error(Throwable("500"))
                }
            }.firstOrError()
    }


    private fun constructVariables(
        jsonObject: JSONObject,
        originalBasketRef: String,
        basketReference: String,
        token: String,
        bookingChannel: String,
        country: String,
        language: String,
        cancelInformationRequestBody: CancelInformationRequestBody
    ) : JSONObject {
        return jsonObject.apply {
            put("basketReference", basketReference)
            put("copyBasketRef", basketReference)
            put("originalBasketRef", originalBasketRef)
            put("token", token)
            put("bookingChannel", bookingChannel)
            put("country", country)
            put("language", language)

            put("cancelInformationCriteria", JSONObject().apply {
                put("basketReference", cancelInformationRequestBody.basketReference)
                put("hotelId", cancelInformationRequestBody.hotelId)
                put("userDateTime", cancelInformationRequestBody.userDateTime)
                put("token", cancelInformationRequestBody.token)
                put("bookingChannel", JSONObject(Gson().toJson(cancelInformationRequestBody.bookingChannel)))
            })
        }
    }

    override fun updateReservationDatesAndUpsells(
            reservationId: String, dates: Pair<LocalDate, LocalDate>, updatedReservation: Reservation): Completable {
        return Completable.fromCallable {
            val originalFullReservation = getOriginalReservation()

            if (originalFullReservation != null && originalFullReservation.upsells.isNotEmpty()) {
                val originalCriteriaRoomIds =
                        originalFullReservation.upsells.map { it.roomId }.distinct()
                val originalNights = Period.between(
                        originalFullReservation.arrival,
                        originalFullReservation.departure
                ).days
                val amendedNights = Period.between(dates.first, dates.second).days
                val originalUpsellsPerRoom =
                        originalFullReservation.upsells.groupingBy { it.roomId }
                                .eachCount().values.distinct()[0]
                val iterationCountMax = when {
                    (originalUpsellsPerRoom < originalNights && originalUpsellsPerRoom < amendedNights) -> originalUpsellsPerRoom
                    (amendedNights <= originalNights) -> amendedNights
                    (amendedNights > originalNights) -> originalNights
                    else -> error(INVALID_ERROR)
                }
                val newUpsellDates = mutableListOf<LocalDate>()
                if (iterationCountMax != 0) {
                    for (index in 1..iterationCountMax) {
                        newUpsellDates.add(dates.first.plusDays(index.toLong()))
                    }
                }
                val updatedUpsellEntityList = mutableListOf<RoomUpsellEntity>()
                for (roomId in originalCriteriaRoomIds) {
                    val originalUpsellByRoomId =
                            originalFullReservation.upsells.firstOrNull { it.roomId == roomId }
                    if (originalUpsellByRoomId != null) {
                        for (date in newUpsellDates) {
                            updatedUpsellEntityList.add(
                                    originalUpsellByRoomId.copy(postingDate = date)
                                            .toEntity(reservationId, originalUpsellByRoomId.roomId)
                            )
                        }

                        roomUpsellDao.removeRoomUpsell(reservationId, roomId)
                        for (upsellEntity in updatedUpsellEntityList) {
                            roomUpsellDao.insert(upsellEntity)
                        }
                        updatedUpsellEntityList.clear()
                    }
                }
            }

            amendedReservationDao.updateDates(reservationId, dates.first, dates.second)

            updateStoredReservation(reservationId, updatedReservation)
        }
    }

    override fun getAmendedReservation(reservationId: String): Observable<Reservation> {
        return amendedReservationDao.getAmendedReservationUpdatesWithRelationsUpdates(reservationId)
                .map { it.sortRooms().toDomain() }
    }

    override fun submit(reservationId: String, arrivalDate: LocalDate, surname: String,
                        updatedReservation: Reservation, paymentDetails: PaymentDetails,
                        cardSecurityCode: String, availableUpsells: List<UpsellAvailable>): Single<AmendReservationDomainDetails?> {
        val deviceLocale = deviceLocaleProvider.getDeviceLocale()
        val country = deviceLocaleProvider.getCountryIfRegion(deviceLocale)

        return remote.updateAmendReservation(
                amendReservationRequest = updatedReservation.toAmendRequest(availableUpsells, paymentDetails, cardSecurityCode, deviceLocale.language),
                reservationId = reservationId,
                sessionId = simplePersistenceManager.getAmendReservationID(),
                bookingChannel = deviceLocaleProvider.getBookingChannel(),
                arrivalDate = arrivalDate,
                country = country,
                language = deviceLocale.language)
                .map { it.toDomain() }
                .doOnSuccess { savePendingAmendId(it.pendingAmendId) }
                .onErrorResumeNext { e ->
                    if (e is ApiThrowable.Http && e.isErrorSessionId) {
                        remote.getReservation(reservationId = reservationId,
                                surname = surname,
                                arrivalDate = arrivalDate,
                                bookingChannel = deviceLocaleProvider.getBookingChannel(),
                                country = country,
                                language = deviceLocale.language)
                                .flatMap {
                                    throwApiExceptionIfNullable(it)
                                    simplePersistenceManager.storeAmendReservationID(it.sessionId)
                                    remote.updateAmendReservation(
                                            amendReservationRequest = updatedReservation.toAmendRequest(
                                                    availableUpsells, paymentDetails, cardSecurityCode, deviceLocale.language),
                                            reservationId = reservationId,
                                            sessionId = it.sessionId!!,
                                            bookingChannel = deviceLocaleProvider.getBookingChannel(),
                                            arrivalDate = arrivalDate,
                                            country = country,
                                            language = deviceLocale.language)
                                            .map { response -> response.toDomain() }
                                            .doOnSuccess { amendReservationDomainDetails ->
                                                savePendingAmendId(amendReservationDomainDetails.pendingAmendId)
                                            }
                                }
                    } else {
                        Single.error(e)
                    }
                }
    }

    private fun savePendingAmendId(pendingAmendId: String?) {
        if (pendingAmendId != null) {
            simplePersistenceManager.storePendingAmendId(pendingAmendId)
        }
    }

    override fun completePendingAmend(pares: String): Single<AmendReservationDomainDetails?> {
        return remote.completePendingAmend(CompletePendingAmendRequest(pares = pares), simplePersistenceManager.getPendingAmendId())
                .map { it.toDomain() }
    }

    override fun updateBooking(
            reservationId: String,
            arrival: LocalDate,
            departure: LocalDate,
            leadGuestSurname: String
    ): Completable {
        return Completable.fromCallable {
            bookingDao.updateBooking(reservationId, arrival, departure, leadGuestSurname)
        }
    }

    override fun updateReservation(
            reservationId: String,
            updatedReservation: Reservation
    ): Completable {
        return Completable.fromCallable {
            updateStoredReservation(reservationId, updatedReservation)
        }
    }

    private fun updateStoredReservation(reservationId: String, updatedReservation: Reservation) {
        val originalFullReservation =
                amendedReservationDao.getAmendedReservationWithRelations(reservationId)
                        .map { it.sortRooms() }.blockingGet()
        val originalCriteriaRowIds = originalFullReservation.roomCriteria.map { it.roomId }
        val updatedGuestEntityList =
                updatedReservation.roomsLeadGuest.mapIndexed { index, updatedGuest ->
                    updatedGuest.toEntity(reservationId, originalCriteriaRowIds[index])
                }
        val updatedCriteriaEntityList =
                updatedReservation.roomsCriteria.mapIndexed { index, updatedCriteria ->
                    updatedCriteria.toEntity(reservationId, originalCriteriaRowIds[index])
                }
        val updatedUpsellEntityList = updatedReservation.upsells.mapIndexed { _, updatedUpsell ->
            updatedUpsell.toEntity(reservationId, updatedUpsell.roomId)
        }
        val updatedRoomBreakdownEntityList =
                updatedReservation.roomsBreakdown.mapIndexed { _, updatedRoomBreakdown ->
                    updatedRoomBreakdown.toEntity(reservationId, updatedRoomBreakdown.roomId)
                }
        val updatedReservationEntity =
                updatedReservation.toEntity(basketReference = originalFullReservation.reservationEntity.basketReference)

        amendedReservationDao.updateDates(
                reservationId,
                updatedReservation.arrival,
                updatedReservation.departure
        )

        amendedReservationDao.insert(updatedReservationEntity)
        for (guestEntity in updatedGuestEntityList) {
            roomGuestDao.insert(guestEntity)
        }
        for (roomCriteriaEntity in updatedCriteriaEntityList) {
            roomCriteriaDao.insert(roomCriteriaEntity)
        }
        for (upsellEntity in updatedUpsellEntityList) {
            roomUpsellDao.insert(upsellEntity)
        }
        for (roomBreakdownEntity in updatedRoomBreakdownEntityList) {
            roomBreakdownDao.insert(roomBreakdownEntity)
        }
    }

    override fun clearDaoLinkedWithAmend() {
        amendedReservationDao.deleteAll()
        roomCriteriaDao.deleteAll()
        upsellItemAvailableDao.deleteAll()
        roomGuestDao.deleteAll()
        roomUpsellDao.deleteAll()
        roomBreakdownDao.deleteAll()
    }

    override fun clearAmendReservationWithResId(reservationId: String): Completable{
        return Completable.fromCallable {
            amendedReservationDao.deleteByReference(reservationId)
        }
    }

    override fun removeRoom(reservationId: String, bookingRef: String): Completable {
        return Completable.fromCallable {
            roomGuestDao.removeGuest(bookingRef, reservationId)
            roomCriteriaDao.removeRoomCriteria(bookingRef, reservationId)
            roomBreakdownDao.removeRoomBreakdown(
                bookingRef,
                reservationId
            )
                roomUpsellDao.removeRoomUpsell(bookingRef, reservationId)
        }
    }

    override fun updateReservationAddRoom(
            reservationId: String,
            updatedReservation: Reservation
    ): Completable {
        return Completable.fromCallable {
            val originalFullReservation =
                    amendedReservationDao.getAmendedReservationWithRelations(reservationId)
                            .map { it.sortRooms() }.blockingGet()
            val originalCriteriaRowIds = originalFullReservation.roomCriteria.map { it.roomId }
            val roomId = updatedReservation.roomsCriteria.last().roomId

            val updatedGuestEntityList =
                    updatedReservation.roomsLeadGuest.mapIndexed { index, updatedGuest ->

                        when (updatedGuest.roomId) {
                            roomId -> {
                                updatedGuest.toEntity(reservationId, roomId)
                            }
                            else -> updatedGuest.toEntity(reservationId, originalCriteriaRowIds[index])
                        }
                    }
            val updatedCriteriaEntityList =
                    updatedReservation.roomsCriteria.mapIndexed { index, updatedCriteria ->
                        when (updatedCriteria.roomId) {
                            roomId -> {
                                updatedCriteria.toEntity(reservationId, roomId)
                            }
                            else -> updatedCriteria.toEntity(
                                    reservationId,
                                    originalCriteriaRowIds[index]
                            )
                        }
                    }
            val updatedUpsellEntityList =
                    updatedReservation.upsells.mapIndexed { _, updatedUpsell ->
                        if (updatedUpsell.roomId.isNotEmpty()) {
                            updatedUpsell.toEntity(reservationId, updatedUpsell.roomId)
                        } else {
                            updatedUpsell.toEntity(reservationId, roomId)
                        }
                    }
            val updatedRoomBreakdownEntityList =
                    updatedReservation.roomsBreakdown.mapIndexed { index, updatedRoomBreakdown ->
                        when (updatedRoomBreakdown.roomId) {
                            roomId -> {
                                updatedRoomBreakdown.toEntity(reservationId, roomId)
                            }
                            else -> updatedRoomBreakdown.toEntity(
                                    reservationId,
                                    originalCriteriaRowIds[index]
                            )
                        }
                    }
            val updatedReservationEntity =
                    updatedReservation.toEntity(basketReference = originalFullReservation.reservationEntity.basketReference)

            try {
                amendedReservationDao.insert(updatedReservationEntity)
            } catch (e: Exception) {
                errorLogger.logException(e, "Failed to insert amended reservation")
            }
            try {
                for (guestEntity in updatedGuestEntityList) {
                    roomGuestDao.insert(guestEntity)
                }
            } catch (e: Exception) {
                errorLogger.logException(e, "Failed to insert room guest")
            }
            try {
                for (roomCriteriaEntity in updatedCriteriaEntityList) {
                    roomCriteriaDao.insert(roomCriteriaEntity)
                }
            } catch (e: Exception) {
                errorLogger.logException(e, "Failed to insert room criteria")
            }
            try {
                for (upsellEntity in updatedUpsellEntityList) {
                    roomUpsellDao.insert(upsellEntity)
                }
            } catch (e: Exception) {
                errorLogger.logException(e, "Failed to insert room upsells")
            }
            try {
                for (roomBreakdownEntity in updatedRoomBreakdownEntityList) {
                    roomBreakdownDao.insert(roomBreakdownEntity)
                }
            } catch (e: Exception) {
                errorLogger.logException(e, "Failed to insert room breakdown")
            }
        }
    }

    private fun storeReservationAsAmended(
        booking: BookingConfirmationGraphQLContract.BookingConfirmationData,
        tempBasketReference: String,
        originalBookingRef: String,
        listOfRoomSelections: List<RoomSelectionDomain>?,
        packages: DataPackagesDomain,
        departureDate: String
    ) {
        booking.data.bookingConfirmation?.let { bookingConf ->

                transactionRunner {
                    val currency = bookingConf.currencyCode

                    amendedReservationDao.insert(
                        booking.toAmendedReservationEntity(
                            tempBasketReference,
                            originalBookingRef
                        )
                    )

                    val rooms = bookingConf.reservationByIdList
                    rooms.forEach { eachRoom ->
                        roomCriteriaDao.insert(eachRoom.toAmendRoomCriteriaEntity(originalBookingRef))
                        eachRoom.reservationGuestList.forEach { guest ->
                            roomGuestDao.insert(
                                guest.toAmendedGuestEntity(
                                    originalBookingRef,
                                    eachRoom.reservationId
                                )
                            )
                        }

                        roomBreakdownDao.insert(
                            eachRoom.roomStay.toAmendedRoomBreakdownEntity(
                                originalBookingRef,
                                eachRoom.reservationId,
                                currency
                            )
                        )
                    }
            }
        }

        saveSelectedUpsellItemsInDB(listOfRoomSelections, originalBookingRef, packages, departureDate)
    }

    fun storeReservationAsAmendedWithAmendSummaryResponse(
        booking: BookingConfirmationAndAmendSummaryGraphQLContract.BookingConfirmationAndAmendSummaryData,
        tempBasketReference: String,
        originalBookingRef: String,
        listOfRoomSelections: List<RoomSelectionDomain>?,
        packages: DataPackagesDomain,
        departureDate: String
    ) {
        booking.data.bookingConfirmation?.let { bookingConf ->

            transactionRunner {
                val currency = bookingConf.currencyCode

                amendedReservationDao.insert(
                    booking.toAmendedReservationEntity(
                        tempBasketReference,
                        originalBookingRef
                    )
                )

                val rooms = bookingConf.reservationByIdList
                rooms.forEach { eachRoom ->
                    roomCriteriaDao.insert(eachRoom.toAmendRoomCriteriaEntity(originalBookingRef))
                    eachRoom.reservationGuestList.forEach { guest ->
                        roomGuestDao.insert(
                            guest.toAmendedGuestEntity(
                                originalBookingRef,
                                eachRoom.reservationId
                            )
                        )
                    }

                    roomBreakdownDao.insert(
                        eachRoom.roomStay.toAmendedRoomBreakdownEntity(
                            originalBookingRef,
                            eachRoom.reservationId,
                            currency
                        )
                    )
                }
            }
        }
        saveSelectedUpsellItemsInDB(listOfRoomSelections, originalBookingRef, packages, departureDate)
    }

    private fun saveSelectedUpsellItemsInDB(
        listOfRoomSelections: List<RoomSelectionDomain>?,
        bookingRef: String,
        packages: DataPackagesDomain,
        departureDate: String
    ) {
        listOfRoomSelections?.forEach { roomSelection ->
            roomSelection.packagesSelection.forEach { selectedMeals ->
                val upsellSelectedPresent = roomSelection.toRoomUpsellEntity(bookingRef, selectedMeals, packages, departureDate)
                if (upsellSelectedPresent != null) {
                    transactionRunner {
                        roomUpsellDao.insert(upsellSelectedPresent)
                    }
                }
            }
        }
    }

    private fun storeOriginalBookingInSP(
        booking: BookingConfirmationGraphQLContract.BookingConfirmationData,
        tempBasketRef: String, originalBookingRef: String, packages: PackagesPackagesDomain,
        departureDate: String) {
        val originalReservation = booking.toAmendedReservationEntity(tempBasketRef, originalBookingRef)
        val listOfUpsells = mutableListOf<Upsell>()
        val listOfGuests = mutableListOf<Guest>()
        val listOfRooms = mutableListOf<RoomCriteria>()
        val listOfRoomBreakdown = mutableListOf<RoomBreakdown>()

        booking.data.bookingConfirmation?.let { bookingConf ->
            val currency = booking.data.bookingConfirmation.currencyCode
            val rooms = booking.data.bookingConfirmation.reservationByIdList

            rooms.forEach { eachRoom ->
                listOfRooms += eachRoom.toAmendRoomCriteriaEntity(originalBookingRef).toDomain()
                eachRoom.reservationGuestList.forEach { guest ->
                    listOfGuests += guest.toAmendedGuestEntity(originalBookingRef, eachRoom.reservationId).toDomain()
                }
                listOfRoomBreakdown += eachRoom.roomStay.toAmendedRoomBreakdownEntity(originalBookingRef,
                    eachRoom.reservationId, currency).toDomain()

                val roomid = eachRoom.reservationId
                val findRoomSelectionDetails = packages.roomSelection?.find { it.reservationId == roomid }
                findRoomSelectionDetails?.packagesSelection?.forEach { selectedPackages ->
                    if (selectedPackages.id in DONATION_LIST) {
                        val donationPackage = bookingConf.reservationByIdList
                            .flatMap { it.reservationPackageList }
                            .find { it.packageCode == selectedPackages.id }

                        val donation = Upsell(
                            quantity = selectedPackages.noOfSelections,
                            category = Upsell.Category.OTHER,
                            legend = donationPackage?.description ?: EMPTY_STRING,
                            postingDate = departureDate.toLocalDate(),
                            unitCost = PriceDomain(donationPackage?.computedPrice ?: 0F, GBP),
                            code = selectedPackages.id!!,
                            roomId = findRoomSelectionDetails.reservationId!!
                        )

                        listOfUpsells.add(donation)
                    }
                    if (selectedPackages.id != HAS_TWIN_PACKAGE_CODE) {
                        val upsell = findRoomSelectionDetails.toUpsell(selectedPackages, packages, departureDate.toLocalDate())
                        upsell?.let {
                            listOfUpsells.add(upsell)
                        }
                    }
                }
            }

                val reservationDomain = toReservationDomain(
                    originalBookingRef,
                    originalReservation,
                    listOfRooms,
                    listOfGuests,
                    listOfUpsells,
                    listOfRoomBreakdown
                )
                simplePersistenceManager.storeReservation(reservationDomain)
        }
    }

    override fun getOriginalReservation(): Reservation? {
        return simplePersistenceManager.getReservation()
    }

    private fun saveOriginalRoomSelectionInSP(listOfRoomSelections: List<RoomSelectionDomain>?) {
        if (!listOfRoomSelections.isNullOrEmpty()) {
            val modifiedListOfRoomSelections = mutableListOf<RoomSelectionDomain>()
            listOfRoomSelections.forEach { roomSelectionDomain ->
                val findPackageCodeWithoutHsatwn = roomSelectionDomain.packagesSelection.filterNot { it.id == HAS_TWIN_PACKAGE_CODE }
                val modifiedRoomSelectionDomain: RoomSelectionDomain = if (findPackageCodeWithoutHsatwn.isNotEmpty()) {
                    roomSelectionDomain.copy(packagesSelection = findPackageCodeWithoutHsatwn)
                } else {
                    roomSelectionDomain
                }
                modifiedListOfRoomSelections += modifiedRoomSelectionDomain
            }

            simplePersistenceManager.storeOriginalRoomSelection(modifiedListOfRoomSelections)
        }
    }

    fun saveUpdatedRoomSelectionAfterRemoveInSP(listOfRoomSelections: List<RoomSelectionDomain>?) {
        if (!listOfRoomSelections.isNullOrEmpty()) {
            simplePersistenceManager.storeUpdatedRoomSelectionAfterRemoveRoom(listOfRoomSelections)
        }
    }

    fun saveUpsellAvailableItems(
        upsellItemsAvailable: List<UpsellAvailable>) {
        val listOfItemsAvailable = mutableListOf<UpsellItemAvailableEntity>()
        if (upsellItemsAvailable.isNotEmpty()) {
            upsellItemsAvailable.forEach {
                listOfItemsAvailable += it.toAmendedUpsellItemsAvailableEntity()
            }
            upsellItemAvailableDao.updateUpsellItemsAvailable(listOfItemsAvailable)
        }
    }

    override fun getUpsellsAvailable(): Observable<List<UpsellAvailable>> {
        return upsellItemAvailableDao.getUpsellItemsAvailable().map {
            it.toUpsellsAvailable()
        }
    }

    override fun clearReservation() {
        simplePersistenceManager.clearSavedReservation()
    }

    companion object {
        const val INVALID_ERROR = "Invalid dates or upsell data"
    }
}