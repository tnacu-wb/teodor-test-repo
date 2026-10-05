package com.whitbread.premierinn.domain.customer.usecase


import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateCustomerMarketingPreferences @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val authenticationRepository: AuthenticationRepository
) {

    fun execute(customer: Customer, marketingOptIn: Boolean, deviceLanguage: String): Completable {
        return authenticationRepository.getIdToken()
            .flatMapCompletable { idToken ->
                customerRepository.createCustomerMarketingPrefs(
                    idToken,
                    customer,
                    marketingOptIn,
                    deviceLanguage
                )
            }
            .onErrorComplete()
            .subscribeOn(Schedulers.io())
    }
}