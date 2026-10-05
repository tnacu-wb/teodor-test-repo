package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import java.net.URLEncoder
import javax.inject.Inject

const val ONE_HOUR_SECONDS = 60 * 60

class UpdateStoredBookingData @Inject constructor(private val repository: BookingRepository,
                                                  private val graphQLFindBookingUseCase: GraphQLFindBookingUseCase,
                                                  private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase) {
    fun execute(language: String, country: String, userDateTime: String): Completable {
        return repository.getLocalStaleBookings(ONE_HOUR_SECONDS)
                .toObservable()
                .flatMapCompletable { booking ->
                    val channel = Channel.BB.name.takeIf { booking.isBusinessBooking } ?: Channel.PI.name
                    getBooking(
                        booking.bookingReference,
                        booking.hotelCode,
                        booking.leadGuestSurname,
                        booking.arrivalDate,
                        userDateTime,
                        language, country, channel)
                }
    }

    private fun getBooking(
        bookingReference: String,
        hotelCode: String,
        surname: String,
        arrivalDate: LocalDate,
        userDateTime: String,
        language: String,
        country: String,
        channel: String
    ): Completable {
        return graphQLFindBookingUseCase.findBooking(
            FindBookingRequestBody(
                bookingReference, surname, arrivalDate.toString(), language, country,
                BookingChannelDetails(channel, SUB_CHANNEL, language.lowercase())
            )
        )
            .flatMapCompletable { findBooking ->
                graphQLBookingDetailsUseCase
                    .bookingConfirmationAndManageBookingWithHotelInfo(
                        uuidBasketReference = findBooking.uuidBasketReference,
                        country = country,
                        language = language,
                        bookingChannel = channel,
                        cancelInformationRequestBody = CancelInformationRequestBody(
                            findBooking.uuidBasketReference, hotelCode, userDateTime,
                            URLEncoder.encode(findBooking.token, "UTF-8"),
                            BookingChannelDetails(channel, SUB_CHANNEL, language.lowercase())
                        )
                    )
                    .doOnNext {
                        repository.store(it)
                    }
                    .ignoreElements()
            }
    }
}