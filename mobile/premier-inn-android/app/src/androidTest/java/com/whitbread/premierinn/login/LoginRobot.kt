package com.whitbread.premierinn.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot

/**
 *
 */
class LoginRobot : BaseRobot() {
    fun enterUsername(value: String) {
        //fillEditText(R.id.et_login_email, value)
    }

    fun enterPassword(value: String) {
        //fillEditText(R.id.et_login_password, value)
    }

    fun login() {
        //clickView(R.id.cab_log_in_log_in)
    }

    fun continueAsGuest() {
        clickView(R.id.cab_login_continue_guest_button)
    }

    fun verifyPage(toolbarTitle : String) = apply {
        Espresso.onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(
                ViewMatchers.hasDescendant(ViewMatchers.withText(toolbarTitle))))
    }
}