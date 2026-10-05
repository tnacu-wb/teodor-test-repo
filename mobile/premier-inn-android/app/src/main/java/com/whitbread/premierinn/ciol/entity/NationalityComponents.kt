package com.whitbread.premierinn.ciol.entity

import com.whitbread.premierinn.domain.countries.entity.CountryDomain

data class Nationality(
    var selectedCountry: CountryDomain? = null,
    var isValid: Boolean = false
)

data class IdentificationType(
    var idType: String? = null,
    var isValid: Boolean = false
)

data class IdentificationNumber(
    var idNumber: String? = null,
    var isValid: Boolean = false
)
