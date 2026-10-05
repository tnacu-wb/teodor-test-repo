package com.whitbread.premierinn.amend.amendreview.uimodel

import android.text.Spanned
import com.whitbread.premierinn.amend.amendreview.adapter.Diffable
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.reservation.entity.Reservation

class ReviewAmendConfirmItem(val termsAndConditionText: Spanned? = null,
                             val outstandingAmount: AmendedTotal? = null,
                             val amendedReservation: Reservation? = null) : Diffable {
    override val identifier: String
        get() = this.javaClass.name

    fun mapToConfirmItem(termsAndConditionText: Spanned, outstandingAmount: AmendedTotal?,
                         amendedReservation: Reservation?): ReviewAmendConfirmItem {
        return ReviewAmendConfirmItem(
                termsAndConditionText = termsAndConditionText,
                outstandingAmount = outstandingAmount,
                amendedReservation = amendedReservation,
        )
    }
}