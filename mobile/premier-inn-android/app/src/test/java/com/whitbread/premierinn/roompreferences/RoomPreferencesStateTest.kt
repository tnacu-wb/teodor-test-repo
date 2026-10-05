package com.whitbread.premierinn.roompreferences

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Passport
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RoomPreferencesStateTest {

    @MockK
    lateinit var roomPreferencesState: RoomPreferencesState
    private val customerResult: AsyncResult.Success<Customer> = AsyncResult.Success(customer)
    private val roomCriteriaUpdateResult: AsyncResult.Success<RoomCriteria> = mockk()

    @Before
    fun setUp() {
        roomPreferencesState = RoomPreferencesState(customerResult, roomCriteriaUpdateResult)
    }

    @Test
    fun `When customer is being fetched Then loading is returned`() {
        roomPreferencesState = roomPreferencesState.copy(
                customer = AsyncResult.Loading(),
                roomCriteriaUpdateResult = roomCriteriaUpdateResult)
        assertEquals(roomPreferencesState.isLoading, true)
    }

    @Test
    fun `When customer is fetched Then customer is returned`() {
        roomPreferencesState = roomPreferencesState.copy(
                customer = customerResult,
                roomCriteriaUpdateResult = roomCriteriaUpdateResult)

        assertEquals(roomPreferencesState.roomConfiguration,
               customer.bookingPreferences.roomCriteriaPreference)
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

        val customer = Customer(
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