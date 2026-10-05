package com.whitbread.premierinn.data.search.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.whitbread.premierinn.data.common.BRAND_HUB
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.MIGRATION_1_2
import com.whitbread.premierinn.data.common.PremierInnDatabase
import com.whitbread.premierinn.data.search.entity.SearchEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 *
 */
@RunWith(AndroidJUnit4::class)
class SearchEntityDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var database: PremierInnDatabase
    lateinit var dao: SearchEntityDao
    lateinit var item1: SearchEntity
    lateinit var item1CloneWithSamePk: SearchEntity
    lateinit var item2: SearchEntity
    lateinit var item3: SearchEntity
    lateinit var item4: SearchEntity
    lateinit var item5: SearchEntity
    lateinit var item6: SearchEntity
    lateinit var item7: SearchEntity

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, PremierInnDatabase::class.java)
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .build()

        dao = database.searchEntityDao()

        item1 = SearchEntity("NW3", 0.0, 0.0, DATE_16_02_2018, "LONMON", BRAND_PI)
        item1CloneWithSamePk = SearchEntity("NW3", 1.0, 1.0, DATE_16_02_2018, "LONMON", BRAND_PI)

        item2 = SearchEntity("NW4", 0.0, 1.0, DATE_17_02_2018)
        item3 = SearchEntity("NW5", 2.0, 0.0, DATE_18_02_2018)
        item4 = SearchEntity("NW6", 0.0, 1.0, DATE_19_02_2018)
        item5 = SearchEntity("London", 0.0, 2.0, DATE_20_02_2018, "LONDON", BRAND_HUB)
        item6 = SearchEntity("NW8", 3.0, 4.0, DATE_21_02_2018)
        item7 = SearchEntity("NW9", 3.0, 4.0, DATE_22_02_2018)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun get_zero_items_With_empty_table() {
        dao.getAll().test()
                .assertValue(emptyList())
                .assertComplete()
    }


    @Test
    fun get_zero_items_When_all_deleted() {
        dao.insertOrReplace(item1)
        dao.deleteAll()

        dao.getAll().test()
                .assertValue(emptyList())
                .assertComplete()
    }

    @Test
    fun get_one_item_When_inserted() {
        dao.insertOrReplace(item1)

        dao.getAll().test()
                .assertValue(listOf(item1))
                .assertComplete()
    }

    @Test
    fun get_one_updated_item_When_replaced() {

        dao.insertOrReplace(item1)
        dao.insertOrReplace(item1CloneWithSamePk)

        dao.getAll().test()
                .assertValue(listOf(item1CloneWithSamePk))
                .assertComplete()
    }

    @Test
    fun get_max_5_recent_items_sorted_by_date() {

        dao.insertOrReplace(item1)
        dao.insertOrReplace(item2)
        dao.insertOrReplace(item3)
        dao.insertOrReplace(item4)
        dao.insertOrReplace(item5)
        dao.insertOrReplace(item6)
        dao.insertOrReplace(item7)

        dao.getAll().test()
                .assertValue(Arrays.asList(item7, item6, item5, item4, item3))
                .assertComplete()
    }

    companion object {

        val DATE_16_02_2018 = 1518789245000L
        val DATE_17_02_2018 = 1518875645000L
        val DATE_18_02_2018 = 1518962045000L
        val DATE_19_02_2018 = 1519048445000L
        val DATE_20_02_2018 = 1519134845000L
        val DATE_21_02_2018 = 1519221245000L
        val DATE_22_02_2018 = 1519307645000L
    }
}