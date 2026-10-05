package com.whitbread.premierinn.login

data class Screen(val screen: String, val trackingScreen : String)

enum class ScreenType {
    BOOKING_FLOW_LOGIN,
    ACCOUNT_LOGIN
}