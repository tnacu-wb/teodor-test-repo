package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URLEncoder
import javax.inject.Inject

/**
 * Import Booking
 *
 * Throws ImportBookingException if booking Exists
 * Throws PastBookingException if booking departureDate has passed.
 * Throws BusinessBookingImportError if leisure customer attempts to import business booking.
 */
private const val DATE_TIME_WITH_OFFSET: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
class ImportBooking @Inject constructor(private val repository: BookingRepository,
                                        private val graphQLFindBookingUseCase: GraphQLFindBookingUseCase,
                                        private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase
) {

    fun execute(
        bookingReference: String,
        surname: String,
        arrivalDate: LocalDate,
        language: String,
        country: String
    ): Single<FindBookingDomain> {
        return graphQLFindBookingUseCase.findBooking(
            FindBookingRequestBody(
                bookingReference,
                surname,
                arrivalDate.toString(),
                language,
                country,
                BookingChannelDetails(
                    Channel.PI.name, SUB_CHANNEL,
                    language)
            )
        )
            .flatMap { findBooking ->
                (
                        graphQLBookingDetailsUseCase
                            .bookingConfirmationAndManageBookingWithHotelInfo(
                                uuidBasketReference = findBooking.uuidBasketReference,
                                country = country,
                                language = language,
                                bookingChannel = Channel.PI.name,
                                cancelInformationRequestBody = CancelInformationRequestBody(
                                    basketReference = findBooking.uuidBasketReference,
                                    hotelId = findBooking.hotelId,
                                    userDateTime = OffsetDateTime.now().format(
                                        DateTimeFormatter.ofPattern(DATE_TIME_WITH_OFFSET)
                                    ),
                                    token = URLEncoder.encode(findBooking.token, "UTF-8"),
                                    bookingChannel = BookingChannelDetails(
                                        Channel.PI.name,
                                        SUB_CHANNEL,
                                        language.lowercase()
                                    )
                                )
                            )
                        )
                    .map { booking ->
                        val today = LocalDate.now()
                        val status = when {
                            booking.isCancelled -> BOOKING_STATUS_CANCELLED
                            booking.arrivalDate.isAfter(today) || booking.arrivalDate.isEqual(today) -> BOOKING_STATUS_FUTURE
                            else -> BOOKING_STATUS_PAST
                        }
                        booking.copy(
                            bookingStatus = status
                        )
                    }
                    .doOnNext { bk ->
                        if (bk.numberOfRooms == -1) {
                            throw ImportBookingException(KEY_BOOKING_NOT_FOUND)
                        } else if (!repository.bookingExist(bk.bookingReference)) {
                            repository.store(bk)
                        } else {
                            throw ImportBookingException(KEY_BOOKING_EXISTS)
                        }
                    }.ignoreElements()
                    .andThen(Single.just(findBooking))
            }
    }
}

const val KEY_BOOKING_EXISTS = "BOOKING_EXISTS"
const val KEY_PAST_BOOKING = "PAST_BOOKING"
const val KEY_BOOKING_NOT_FOUND = "BOOKING_NOT_FOUND"
const val BOOKING_STATUS_CANCELLED = "CANCELLED"
const val BOOKING_STATUS_FUTURE = "FUTURE"
const val BOOKING_STATUS_PAST = "PAST"

class ImportBookingException(val key: String) : Throwable()
object BusinessBookingImportError : Exception()