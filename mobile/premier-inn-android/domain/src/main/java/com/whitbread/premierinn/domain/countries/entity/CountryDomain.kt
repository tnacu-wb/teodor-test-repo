package com.whitbread.premierinn.domain.countries.entity

data class CountryDomain(
        val countryCode: String,
        val countryIsoCode: String,
        val countryName: String,
        val requiresPassportInfo: Boolean,
        val nationality: String?): Comparable<CountryDomain> {

    override fun compareTo(other: CountryDomain): Int {
        return countryName.compareTo(other.countryName)
    }

    companion object {
        const val UK_CODE = "GB"
        const val GERMANY_CODE = "D"
        const val GERMANY_ISO_CODE = "DE"

        const val UK_LOCALE = "UK"
        const val DE_LOCALE = "DE"

        fun isCountryUk(countryCode: String): Boolean {
            return countryCode == UK_CODE
        }
        fun isCountryGermany(countryCode: String): Boolean {
            return countryCode == GERMANY_CODE
        }
        fun isCountryGermanyUsingIsoCode(countryCode: String): Boolean {
            return countryCode == GERMANY_ISO_CODE
        }

        fun getLocaleFromCountryCode(isoCountryCode: String): String {
            return when {
                isoCountryCode.uppercase() == GERMANY_ISO_CODE -> DE_LOCALE
                isoCountryCode.uppercase() == UK_CODE -> UK_LOCALE
                else -> UK_LOCALE
            }
        }
    }
}
