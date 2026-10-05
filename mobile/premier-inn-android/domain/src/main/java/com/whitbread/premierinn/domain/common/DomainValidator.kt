package com.whitbread.premierinn.domain.common

import java.util.regex.Pattern

class DomainValidator {
    companion object {
        private val GERMAN_POSTCODE_PATTERN = Pattern.compile("^([0123456789]\\d{4})(?![\\s$&+,:;=?@#|'<>.-^*()%!])$")
        private val UK_POSTCODE_PATTERN =
            Pattern.compile("^(GIR ?0AA|[A-PR-UWYZ](\\d{1,2}|([A-HK-Y]\\d([0-9ABEHMNPRV-Y])?)|\\d[A-HJKPS-UW]) ?\\d[ABD-HJLNP-UW-Z]{2})$")

        fun isGermanPostcodeValid(postcode: String): Boolean = GERMAN_POSTCODE_PATTERN.matcher(postcode.trim()).matches()
        fun isUkPostcodeValid(postcode: String): Boolean = UK_POSTCODE_PATTERN.matcher(postcode.trim()).matches()
    }
}
