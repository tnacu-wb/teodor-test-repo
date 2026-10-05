package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.booking.NoLinkedAccountBookingsFound
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.myBookings.repository.GraphQLMyBookingsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
/**
 *
 */

const val PAST_BOOKING_STATUS = "PAST"
class SyncCustomerFutureBookings @Inject constructor(private val repository: BookingRepository,
                                                     private val myBookingRepository: GraphQLMyBookingsRepository,
                                                     private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                     private val authenticationRepository: AuthenticationRepository,
                                                     private val businessCustomerRepository: BusinessCustomerRepository,
                                                     private val isFeatureOn: IsFeatureOn) {

    fun execute(bookingHistoryRequestBody: BookingHistoryRequestBody): Completable {
        return if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA) &&
            businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()) {
            syncCustomerBooking(bookingHistoryRequestBody, true)
        } else {
            syncCustomerBooking(bookingHistoryRequestBody, false)
        }
    }

    private fun syncCustomerBooking(bookingHistoryRequestBody: BookingHistoryRequestBody, business: Boolean): Completable {
        return authenticationRepository.getIdToken()
            .flatMapCompletable {
                fetchLinkedAccountBookingsBookingHistory(it, bookingHistoryRequestBody, business)
            }
            .retryWhen(getFreshIdTokenAndRetryOnce())
            .subscribeOn(Schedulers.io())
    }

    private fun fetchLinkedAccountBookingsBookingHistory(token: String, bookingHistoryRequestBody: BookingHistoryRequestBody,
                                                         business: Boolean): Completable {
        return myBookingRepository.getBookingHistory(bookingHistoryRequestBody, token)
            .flatMapCompletable { bookings ->
                if (bookings.isEmpty()) {
                    Completable.error(NoLinkedAccountBookingsFound)
                } else {
                    val nextBookings = bookings.filter { booking -> booking.bookingStatus != PAST_BOOKING_STATUS}
                    repository.storeBookingStatus(nextBookings)

                    val hotelCountryBookings = bookings.filter { booking -> booking.hotelCountry != null}
                    repository.storeBookingHotelCountry(hotelCountryBookings)

                    repository.updateLinkedAccountBookings(bookings, business)
                }
            }
    }
}