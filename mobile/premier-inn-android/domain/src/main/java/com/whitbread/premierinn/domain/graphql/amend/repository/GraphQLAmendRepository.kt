package com.whitbread.premierinn.domain.graphql.amend.repository

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.CopyBookingDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.UpdateReservationPackagesResponseDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ChangeBookingDatesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AddNewRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSummaryRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ChangeBookingDatesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CopyBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.result.Result
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

typealias GetPackagesAndAncillariesCloseoutResult = Result<PackagesAndAncillaryCloseoutDomain, DataError.Network>

interface GraphQLAmendRepository {

    fun copyBooking(copyBookingRequestBody: CopyBookingRequestBody): Single<CopyBookingDomain>

    fun removeRoom(removeRoomRequestBody: RemoveRoomRequestBody): Single<TempBookingRefDomain>

    fun amendEditRoom(amendEditRoomRequestBody: AmendEditRoomRequestBody, token: String?): Single<TempBookingRefDomain>

    fun addNewRoom(addNewRoomRequestBody: AddNewRoomRequestBody): Single<TempBookingRefDomain>

    fun confirmAmendLogic(confirmAmendLogicRequestBody: ConfirmAmendLogicRequestBody): Single<ConfirmAmendLogicDomain>

    fun packagesAndAncillariesCloseoutInfo(packagesRequestBody: HotelPackagesRequestBody): Single<PackagesAndAncillaryCloseoutDomain>

    suspend fun getPackagesAndAncillariesCloseoutInfo(input: HotelPackagesRequestBody): Flow<GetPackagesAndAncillariesCloseoutResult>

    fun changeBookingDates(changeBookingDatesRequestBody: ChangeBookingDatesRequestBody): Single<ChangeBookingDatesDomain>

    fun updateReservationPackagesByReservation(updateReservationWithAncillariesRequestBody: UpdateReservationWithAncillariesRequestBody): Single<UpdateReservationPackagesResponseDomain>

    fun amendSummary(amendSummaryRequestBody: AmendSummaryRequestBody): Single<AmendSummaryDomain>

}