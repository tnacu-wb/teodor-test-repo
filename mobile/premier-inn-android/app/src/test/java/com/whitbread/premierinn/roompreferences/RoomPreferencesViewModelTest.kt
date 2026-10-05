package com.whitbread.premierinn.roompreferences

import com.whitbread.premierinn.api.response.InstanceFactory.create
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Passport
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerBookingPreferences
import com.whitbread.premierinn.utils.TestSchedulerRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class RoomPreferencesViewModelTest {

    @get:Rule
    val rxRule = TestSchedulerRule()

    private val getCustomerMock: GetCustomer = mockk()
    private val updateCustomerBookingPreferencesMock: UpdateCustomerBookingPreferences = mockk()
    private val analyticsMock: TrackingAnalytics = mockk()

    private lateinit var roomPreferencesViewModel: RoomPreferencesViewModel

    @Before
    fun setup() {
        every {
            analyticsMock.track(AnalyticsConstants.ScreenState.ROOM_PREFERENCES,
                AnalyticsConstants.Type.MY_PREMIER_INN)
        } just Runs
    }

    @Test
    fun `When customer is fetched Then returns room Preferences`() {
        val successCustomerResponse : AccountApiContract.CustomerResponse = create(AccountApiContract.CustomerResponse::class.java, "apiTest/customer-success.json")
        val customerDomain = successCustomerResponse.toDomain()

        every { getCustomerMock.getCustomerFromSharedPref() } returns customerDomain

        roomPreferencesViewModel = RoomPreferencesViewModel(
            getCustomerMock,
            updateCustomerBookingPreferencesMock,
            analyticsMock)

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        val actual = roomPreferencesViewModel.currentState().roomConfiguration

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        assertEquals(customerDomain.bookingPreferences.roomCriteriaPreference, actual)
    }

    @Test
    fun `When customer is fetched without preferences Then returns no room Preferences`() {
        val successCustomerResponse :  AccountApiContract.CustomerResponse = create(
            AccountApiContract.CustomerResponse::class.java, "apiTest/customer-no-booking-prefs.json")
        val customerDomain = successCustomerResponse.toDomain()

        every { getCustomerMock.getCustomerFromSharedPref() } returns customerDomain

        roomPreferencesViewModel = RoomPreferencesViewModel(
            getCustomerMock,
            updateCustomerBookingPreferencesMock,
            analyticsMock
        )

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        val actual = roomPreferencesViewModel.currentState().roomConfiguration

        assertEquals(customerDomain.bookingPreferences.roomCriteriaPreference, actual)
    }

    @Test
    fun `When customer returns emptyCustomer Then display error`() {
        every { getCustomerMock.getCustomerFromSharedPref() } returns Customer.emptyCustomer("null")

        roomPreferencesViewModel = RoomPreferencesViewModel(
            getCustomerMock,
            updateCustomerBookingPreferencesMock,
            analyticsMock
        )

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        val actual = roomPreferencesViewModel.currentState().getCustomer

        assertEquals(actual, Customer.emptyCustomer("null"))
    }



    companion object TestData {
        val customerName = FullName(
            title = "Ms",
            firstName = "Android",
            lastName = "bot"
        )

        val customerContact = Contact(
            email = "android.bot@gmail.com",
            mobile = "07544678768",
            telephone = "07544678768"
        )

        val RoomCriteriaCustomer = RoomCriteria(
            numberOfInfants = 0,
            numberOfChildren = 1,
            numberOfAdults = 2,
            includeCot = false,
            roomType = RoomType.FAMILY)

        val cardPaymentCard = PaymentCard(
            number = "************1111",
            expiryDate = "06/23",
            holdersFullName = "Android Bot",
            cardType = "")

        val bookingPreferences = BookingPreferences(0, RoomCriteriaCustomer)

        val params = UpdateCustomerBookingPreferences.Params(bookingPreferences)

        var customer = Customer(
            customerAccountID = "234234234",
            guestHistoryNumber = "G49253351",
            fullName = customerName,
            contact = customerContact,
            address = Address(line1 = "120 Holborn"),
            nationality = "GB",
            passport = Passport("", ""),
            businessUse = false,
            bookingPreferences = bookingPreferences,
            paymentCard = cardPaymentCard,
            carRegistration = "")

    }
}