package com.whitbread.premierinn.reviewbooking

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot
import org.hamcrest.CoreMatchers.allOf

class EditGuestDetailsRobot : BaseRobot() {

    fun guestFirstName(firstName: String) = apply {
        onView(allOf(withId(R.id.guest_details_first_name_input), isDisplayed())).perform(ViewActions.scrollTo())
        onView(allOf(withId(R.id.guest_details_first_name_input), isDisplayed()))
                .perform(ViewActions.replaceText(firstName), ViewActions.closeSoftKeyboard())
    }

    fun guestLastName(lastName: String) = apply {
        onView(allOf(withId(R.id.guest_details_last_name_input), isDisplayed())).perform(ViewActions.scrollTo())
        onView(allOf(withId(R.id.guest_details_last_name_input), isDisplayed()))
                .perform(ViewActions.replaceText(lastName), ViewActions.closeSoftKeyboard())
    }

    fun guestEmail(email: String) = apply {
        onView(allOf(withId(R.id.guest_details_email_input), isDisplayed())).perform(ViewActions.scrollTo())
        onView(allOf(withId(R.id.guest_details_email_input), isDisplayed()))
                .perform(ViewActions.replaceText(email), ViewActions.closeSoftKeyboard())
    }

    fun save() {
        scrollToView(R.id.cab_edit_guest_update_details)
        clickView(R.id.cab_edit_guest_update_details)
    }
}