package com.whitbread.premierinn.e2e

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.whitbread.premierinn.addextras.AddExtrasRobot
//import com.whitbread.premierinn.common.dagger.ComponentsManager
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.guestdetails.GuestDetailsRobot
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.hoteldetails.HotelDetailsRobot
import com.whitbread.premierinn.mybookings.MyBookingsRobot
import com.whitbread.premierinn.reviewbooking.ReviewBookingRobot
import com.whitbread.premierinn.searchresults.SearchResultsInput
import com.whitbread.premierinn.utils.replaceJSONTemplate
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

@RunWith(AndroidJUnit4::class)
class SemiFlexHubBookingPayNowDebitCardTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HotelDetailsActivity::class.java, true, false)
    private lateinit var intentInput: HotelDetailsInput
    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val reservationId: String = "WiAeFVuIlL1wOwsf"
    private val hotelName: String = "hub London Covent Garden"
    private val hotelCode: String = "LONSTM"
    private val roomTypeCode: String = "DB"
    private val confirmationNumber: String = "AVPR273063"
    private val availabilityJson: String = "availability-1room-bigger.json"
    private val threeDSNotRequiredJson: String = "three_d_s_not_required.json"
    private val customerResponseJson = "customer_test_with_VISA_Debit.json"
    private val hubLondonCoventGardenHoldOkJson = "lonstm_hold_ok.json"
    private val auth0LoginJson = "auth0_login_OK.json"
    private val bookingPayNowJson = "booking-OK-PN.json"
    private val reservationJson = "reservation_response.json"

    /** Prerequisite:
     * User logged in
     * Saved Debit Card (4582 6200 0000 0037)
     *
     * Scenario: SemiFlex Hub booking (PayNow with Debit Card)
     *
     * GIVEN I am on the hotel details page
     * AND 3 rates are available
     * WHEN I view the rates
     * THEN I should be able to see the rate name
     * Semi Flex and associated cancellation policy with the description
     * 'Pay now. Change arrival date. Fully refundable up to 3 days before arrival.'
     *
     * GIVEN I am on the hotel details page
     * AND Semi Flex rate is available
     * WHEN I select the rate type Semi Flex
     * AND click on Book Now
     * THEN I should be redirected to the 'Customise your stay' page
     *
     * GIVEN I am on the 'Customise your stay' page
     * WHEN I click on continue button
     * THEN I should be redirected to the Personal details page
     *
     * GIVEN I am on the Personal details page
     * AND I have filled in all valid inputs in to the fields
     * WHEN I click on 'Continue to final step' button
     * THEN I should be redirected to the Review & Book page
     * AND I should be able to see the rate name Semi Flex and associated cancellation policy with the description
     * AND the correct Total Cost
     *
     * GIVEN I am on the Review & Book page
     * AND I have selected the 'Pay Now' option
     * AND saved Debit Card (4582 6200 0000 0037)details
     * AND when I enter the correct CVV number
     * WHEN I click on 'Confirm booking' button
     * THEN I should see the Confirmation page
     * AND I should be able to see the rate name Semi Flex
     * AND the correct Total of the booking
     *
     */

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

//        sharedPrefs = ComponentsManager.getInstance().appComponent.preferences()
//        editor = sharedPrefs.edit()
        setLoggedIn()

        launchHotelDetailsActivity()
    }

    @Test
    fun semi_flex_hub_booking_pay_now_with_debit_card() {
        //FIRST GIVEN
        i_am_on_the_hotel_details_page()
        three_rates_are_available()
        i_view_the_rates(1)
        i_should_be_able_to_see_the_rate_name_and_associated_cancellation_policy_with_the_description()

        //SECOND GIVEN
        i_am_still_on_the_hotel_details_page()
        semi_flex_rate_is_available()
        click_on_book_now()
        i_should_be_redirected_to_the_customise_your_stay_page()

        //THIRD GIVEN
        i_am_on_the_customise_your_stay_page()
        when_i_click_on_continue_button()
        i_should_be_redirected_to_the_personal_details_page()

        //FOURTH GIVEN
        i_am_on_the_personal_details_page()
        i_click_on_continue_to_final_step_button()
        i_should_be_redirected_to_the_review_and_book_page()
        i_should_be_able_to_see_the_rate_name()
        the_correct_total_cost()

        //FIFTH GIVEN
        i_am_on_the_review_and_book_page()
        i_have_selected_the_Pay_Now_option()
        i_have_filled_in_valid_debit_card()
        i_click_on_confirm_booking_button()
        i_should_see_the_confirmation_page()
        i_should_be_able_to_see_the_rate_name_Semi_Flex()
        the_correct_total_of_the_booking()
    }

    //Prerequisite:
    //User logged in
    private fun setLoggedIn() {
        editor.putString(SimplePersistenceManagerImpl.Constants.KEY_USERNAME, username).apply()
        editor.putString(SimplePersistenceManagerImpl.Constants.KEY_PASSWORD, password).apply()
    }

    //GIVEN I am on the hotel details page (Checks hotel name)
    private fun i_am_on_the_hotel_details_page() {
        HotelDetailsRobot().verifyHotelDetailPage(hotelName)
    }

    //AND 3 rates are available
    private fun three_rates_are_available() {
        HotelDetailsRobot().view_rates(1)
        HotelDetailsRobot().containsAlternativeFirstRateBoxWithName(rateName = advanceRate)
        HotelDetailsRobot().containsAlternativeSecondRateBoxWithName(rateName = semiFlexRate)
        HotelDetailsRobot().containsAlternativeThirdRateBoxWithName(rateName = flexRate)
    }

    //WHEN I view the rates
    private fun i_view_the_rates(position: Int) {
        HotelDetailsRobot().view_rates(position)
    }

    //THEN I should be able to see the rate name
    //Semi Flex and associated cancellation policy with the description
    //Pay now or on arrival. Cancel up to 1pm on arrival day.
    private fun i_should_be_able_to_see_the_rate_name_and_associated_cancellation_policy_with_the_description() {
        HotelDetailsRobot().containsAlternativeFirstRateBoxWithName(rateName = advanceRate)
        HotelDetailsRobot().containsAlternativeSecondRateBoxWithName(rateName = semiFlexRate)
        HotelDetailsRobot().containsAlternativeThirdRateBoxWithName(rateName = flexRate)

        HotelDetailsRobot().containsAlternativeFirstRateBoxWithDescription(description = advanceDescription)
        HotelDetailsRobot().containsAlternativeSecondRateBoxWithDescription(description = semiFlexDescription)
        HotelDetailsRobot().containsAlternativeThirdRateBoxWithDescription(description = flexDescription)
    }

    //GIVEN I am on the hotel details page (Checks hotel name in toolbar)
    private fun i_am_still_on_the_hotel_details_page() {
        HotelDetailsRobot().verifyHotelDetailToolbar(hotelName)
    }

    //AND Semi Flex rate is available
    private fun semi_flex_rate_is_available() {
        HotelDetailsRobot().containsAlternativeSecondRateBoxWithName(rateName = semiFlexRate)
        HotelDetailsRobot().containsAlternativeSecondRateBoxWithDescription(description = semiFlexDescription)
        HotelDetailsRobot().verifyAlternativeSemiFlexAvailability(position = semiFlexRatePosition)
    }

    //WHEN I select the rate type Semi Flex
    //AND click on Book Now
    private fun click_on_book_now() {
        HotelDetailsRobot().chooseAlternativeRate(semiFlexRatePosition)
    }

    //THEN I should be redirected to the 'Customise your stay' page
    private fun i_should_be_redirected_to_the_customise_your_stay_page() {
        AddExtrasRobot().verifyPage("Add extras")
    }

    //GIVEN I am on the 'Customise your stay' page
    private fun i_am_on_the_customise_your_stay_page() {
        AddExtrasRobot().verifyPage("Add extras")
    }

    //WHEN I click on continue button
    private fun when_i_click_on_continue_button() {
        AddExtrasRobot().continueWithDefaults()
    }

    //THEN I should be redirected to the Personal details page
    private fun i_should_be_redirected_to_the_personal_details_page() {
        AddExtrasRobot().verifyPage("Your details")
    }

    //GIVEN I am on the Personal details page
    private fun i_am_on_the_personal_details_page() {
        AddExtrasRobot().verifyPage("Your details")
    }

    //WHEN I click on 'Continue to final step' button
    private fun i_click_on_continue_to_final_step_button() {
        GuestDetailsRobot().continueWithDetails()
    }

    //THEN I should be redirected to the Review & Book page
    private fun i_should_be_redirected_to_the_review_and_book_page() {
        ReviewBookingRobot(context).verifyPage("Review and book")
    }

    //AND I should be able to see the rate name Semi Flex and associated cancellation policy with the description
    private fun i_should_be_able_to_see_the_rate_name() {
        ReviewBookingRobot(context).verifyRateName(semiFlexRate)
    }

    //AND the correct Total Cost
    private fun the_correct_total_cost() {
        ReviewBookingRobot(context).successTotalPriceDisplayed(totalPrice)
    }

    //GIVEN I am on the Review & Book page
    private fun i_am_on_the_review_and_book_page() {
        ReviewBookingRobot(context).verifyPage("Review and book")
    }

    //AND I have selected the 'Pay Now' option
    private fun i_have_selected_the_Pay_Now_option() {
        ReviewBookingRobot(context).selectsPayNow()
    }

    //AND saved Debit Card (4582 6200 0000 0037)details
    //AND when I enter the correct CVV number
    private fun i_have_filled_in_valid_debit_card() {
        ReviewBookingRobot(context).verifyCvvIsDisplayed()
        ReviewBookingRobot(context).enterCvv(123)
    }

    //WHEN I click on 'Confirm booking' button
    private fun i_click_on_confirm_booking_button() {
        ReviewBookingRobot(context).clickMakeBooking()
    }

    //THEN I should see the Confirmation page
    private fun i_should_see_the_confirmation_page() {
        AddExtrasRobot().verifyPage("Booking details")
    }

    //AND I should be able to see the rate name Semi Flex
    private fun i_should_be_able_to_see_the_rate_name_Semi_Flex() {
        MyBookingsRobot().verifyRateName(rateName = semiFlexRate)
    }

    //AND the correct Total of the booking
    private fun the_correct_total_of_the_booking() {
        MyBookingsRobot().verifyBookingTotalPrice(totalPrice = totalPrice)
    }

    private fun launchHotelDetailsActivity() {
        intentInput = createInputIntent()
        stubCalls()
        launchScreen(intentInput)
    }

    private fun createInputIntent(): HotelDetailsInput {
        return HotelDetailsInput.builder()
                .cameFromMapView(false)
                .distanceFromSearchedLocation(0.0f)
                .hotelCode(hotelCode)
                .hotelImageUrl(null)
                .hotelName(hotelName)
                .searchResultsInput(SearchResultsInput.builder()
                        .adults(listOf(1))
                        .children(emptyList())
                        .infants(emptyList())
                        .roomTypeCodes(listOf(roomTypeCode))
                        .cots(listOf(false))
                        .placeName(hotelName)
                        .numRooms(1)
                        .arrivalDate(LocalDate.now())
                        .departureDate(LocalDate.now().plusDays(1))
                        .latitude(51.527736f)
                        .longitude(-0.129068f)
                        .build()
                ).build()
    }

    private fun stubCalls() {
        stubApiCallsAvailability()
        stubHotelApi()
        stubHoldApi()
        stubLoginApi()
        stubCustomerApi()
        stubPaymentApi()
        stubBookingApi()
        stubReservationApi()
    }

    private fun launchScreen(input: HotelDetailsInput) {
        activityRule.launchActivity(HotelDetailsActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, input))
    }

    private fun stubApiCallsAvailability() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/booking/hotels/${hotelCode}/availability"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/${hotelCode}/$availabilityJson")
    }

    private fun stubHotelApi() {
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/hotels/${hotelCode}"))
                .thenReturnFile(successResponseCode, "apiTest/hotels/${hotelCode}.json")
    }

    private fun stubHoldApi() {
        RESTMockServer.whenPUT(RequestMatchers.pathEndsWith("/booking/hotels/${reservationId}/hold?hotelBrand=PI"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/hold/$hubLondonCoventGardenHoldOkJson")
    }

    private fun stubLoginApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/oauth/token"))
                .thenReturnFile(successResponseCode, "apiTest/auth/hotels/login/$auth0LoginJson")
    }

    private fun stubCustomerApi() {
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/customers/hotels/pi5@test.com"))
                .thenReturnString(successResponseCode,
                        replaceJSONTemplate(this::class.java, "/apiTest/customers/hotels/$customerResponseJson", 3))
    }

    private fun stubPaymentApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/payment/hotels"))
                .thenReturnFile(successResponseCode, "apiTest/payment/hotels/$threeDSNotRequiredJson")
    }

    private fun stubBookingApi() {
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/booking/hotels/$reservationId"))
                .thenReturnFile(successResponseCode, "apiTest/booking/hotels/booking-complete/$bookingPayNowJson")
    }

    private fun stubReservationApi() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("/reservation/hotels/$confirmationNumber"))
                .thenReturnString(successResponseCode, replaceJSONTemplate(
                        this::class.java,"/apiTest/reservation/hotels/$reservationJson", semiFlexRate, hotelCode, confirmationNumber, totalPrice))
    }

    companion object {
        const val successResponseCode = 200
        const val username: String = "pi5@test.com"
        const val password: String = "Password@1"

        const val advanceRate: String = "Advance"
        const val semiFlexRate: String = "Semi-Flex"
        const val flexRate: String = "Flex"

        const val advanceDescription: String = "Pay now. Change arrival date. Fully refundable up to 28 days before arrival."
        const val semiFlexDescription: String = "Pay now. Change arrival date. Fully refundable up to 3 days before arrival."
        const val flexDescription: String = "Pay now. Cancel up to 1 pm on arrival day."

        const val totalPrice: String = "£51.00"

        const val semiFlexRatePosition: Int = 2
    }
}