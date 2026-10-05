package com.businessbooker.paymentmethods

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.mapper.toCardName
import com.whitbread.premierinn.common.utils.CardTypeEnum
import com.whitbread.premierinn.databinding.ViewBusinessBookingPaymentCardBinding
import com.whitbread.premierinn.domain.customer.entity.PaymentCard

class BusinessBookerPaymentCardView(
    context: Context,
    private val personalPaymentCard: PaymentCard?,
    private val businessPaymentCard: com.whitbread.premierinn.businessbooker.domain.company.PaymentCard?,
    private val cardLabel: String,
    private val notificationText: String,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {

    private val businessBookingPaymentCardBinding = ViewBusinessBookingPaymentCardBinding.inflate(LayoutInflater.from(context))
    val view: View = businessBookingPaymentCardBinding.root

    init {
        init()
    }

    private fun init() {
        personalPaymentCard?.let { personalPaymentCard ->
            setPaymentCardType(personalPaymentCard.cardType.toCardName())
            setPaymentCardNumber(personalPaymentCard.number)
            setPaymentCardName(personalPaymentCard.holdersFullName)
            setPaymentCardExpiryDate(personalPaymentCard.expiryDate)
            setPaymentCardLabel(cardLabel)
            when (personalPaymentCard.cardType) {
                CardTypeEnum.AT.name -> {
                    showPaymentCardNotificationForPiba(context.getString(R.string.business_booker_piba_uk_notification))
                }
                CardTypeEnum.BD.name -> {
                    showPaymentCardNotificationForPiba(context.getString(R.string.business_booker_piba_euro_notification))
                }
                else -> {
                    showPaymentCardNotification(notificationText)
                }
            }
        }
        businessPaymentCard?.let {businessPaymentCard ->
            setPaymentCardType(businessPaymentCard.cardLabel)
            setPaymentCardNumber(businessPaymentCard.cardNumber)
            setPaymentCardName(businessPaymentCard.nameOnCard)
            setPaymentCardExpiryDate(businessPaymentCard.expiryDate)
            setPaymentCardLabel(cardLabel)
            when (businessPaymentCard.cardType) {
                CardTypeEnum.AT.name -> {
                    showPaymentCardNotificationForPiba(context.getString(R.string.business_booker_piba_uk_notification))
                }
                CardTypeEnum.BD.name -> {
                    showPaymentCardNotificationForPiba(context.getString(R.string.business_booker_piba_euro_notification))
                }
                else -> {
                    showPaymentCardNotification(notificationText)
                }
            }
        }
    }

    private fun setPaymentCardType(type: String) {
        businessBookingPaymentCardBinding.paymentCardType.text = type
    }

    private fun setPaymentCardNumber(number: String) {
        businessBookingPaymentCardBinding.paymentCardNumber.text = context.getString(R.string.card_ending_in, number.takeLast(4))
    }

    private fun setPaymentCardName(name: String) {
        businessBookingPaymentCardBinding.paymentCardName.text = name
    }

    private fun setPaymentCardExpiryDate(expiryDate: String) {
        businessBookingPaymentCardBinding.paymentCardExpiryDate.text = context.getString(
            R.string.card_expires,
            "${expiryDate.substring(0, 2)}/${expiryDate.takeLast(2)}"
        )
    }

    private fun setPaymentCardLabel(label: String) {
        businessBookingPaymentCardBinding.paymentCardLabel.text = label
    }

    private fun showPaymentCardNotification(text: String) {
        businessBookingPaymentCardBinding.paymentCardNotification.isVisible = text.isNotEmpty()
        businessBookingPaymentCardBinding.paymentCardNotification.setText(text)
    }

    private fun showPaymentCardNotificationForPiba(text: String) {
        if (text.isNotEmpty()) {
            businessBookingPaymentCardBinding.paymentCardNotification.apply {
                isVisible = true
                setText(text)
                setIconResource(R.drawable.ic_info)
                background = ContextCompat.getDrawable(
                    context,
                    R.drawable.information_notification_background
                )
                setIconColorFilter(R.color.blue)
            }
        } else {
            businessBookingPaymentCardBinding.paymentCardNotification.isVisible = false
        }
    }
}