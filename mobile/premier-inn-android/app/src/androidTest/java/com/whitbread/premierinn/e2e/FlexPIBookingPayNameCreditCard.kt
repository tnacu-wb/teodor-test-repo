package com.whitbread.premierinn.e2e

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.whitbread.premierinn.addextras.AddExtrasRobot
import com.whitbread.premierinn.guestdetails.GuestDetailsRobot
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.hoteldetails.HotelDetailsRobot
import com.whitbread.premierinn.login.LoginRobot
import com.whitbread.premierinn.mybookings.MyBookingsRobot
import com.whitbread.premierinn.reviewbooking.ReviewBookingRobot
import com.whitbread.premierinn.searchresults.SearchResultsInput
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

@RunWith(AndroidJUnit4::class)
class FlexPIBookingPayNameCreditCard {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HotelDetailsActivity::class.java, true, false)

    private lateinit var intentInput: HotelDetailsInput
    private lateinit var context: Context

    private val reservationId: String = "IoH2LAeGP50LJwfg"
    private val hotelName: String = "London Euston"
    private val hotelCode: String = "LONEUS"
    private val availability: String = "availability-1room-standard"
    private val threeDSNotRequired: String = "three_d_s_not_required.json"
    private val customerJsonResponse = "customer_test_with_VISA.json"
    private val londonEustonHoldOk = "loneus_hold_ok.json"
    private val auth0LoginJson = "auth0_login_OK"
    private val bookingPayNowJson = "booking-OK-PN.json"

    /**
     *    Prerequisite:
     *    User logged in
     *    Saved Credit Card  (4444 3333 2222 1111)
     */

    /**
     * GIVEN I am on the hotel details page
     * AND 3 rates are available
     * WHEN I view the rates
     * THEN I should be able to see the rate name
     * Flex and associated cancellation policy with the description
     * 'Pay now or on arrival. Cancel up to 1pm on arrival day.'
     *
     * GIVEN I am on the hotel details page
     * AND Flex rate is available
     * WHEN I select the rate type Flex
     * AND click on Book Now
     * THEN I should be redirected to the 'Customise your stay' page
     * AND I should be able to see the rate name Flex
     *
     * GIVEN I am on the 'Customise your stay' page
     * AND I select 'Premier Inn breakfast'
     * WHEN I click on continue button
     * THEN I should be redirected to the Personal details page
     *
     * GIVEN I am on the Personal details page
     * AND i have filled in all valid inputs in to the fields
     * WHEN I click on 'Continue to final step' button
     * THEN I should be redirected to the Review & Book page
     * AND I should be able to see the rate name Flex  and associated cancellation policy with the description
     * AND I should see 'Premier Inn breakfast' under Extras
     * AND the correct Total Cost
     *
     * GIVEN I am on the Review & Book page
     * AND I have selected the 'Pay Now' option
     * AND I have filled in valid Credit Card (4444 3333 2222 1111) details with correct CVV number
     * AND I have checked and agreed to T&C's
     * WHEN I click on 'Confirm booking' button
     * THEN I should see the Confirmation page
     * AND I should be able to see the rate name Flex
     * AND the correct Total of the booking
     **/

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        launchHotelDetailsActivity()
    }

    @Test
    fun flex_pi_booking_pay_name_with_credit_card() {
        //FIRST GIVEN
        i_am_on_the_hotel_details_page(hotelName)
        three_rates_are_available()
        i_view_the_rates(1)
        i_should_be_able_to_see_the_rate_name_and_associated_cancellation_policy_with_the_description()

        //SECOND GIVEN
        i_am_still_on_the_hotel_details_page(hotelName)
        flex_rate_is_available()
        i_select_the_rate_type_flex(flexRatePosition)
        click_on_book_now()
        i_should_be_redirected_to_the_customise_your_stay_page("Add extras")

        //THIRD GIVEN
        i_am_on_the_customise_your_stay_page("Add extras")
        i_select_premier_inn_breakfast()
        when_i_click_on_continue_button()
        login()
        i_should_be_redirected_to_the_personal_details_page("Your details")

        //FOURTH GIVEN
        i_am_on_the_personal_details_page("Your details")
        i_have_filled_in_all_valid_inputs_to_the_fields()
        i_click_on_continue_to_final_step_button()
        i_should_be_redirected_to_the_review_and_book_page("Review and book")
        i_should_be_able_to_see_the_rate_name()
        the_correct_total_cost()

        //FIFTH GIVEN
        i_am_on_the_review_and_book_page()
        i_have_filled_in_valid_credit_card()
        i_click_on_confirm_booking_button()
        i_should_see_the_confirmation_page("Booking details")
        //i_should_be_able_to_see_the_rate_name_Flex()
        the_correct_total_of_the_booking()
    }

    //    *    Prerequisite:
    //    *    User logged in
    private fun login() {
        with(LoginRobot()) {
            enterUsername(username)
            enterPassword(password)
            login()
        }
    }

    private fun launchHotelDetailsActivity() {
        intentInput = createInputIntent(listOf("DB"))
        stubCalls()
        launchScreen(intentInput)
    }

    //GIVEN I am on the hotel details page (Checks hotel name)
    private fun i_am_on_the_hotel_details_page(hotelName: String) {
        HotelDetailsRobot().verifyHotelDetailPage(hotelName)
    }

    //GIVEN I am on the hotel details page (Checks hotel name in toolbar)
    private fun i_am_still_on_the_hotel_details_page(hotelName: String) {
        HotelDetailsRobot().verifyHotelDetailToolbar(hotelName)
    }

    private fun stubCalls() {
        stubApiCallsAvailability()
        stubHotelApi()
        stubHoldApi()
        stubLoginApi()
        stubCustomerApi()
        stubPaymentApi()
        stubBookingApi()
        stubValidationSuccess()
    }

    private fun launchScreen(input: HotelDetailsInput) {
        activityRule.launchActivity(HotelDetailsActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input))
    }

    //AND 3 rates are available
    private fun three_rates_are_available() {
        HotelDetailsRobot().view_rates(1)
        HotelDetailsRobot().containsFirstRateBoxWithName(rateName = advanceRate)
        HotelDetailsRobot().containsSecondRateBoxWithName(rateName = flexRate)
        HotelDetailsRobot().containsThirdRateBoxWithName(rateName = nonFlexRate)
    }

    //WHEN I view the rates
    private fun i_view_the_rates(position: Int) {
        HotelDetailsRobot().view_rates(position)
    }

    //THEN I should be able to see the rate name
    //Flex and associated cancellation policy with the description
    //Pay now or on arrival. Cancel up to 1pm on arrival day.
    private fun i_should_be_able_to_see_the_rate_name_and_associated_cancellation_policy_with_the_description() {
        HotelDetailsRobot().containsFirstRateBoxWithName(rateName = advanceRate)
        HotelDetailsRobot().containsSecondRateBoxWithName(rateName = flexRate)
        HotelDetailsRobot().containsThirdRateBoxWithName(rateName = nonFlexRate)

        HotelDetailsRobot().containsFirstRateBoxWithDescription(description = advanceDescription)
        HotelDetailsRobot().containsSecondRateBoxWithDescription(description = flexDescription)
        HotelDetailsRobot().containsThirdRateBoxWithDescription(description = nonFlexDescription)
    }

    // AND Flex rate is available
    private fun flex_rate_is_available() {
        HotelDetailsRobot().containsSecondRateBoxWithName(rateName = flexRate)
        HotelDetailsRobot().containsSecondRateBoxWithDescription(description = flexDescription)
        HotelDetailsRobot().verifyFlexAvailability(position = flexRatePosition)
    }

    // WHEN I select the rate type Flex
    private fun i_select_the_rate_type_flex(position: Int) {
        HotelDetailsRobot().view_rates(position)
    }

    // AND click on Book Now
    private fun click_on_book_now() {
        HotelDetailsRobot().chooseRate(flexRatePosition)
    }

    // THEN I should be redirected to the 'Customise your stay' page
    private fun i_should_be_redirected_to_the_customise_your_stay_page(toolbarTitle: String) {
        AddExtrasRobot().verifyPage(toolbarTitle)
    }

    // AND I should be able to see the rate name Flex

    // GIVEN I am on the 'Customise your stay' page
    private fun i_am_on_the_customise_your_stay_page(toolbarTitle: String) {
        AddExtrasRobot().verifyPage(toolbarTitle)
    }

    // AND I select 'Premier Inn breakfast'
    private fun i_select_premier_inn_breakfast() {
        //AddExtrasRobot().selectPremierInnBreakfast()
    }

    //    WHEN I click on continue button
    private fun when_i_click_on_continue_button() {
        AddExtrasRobot().continueWithDefaults()
    }

    //    THEN I should be redirected to the Personal details page
    private fun i_should_be_redirected_to_the_personal_details_page(toolbarTitle: String) {
        AddExtrasRobot().verifyPage(toolbarTitle)
    }

    //    GIVEN I am on the Personal details page
    private fun i_am_on_the_personal_details_page(toolbarTitle: String) {
        AddExtrasRobot().verifyPage(toolbarTitle)
    }

    //    AND i have filled in all valid inputs in to the fields
    private fun i_have_filled_in_all_valid_inputs_to_the_fields() {
        GuestDetailsRobot().bookerFirstName("Joshua")
            .bookerLastName("Onabanjo")
            .email("joshuatest.@test.com")
            .number("012345678")
            .home()
            .postCode("NP7 5BG")
            .manualAddress()
            .address1("Trinity Square")
            .address2("Abergavenny")
            .address3("London")
    }

    //    WHEN I click on 'Continue to final step' button
    private fun i_click_on_continue_to_final_step_button() {
        GuestDetailsRobot().continueWithDetails()
    }

    //THEN I should be redirected to the Review & Book page
    private fun i_should_be_redirected_to_the_review_and_book_page(toolbarTitle: String) {
        ReviewBookingRobot(context).verifyPage(toolbarTitle)
    }

    //AND I should be able to see the rate name Flex and associated cancellation policy with the description
    private fun i_should_be_able_to_see_the_rate_name() {
        ReviewBookingRobot(context).verifyRateName(flexRate)
    }

    //AND I should see 'Premier Inn breakfast' under Extras
    //AND the correct Total Cost
    private fun the_correct_total_cost() {
        ReviewBookingRobot(context).successTotalPriceDisplayed(totalPrice)
    }

    //GIVEN I am on the Review & Book page
    private fun i_am_on_the_review_and_book_page() {
        ReviewBookingRobot(context).verifyPage("Review and book")
    }

    //    AND I have filled in valid Credit Card (4444 3333 2222 1111) details with correct CVV number
    private fun i_have_filled_in_valid_credit_card() {
        ReviewBookingRobot(context).verifyCvvIsDisplayed()
        ReviewBookingRobot(context).enterCvv(123)
    }

    //  WHEN I click on 'Confirm booking' button
    private fun i_click_on_confirm_booking_button() {
        ReviewBookingRobot(context).clickMakeBooking()
    }

    // THEN I should see the Confirmation page
    private fun i_should_see_the_confirmation_page(toolbarTitle: String) {
        AddExtrasRobot().verifyPage(toolbarTitle)
    }

    // AND I should be able to see the rate name Flex
    private fun i_should_be_able_to_see_the_rate_name_Flex() {
        MyBookingsRobot().verifyRateName(rateName = flexRate)
    }

    // AND the correct Total of the booking
    private fun the_correct_total_of_the_booking() {
        MyBookingsRobot().verifyBookingTotalPrice(totalPrice = totalPrice)
    }

    private fun createInputIntent(roomTypes: List<String>): HotelDetailsInput {
        return HotelDetailsInput.builder()
                .cameFromMapView(false)
                .distanceFromSearchedLocation(0.0f)
                .hotelCode(hotelCode)
                .hotelImageUrl(null)
                .hotelName(hotelName)
                .searchResultsInput(SearchResultsInput.builder()
                        .adults(listOf(1))
                        .infants(emptyList())
                        .children(listOf(1))
                        .infants(listOf(1))
                        .roomTypeCodes(roomTypes)
                        .cots(listOf(false))
                        .placeName(hotelName)
                        .numRooms(1)
                        .arrivalDate(LocalDate.of(2020, 8, 20))
                        .departureDate(LocalDate.of(2020, 8, 21))
                        .latitude(51.527736f)
                        .longitude(-0.129068f)
                        .build()
                ).build()
    }

    private fun stubApiCallsAvailability() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/booking/hotels/${hotelCode}/availability"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/${hotelCode}/$availability.json")
    }

    private fun stubHotelApi() {
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/hotels/${hotelCode}"))
                .thenReturnFile(successResponseCode, "apiTest/hotels/${hotelCode}.json")
    }

    private fun stubHoldApi() {
        RESTMockServer.whenPUT(RequestMatchers.pathEndsWith("/booking/hotels/${reservationId}/hold?hotelBrand=PI"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/hold/$londonEustonHoldOk")
    }

    private fun stubLoginApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/oauth/token"))
                .thenReturnFile(successResponseCode, "apiTest/auth/hotels/login/$auth0LoginJson.json")
    }

    private fun stubCustomerApi() {
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/customers/hotels/test@gmail.com"))
                .thenReturnFile(successResponseCode, "apiTest/customers/hotels/$customerJsonResponse")
    }

    private fun stubPaymentApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/payment/hotels"))
                .thenReturnFile(successResponseCode, "apiTest/payment/hotels/$threeDSNotRequired")
    }

    private fun stubBookingApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/booking/hotels/$reservationId"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/booking-complete/$bookingPayNowJson")
    }

    private fun stubValidationSuccess() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/payment/validations"))
            .thenReturnFile(200, "apiTest/bin-validation-success.json")
    }

    companion object {
        const val successResponseCode = 200
        const val username: String = "test@gmail.com"
        const val password: String = "Password2"

        const val advanceRate: String = "Advance"
        const val flexRate: String = "Flex"
        const val nonFlexRate: String = "Non-Flex"

        const val advanceDescription: String = "Pay now. Change arrival date. Fully refundable up to 28 days before arrival."
        const val flexDescription: String = "Pay now or later. Cancel up to 1 pm on arrival day. "
        const val nonFlexDescription: String = "Pay now. No amends.No Cancellation beyond 24hrs of booking."

        const val totalPrice: String = "£118.99"

        const val flexRatePosition: Int = 2
    }

}