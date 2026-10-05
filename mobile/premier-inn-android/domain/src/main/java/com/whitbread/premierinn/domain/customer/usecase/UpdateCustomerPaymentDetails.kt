package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UpdateCustomerPaymentDetails @Inject constructor(private val authenticationRepository: AuthenticationRepository,
                                                       private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce,
                                                       private val customerRepository: CustomerRepository) {
    operator fun invoke(params: Params): Completable {
        return authenticationRepository.getIdToken()
                .flatMapCompletable { token ->
                    customerRepository.updatePaymentDetails(token, customerRepository.getLoggedInCustomerEmail(), params.card, params.address)
                }
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .subscribeOn(Schedulers.io())
    }

    fun updateAndSaveCustomerPaymentDetailsToSharedPreferences(paymentCard: PaymentCard) {
        val updatedCustomer = customerRepository.getCustomerFromSharedPref().copy(paymentCard = paymentCard)
        customerRepository.saveCustomerToSharedPref(updatedCustomer)
    }

    data class Params(val card: PaymentCard, val address: Address?)
}