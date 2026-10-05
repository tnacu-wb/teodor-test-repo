package com.whitbread.premierinn.domain.error

import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase

interface DomainError : Error {

    sealed class PaymentError : DomainError {
        abstract val message: String?

        data class CouldNotInitiatePaymentError(override val message: String?) : PaymentError()
        data class GenericError(override val message: String?) : PaymentError()
    }

    sealed class PaymentPollingError : DomainError {
        object PaymentPendingError : PaymentPollingError()
        object PaymentFailedError : PaymentPollingError()
        object GenericError : PaymentPollingError()
    }

    sealed class BillingAddressValidationError : DomainError {
        data class InvalidFieldsError(
            val invalidFields: List<ValidateBillingAddressUseCase.InvalidBillingAddressFields>
        ) : BillingAddressValidationError()
        object CountriesRetrievalError : BillingAddressValidationError()
    }

    class CountriesError : DomainError

    class SpecialOccasionsError : DomainError

    class GuestRegCardValidationError(val invalidFields: List<ValidateRegCardGuestUseCase.InvalidField>) : DomainError

    class GenericError : DomainError
}