package com.whitbread.premierinn.domain.utils

import com.whitbread.premierinn.domain.countries.entity.CountryDomain

fun positionOfSelectedCountry(countries: List<CountryDomain>, countryCode: String): Int {
    var position = countries.indexOfFirst { it.countryCode == countryCode }
    if (position < 0 && !CountryDomain.UK_CODE.equals(countryCode, ignoreCase = true)) {
        position = countries.indexOfFirst { it.countryCode == CountryDomain.UK_CODE }
    }
    return position
}
