package com.whitbread.premierinn.guestdetails

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher


class GuestDetailsRobot : BaseRobot() {

    fun bookerFirstName(value: String) = apply { fillEditText(R.id.guest_details_first_name_input, value) }

    fun bookerLastName(value: String) = apply { fillEditText(R.id.guest_details_last_name_input, value) }

    fun number(value: String) = apply { fillEditText(R.id.guest_details_contact_number_input, value) }

    fun email(value: String) = apply { fillEditText(R.id.guest_details_email_input, value) }

    fun home() = apply {
        scrollToView(R.id.et_address_form_postcode)
        clickView("Home")
    }

    fun work() = apply {
        scrollToView(R.id.et_address_form_postcode)
        clickView("Work")
    }

    fun postCode(value: String) = apply {
        scrollToView(R.id.et_address_form_postcode)
        fillEditText(R.id.et_address_form_postcode, value)
    }

    fun manualAddress() = apply {
        scrollToView(R.id.tv_address_form_manual_address)
        clickView(R.id.tv_address_form_manual_address)
    }

    fun company(value: String) = apply {
        scrollToView(R.id.et_address_form_company)
        fillEditText(R.id.et_address_form_company, value)
    }

    fun address1(value: String) = apply {
        scrollToView(R.id.et_address_form_address_line1)
        fillEditText(R.id.et_address_form_address_line1, value)
    }

    fun address2(value: String) = apply {
        scrollToView(R.id.et_address_form_address_line2)
        fillEditText(R.id.et_address_form_address_line2, value)
    }

    fun address3(value: String) = apply {
        scrollToView(R.id.et_address_form_address_line3)
        fillEditText(R.id.et_address_form_address_line3, value)
    }

    fun bookingButNotSayingToggle() = apply {
        scrollToView(R.id.sc_guest_details_booking_not_staying)
        toggleCheckboxOn(R.id.sc_guest_details_booking_not_staying)
    }

    fun room1StayerFirstName(value: String) = apply {
        val firstNameView = viewTextDescendantFromView(R.id.guest_details_first_name_input, room1StayerFormParentMatcher())
        scrollToView(firstNameView)
        fillEditText(firstNameView, value)
    }

    fun room1StayerLastName(value: String) = apply {
        val lastNameView = viewTextDescendantFromView(R.id.guest_details_last_name_input, room1StayerFormParentMatcher())
        scrollToView(lastNameView)
        fillEditText(lastNameView, value)
    }

    fun chooseBusiness() = apply {
//        scrollToView(R.id.guest_details_trip_type_checkbox)
//        toggleCheckboxOn(R.id.guest_details_trip_type_checkbox)
    }

    fun successGoToPaymentDetails() = apply {
        view("Payment details").check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun successGoToReviewAndBook() = apply {
        view("Review and book").check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun continueWithDetails() = apply {
        Espresso.closeSoftKeyboard()
        scrollToView(R.id.cta_guest_details_continue)
        clickView(R.id.cta_guest_details_continue)
    }

    fun success() = apply {
        scrollToView(R.id.cta_guest_details_continue)
        assertEnabled(R.id.cta_guest_details_continue)
    }

    fun failure() = apply {
        scrollToView(R.id.cta_guest_details_continue)
        assertDisabled(R.id.cta_guest_details_continue)
    }

    fun addressCountry(countryName: String) = apply {
        scrollToView(R.id.s_address_form_countries)
        selectCountryInCountryList(countryName, R.id.s_address_form_countries)
    }

    private fun room1StayerFormParentMatcher() = roomStayerFormParentMatcher("Room 1")

    private fun room2StayerFormParentMatcher() = roomStayerFormParentMatcher("Room 2")

    private fun roomStayerFormParentMatcher(roomTitle: String): Matcher<View> {
        return allOf(withId(R.id.rgdv_guest_details_room), withChild(withText(roomTitle)))
    }

    fun verifyPage(toolbarTitle : String) = apply {
        Espresso.onView(withId(R.id.toolbar)).check(ViewAssertions.matches(
                ViewMatchers.hasDescendant(withText(toolbarTitle))))
    }
}