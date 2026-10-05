package com.whitbread.premierinn.data.booking.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.data.booking.entity.*
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.MockErrorLogger
import com.whitbread.premierinn.data.common.PremierInnDatabase
import com.whitbread.premierinn.data.common.addBookingLastModifiedDateTriggers
import com.whitbread.premierinn.domain.common.RoomType
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

@RunWith(AndroidJUnit4::class)
class BookingDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var database: PremierInnDatabase
    lateinit var dao: BookingDao
    lateinit var logger: ErrorLogger

    val booking = BookingEntity(bookingReference = "ref1", leadGuestFullName = "Mr John Smith",
            arrivalDate = LocalDate.ofEpochDay(DATE_16_02_2018), departureDate = LocalDate.ofEpochDay(DATE_17_02_2018),
            hotelCode = "LONMON", hotelName = "London Covent Garden", numberOfRooms = 1,
            rateClass = "Flex", totalCost = PriceEntity(10f, "GBP"),
            prePaidAmount = null, isCanceled = false, isLinkedToAccount = false,
            leadGuestSurname = "Smith", amendRestrictions = AmendRestrictionsEntity(nights = false, rooms = false,
            guestNames = false, upsell = false, restricted = false), bookingStatus = "FUTURE",
            isCheckInOnlineAvailable = false, isCheckOutOnlineAvailable = false, hotelCountry = "")

    @Before
    fun setup() {
        logger = MockErrorLogger()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun findSortedNonPastBookings() {
        createDb(disableTriggers = true)

        val testSubscriber = TestObserver<List<BookingEntity>>()

        val expectedItem1 = booking.copy(isLinkedToAccount = true)
        val expectedItem2 = booking.copy(bookingReference = "ref123", arrivalDate = LocalDate.ofEpochDay(DATE_07_08_2078), departureDate = LocalDate.ofEpochDay(DATE_09_08_2078))
        val expectedItem3 = booking.copy(bookingReference = "ref456", arrivalDate = LocalDate.ofEpochDay(DATE_30_12_2017), departureDate = LocalDate.ofEpochDay(DATE_01_01_2018))
        val expectedItem4 = booking.copy(bookingReference = "ref987", arrivalDate = LocalDate.ofEpochDay(DATE_07_08_2078), departureDate = LocalDate.ofEpochDay(DATE_09_08_2078), isCanceled = true)
        val unExpectedItem5 = booking.copy(bookingReference = "ref078", arrivalDate = LocalDate.ofEpochDay(DATE_30_12_2017), departureDate = LocalDate.ofEpochDay(DATE_30_12_2017))

        dao.getSortedBookings().subscribe(testSubscriber)

        dao.insertOrReplace(listOf(expectedItem4, expectedItem1, unExpectedItem5, expectedItem2, expectedItem3))

        testSubscriber
                .assertValues(emptyList(), listOf(expectedItem2, expectedItem1, unExpectedItem5, expectedItem3, expectedItem4))
                .assertNotComplete()
    }

    /**
     * We need to manually create the DB at the start of each test because for some tests we do
     * not want to enable the triggers as this makes it difficult to compare final values since
     * last_modified is based on time
     */
    private fun createDb(disableTriggers: Boolean = false) {
        var dbBuilder = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java)

        if (!disableTriggers) {
            dbBuilder = dbBuilder.addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    addBookingLastModifiedDateTriggers(db)
                }
            })
        }

        database = dbBuilder.allowMainThreadQueries()
                .build()

        dao = database.bookingEntityDao()
        dao.deleteAll()
    }

    @Test
    fun getBookingById() {
        createDb(disableTriggers = true)
        assertThat(dao.getBookingById("ref1")).isNull()

        dao.insertOrReplace(booking)

        assertThat(dao.getBookingById("ref1")).isEqualTo(booking)
    }

    @Test
    fun getBookingUpdatesById() {
        createDb(disableTriggers = true)
        val testSubscriber = TestSubscriber<List<BookingEntity>>()

        dao.getBookingUpdatesById("ref1").subscribe(testSubscriber)

        dao.insertOrReplace(booking)

        testSubscriber
                .assertValues(emptyList(), listOf(booking))
                .assertNotComplete()
    }

    @Test
    fun count() {
        createDb()
        assertThat(dao.count()).isEqualTo(0)
        dao.insertOrReplace(booking)
        assertThat(dao.count()).isEqualTo(1)
    }

    @Test
    fun updateLinkedAccountBookings() {
        createDb()
        val nonLinkedAccountExpectedItem0 = booking
        val linkedAccountItem = booking.copy(bookingReference = "ref012", isLinkedToAccount = true)
        val linkedAccountItem2 = linkedAccountItem.copy(bookingReference = "ref123", arrivalDate = LocalDate.ofEpochDay(DATE_07_08_2078), departureDate = LocalDate.ofEpochDay(DATE_09_08_2078))
        val linkedAccountItem3 = linkedAccountItem.copy(bookingReference = "ref456", arrivalDate = LocalDate.ofEpochDay(DATE_30_12_2017), departureDate = LocalDate.ofEpochDay(DATE_01_01_2018))
        val linkedAccountItem4 = linkedAccountItem.copy(bookingReference = "ref987", arrivalDate = LocalDate.ofEpochDay(DATE_07_08_2078), departureDate = LocalDate.ofEpochDay(DATE_09_08_2078), isCanceled = true)

        dao.insertOrReplace(listOf(nonLinkedAccountExpectedItem0, linkedAccountItem, linkedAccountItem2, linkedAccountItem3, linkedAccountItem4))

        dao.updateLinkedAccountBookings(listOf(nonLinkedAccountExpectedItem0, linkedAccountItem))

        assertThat(dao.count()).isEqualTo(3)
    }

    @Test
    fun lastModifiedDateSetOnInsert() {
        createDb()
        val timeOfInsertSeconds = System.currentTimeMillis() / 1000
        dao.insertOrReplace(booking)

        val bookingEntity = dao.getBookingById(booking.bookingReference)

        assertThat(bookingEntity!!.lastModified).isGreaterThan(timeOfInsertSeconds - 1)
    }

    @Test
    fun lastModifiedDateUpdatedOnUpdate() {
        createDb()
        dao.insertOrReplace(booking)
        val timeOfUpdateSeconds = System.currentTimeMillis() / 1000
        dao.insertOrReplace(booking.copy(departureDate = (LocalDate.ofEpochDay(booking.departureDate.toEpochDay() + ONE_DAY))))

        val bookingEntity = dao.getBookingById(booking.bookingReference)

        assertThat(bookingEntity!!.lastModified).isGreaterThan(timeOfUpdateSeconds - 1)
    }

    @Test
    fun deleteAll() {
        createDb()
        dao.insertOrReplace(booking)
        dao.deleteAll()
        assertThat(dao.count()).isEqualTo(0)
    }

    @Test
    fun deleteLinkedAndNotLinkedBookings() {
        createDb()
        val linkedAccountBooking = booking.copy(isLinkedToAccount = true, bookingReference = "1234")
        val anonymousBooking = booking.copy(isLinkedToAccount = false, bookingReference = "4321")

        dao.insertOrReplace(linkedAccountBooking)
        dao.insertOrReplace(anonymousBooking)

        dao.deleteAll()

        assertThat(dao.count()).isEqualTo(0)
    }

    @Test
    fun deleteAllWithLinkedAccountRetainingCancelled() {
        createDb()
        val linkedAccountBooking1 = booking.copy(isLinkedToAccount = true, bookingReference = "1234")
        val linkedAccountBooking2 = booking.copy(isLinkedToAccount = true, bookingReference = "5678")
        val cancelledAccountBooking1 = booking.copy(isLinkedToAccount = true, bookingReference = "7890",isCanceled = true)
        val cancelledAccountBooking2 = booking.copy(isLinkedToAccount = true, bookingReference = "5623",isCanceled = true)
        val anonymousBooking = booking.copy(isLinkedToAccount = false, bookingReference = "4321")

        dao.insertOrReplace(linkedAccountBooking1)
        dao.insertOrReplace(linkedAccountBooking2)
        dao.insertOrReplace(cancelledAccountBooking1)
        dao.insertOrReplace(cancelledAccountBooking2)
        dao.insertOrReplace(anonymousBooking)

        dao.deleteAllWithLinkedAccountRetainingCancelled()

        assertThat(dao.count()).isEqualTo(3)
    }

    @Test
    fun updateAsCanceled() {
        createDb(disableTriggers = true)

        dao.insertOrReplace(booking)

        assertThat(booking.isCanceled).isFalse()

        dao.updateAsCanceled(booking.bookingReference)

        val updatedItem = dao.getBookingById(booking.bookingReference)

        assertThat(updatedItem?.isCanceled).isTrue()
    }

    @Test
    fun savingBookingWithRooms() {
        createDb(disableTriggers = true)
        val bookingWithRooms = BookingWithRooms()

        bookingWithRooms.booking = booking
        bookingWithRooms.rooms = listOf(RoomEntity(
                bookingReference = "ref1",
                roomId = "room1",
                lettingType = "DB",
                roomType = RoomType.FAMILY,
                numberOfAdults = 1,
                numberOfChildren = 0,
                leadGuest = RoomEntityGuest(title = "Mrdr", firstName = "First", lastName = "Last")))
        dao.insertOrReplaceBookingWithRooms(bookingWithRooms, logger )

        val test = dao.getBookingWithRoomsUpdatesById("ref1").test()
        test.assertValueAt(0) { list ->
            val rooms = list[0].rooms
            rooms.size == 1 && rooms[0].bookingReference == "ref1"
                    && rooms[0].roomId == "room1" && rooms[0].roomType == RoomType.FAMILY
                    && rooms[0].leadGuest.title == "Mrdr"
                    && rooms[0].leadGuest.firstName == "First"
                    && rooms[0].leadGuest.lastName == "Last"
        }
    }

    @Test
    fun getUpcomingBooking() {
        createDb(disableTriggers = true)

        val testSubscriber = TestObserver<BookingEntity>()

        val item1 = booking.copy(isLinkedToAccount = true)
        val item2 = booking.copy(bookingReference = "ref123", arrivalDate = LocalDate.ofEpochDay(DATE_07_08_2078), departureDate = LocalDate.ofEpochDay(DATE_09_08_2078))
        val item3 = booking.copy(bookingReference = "ref456", arrivalDate = LocalDate.ofEpochDay(DATE_30_12_2017), departureDate = LocalDate.ofEpochDay(DATE_01_01_2018), isCanceled = true)
        val expectedItem4 = booking.copy(bookingReference = "ref987", arrivalDate = LocalDate.ofEpochDay(DATE_16_02_2018), departureDate = LocalDate.ofEpochDay(DATE_17_02_2018))

        dao.insertOrReplace(listOf(item3, expectedItem4, item1, item2))
        dao.getUpcomingBooking(TODAY_01_01_2018).subscribe(testSubscriber)

        testSubscriber
                .assertValue(expectedItem4)
                .assertComplete()
    }

    companion object {

        const val TODAY_01_01_2018 = 1514764800L
        const val DATE_30_12_2017 = 1514592000L
        const val DATE_01_01_2018 = 1514764800L
        const val DATE_16_02_2018 = 1518739200L
        const val DATE_17_02_2018 = 1518825600L
        const val DATE_07_08_2078 = 3427056000L
        const val DATE_09_08_2078 = 3427228800L
        const val ONE_DAY = 86400000L
    }
}