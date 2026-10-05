package com.whitbread.premierinn.summary

import android.os.Parcelable
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethodsDetailsInput
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryState(
    val isLoading: Boolean = false,
    val basketReference: String = EMPTY_STRING_DOMAIN,
    val error: SummaryError? = null,
    val roomList: List<SummaryRoomItem> = emptyList(),
    val totalStayPrice: String = EMPTY_STRING_DOMAIN,
    val paymentMethods: ParcelablePaymentMethodsDetailsInput? = null
) : Parcelable
