package com.whitbread.premierinn.domain.customer.usecase

import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateCustomer @Inject constructor(private val customerRepository: CustomerRepository) {
    operator fun invoke(params: Params): Completable {
        return customerRepository.createCustomer(
                customer = params.customer,
                password = params.password,
                deviceLanguage = params.deviceLanguage)
                .subscribeOn(Schedulers.io())
    }

    data class Params(val customer: Customer, val password: String, val deviceLanguage: String)
}