package com.whitbread.premierinn.data.common

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate


import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DbMigrationsTest {

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            PremierInnDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    companion object {
        const val TEST_DB = "migration-test"
    }

    private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
            MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11,
            MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
            MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20, MIGRATION_20_21)

    @Test
    @Throws(IOException::class)
    fun arrivalAndDepartureTimestampMigration2To21() {
        val arrival = 1554371871000 // 4 April 2019 09:57:51 GMT
        val departure = 1554717485000   // 8 April 2019 09:58:05 GMT

        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO booking " +
                    "(booking_reference, lead_guest_surname, " +
                    "arrival_date, " +
                    "departure_date, " +
                    "hotel_code, hotel_name, num_of_rooms, lead_guest_full_name, rate_class, " +
                    "check_in_online_opened, checked_in, canceled, amendable, linked_to_account)" +
                    "VALUES " +
                    "(\"booking reference\", \"Surname\"," +
                    arrival + ", " +
                    departure + ", " +
                    "\"hotel code\", \"hotel name\", 2, \"Full Name\", \"A\", " +
                    "1, 1, 1, 1, 1)")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 21, true, MIGRATION_2_3,
                MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
                MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19,
                MIGRATION_19_20, MIGRATION_20_21)

        val roomDbBuilder = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java, TEST_DB)
        val bookingEntityDao = roomDbBuilder.build().bookingEntityDao()

        val bookingEntity = bookingEntityDao.getBookingById("booking reference")!!
        Assert.assertEquals(LocalDate.of(2019, 4, 4), bookingEntity.arrivalDate)
        Assert.assertEquals(LocalDate.of(2019, 4, 8), bookingEntity.departureDate)
    }

    @Test
    @Throws(IOException::class)
    fun arrivalAndDepartureTimestampMigration3To21_GmtMinus10() {
        val arrival = 1554372000000 // 4 April 2019 10h UTC = 4 April 2019 GMT-10
        val departure = 1554717600000   // 8 April 2019 10h UTC = 8 April 2019 GMT-10

        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO booking " +
                    "(booking_reference, lead_guest_surname, " +
                    "arrival_date, " +
                    "departure_date, " +
                    "hotel_code, hotel_name, num_of_rooms, lead_guest_full_name, rate_class, " +
                    "check_in_online_opened, checked_in, canceled, amendable, linked_to_account, " +
                    "last_modified) " +
                    "VALUES " +
                    "(\"booking reference\", \"Surname\"," +
                    arrival + ", " +
                    departure + ", " +
                    "\"hotel code\", \"hotel name\", 2, \"Full Name\", \"A\", " +
                    "1, 1, 1, 1, 1," +
                    "12345)")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 21, true, MIGRATION_3_4,
                MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
            MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19,
            MIGRATION_19_20, MIGRATION_20_21)

        val roomDbBuilder = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java, TEST_DB)
        val bookingEntityDao = roomDbBuilder.build().bookingEntityDao()

        val bookingEntity = bookingEntityDao.getBookingById("booking reference")!!
        Assert.assertEquals(LocalDate.of(2019, 4, 4), bookingEntity.arrivalDate)
        Assert.assertEquals(LocalDate.of(2019, 4, 8), bookingEntity.departureDate)
    }

    @Test
    @Throws(IOException::class)
    fun arrivalAndDepartureTimestampMigration3To21_GmtPlus11() {
        val arrival = 1554296400000 // 3 April 2019 13h UTC = 4 April 2019 GMT+11
        val departure = 1554642000000   // 7 April 2019 13h UTC = 8 April 2019 GMT+11

        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO booking " +
                    "(booking_reference, lead_guest_surname, " +
                    "arrival_date, " +
                    "departure_date, " +
                    "hotel_code, hotel_name, num_of_rooms, lead_guest_full_name, rate_class, " +
                    "check_in_online_opened, checked_in, canceled, amendable, linked_to_account, " +
                    "last_modified) " +
                    "VALUES " +
                    "(\"booking reference\", \"Surname\"," +
                    arrival + ", " +
                    departure + ", " +
                    "\"hotel code\", \"hotel name\", 2, \"Full Name\", \"A\", " +
                    "1, 1, 1, 1, 1," +
                    "12345)")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 21, true, MIGRATION_3_4,
                MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
            MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19,
            MIGRATION_19_20, MIGRATION_20_21)

        val roomDbBuilder = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java, TEST_DB)
        val bookingEntityDao = roomDbBuilder.build().bookingEntityDao()

        val bookingEntity = bookingEntityDao.getBookingById("booking reference")!!
        Assert.assertEquals(LocalDate.of(2019, 4, 4), bookingEntity.arrivalDate)
        Assert.assertEquals(LocalDate.of(2019, 4, 8), bookingEntity.departureDate)
    }

    @Test
    @Throws(IOException::class)
    fun arrivalAndDepartureTimestampMigration3To21_GmtPlus13() {
        val arrival = 1554289200000 // 3 April 2019 11h UTC = 4 April 2019 GMT+13
        val departure = 1554634800000   // 7 April 2019 11h UTC = 8 April 2019 GMT+13

        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO booking " +
                    "(booking_reference, lead_guest_surname, " +
                    "arrival_date, " +
                    "departure_date, " +
                    "hotel_code, hotel_name, num_of_rooms, lead_guest_full_name, rate_class, " +
                    "check_in_online_opened, checked_in, canceled, amendable, linked_to_account, " +
                    "last_modified) " +
                    "VALUES " +
                    "(\"booking reference\", \"Surname\"," +
                    arrival + ", " +
                    departure + ", " +
                    "\"hotel code\", \"hotel name\", 2, \"Full Name\", \"A\", " +
                    "1, 1, 1, 1, 1," +
                    "12345)")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 21, true, MIGRATION_3_4,
                MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
            MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19,
            MIGRATION_19_20, MIGRATION_20_21)

        val roomDbBuilder = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java, TEST_DB)
        val bookingEntityDao = roomDbBuilder.build().bookingEntityDao()

        val bookingEntity = bookingEntityDao.getBookingById("booking reference")!!
        Assert.assertEquals(LocalDate.of(2019, 4, 4), bookingEntity.arrivalDate)
        Assert.assertEquals(LocalDate.of(2019, 4, 8), bookingEntity.departureDate)
    }

    @Test
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 2).apply {
            close()
        }

        Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, PremierInnDatabase::class.java, TEST_DB)
        .addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }
    }
}