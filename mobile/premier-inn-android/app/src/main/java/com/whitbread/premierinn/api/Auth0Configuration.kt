package com.whitbread.premierinn.api

// TODO try to find a way to hide clientId
sealed class Auth0Configuration(val clientId: String, val domain: String) {
    object Stage : Auth0Configuration("1v4m1df7ZJCcEEkb6drvdgTtAY3hYgry", "auth0.premierinn.digital")
    object Production : Auth0Configuration("VKHFCruuP9oLTIsh2Irf1eW6Fv6pWrqa", "auth0.premierinn.com")
}