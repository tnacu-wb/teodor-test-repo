package com.whitbread.premierinn.amend.amendreview.adapter

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendConfirmItem
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.view.ToggleButtonView
import com.whitbread.premierinn.databinding.ViewReviewAmendConfirmBinding
import com.whitbread.premierinn.domain.reservation.entity.Reservation

class ReviewAmendConfirmDelegate(private val isPayNowFlow: Boolean,
                                 private val onCancelChangesClick: () -> Unit,
                                 private val selectedPaymentTimingChoice: (paymentTimingChoice: PaymentTimingChoice) -> Unit,
                                 private val onConfirmChangesClicked: (amendedTotalCost: AmendedTotal,
                                                                       amendedReservation: Reservation,
                                                                       selectedPaymentTimingChoice: PaymentTimingChoice) -> Unit,
                                 private val disposable: AutoCompositeDisposable)
    : AbsListItemAdapterDelegate<ReviewAmendConfirmItem, Diffable, ReviewAmendConfirmDelegate.ViewHolder>() {

    private lateinit var binding : ViewReviewAmendConfirmBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewReviewAmendConfirmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
                binding,
                onCancelChangesClick,
                selectedPaymentTimingChoice,
                onConfirmChangesClicked,
                isPayNowFlow,
                disposable)
    }

    override fun isForViewType(
            item: Diffable,
            items: MutableList<Diffable>,
            position: Int
    ): Boolean {
        return items[position] is ReviewAmendConfirmItem
    }

    override fun onBindViewHolder(
            item: ReviewAmendConfirmItem,
            holder: ViewHolder,
            payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewReviewAmendConfirmBinding,
                     private val onCancelChangesClick: () -> Unit,
                     private val selectedPaymentTimingChoice: (paymentTimingChoice: PaymentTimingChoice) -> Unit,
                     private val onConfirmChangesClicked: (amendedTotalCost: AmendedTotal,
                                                           amendedReservation: Reservation,
                                                           selectedPaymentTimingChoice: PaymentTimingChoice) -> Unit,
                     private val isPayNowFlow: Boolean,
                     private val disposable: AutoCompositeDisposable
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewAmendConfirmItem) {
            binding.termsAndConditionText.text = item.termsAndConditionText
            binding.termsAndConditionText.movementMethod = LinkMovementMethod.getInstance()

            var selectedPaymentTimingChoice: PaymentTimingChoice = PaymentTimingChoice.PAY_LATER
            if (isPayNowFlow) {
                binding.amendPaymentOptions.visibility = View.VISIBLE
                disposable.add(
                    binding.amendPayOnArivalPayNowToggle.state
                        .subscribe { state ->
                            when(state) {
                                ToggleButtonView.State.LEFT -> {
                                    selectedPaymentTimingChoice = PaymentTimingChoice.PAY_LATER
                                    binding.amendPaymentTimingBanner.visibility = View.INVISIBLE
                                    selectedPaymentTimingChoice(PaymentTimingChoice.PAY_LATER)
                                }
                                ToggleButtonView.State.RIGHT -> {
                                    selectedPaymentTimingChoice = PaymentTimingChoice.PAY_NOW
                                    binding.amendPaymentTimingBanner.visibility = View.VISIBLE
                                    binding.amendPaymentTimingBanner.setText(itemView.context.getString(R.string.review_booking_pay_now_info_message))
                                    selectedPaymentTimingChoice(PaymentTimingChoice.PAY_NOW)
                                }
                                else -> {
                                    throw IllegalStateException("Illegal button state: $state")
                                }
                            }
                        })
            }

            binding.reviewBookingDiscardButton.setOnClickListener { onCancelChangesClick.invoke() }
            binding.reviewAmendConfirmButton.setOnClickListener {
                if (item.outstandingAmount != null && item.amendedReservation != null) {
                    onConfirmChangesClicked(item.outstandingAmount, item.amendedReservation, selectedPaymentTimingChoice)
                }
            }
        }
    }
}