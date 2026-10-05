package uk.co.whitbread.integrationtests.framework.config.validation

import java.net.URI

/** Validates and normalizes one HTTP(S) origin used as a runtime base URL. */
internal fun validateHttpBaseUrl(
    setting: String,
    value: String,
): String {
    require(value.isNotBlank()) { "$setting must not be blank" }
    val uri =
        try {
            URI(value)
        } catch (failure: Exception) {
            throw IllegalArgumentException("$setting must be a valid HTTP URL: '$value'", failure)
        }

    require(uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true)) {
        "$setting must use http or https: '$value'"
    }
    require(!uri.host.isNullOrBlank()) { "$setting must include a host: '$value'" }
    require(uri.port == -1 || uri.port in 1..65535) {
        "$setting port must be between 1 and 65535: '$value'"
    }
    require(uri.userInfo == null) { "$setting must not include user information: '$value'" }
    require(uri.rawQuery == null) { "$setting must not include a query: '$value'" }
    require(uri.rawFragment == null) { "$setting must not include a fragment: '$value'" }
    require(uri.path.isNullOrEmpty() || uri.path == "/") {
        "$setting must not include a path: '$value'"
    }

    return value.removeSuffix("/")
}
