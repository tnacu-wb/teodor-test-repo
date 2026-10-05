package com.whitbread.premierinn.addextras

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot
import org.hamcrest.CoreMatchers.allOf

/**
 *
 */
class AddExtrasRobot : BaseRobot() {

    fun continueWithDefaults() {
        view(R.id.cab_summary_continue_to_login).perform(ViewActions.click())
    }

    fun verifyPage(toolbarTitle : String) = apply {
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(toolbarTitle))))
    }

//    fun selectPremierInnBreakfast() {
//        val premierInnBreakfastButton = Espresso.onView(
//                allOf(withId(R.id.rb_breakfast_radio_button),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.ll_summary_breakfast_container),
//                                        position = 2),
//                                position = 1)))
//
//        premierInnBreakfastButton.perform(ViewActions.scrollTo(), ViewActions.click())
//    }

}