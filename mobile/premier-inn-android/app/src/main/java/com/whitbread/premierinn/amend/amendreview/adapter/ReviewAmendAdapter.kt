package com.whitbread.premierinn.amend.amendreview.adapter

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.domain.reservation.entity.Reservation

class ReviewAmendAdapter(
        onCancelChangesClicked: () -> Unit,
        selectedPaymentTimingChoice: (paymentTimingChoice: PaymentTimingChoice) -> Unit,
        onConfirmChangesClicked: (amendedTotalCost: AmendedTotal, amendedReservation: Reservation, selectedPaymentTimingChoice: PaymentTimingChoice) -> Unit,
        isPayNowFlow: Boolean,
        isEciLcoBooking: Boolean,
        disposable: AutoCompositeDisposable
) : ListDelegationAdapter<List<Diffable>>(
        ReviewAmendEciLcoInfoBoxDelegate(isEciLcoBooking),
        ReviewAmendTitleDelegate(),
        ReviewChangeTitleDelegate(),
        ReviewAmendSeparatorDelegate(),
        ReviewAmendTextDelegate(),
        ReviewBalanceTextDelegate(),
        ReviewAmendConfirmDelegate(isPayNowFlow, onCancelChangesClicked, selectedPaymentTimingChoice, onConfirmChangesClicked, disposable)
)