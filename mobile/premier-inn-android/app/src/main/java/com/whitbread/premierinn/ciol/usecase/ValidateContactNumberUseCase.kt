package com.whitbread.premierinn.ciol.usecase

import com.whitbread.premierinn.common.Validator
import javax.inject.Inject

class ValidateContactNumberUseCase @Inject constructor() : ValidatorUseCase {

    override operator fun invoke(inputToValidate: String) = Validator.isPhoneNumberValid(inputToValidate)

}
