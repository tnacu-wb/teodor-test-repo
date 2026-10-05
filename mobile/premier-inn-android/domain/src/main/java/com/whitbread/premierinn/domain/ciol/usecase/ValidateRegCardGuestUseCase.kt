package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetailsRegCard
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.isAdult
import com.whitbread.premierinn.domain.utils.isValidBirthDate
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

const val NAME_MIN_CHARS = 2
const val FIRST_NAME_MAX_CHARS = 20
const val LAST_NAME_MAX_CHARS = 30
const val PASSPORT_NUMBER_MIN_CHARS = 2
const val PASSPORT_NUMBER_MAX_CHARS = 20
const val HOME_ADDRESS_MAX_CHARS = 35
const val CITY_NAME_MAX_CHARS = 35
const val DATE_OF_BIRTH_DELIMITER = "-"
const val CITY_NAME_REGEX = "^[\\p{L}0-9\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC\u00DF/'.,\\s-]*$"

typealias ValidateRegCardGuestResponse = Result<Unit, DomainError.GuestRegCardValidationError>

class ValidateRegCardGuestUseCase @Inject constructor (
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase
) {

    operator fun invoke(
        stayingGuest: StayingGuestDetailsRegCard, isLeadGuest: Boolean
    ) = flow {
        validateBaseFields(stayingGuest, isLeadGuest).run {
            if (isLeadGuest) {
                this.toMutableList().apply { addAll(validateExtendedFields(stayingGuest)) }
            } else {
                this
            }
        }.let { invalidFields ->
            if (invalidFields.isEmpty()) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(DomainError.GuestRegCardValidationError(invalidFields)))
            }
        }
    }

    private fun validateExtendedFields(stayingGuest: StayingGuestDetailsRegCard) : List<InvalidField> {
        val invalidFields = mutableListOf<InvalidField>()

        stayingGuest.address.run {
            getHomeAddressValidityError()?.let { homeAddressError ->
                invalidFields.add(homeAddressError)
            }

            getPostcodeValidityError()?.let { postcodeValidityError  ->
                invalidFields.add(postcodeValidityError)
            }

            getCityValidityError()?.let { cityValidityError ->
                invalidFields.add(cityValidityError)
            }

            getCountryValidityError()?.let { countryError ->
                invalidFields.add(countryError)
            }
        }

        return invalidFields
    }

    private fun validateBaseFields(stayingGuest: StayingGuestDetailsRegCard, isLeadGuest: Boolean): List<InvalidField> {
        val invalidFields = mutableListOf<InvalidField>()

        getFirstNameValidityError(stayingGuest.firstName)?.let { firstNameError ->
            invalidFields.add(firstNameError)
        }

        getLastNameValidityError(stayingGuest.lastName)?.let { lastNameError ->
            invalidFields.add(lastNameError)
        }

        if (stayingGuest.additionalDetails.nationality.isNullOrEmpty()) {
            invalidFields.add(InvalidField.NationalityMissing)
        } else {
            getPassportNumberValidityError(
                stayingGuest.additionalDetails.nationality,
                stayingGuest.additionalDetails.passportNumber
            )?.let { passportNumberError ->
                invalidFields.add(passportNumberError)
            }
        }

        getDateOfBirthValidityError(stayingGuest.additionalDetails.dob, isLeadGuest)?.let { dateOfBirthError ->
            invalidFields.add(dateOfBirthError)
        }

        return invalidFields
    }

    private fun getFirstNameValidityError(firstName: String): InvalidField? = when {
        firstName.isEmpty() -> InvalidField.FirstNameMissing
        firstName.length < NAME_MIN_CHARS -> InvalidField.FirstNameTooShort
        firstName.length > FIRST_NAME_MAX_CHARS -> InvalidField.FirstNameTooLong
        else -> null
    }

    private fun getLastNameValidityError(lastName: String): InvalidField? = when {
        lastName.isEmpty() -> InvalidField.LastNameMissing
        lastName.length < NAME_MIN_CHARS -> InvalidField.LastNameTooShort
        lastName.length > LAST_NAME_MAX_CHARS -> InvalidField.LastNameTooLong
        else -> null
    }

    private fun getPassportNumberValidityError(nationality: String, passportNumber: String?): InvalidField? {
        if (getIsoCodeFromCountryNameUseCase(nationality) == GERMANY_ISO_CODE) {
            return null
        }

        return when {
            passportNumber.isNullOrEmpty() -> InvalidField.PassportNumberMissing
            passportNumber.length < PASSPORT_NUMBER_MIN_CHARS -> InvalidField.PassportNumberTooShort
            passportNumber.length > PASSPORT_NUMBER_MAX_CHARS -> InvalidField.PassportNumberTooLong
            else -> null
        }
    }

    private fun getDateOfBirthValidityError(dateOfBirth: String?, isLeadGuest: Boolean): InvalidField? {
        if (dateOfBirth.isNullOrEmpty()) {
            return InvalidField.DateOfBirthMissing
        }

        return if (isLeadGuest) {
            if (isValidBirthDate(dateOfBirth)) {
                if (isAdult(dateOfBirth)) null else InvalidField.GuestNotAdult
            } else InvalidField.BirthDateInvalid
        } else {
            if (isValidBirthDate(dateOfBirth)) null else InvalidField.BirthDateInvalid
        }
    }

    private fun StayingGuestAddress?.getHomeAddressValidityError(): InvalidField? = when {
        this?.addressLine1 == null || addressLine1.isEmpty() -> InvalidField.HomeAddressMissing
        addressLine1.length > HOME_ADDRESS_MAX_CHARS -> InvalidField.HomeAddressTooLong
        else -> null
    }

    private fun StayingGuestAddress?.getPostcodeValidityError(): InvalidField? =
        if (this == null || postalCode.isEmpty()) {
            InvalidField.PostcodeMissing
        } else {
            null
        }

    private fun StayingGuestAddress?.getCityValidityError(): InvalidField? = when {
        this == null || cityName.isEmpty() -> InvalidField.CityNameMissing

        cityName.length > CITY_NAME_MAX_CHARS -> InvalidField.CityNameTooLong

        !Regex(CITY_NAME_REGEX).matches(cityName.trim()) -> InvalidField.CityNameInvalid
        else -> null
    }

    private fun StayingGuestAddress?.getCountryValidityError(): InvalidField? =
        if (this == null || countryCode.isEmpty()) {
            InvalidField.CountryMissing
        } else {
            null
        }

    sealed class InvalidField {
        data object FirstNameMissing: InvalidField()
        data object FirstNameTooShort: InvalidField()
        data object FirstNameTooLong: InvalidField()
        data object LastNameMissing: InvalidField()
        data object LastNameTooShort: InvalidField()
        data object LastNameTooLong: InvalidField()
        data object NationalityMissing: InvalidField()
        data object PassportNumberMissing: InvalidField()
        data object PassportNumberTooShort: InvalidField()
        data object PassportNumberTooLong: InvalidField()
        data object DateOfBirthMissing: InvalidField()
        data object GuestNotAdult: InvalidField()
        data object BirthDateInvalid: InvalidField()
        data object HomeAddressMissing: InvalidField()
        data object HomeAddressTooLong: InvalidField()
        data object PostcodeMissing: InvalidField()
        data object CityNameMissing: InvalidField()
        data object CityNameTooLong: InvalidField()
        data object CityNameInvalid: InvalidField()
        data object CountryMissing: InvalidField()
    }
}
