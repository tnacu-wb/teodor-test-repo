package com.whitbread.premierinn.e2e

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.whitbread.premierinn.reviewbooking.ReviewBookingRobot
import com.whitbread.premierinn.utils.End2EndTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@End2EndTest
@RunWith(AndroidJUnit4::class)
//TODO: 3DS Try and Convert to 3CP
class BookingFlowTestPOA3DsOnEndToEndTest : BaseBookingFlowTest() {

    @Before
    fun init() {
        //TODO: 3DS Try and Convert to 3CP
        //buildDaggerGraphWithPayOnArrival3ds(true)
    }

    @Test
    fun loggedIn_User_Flex_Rate_Pay_On_Arrival() {
        stubApiCalls("VISA")

        executeBookingFlowFor("Flex")

        with(ReviewBookingRobot(context)) {
            verifyPayOnArrivalMessage()
            verifyStandardAmendAndCancelMessage()
            clickMakeBooking()
         //   verify3dsIsDisplayed()
        }
    }
}
