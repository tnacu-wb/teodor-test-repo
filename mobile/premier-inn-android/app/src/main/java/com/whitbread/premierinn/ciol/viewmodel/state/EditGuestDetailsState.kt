package com.whitbread.premierinn.ciol.viewmodel.state

import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.domain.ciol.usecase.ValidateRegCardGuestUseCase

sealed class FormValidation {
    data class FormValid(val regCardGuest: RegCardGuest, val isLeadGuest: Boolean): FormValidation()
    data object FirstNameMissing: FormValidation()
    data object FirstNameTooShort: FormValidation()
    data object FirstNameTooLong: FormValidation()
    data object LastNameMissing: FormValidation()
    data object LastNameTooShort: FormValidation()
    data object LastNameTooLong: FormValidation()
    data object NationalityMissing: FormValidation()
    data object PassportNumberMissing: FormValidation()
    data object PassportNumberTooShort: FormValidation()
    data object PassportNumberTooLong: FormValidation()
    data object DateOfBirthMissing: FormValidation()
    data object GuestNotAdult: FormValidation()
    data object BirthDateInvalid: FormValidation()
    data object CityNameMissing: FormValidation()
    data object CityNameTooLong: FormValidation()
    data object CityNameInvalid: FormValidation()
    data object CountryMissing: FormValidation()
    data object HomeAddressMissing: FormValidation()
    data object HomeAddressTooLong: FormValidation()
    data object PostcodeMissing: FormValidation()
}

fun ValidateRegCardGuestUseCase.InvalidField.toFormValidationError() = when(this) {
    ValidateRegCardGuestUseCase.InvalidField.FirstNameMissing -> FormValidation.FirstNameMissing
    ValidateRegCardGuestUseCase.InvalidField.FirstNameTooLong -> FormValidation.FirstNameTooLong
    ValidateRegCardGuestUseCase.InvalidField.FirstNameTooShort -> FormValidation.FirstNameTooShort
    ValidateRegCardGuestUseCase.InvalidField.LastNameMissing -> FormValidation.LastNameMissing
    ValidateRegCardGuestUseCase.InvalidField.LastNameTooLong -> FormValidation.LastNameTooLong
    ValidateRegCardGuestUseCase.InvalidField.LastNameTooShort -> FormValidation.LastNameTooShort
    ValidateRegCardGuestUseCase.InvalidField.NationalityMissing -> FormValidation.NationalityMissing
    ValidateRegCardGuestUseCase.InvalidField.PassportNumberMissing -> FormValidation.PassportNumberMissing
    ValidateRegCardGuestUseCase.InvalidField.PassportNumberTooLong -> FormValidation.PassportNumberTooLong
    ValidateRegCardGuestUseCase.InvalidField.PassportNumberTooShort -> FormValidation.PassportNumberTooShort
    ValidateRegCardGuestUseCase.InvalidField.DateOfBirthMissing -> FormValidation.DateOfBirthMissing
    ValidateRegCardGuestUseCase.InvalidField.GuestNotAdult -> FormValidation.GuestNotAdult
    ValidateRegCardGuestUseCase.InvalidField.BirthDateInvalid -> FormValidation.BirthDateInvalid
    ValidateRegCardGuestUseCase.InvalidField.CityNameMissing -> FormValidation.CityNameMissing
    ValidateRegCardGuestUseCase.InvalidField.CityNameTooLong -> FormValidation.CityNameTooLong
    ValidateRegCardGuestUseCase.InvalidField.CityNameInvalid -> FormValidation.CityNameInvalid
    ValidateRegCardGuestUseCase.InvalidField.CountryMissing -> FormValidation.CountryMissing
    ValidateRegCardGuestUseCase.InvalidField.HomeAddressMissing -> FormValidation.HomeAddressMissing
    ValidateRegCardGuestUseCase.InvalidField.HomeAddressTooLong -> FormValidation.HomeAddressTooLong
    ValidateRegCardGuestUseCase.InvalidField.PostcodeMissing -> FormValidation.PostcodeMissing
}
