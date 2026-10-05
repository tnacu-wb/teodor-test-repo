package com.whitbread.premierinn.reviewbooking

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule.grant
import com.squareup.spoon.SpoonRule
//import com.whitbread.premierinn.common.dagger.ComponentsManager
import com.whitbread.premierinn.reviewbooking.ReviewBookingPowerPack.BookingOutcome.*
import com.whitbread.premierinn.reviewbooking.ReviewBookingPowerPack.CardType.BUSINESS
import com.whitbread.premierinn.reviewbooking.ReviewBookingPowerPack.CardType.MASTERCARD
import com.whitbread.premierinn.reviewbooking.ReviewBookingPowerPack.PayNowLaterChoice.*
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ReviewBookActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<ReviewBookActivity>(ReviewBookActivity::class.java, true, false)

    @Rule
    @JvmField
    var grantAccessRule = grant(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    @Rule
    @JvmField
    var screenshotRule = SpoonRule()


    @Rule
    @JvmField
    var testName = TestName()

    lateinit var context: Context
    lateinit var reviewBookRobot: ReviewBookingRobot
    lateinit var editGuestRobot: EditGuestDetailsRobot
    lateinit var input: ReviewBookingInput
    lateinit var sharedPrefs: SharedPreferences
    lateinit var powerPack: ReviewBookingPowerPack

    private val validBacsCardNumber = "3089500100045005041"

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        reviewBookRobot = ReviewBookingRobot(context)
        editGuestRobot = EditGuestDetailsRobot()
//        sharedPrefs = ComponentsManager.getInstance().appComponent.preferences()
//        powerPack = ReviewBookingPowerPack(preferences = sharedPrefs)
        RESTMockServer.reset()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun storedCard_Flex_Mastercard_Detailsshown() {
        input = powerPack.createInput(rateType = "Flex", storedCard = true, payNowLaterChoice = PAY_NOW_LATER_NOT_CHOSEN, loggedIn = true,
                cardType = MASTERCARD, bookingResponse = SUCCESS)

        launchActivity(input)

        reviewBookRobot.successPaymentOptionsDisplayed()
                .successPayNowLaterOptionDisplayed()
                .successGuestNameDisplayed("Mr. Miguel Santos")
                .successPaymentCardTypeDisplayed("Mastercard")
                .successPaymentCardNumberDisplayed("1111")
                .successBillPayerNameDisplayed("Miguel Santos")
                .successGuestEmailDisplayed("email@email.com")
                .successCardExpirationDisplayed("03/29")
                .verifyPayOnArrivalMessage()
                .successPayOnArrivalTotalDisplayed()
                .successRateFlexDisplayed()
                .successTotalPriceDisplayed("£356.00")
                .clickMakeBooking()
                .success()
    }


    @Test
    fun bacs_stored_bacsOptionsShown() {
        input = powerPack.createInput(rateType = "Flex", storedCard = true, payNowLaterChoice =
        PAY_NOW_LATER_NOT_CHOSEN,
                loggedIn = true, cardType = BUSINESS, cnpInfoEntered = true, bookingResponse = SUCCESS)

        launchActivity(input)

        reviewBookRobot.successInputtedCnpInformationDisplayed()
                .toggleCnpPayment()
                .cnpMemorableWord("Password")
                .toggleOnCnpDinnerAllowance()
                .cnpDinnerAllowance("57")
                .toggleOnCnpAlcohol()
                .toggleOnCnpWifi()
                .toggleOnCnpParking()
                .clickMakeBooking()
                .success()
    }

    @Test
    fun paymentBreakdownOpens() {
        input = powerPack.createInput(rateType = "Flex", cardType = MASTERCARD)

        launchActivity(input)

        reviewBookRobot.paymentBreakdown()
                .successOpenPaymentBreakdown()
    }

    @Test
    fun updateGuestDetails() {
        input = powerPack.createInput(rateType = "Flex", cardType = MASTERCARD)

        launchActivity(input)

        reviewBookRobot.editGuestDetails()

        editGuestRobot
                .guestFirstName("Mark")
                .guestLastName("O'Meara")
                .guestEmail("mark@whitbread.com")
                .save()

        reviewBookRobot
                .successGuestNameDisplayed("Mr. Mark O'Meara")
                .successGuestEmailDisplayed("mark@whitbread.com")
    }


    @Test
    fun cvvShownForPayNow() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice = PAY_NOW, cardType =
        MASTERCARD)

        launchActivity(input)

        reviewBookRobot.verifyCvvIsDisplayed()
    }

    @Test
    fun cvvDisplayChangesWhenPaymentChoiceChanges() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice =
        PAY_NOW_LATER_NOT_CHOSEN, cardType = MASTERCARD, loggedIn = true)

        launchActivity(input)

        reviewBookRobot.selectsPayNow()
                .verifyCvvIsDisplayed()
                .selectsPayOnArrival()
                .verifyCvvNotDisplayed()
    }

    @Test
    fun testCvvShownForPayNowSelected() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice = PAY_NOW, cardType =
        MASTERCARD, loggedIn = true,
                storedCard = false)

        launchActivity(input)

        reviewBookRobot.verifyCvvIsDisplayed()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            screenshotRule.screenshot(activityRule.activity, "testCvvShownForPayNowSelected")
        }
    }

    @Test
    fun cvvNotShownForBacs() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice =
        PAY_NOW_LATER_NOT_CHOSEN, cardType = BUSINESS, loggedIn = true,
                storedCard = false)

        launchActivity(input)

        reviewBookRobot.verifyCvvNotDisplayed()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            screenshotRule.screenshot(activityRule.activity, "cvvNotShownForBacs")
        }
    }

    @Test
    fun makeBookingFails() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice = PAY_LATER, cardType =
        MASTERCARD, bookingResponse = FAILURE)

        launchActivity(input)

        reviewBookRobot
                .clickMakeBooking()
                .failure()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            screenshotRule.screenshot(activityRule.activity, "makeBookingFails")
        }
    }

    @Test
    fun makeBookingFailsThenSucceedsOnRetry() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice = PAY_LATER,
                cardType = MASTERCARD, bookingResponse = FAIL_THEN_SUCCEED)

        launchActivity(input)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            screenshotRule.screenshot(activityRule.activity, "makeBookingFailsThenSucceedsOnRetry")
        }

        reviewBookRobot
                .clickMakeBooking()
                .failure()
                .dismissDialog()
                .clickMakeBooking()
                .success()
    }

    @Test
    fun payNowNoPaymentOptionsDisplayed() {
        input = powerPack.createInput(payNowLaterChoice = PAY_NOW, rateType = "Saver", cardType =
        MASTERCARD)

        launchActivity(input)

        reviewBookRobot.successPaymentOptionsNotDisplayed()
    }

    @Test
    fun noAmendsOrCancellationMessageDisplayed() {
        input = powerPack.createInput(rateType = "Saver", cardType = MASTERCARD, amendmentAllowed =
        false, cancellationAllowed = false)

        launchActivity(input)

        reviewBookRobot.verifyNoAmendmentsOrCancellationsAllowedMessage()
    }

    @Test
    fun amendCancellationsAllowedMessageDisplayed() {
        input = powerPack.createInput(rateType = "Saver", cardType = MASTERCARD, amendmentAllowed =
        true, cancellationAllowed = true)

        launchActivity(input)

        reviewBookRobot.verifyStandardAmendAndCancelMessage()
    }

    @Test
    fun holdHasExpired() {
        input = powerPack.createInput(rateType = "Flex", payNowLaterChoice = PAY_LATER, cardType =
        MASTERCARD, bookingResponse = HOLD_EXPIRED)

        launchActivity(input)

        reviewBookRobot
                .clickMakeBooking()
                .failureHoldHasExpired()
                .dismissDialog()
                .successBackToLanding()
    }

    @Test
    fun changeToBacs_businessOptionsShown() {
        // Since tests goes to the Payment Details screen
        RESTMockServer.whenGET(RequestMatchers.pathContains("/payment/validations"))
                .thenReturnFile(200, "apiTest/bin-validation-bacs-success.json")

        input = powerPack.createInput(rateType = "Flex", storedCard = false, payNowLaterChoice =
        PAY_LATER, loggedIn = false,
                cardType = MASTERCARD)

        launchActivity(input)

        reviewBookRobot.changePaymentDetails()

        reviewBookRobot
                .successCnpSectionDisplayed()
                .successCnpPaymentToggleDisplayed()
    }

    @Test
    fun guestsInMultipleRoomsAreShown() {
        input = powerPack.createInput(rateType = "Flex", cardType = MASTERCARD, multipleRooms =
        true)

        launchActivity(input)

        reviewBookRobot
                .successRoom1Details()
                .successRoom2Details()
    }

    // TODO Test for hub cases

    private fun launchActivity(input: ReviewBookingInput) {
        activityRule.launchActivity(ReviewBookActivity.createIntent(context, input))
    }
}
