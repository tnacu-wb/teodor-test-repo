package com.whitbread.premierinn.domain.payment.repository

import com.whitbread.premierinn.domain.payment.entity.ThreeCPBookingConfirmationResponseEntity
import com.whitbread.premierinn.domain.payment.entity.ThreeCPaymentServiceResponseEntity
import com.whitbread.premierinn.domain.payment.entity.ThreeCpPaymentDetailsEntity
import com.whitbread.premierinn.domain.payment.entity.ThreeCpPaymentStorageDetailsEntity
import io.reactivex.Observable
import io.reactivex.Single

interface ThreeCpRepository {

    fun makeThreeCPayment(threeCpPaymentDetailsEntity: ThreeCpPaymentDetailsEntity)
            : Single<ThreeCPaymentServiceResponseEntity>

    fun storePaymentsData(threeCpPaymentStorageDetailsEntity: ThreeCpPaymentStorageDetailsEntity, sessionId: String)
            : Single<Boolean>

    fun getPaymentStatus(bookingId: String, maxAttempts:Long, pollingInterval: Long, initialWait: Long)
            :  Observable<ThreeCPBookingConfirmationResponseEntity>
}