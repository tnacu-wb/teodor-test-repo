package com.whitbread.premierinn.domain.payment.entity

import com.whitbread.premierinn.domain.common.Booker
import com.whitbread.premierinn.domain.common.Breakfast
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.GuestDetailsPayment
import com.whitbread.premierinn.domain.common.PaymentCardEntity
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.customer.entity.PaymentCard


data class ThreeCpPaymentStorageDetailsEntity(
        val booker: Booker,
        val guests: List<GuestDetailsPayment>,
        val paymentCard: PaymentCardEntity,
        val breakfast: List<Breakfast>,
        val donation: PriceDomain,
        val business: Boolean,
        val paymentId: String
)
