package com.whitbread.premierinn.data.recentsearch.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.whitbread.premierinn.data.common.MIGRATION_8_9
import com.whitbread.premierinn.data.common.PremierInnDatabase
import com.whitbread.premierinn.data.recentsearch.entity.RecentSearchEntity
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import java.util.*

@RunWith(AndroidJUnit4::class)
class RecentSearchDaoTest {

    lateinit var database: PremierInnDatabase
    lateinit var dao: RecentSearchDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                PremierInnDatabase::class.java)
                .addMigrations(MIGRATION_8_9)
                .allowMainThreadQueries()
                .build()

        dao = database.recentSearchDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun get_zero_items_with_empty_table() {
        val result = dao.getRecentSearches(today.toEpochDay())
        assertEquals(emptyList<RecentSearchEntity>(), result)
    }

    @Test
    fun get_count_of_items_inserted() {
        dao.insertOrReplace(createItem("London", Date().time))
        val result = dao.count()
        assertEquals(1, result)
    }

    @Test
    fun insert_or_replace_entry() {
        val item1 = createItem("Sydney", Date().time)
        val item2 = createItem("Delhi", Date().time.plus(1))
        val duplicateItem1 = createItem("Sydney", Date().time.plus(2))

        dao.insertOrReplace(item1)
        dao.insertOrReplace(item2)
        val result1 = dao.getRecentSearches(today.toEpochDay())

        dao.insertOrReplace(duplicateItem1)
        val result2 = dao.getRecentSearches(today.toEpochDay())

        assertEquals(listOf(item2, item1), result1)
        assertEquals(listOf(duplicateItem1, item2), result2)
        assertEquals(2, dao.count())
    }

    @Test
    fun get_max_two_recent_searches() {
        dao.insertOrReplace(createItem("Ben Nevis", Date().time))
        dao.insertOrReplace(createItem("Snowdonia", Date().time.plus(1)))
        dao.insertOrReplace(createItem("Scafell Pike", Date().time.plus(2)))

        assertEquals(3, dao.count())
        assertEquals(2, dao.getRecentSearches(today.toEpochDay()).size)
    }

    @Test
    fun get_recent_searches_within_a_week() {
        dao.insertOrReplace(createItem("Ben Nevis", Date().time, LocalDate.parse("2020-12-16")))
        dao.insertOrReplace(createItem("Snowdonia", Date().time.plus(1), LocalDate.parse("2020-12-25")))
        dao.insertOrReplace(createItem("Scafell Pike", Date().time.plus(2), LocalDate.parse("2021-07-11")))

        assertEquals(3, dao.count())
        assertEquals(1, dao.getRecentSearchesWithinAWeek(today.toEpochDay(),
                oneWeek.toEpochDay()).size)
    }

    @Test
    fun max_table_size_is_ten_and_old_entries_are_deleted() {
        var count = 12
        while (count != 0) {
            dao.insertRecentSearch(createItem("London$count", Date().time.plus(count)))
            count --
        }
        assertEquals(10, dao.count())
    }

    @Test
    fun delete_all_recent_searches() {
        dao.insertOrReplace(createItem("Ben Nevis", Date().time))
        dao.insertOrReplace(createItem("Snowdonia", Date().time.plus(1)))
        dao.insertOrReplace(createItem("Scafell Pike", Date().time.plus(2)))

        dao.deleteAllRecentSearches()

        assertEquals(0, dao.count())
    }

    @Test
    fun get_booked_recent_search() {
        val expected = createItem("Holborn", Date().time)

        dao.insertOrReplace(expected)
        val actual = dao.getBookedRecentSearch(hotelCode = "LONHOL", arrivalDate = LocalDate.parse("2020-12-11").toEpochDay(),
                departureDate = LocalDate.parse("2020-12-12").toEpochDay(), roomsCount = 2, adults = "1, 2", children = "0, 2",
                infants = "2, 0", cots = "false, false")

        assertEquals(expected, actual)
    }

    private fun createItem(searchTerm: String, dateCreated: Long, arrivalDate : LocalDate = arrival) = RecentSearchEntity(
            searchTerm = searchTerm, hotelCode = "LONHOL", latitude = 51.555553F, longitude = 0.0949867F, hotelBrand = "PI",
            arrivalDate = arrivalDate, departureDate = LocalDate.parse("2020-12-12"), roomsCount = 2,
            adults = listOf(1, 2), children = listOf(0, 2), infants = listOf(2, 0), cots = listOf(false, false),
            roomTypeCodes = listOf("DB", "FAM"), dateCreated = dateCreated)

    companion object {
        val today: LocalDate = LocalDate.parse("2020-12-10")
        val oneWeek: LocalDate = LocalDate.parse("2020-12-17")
        val arrival: LocalDate = LocalDate.parse("2020-12-11")
    }
}