package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import io.reactivex.Completable
import javax.inject.Inject

class AuthenticateCustomer @Inject constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val customerRepository: CustomerRepository,
        private val businessRepository: BusinessCustomerRepository) {
    operator fun invoke(params: Params): Completable {
        return authenticationRepository.authenticate(params.username, params.password, params.userType)
            .andThen(Completable.fromCallable {
                if (params.userType == UserType.LEISURE) {
                    customerRepository.saveLoggedInCustomerEmail(params.username)
                    customerRepository.saveLoggedInCustomerPassword(params.password)
                } else {
                    businessRepository.saveLoggedInBusinessEmail(params.username)
                    businessRepository.saveLoggedInBusinessPassword(params.password)
                }
            })
    }

    data class Params(val username: String, val password: String, val userType: UserType)
}