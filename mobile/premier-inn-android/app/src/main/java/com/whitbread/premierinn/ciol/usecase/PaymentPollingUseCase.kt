package com.whitbread.premierinn.ciol.usecase

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.whitbread.premierinn.data.remote.graphql.ConfirmationSpinnerMessages
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.reviewBooking.usecase.GraphQLReviewBookingUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetLongResource
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.result.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

typealias PaymentPollingResult = Result<Any, DomainError.PaymentPollingError>

private val TAG = PaymentPollingUseCase::class.simpleName

class PaymentPollingUseCase @Inject constructor(
    private val getLongResource: GetLongResource,
    private val getStringResource: GetStringResource,
    private val graphQLReviewBookingUseCase: GraphQLReviewBookingUseCase,
) {

    private lateinit var confirmationSpinnerMessages: ConfirmationSpinnerMessages

    @SuppressLint("CheckResult")
    fun invoke(
        basketReference: String,
        onResult: ((result: PaymentPollingResult) -> Unit)
    ) {
        val initialPollStartTime: Long =
            getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_DELAY)
        val pollInterval: Long =
            getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_INTERVAL)
        confirmationSpinnerMessages = Gson()
            .fromJson(
                getStringResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_MESSAGES_CONFIG),
                ConfirmationSpinnerMessages::class.java
            )
        val maxAttempts = confirmationSpinnerMessages.getMaxAttempts()
        var attemptsMade = 0
        var timerStart = 0L
        graphQLReviewBookingUseCase.getBasketStatusRevisedPayments(basketReference).toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delaySubscription(initialPollStartTime, TimeUnit.SECONDS)
            .repeatWhen { it.delay(pollInterval, TimeUnit.SECONDS) }
            .takeUntil { status ->
                Log.d(TAG, "takeUntil status: ${status.basketStatus.name}, attemptsMade: $attemptsMade")
                if (attemptsMade >= maxAttempts) {
                    true
                } else {
                    when (status.basketStatus.name) {
                        "PAY_PENDING", "PROCESSING" -> false
                        "PRE_CHECKED_IN", "FAILED", "OPEN", "CIOL_FAILED" -> true
                        else -> false
                    }
                }
            }.doOnNext {
                attemptsMade++
                val message = if (attemptsMade == 1) {
                    timerStart = System.currentTimeMillis()
                    confirmationSpinnerMessages.messages[1].message

                } else {
                    val elapsedTime: Long = System.currentTimeMillis() - timerStart
                    if (elapsedTime <= confirmationSpinnerMessages.messages[1].seconds * 1000) {
                        confirmationSpinnerMessages.messages[1].message
                    } else {
                        confirmationSpinnerMessages.messages[2].message
                    }
                }
                Log.d(TAG, "Polling message: $message")
            }.retry(maxAttempts)
            .lastElement().toObservable()
            .subscribe({ success ->
                Log.i(TAG, "Success: ${success.basketStatus.name}")
                when (success.basketStatus.name) {
                    "PAY_PENDING", "PROCESSING" -> onResult(Result.Error(DomainError.PaymentPollingError.PaymentPendingError))
                    "PRE_CHECKED_IN" -> onResult(Result.Success(Unit))
                    "FAILED", "CIOL_FAILED" -> onResult(Result.Error(DomainError.PaymentPollingError.PaymentFailedError))
                    "OPEN" -> onResult(Result.Error(DomainError.PaymentPollingError.GenericError))
                    else -> onResult(Result.Error(DomainError.PaymentPollingError.GenericError))
                }
            }, { error ->
                Log.e(TAG, "Polling error occurred: ${error.message}")
                onResult(Result.Error(DomainError.PaymentPollingError.PaymentFailedError))
            })
    }
}