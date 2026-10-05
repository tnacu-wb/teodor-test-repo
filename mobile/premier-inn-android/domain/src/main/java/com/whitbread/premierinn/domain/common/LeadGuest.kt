package com.whitbread.premierinn.domain.common

data class LeadGuest(val guest: Guest,
                     val nationalityCountryCode: String?,
                     val carRegistration: String? = null,
                     val passport: Passport?,
                     val nextDestination: String?) {
    companion object {
        fun createDefault(): LeadGuest {
            return LeadGuest(guest = Guest.createDefault(), nationalityCountryCode = null,
                carRegistration = EMPTY_STRING_DOMAIN, passport = null, nextDestination = EMPTY_STRING_DOMAIN)
        }

    }
}