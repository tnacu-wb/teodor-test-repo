package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RemoveRoomRequestBody
import io.reactivex.Observable
import javax.inject.Inject

class RemoveRoomUseCase @Inject constructor(
    private val graphQLAmendUseCase: GraphQLAmendUseCase
) {
    fun invoke(removeRoomRequestBody: RemoveRoomRequestBody)
    : Observable<TempBookingRefDomain> {
        return graphQLAmendUseCase.removeRoom(removeRoomRequestBody)
            .toObservable()
    }
}
