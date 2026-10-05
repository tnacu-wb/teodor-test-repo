package com.whitbread.premierinn.common.appsflyer

import android.content.Context
import com.appsflyer.AFInAppEventParameterName.*
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class AppsFlyerHelperTest {

    private val context: Context = mock()
    private val mockAppsFlyer: AppsFlyerLib = mock()

    @BeforeEach
    fun setUp() {
        val appsFlyerField = AppsFlyerHelper::class.java.getDeclaredField("appsFlyer")
        appsFlyerField.isAccessible = true
        appsFlyerField.set(null, mockAppsFlyer)
    }

    @Test
    fun `logBookingConfirmationEvent should log correct event with parameters`() {
        val params = AppsFlyerBookingConfirmationParams(
            bookingReference = "BR12345",
            revenue = 100.0,
            currency = "GBP",
            hotelCode = "LONEUS"
        )

        AppsFlyerHelper.logBookingConfirmationEvent(context, params)

        verify(mockAppsFlyer).logEvent(
            eq(context),
            eq(AFInAppEventType.PURCHASE),
            argThat {
                this[ORDER_ID] == "BR12345"
                        && this[REVENUE] == 100.0
                        && this[CONTENT_ID] == "hotel"
                        && this[CURRENCY] == "GBP"
                        && this[CONTENT_TYPE] == "LONEUS"
            }
        )
    }
}