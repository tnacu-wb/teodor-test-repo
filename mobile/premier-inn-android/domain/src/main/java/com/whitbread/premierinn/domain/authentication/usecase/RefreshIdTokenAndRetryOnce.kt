package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import javax.inject.Inject

class RefreshIdTokenAndRetryOnce @Inject constructor(
    private val authenticateCustomer: AuthenticateCustomer,
    private val logoutCustomer: LogoutCustomer,
    private val customerRepository: CustomerRepository,
    private val businessCustomerRepository: BusinessCustomerRepository
) {
    operator fun invoke(error: Throwable): Boolean {
        if (error !is UnAuthorizedCustomerError || !isCustomerLoggedInSessionExpired(error)) {
            return false
        }

        return try {
            if (businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()) {
                authenticateBusinessCustomerSilently()
            } else {
                authenticateCustomerSilently()
            }
            true
        } catch (e: Throwable) {
            if (e is AuthenticationError && e.errorType != AuthenticationError.Type.NETWORK) {
                logoutUser()
            }
            false
        }
    }

    private fun authenticateCustomerSilently() {
        authenticateCustomer(
            AuthenticateCustomer.Params(
                customerRepository.getLoggedInCustomerEmail(),
                customerRepository.getLoggedInCustomerPassword(),
                UserType.LEISURE
            )
        )
    }

    private fun authenticateBusinessCustomerSilently() {
        authenticateCustomer(
            AuthenticateCustomer.Params(
                businessCustomerRepository.getLoggedInBusinessEmail(),
                businessCustomerRepository.getLoggedInBusinessPassword(),
                UserType.BUSINESS
            )
        )
    }

    private fun isCustomerLoggedInSessionExpired(e: Throwable): Boolean {
        val leisureLoggedIn = customerRepository.getLoggedInCustomerEmail().isNotEmpty() &&
                customerRepository.getLoggedInCustomerPassword().isNotEmpty()
        val businessLoggedIn = businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty() &&
                businessCustomerRepository.getLoggedInBusinessPassword().isNotEmpty()

        val result = e is UnAuthorizedCustomerError && leisureLoggedIn.xor(businessLoggedIn)
        return result
    }

    private fun logoutUser(): Boolean = try {
        logoutCustomer()
        true
    } catch (e: Exception) {
        false
    }
}