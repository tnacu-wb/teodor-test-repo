package com.whitbread.premierinn.e2e

import android.content.Context
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
import org.threeten.bp.LocalDate

class GuestUserFlexPIBooking {

    /**
     *    Prerequisite:
     *    NOT logged in
     *    Does NOT have a saved payment card
     */

    /**
    GIVEN I am NOT logged in to the PI app
    AND do NOT have a saved payment card
    AND I am on the hotel details page 
    AND 3 rates are available 
    WHEN I view the rates 
    THEN I should be able to see the rate name  Flex and associated cancellation policy with the description 'Pay now or on arrival. Cancel up to 1 pm on arrival day.'

    GIVEN I am on the hotel details page
     AND Flex rate is available 
    WHEN I select the rate type Flex 
    AND click on Book Now 
    THEN I should be redirected to the 'add extras' page
     AND I should be able to see the rate name Flex

    GIVEN I am on the 'add extras' page 
    WHEN I click on continue button 
    THEN I should be redirected to the Log in page

    GIVEN I am on the Log in page

    AND I tap on the ‘Continue as a guest’

    THEN I should be redirected to the ‘Your details’ page

    GIVEN I am on the ‘Your details’ page
     AND I have filled in all valid inputs to the fields
     WHEN I click on ‘Continue to payment’ button
    THEN I should be redirected to the Payment details page

    GIVEN I am on the ‘Payment details’ page
    AND when I have filled in all valid inputs into the fields including Debit card (4582 6200 0000 0037)

    AND I select ‘PAY NOW’
    AND I click ‘Continue to final step’
    THEN I should be redirected to the Review & Book page

    GIVEN I am on the Review & Book page 
    WHEN I view the Review & Book page I should be able to see the rate name Flex and associated cancellation policy with the description
    AND Debit Card (4582 6200 0000 0037) should be prefilled

    AND when I enter the correct CVV
    AND I have checked and agreed to T&C's 
    WHEN I click on 'Confirm booking' button 
    THEN I should see the Confirmation page 
    AND I should be able to see the rate name Flex  (rate type is not currently shown, this isn't a test issue but an application issue)
     */

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HotelDetailsActivity::class.java, true, false)

    private lateinit var intentInput: HotelDetailsInput
    private lateinit var context: Context

    private lateinit var hotelDetailsRobot: HotelDetailsRobot
    private lateinit var guestDetailsRobot: GuestDetailsRobot
    private lateinit var reviewBookingRobot: ReviewBookingRobot
    private lateinit var addExtrasRobot: AddExtrasRobot
    private lateinit var loginRobot: LoginRobot
    private lateinit var myBookingsRobot: MyBookingsRobot

    private val reservationId: String = "IoH2LAeGP50LJwfg"
    private val hotelName: String = "London Euston"
    private val hotelCode: String = "LONEUS"
    private val availability: String = "availability-1room-standard"
    private val threeDSNotRequired: String = "three_d_s_not_required.json"
    private val customerJsonResponse = "customer_test_with_VISA.json"
    private val londonEustonHoldOk = "loneus_hold_ok.json"
    private val auth0LoginJson = "auth0_login_OK"
    private val bookingPayNowJson = "booking-OK-PN.json"

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        initRobots()
        stubCalls()
        launchHotelDetailsActivity()
    }

    private fun initRobots() {
        reviewBookingRobot = ReviewBookingRobot(context)
        hotelDetailsRobot = HotelDetailsRobot()
        guestDetailsRobot = GuestDetailsRobot()
        addExtrasRobot = AddExtrasRobot()
        loginRobot = LoginRobot()
        myBookingsRobot = MyBookingsRobot()
    }

    private fun launchHotelDetailsActivity() {
        intentInput = createInputIntent(listOf("DB"))
        launchScreen(intentInput)
    }

    private fun launchScreen(input: HotelDetailsInput) {
        activityRule.launchActivity(HotelDetailsActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input))
    }

    @Test
    fun guest_user_flex_pi_booking_pay_now_with_debit_card() {
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
        i_am_on_the_add_extras_page("Add extras")
        when_i_click_on_continue_button()
        i_should_be_redirected_to_the_log_in_page("Log in")

        //FOURTH GIVEN
        i_am_on_the_login_page("Log in")
        i_tap_on_the_continue_as_a_guest()
        i_should_be_redirected_to_the_your_details_page("Your details")

        //FIFTH GIVEN
        i_am_on_the_your_details_page("Your details")
        i_have_filled_in_all_valid_inputs_to_the_fields()
        i_have_on_continue_to_payment_button()
        i_should_be_redirected_to_the_review_and_book_page()

        //SIXTH GIVEN
        i_am_on_the_review_and_book_page()
        i_view_the_review_and_book_page_i_should_be_able_to_see_the_rate_name_Flex()
        debit_card_should_be_prefilled()

        when_i_enter_the_correct_cvv()
        i_click_on_confirm_booking_button()
        i_should_see_the_confirmation_page("Booking details")
    }

    //GIVEN I am on the hotel details page (Checks hotel name)
    private fun i_am_on_the_hotel_details_page(hotelName: String) {
        hotelDetailsRobot.verifyHotelDetailPage(hotelName)
    }

    //GIVEN I am on the hotel details page (Checks hotel name in toolbar)
    private fun i_am_still_on_the_hotel_details_page(hotelName: String) {
        hotelDetailsRobot.verifyHotelDetailToolbar(hotelName)
    }

    //AND 3 rates are available
    private fun three_rates_are_available() {
        hotelDetailsRobot.view_rates(1)
        hotelDetailsRobot.containsFirstRateBoxWithName(rateName = advanceRate)
        hotelDetailsRobot.containsSecondRateBoxWithName(rateName = flexRate)
        hotelDetailsRobot.containsThirdRateBoxWithName(rateName = nonFlexRate)
    }

    //WHEN I view the rates
    private fun i_view_the_rates(position: Int) {
        hotelDetailsRobot.view_rates(position)
    }

    //THEN I should be able to see the rate name
    //Flex and associated cancellation policy with the description
    //Pay now or on arrival. Cancel up to 1pm on arrival day.
    private fun i_should_be_able_to_see_the_rate_name_and_associated_cancellation_policy_with_the_description() {
        hotelDetailsRobot.containsFirstRateBoxWithName(rateName = advanceRate)
        hotelDetailsRobot.containsSecondRateBoxWithName(rateName = flexRate)
        hotelDetailsRobot.containsThirdRateBoxWithName(rateName = nonFlexRate)

        hotelDetailsRobot.containsFirstRateBoxWithDescription(description = advanceDescription)
        hotelDetailsRobot.containsSecondRateBoxWithDescription(description = flexDescription)
        hotelDetailsRobot.containsThirdRateBoxWithDescription(description = nonFlexDescription)
    }

    // AND Flex rate is available
    private fun flex_rate_is_available() {
        hotelDetailsRobot.containsSecondRateBoxWithName(rateName = flexRate)
        hotelDetailsRobot.containsSecondRateBoxWithDescription(description = flexDescription)
        hotelDetailsRobot.verifyFlexAvailability(position = flexRatePosition)
    }

    // WHEN I select the rate type Flex
    private fun i_select_the_rate_type_flex(position: Int) {
        hotelDetailsRobot.view_rates(position)
    }

    // AND click on Book Now
    private fun click_on_book_now() {
        hotelDetailsRobot.chooseRate(flexRatePosition)
    }

    // THEN I should be redirected to the 'Customise your stay' page
    private fun i_should_be_redirected_to_the_customise_your_stay_page(toolbarTitle: String) {
        addExtrasRobot.verifyPage(toolbarTitle)
    }

    // GIVEN I am on the 'Customise your stay' page
    private fun i_am_on_the_add_extras_page(toolbarTitle: String) {
        addExtrasRobot.verifyPage(toolbarTitle)
    }

    //    WHEN I click on continue button
    private fun when_i_click_on_continue_button() {
        addExtrasRobot.continueWithDefaults()
    }

    //    THEN I should be redirected to the Personal details page
    private fun i_should_be_redirected_to_the_log_in_page(toolbarTitle: String) {
        loginRobot.verifyPage(toolbarTitle)
    }

    // GIVEN I am on the Log in page
    private fun i_am_on_the_login_page(toolbarTitle: String) {
        loginRobot.verifyPage(toolbarTitle)
    }

    // AND I tap on the ‘Continue as a guest’
    private fun i_tap_on_the_continue_as_a_guest() {
        loginRobot.continueAsGuest()
    }

    //    THEN I should be redirected to the ‘Your details’ page
    private fun i_should_be_redirected_to_the_your_details_page(toolbarTitle: String) {
        guestDetailsRobot.verifyPage(toolbarTitle)
    }

    //  GIVEN I am on the ‘Your details’ page
    private fun i_am_on_the_your_details_page(toolbarTitle: String) {
        guestDetailsRobot.verifyPage(toolbarTitle)
    }

    //  AND I have filled in all valid inputs to the fields
    private fun i_have_filled_in_all_valid_inputs_to_the_fields() {
        guestDetailsRobot.bookerFirstName("Joshua")
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

    //  WHEN I click on ‘Continue to payment’ button
    private fun i_have_on_continue_to_payment_button() {
        guestDetailsRobot.continueWithDetails()
    }

    // THEN I should be redirected to the Review & Book page
    private fun i_should_be_redirected_to_the_review_and_book_page() {
        reviewBookingRobot.verifyPage("Review and book")
    }

    // GIVEN I am on the Review & Book page 
    private fun i_am_on_the_review_and_book_page() {
        reviewBookingRobot.verifyPage("Review and book")
    }

    // WHEN I view the Review & Book page I should be able to see the rate name Flex and associated cancellation policy with the description
    private fun i_view_the_review_and_book_page_i_should_be_able_to_see_the_rate_name_Flex() {
        reviewBookingRobot.verifyRateName(flexRate)
    }

    // AND Debit Card (4582 6200 0000 0037) should be prefilled
    private fun debit_card_should_be_prefilled() {
        reviewBookingRobot.successPaymentCardNumberDisplayed("0037")
    }

    // AND when I enter the correct CVV
    private fun when_i_enter_the_correct_cvv() {
        reviewBookingRobot.enterCvv(123)
    }

    //  WHEN I click on 'Confirm booking' button
    private fun i_click_on_confirm_booking_button() {
        reviewBookingRobot.clickMakeBooking()
    }

    // THEN I should see the Confirmation page
    private fun i_should_see_the_confirmation_page(toolbarTitle: String) {
        addExtrasRobot.verifyPage(toolbarTitle)
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

    private fun stubValidationSuccess() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/payment/validations"))
                .thenReturnFile(200, "apiTest/bin-validation-success.json")
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

    companion object {
        const val successResponseCode = 200

        const val advanceRate: String = "Advance"
        const val flexRate: String = "Flex"
        const val nonFlexRate: String = "Non-Flex"

        const val advanceDescription: String = "Pay now. Change arrival date. Fully refundable up to 28 days before arrival."
        const val flexDescription: String = "Pay now or later. Cancel up to 1 pm on arrival day. "
        const val nonFlexDescription: String = "Pay now. No amends.No Cancellation beyond 24hrs of booking."

        const val flexRatePosition: Int = 2
    }

}