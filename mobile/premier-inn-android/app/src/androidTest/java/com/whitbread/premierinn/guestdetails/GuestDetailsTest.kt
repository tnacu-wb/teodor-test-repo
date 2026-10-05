package com.whitbread.premierinn.guestdetails

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.api.request.booking.Breakfast
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.api.response.availability.AvailabilityAddress
import com.whitbread.premierinn.api.response.availability.BookingRule
import com.whitbread.premierinn.api.response.availability.File
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.common.BookingFlowInput
//import com.whitbread.premierinn.common.dagger.ComponentsManager
import com.whitbread.premierinn.common.mapper.toBookingPrice
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PASSWORD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_USERNAME
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.hoteldetails.DailyRateInput
import com.whitbread.premierinn.hoteldetails.toPriceParcelable
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.hoteldetails.SelectedHotel
import com.whitbread.premierinn.hoteldetails.SelectedRate
import com.whitbread.premierinn.summary.SummaryInput
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import io.appflate.restmock.utils.RequestMatchers.pathContains
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

@RunWith(AndroidJUnit4::class)
class GuestDetailsTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<GuestDetailsActivity>(GuestDetailsActivity::class.java, true, false)

    lateinit var context: Context
    lateinit var robot: GuestDetailsRobot
    lateinit var input: BookingFlowInput
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private val date1: LocalDate = LocalDate.of(2018, 1, 17)
    private val date2: LocalDate = LocalDate.of(2018, 1, 18)
    private val date3: LocalDate = LocalDate.of(2018, 1, 19)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

//        sharedPrefs = ComponentsManager.getInstance().appComponent.preferences()
//        editor = sharedPrefs.edit()
        editor.clear().apply()

        robot = GuestDetailsRobot()


        input = createInput(isMultipleRooms = false)

        RESTMockServer.reset()
    }

    @Test
    fun guestUser_autoUkAddress_success_formCompletion() {
        editor.putString(KEY_USERNAME, "user").apply()
        editor.putString(KEY_PASSWORD, "pass").apply()

        RESTMockServer.whenGET(pathContains("/customers/hotels"))
                .thenReturnFile(200, "apiTest/customer-success.json")

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.continueWithDetails()
                .successGoToReviewAndBook()
    }

    @Test
    fun guestUser_manualUkAddress_success_formCompletion() {

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .postCode("w140ry")
                .manualAddress()
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .address3("London")
                .continueWithDetails()
                .successGoToPaymentDetails()
    }

    @Test
    fun guestUser_manualUkWorkAddress_failure_formCompletion() {

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .work()
                .postCode("w140ry")
                .manualAddress()
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .address3("London")
                .chooseBusiness()
                .failure()
    }

    @Test
    fun guestUser_manualUkWorkAddress_formCompletion() {

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .work()
                .postCode("w140ry")
                .manualAddress()
                .company("Whitbread")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .address3("London")
                .chooseBusiness()
                .continueWithDetails()
                .successGoToPaymentDetails()
    }

    @Test
    fun guestUser_manualInternationalAddress_formCompletion() {

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .addressCountry("Jersey")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .chooseBusiness()
                .continueWithDetails()
                .successGoToPaymentDetails()
    }

    @Test
    fun guestUser_bookerNotStaying_SingleRoom_success_formCompletion() {
        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .addressCountry("Afghanistan")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .bookingButNotSayingToggle()
                .room1StayerFirstName("Markkkk")
                .room1StayerLastName("O'Meara")
                .continueWithDetails()
                .successGoToPaymentDetails()
    }

    @Test
    @Ignore
    fun guestUser_bookerNotStaying_MultipleRooms_success_formCompletion() {
        val multipleRoomInput = createInput(isMultipleRooms = true)
        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, multipleRoomInput))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .addressCountry("Afghanistan")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .bookingButNotSayingToggle()
                .room1StayerFirstName("Markkkk")
                .room1StayerLastName("O'Meara")
    }

    @Test
    fun loggedInUser_success_formCompletion() {
        editor.putString(KEY_USERNAME, "user").apply()
        editor.putString(KEY_PASSWORD, "pass").apply()

        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/oauth/token"))
                .thenReturnFile(200, "apiTest/auth/hotels/login/auth0_login_OK.json")

        RESTMockServer.whenGET(pathContains("/customers/hotels"))
                .thenReturnFile(200, "apiTest/customer-success.json")

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.continueWithDetails()
                .successGoToReviewAndBook()
    }

    @Test
    fun loggedInUser_success_formCompletion2() {
        editor.putString(KEY_USERNAME, "user").apply()
        editor.putString(KEY_PASSWORD, "pass").apply()

        RESTMockServer.whenGET(pathContains("/customers/hotels"))
                .thenReturnEmpty(400)

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .addressCountry("Jersey")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .chooseBusiness()
                .continueWithDetails()
                .successGoToPaymentDetails()
    }

    // Todo remember to uncomment after refactoring finished
    // This test is now crashing the app.
    @Test
    @Ignore
    fun loggedInUser_success_formCompletion3() {
        editor.putString(KEY_USERNAME, "user").apply()
        editor.putString(KEY_PASSWORD, "pass").apply()

        RESTMockServer.whenGET(pathContains("/customers/hotels"))
                .thenReturnEmpty(400)
                .thenReturnEmpty(400)

        activityRule.launchActivity(GuestDetailsActivity.createIntent(context, input))

        robot.bookerFirstName("John")
                .bookerLastName("Smith")
                .email("john.smith@test.com")
                .number("012345678")
                .home()
                .addressCountry("Jersey")
                .address1("Fitzjames Avenue")
                .address2("North End Road")
                .chooseBusiness()
                .continueWithDetails()
    }

    private fun createInput(isMultipleRooms: Boolean): BookingFlowInput {

        val selectedUpSellItem = UpsellItem.builder().code("11").legend("Premier Inn Breakfast")
                .price(PriceDomain.createWithGBPCurrency(10.50f).toBookingPrice())
                .description("Some description")
                .freeBreakfastOption(true)
                .freeBreakfastTrigger(true)
                .freeBreakfastCode("15")
                .foodUpsell(true)
                .availableForChildren(true)
                .files(listOf(File.create("/content/dam/global/restaurants/Global/Global Breakfast.pdf", "Breakfast menu")))
                .build()

        val rule1 = BookingRule.builder().policy(BookingRule.Policy.NOT_ALLOWED).type("CANCELLATION").days(0).build()

        val rule2 = BookingRule.builder().policy(BookingRule.Policy.NOT_ALLOWED).type("AMENDMENT").days(0).build()

        val masterCard = AcceptedCreditCard.builder().creditCardCode("AC").feeCurrency("").feeAmount("")
                .name("Mastercard Credit").listOrder(3).paymentOnly(false)
                .schemeLogo("/content/dam/global/booking/Mastercard.jpg").build()

        val hotelAddress = AvailabilityAddress.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .addressLine3("line3")
                .postcode("ec1n2td")
                .country("UK").build()

        val hotel = SelectedHotel.builder()
                .isHub(false)
                .code("hotelCode")
                .imageReference("imageRef")
                .name("hotelName").build()

        val selectedRate = SelectedRate.builder()
                .rateName("Flex")
                .description("Cancel or amend up to 1pm on day of arrival")
                .rateType("Flex")
                .code("RT315")
                .prepaymentRequired(false)
                .guaranteeRequired(true)
                .cardFeeApplies(false)
                .bookingRules(listOf(rule1, rule2)).build()

        val breakfasts = Breakfast.createBreakfasts(selectedUpSellItem, createRoomsInput(isMultipleRooms))

        val summaryInput = SummaryInput.builder()
                .hotel(hotel)
                .roomBookings(createRoomsInput(isMultipleRooms))
                .rate(selectedRate)
                .bookingId("bookingId")
                .upsellItems(listOf(selectedUpSellItem))
                .totalNights(1)
                .isAlternativeRoom(false)
                .prepaymentAllowed(false)
                .cityTaxForBusiness(false)
                .cityTaxForLeisure(false)
                .build()

        return BookingFlowInput.create(summaryInput,
            breakfasts, selectedUpSellItem, emptyList(), 100f, emptyList())
    }

    private fun createRoomsInput(isMultipleRooms: Boolean): List<RoomBooking> {
        val room1 = RoomBooking(type = RoomType.DOUBLE.code,
                lettingCode = "DBS",
                adults = 1,
                children = 0,
                cot = false,
                dailyRates = listOf(
                    DailyRateInput(date1, PriceDomain.createWithGBPCurrency(160f).toPriceParcelable()),
                    DailyRateInput(date2, PriceDomain.createWithGBPCurrency(115f).toPriceParcelable()),
                    DailyRateInput(date3, PriceDomain.createWithGBPCurrency(81f).toPriceParcelable())),
                cityTax = PriceDomain.createWithGBPCurrency(0f).toParcelable(),
                roomNumber = 1,
                infants = 0,
            baseRateAmount = 0f)

        return if (!isMultipleRooms) {
            listOf(room1)
        } else {
            val room2 = room1.copy(roomNumber = 2)
            listOf(room1, room2)
        }
    }
}