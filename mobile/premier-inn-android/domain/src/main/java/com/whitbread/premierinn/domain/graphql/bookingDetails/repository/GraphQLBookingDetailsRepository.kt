package com.whitbread.premierinn.domain.graphql.bookingDetails.repository

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ResendInvoiceDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ResendInvoiceRequestBody
import io.reactivex.Single

interface GraphQLBookingDetailsRepository {

    fun bookingConfirmationAndManageBooking(basketReference: String, country: String, language: String, bookingChannel: String, hotelName: String? = EMPTY_STRING_DOMAIN,
                                            cancelInformationRequestBody: CancelInformationRequestBody, token: String?): Single<Booking>

    fun bookingConfirmationForFindBooking(uuidBasketReference: String, country: String, language: String, hotelName: String? = EMPTY_STRING_DOMAIN): Single<BookingConfirmation>

    fun getRoomKeyInstructions(categoryLabelsRequestBody: CategoryLabelsRequestBody): Single<RoomKeyInstructionsDomain>

    fun cancelReservation(cancelReservationRequestBody: CancelReservationRequestBody, reference: String): Single<CancelReservationDomain>

    fun packages(packagesRequestBody: HotelPackagesRequestBody): Single<DataPackagesDomain>

    fun resendInvoiceEmail(body: ResendInvoiceRequestBody): Single<ResendInvoiceDomain>
}
