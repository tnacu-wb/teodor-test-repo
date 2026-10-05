package com.whitbread.premierinn.reviewbooking

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whitbread.premierinn.R
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity
import com.whitbread.premierinn.landing.LandingActivity
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownActivity
import com.whitbread.premierinn.utils.BaseRobot
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not

class ReviewBookingRobot(val context: Context) : BaseRobot() {

    //TODO: Try and Convert to 3CP
    fun successPaymentOptionsDisplayed() = apply {
        //scrollToView(R.id.review_booking_pay_options_toggle).check(matches(isDisplayed()))
    }

    //TODO: Try and Convert to 3CP
    fun successPayNowLaterOptionDisplayed() = apply {
       //view(R.id.review_booking_pay_options_toggle).check(matches(isDisplayed()))
    }
    //TODO: Try and Convert to 3CP or Delete
    fun successPaymentCardTypeDisplayed(cardType: String) = apply {
//        scrollToView(R.id.review_booking_payment_card_type_label)
//        matchText(R.id.review_booking_payment_card_type_label, cardType)
//                .check(matches(isDisplayed()))
    }
    //TODO: Try and Convert to 3CP or Delete
    fun successPaymentCardNumberDisplayed(lastFourDigits: String) = apply {
//        scrollToView(R.id.review_booking_payment_card_digits_label)
//        matchText(R.id.review_booking_payment_card_digits_label, "Ending in $lastFourDigits")
//                .check(matches(isDisplayed()))
    }
    //TODO: Try and Convert to 3CP or Delete
    fun successBillPayerNameDisplayed(name: String) = apply {
//        scrollToView(R.id.review_booking_payment_booker_name_label)
//        matchText(R.id.review_booking_payment_booker_name_label, name)
//                .check(matches(isDisplayed()))
    }
    //TODO: Try and Convert to 3CP or Delete
    fun successCardExpirationDisplayed(expiration: String) = apply {
//        scrollToView(R.id.review_booking_payment_expiry_date_label)
//        matchText(R.id.review_booking_payment_expiry_date_label, "Expires $expiration")
//                .check(matches(isDisplayed()))
    }

    fun verifyPayOnArrivalMessage() = apply {
        scrollToView(context.getString(R.string.review_booking_payment_no_payment))
                .check(matches(isDisplayed()))
    }

    fun verifyStandardAmendAndCancelMessage() = apply {
        scrollToView(context.getString(R.string.review_booking_amend_cancel_allow_standard))
        view(context.getString(R.string.review_booking_amend_cancel_allow_standard))
                .check(matches(isDisplayed()))
    }

    fun successPayOnArrivalTotalDisplayed() = apply {
        scrollToView(context.getString(R.string.review_booking_pay_on_arrival_label))
        view(context.getString(R.string.review_booking_pay_on_arrival_label))
                .check(matches(isDisplayed()))
    }

    fun successRateFlexDisplayed() = apply {
        scrollToView(R.id.booking_rate_name)
        matchText(R.id.booking_rate_name, "Flex")
                .check(matches(isDisplayed()))
    }

    fun successTotalPriceDisplayed(total: String) = apply {
        scrollToView(R.id.total_booking_price)
        matchText(R.id.total_booking_price, total)
                .check(matches(isDisplayed()))
    }

    fun successPaymentOptionsNotDisplayed() = apply {
        view("Pay now").check(matches(not(isDisplayed())))
        view("Pay on arrival").check(matches(not(isDisplayed())))
    }

    fun clickMakeBooking() = apply {
        scrollToView(R.id.review_booking_confirm_button)
        clickView(R.id.review_booking_confirm_button)
    }

    fun success(price: String = "") = apply {
        intended(hasComponent(BookingDetailsActivity::class.java.name))
        withText("We’ve sent your booking confirmation to test@gmail.com. Your total of $price is to be paid on arrival")
                .matches(isDisplayed())
    }

    fun successFullyPaid() = apply {
        intended(hasComponent(BookingDetailsActivity::class.java.name))
        withText("We’ve sent your booking confirmation to test@gmail.com. Your booking has been paid in full")
                .matches(isDisplayed())
    }

    //TODO: Try and Convert to 3CP
    fun successCnpSectionDisplayed() = apply {
//        scrollToView(R.id.review_booking_additional_information_purchase_number)
//        view(R.id.review_booking_additional_information_purchase_number).check(matches(isDisplayed()))
//
//        scrollToView(R.id.review_booking_additional_information_customer_reference_number)
//        view(R.id.review_booking_additional_information_customer_reference_number).check(matches(isDisplayed()))
    }

    //TODO: Try and Convert to 3CP
    fun successInputtedCnpInformationDisplayed() = apply {
//        scrollToView(R.id.review_booking_additional_information_purchase_number)
//        matchText(R.id.review_booking_additional_information_purchase_number, "purchase_order_number")
//                .check(matches(isDisplayed()))
//        scrollToView(R.id.review_booking_additional_information_customer_reference_number)
//        matchText(R.id.review_booking_additional_information_customer_reference_number, "customer_reference")
//                .check(matches(isDisplayed()))
    }

    //TODO: Try and Convert to 3CP or Delete
    fun successCnpPaymentToggleDisplayed() = apply {
//        scrollToView(R.id.review_booking_payment_cnp_toggle)
//        view(R.id.review_booking_payment_cnp_toggle).check(matches(isDisplayed()))
    }

    //TODO: Try and Convert to 3CP or Delete
    fun toggleCnpPayment() = apply {
//        scrollToView(R.id.review_booking_payment_cnp_toggle)
//        clickView(R.id.review_booking_payment_cnp_toggle)
    }

    fun cnpMemorableWord(text: String) = apply {
        scrollToView(R.id.review_booking_memorable_word_input)
        fillEditText(R.id.review_booking_memorable_word_input, text)
    }

    //TODO: Try and Convert to 3CP or Delete
    fun toggleOnCnpDinnerAllowance() = apply {
//        scrollToView(R.id.review_booking_dinner_allowance_switch)
//        clickView(R.id.review_booking_dinner_allowance_switch)
    }

    fun cnpDinnerAllowance(value: String) = apply {
        scrollToView(R.id.review_booking_dinner_budget_input)
        fillEditText(R.id.review_booking_dinner_budget_input, value)
    }

    //TODO: Try and Convert to 3CP or Delete
    fun toggleOnCnpAlcohol() = apply {
//        scrollToView(R.id.review_booking_include_alcohol_switch)
//        clickView(R.id.review_booking_include_alcohol_switch)
    }

    //TODO: Try and Convert to 3CP or Delete
    fun toggleOnCnpWifi() = apply {
//        scrollToView(R.id.review_booking_wifi_toggle)
//        clickView(R.id.review_booking_wifi_toggle)
    }

    //TODO: Try and Convert to 3CP or Delete
    fun toggleOnCnpParking() = apply {
//        scrollToView(R.id.review_booking_parking_toggle)
//        clickView(R.id.review_booking_parking_toggle)
    }

    fun paymentBreakdown() = apply {
        scrollToView("Payment breakdown")
        clickView("Payment breakdown")
    }

    fun editGuestDetails() = apply {
        scrollToView(R.id.review_booking_guest_details_edit_button)
        clickView(R.id.review_booking_guest_details_edit_button)
    }

    fun successOpenPaymentBreakdown() = apply {
        intended(hasComponent(PaymentBreakdownActivity::class.java.name))
    }

    fun successGuestNameDisplayed(firstname: String) = apply {
        onView(CoreMatchers.allOf(withText(firstname), isDisplayed())).perform(scrollTo())
        onView(CoreMatchers.allOf(withText(firstname), isDisplayed())).check(matches(isDisplayed()))
    }

    fun successGuestEmailDisplayed(email: String) = apply {
        onView(CoreMatchers.allOf(withText(email), isDisplayed())).perform(scrollTo())
        onView(CoreMatchers.allOf(withText(email), isDisplayed())).check(matches(isDisplayed()))
    }

    //TODO: Try and Convert to 3CP or Delete
    fun changePaymentDetails() = apply {
//        scrollToView(R.id.review_booking_payment_edit_button)
//        clickView(R.id.review_booking_payment_edit_button)
    }

    fun verifyCvvIsDisplayed() = apply {
        scrollToView(R.id.et_credit_card_cvv)
        view(R.id.et_credit_card_cvv).check(matches(isDisplayed()))
    }

    fun verifyCvvNotDisplayed() = apply {
        view(R.id.et_credit_card_cvv).check(matches(not(isDisplayed())))
    }

    fun selectsPayNow() = apply {
        scrollToView("Pay now")
        clickView("Pay now")
    }

    fun selectsPayOnArrival() = apply {
        scrollToView("Pay on arrival")
        clickView("Pay on arrival")
    }

    fun failure() = apply {
        view("Payment failed").check(matches(isDisplayed()))
    }

    fun dismissDialog() = apply {
        clickView("OK")
    }

    fun enterCvv(value: Int) = apply {
        scrollToView(R.id.et_credit_card_cvv)
                .perform(typeText("$value"))
    }

    fun failureHoldHasExpired() = apply {
        view("We hold a booking for 25 minutes before we release it.").check(matches(isDisplayed()))
    }

    fun successBackToLanding() = apply {
        intended(hasComponent(LandingActivity::class.java.name))
    }

    fun verifyNoAmendmentsOrCancellationsAllowedMessage() = apply {
        scrollToView(context.getString(R.string.review_booking_amend_cancel_dont_allow))
        view(context.getString(R.string.review_booking_amend_cancel_dont_allow)).check(matches(isDisplayed()))
    }

    fun verifyNoAmendmentsAllowedMessage() = apply {
        scrollToView(context.getString(R.string.review_booking_amend_not_allowed))
                .check(matches(isDisplayed()))
    }

    fun successRoom1Details() = apply {
        scrollToView("Room 1")
        view("Room 1").check(matches(isDisplayed()))
        viewTextWithSiblingText("Mr. Miguel Santos", "Room 1").check(matches(isDisplayed()))
        viewTextWithSiblingText("email@email.com", "Room 1").check(matches(isDisplayed()))
    }

    fun successRoom2Details() = apply {
        scrollToView("Room 2")
        view("Room 2").check(matches(isDisplayed()))
        viewTextWithSiblingText("Mr. Mark O'Meara", "Room 2").check(matches(isDisplayed()))
        viewTextWithSiblingText("mark@email.com", "Room 2").check(matches(isDisplayed()))
    }

    fun verifyCancellationMessageWithin(hours: Int) {
        scrollToView(context.getString(R.string.review_booking_cancel_only_allow_x_hours_booking, hours))
                .check(matches(isDisplayed()))
    }

    fun verifyNoAmendmentsMessage() {
        scrollToView(context.getString(R.string.review_booking_amend_not_allowed))
                .check(matches(isDisplayed()))
    }

    //TODO: 3DS Try and Convert to 3CP
    fun verify3dsIsDisplayed() {
        //intended(hasComponent(Secure3dsActivity::class.java.name))
    }

    fun verifyRateName(rateName: String) = apply {
        scrollToView(R.id.booking_rate_name)
        matchText(R.id.booking_rate_name, rateName)
                .check(matches(isDisplayed()))
    }

    fun verifyPage(toolbarTitle : String) = apply {
        onView(ViewMatchers.withId(R.id.toolbar)).check(matches(ViewMatchers.hasDescendant(withText(toolbarTitle))))
    }

}

