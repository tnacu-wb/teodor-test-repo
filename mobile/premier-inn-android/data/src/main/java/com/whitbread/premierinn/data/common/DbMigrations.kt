package com.whitbread.premierinn.data.common

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 *
 */
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // For exported Schemas see /src/test/resources/schemas
        database.execSQL(
                " CREATE TABLE recent_search (search_term TEXT NOT NULL, lat REAL NOT NULL, lon REAL NOT NULL, "
                        + " date_created INTEGER NOT NULL, hotel_id TEXT, hotel_brand TEXT, PRIMARY KEY(search_term)) "
        )
        // Copy old data from recentSearch table to new recent_search table
        database.execSQL(
                "INSERT INTO recent_search (search_term, lat, lon, date_created, hotel_id) "
                        + " SELECT searchText, locationLat, locationLong, dateAdded, hotelId FROM recentSearch "
        )
        // Delete old recentSearch table
        database.execSQL("DROP TABLE IF EXISTS recentSearch")
        // Create trigger on recent_search INSERT to clear old items
        database.execSQL(
                "CREATE TRIGGER delete_after_recent_search_insert "
                        + " AFTER INSERT ON recent_search "
                        + " BEGIN "
                        + " DELETE FROM recent_search WHERE search_term NOT IN ("
                        + " SELECT search_term FROM recent_search ORDER BY date_created DESC LIMIT 5"
                        + " ); "
                        + " END; "
        )

        database.execSQL(
                " CREATE TABLE booking "
                        + " (booking_reference TEXT NOT NULL, lead_guest_surname TEXT NOT NULL, arrival_date INTEGER NOT NULL, "
                        + " departure_date INTEGER NOT NULL, hotel_code TEXT NOT NULL, hotel_name TEXT NOT NULL, "
                        + " num_of_rooms INTEGER NOT NULL, lead_guest_full_name TEXT NOT NULL, "
                        + " rate_class TEXT NOT NULL, total_cost_amount REAL, "
                        + " total_cost_currency TEXT, prepaid_cost_amount REAL, prepaid_cost_currency TEXT, "
                        + " check_in_online_opened INTEGER NOT NULL, checked_in INTEGER NOT NULL, amendable INTEGER NOT NULL, "
                        + " canceled INTEGER NOT NULL, linked_to_account INTEGER NOT NULL, PRIMARY KEY(booking_reference) )"
        )
    }
}
val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                """ALTER TABLE booking
            ADD COLUMN last_modified INTEGER NOT NULL DEFAULT 0"""
        )
        addBookingLastModifiedDateTriggers(database)
    }
}

fun addBookingLastModifiedDateTriggers(database: SupportSQLiteDatabase) {
    database.execSQL(
            """CREATE TRIGGER update_booking_created_timestamp
            AFTER INSERT ON booking
            BEGIN
            UPDATE booking SET last_modified = strftime('%s','now')
            WHERE booking_reference = new.booking_reference;
            END;"""
    )
    database.execSQL(
            """CREATE TRIGGER update_booking_modified_timestamp
            AFTER UPDATE ON booking
            WHEN new.last_modified = 0 OR new.last_modified = old.last_modified
            BEGIN
            UPDATE booking SET last_modified = strftime('%s','now')
            WHERE booking_reference = new.booking_reference;
            END;"""
    )
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // adding 13 hours (46800000 milliseconds) to the timestamp to stay on the same date after removing the hours part (by dividing by 24h = 86400000 milliseconds)
        // this should work for all bookings except for those made while in timezone GMT-11
        database.execSQL("""UPDATE booking SET arrival_date = (arrival_date + 46800000)/86400000, departure_date = (departure_date + 46800000)/86400000""")
        database.execSQL(
                """CREATE TABLE IF NOT EXISTS room
             (booking_reference TEXT NOT NULL, roomId TEXT NOT NULL,
             roomType TEXT NOT NULL, title TEXT NOT NULL,
             numberOfAdults INTEGER NOT NULL,
             firstName TEXT NOT NULL, lastName TEXT NOT NULL,
             PRIMARY KEY(booking_reference, roomId))"""
        )
    }
}
val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                """CREATE TABLE IF NOT EXISTS reservation 
            (reservation_reference TEXT NOT NULL, reservation_arrival_date INTEGER NOT NULL, 
             reservation_departure_date INTEGER NOT NULL, reservation_hotel_code TEXT NOT NULL, 
             PRIMARY KEY(reservation_reference))"""
        )

        database.execSQL(
                """CREATE TABLE IF NOT EXISTS amended_reservation 
            (session_id TEXT NOT NULL, amended_reservation_reference TEXT NOT NULL, 
            amended_reservation_arrival_date INTEGER NOT NULL, amended_reservation_departure_date INTEGER NOT NULL, 
            amended_reservation_hotel_code TEXT NOT NULL, 
            PRIMARY KEY(amended_reservation_reference))"""
        )

        database.execSQL(
                """CREATE TABLE IF NOT EXISTS room_criteria 
            (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, room_id TEXT NOT NULL, room_type TEXT NOT NULL, 
            adults_count INTEGER NOT NULL, children_count INTEGER NOT NULL, cot INTEGER NOT NULL, 
            fk_reservation_reference TEXT, fk_amended_reservation_reference TEXT, 
            FOREIGN KEY(fk_amended_reservation_reference) REFERENCES amended_reservation(amended_reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE , 
            FOREIGN KEY(fk_reservation_reference) REFERENCES reservation(reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE )"""
        )

        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_criteria_fk_reservation_reference ON room_criteria (fk_reservation_reference)""")
        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_criteria_fk_amended_reservation_reference ON room_criteria (fk_amended_reservation_reference)""")

        database.execSQL(
                """CREATE TABLE IF NOT EXISTS room_guest 
            (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, room_id TEXT NOT NULL, title TEXT NOT NULL, first_name TEXT NOT NULL, 
            last_name TEXT NOT NULL, guest_history_number TEXT, fk_reservation_reference TEXT, fk_amended_reservation_reference TEXT, 
            FOREIGN KEY(fk_amended_reservation_reference) REFERENCES amended_reservation(amended_reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE , 
            FOREIGN KEY(fk_reservation_reference) REFERENCES reservation(reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE )"""
        )

        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_guest_fk_reservation_reference ON room_guest (fk_reservation_reference)""")
        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_guest_fk_amended_reservation_reference ON room_guest (fk_amended_reservation_reference)""")
    }
}
val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE reservation ADD COLUMN reservation_cancelable INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE amended_reservation ADD COLUMN amended_reservation_cancelable INTEGER NOT NULL DEFAULT 0""")

        database.execSQL("""ALTER TABLE booking ADD COLUMN amend_restriction_nights INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE booking ADD COLUMN amend_restriction_rooms INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE booking ADD COLUMN amend_restriction_guest_names INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE booking ADD COLUMN amend_restriction_upsell INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE booking ADD COLUMN amend_restriction_restricted INTEGER NOT NULL DEFAULT 0""")

        database.execSQL(
                """CREATE TABLE IF NOT EXISTS room_upsell
            (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, room_id TEXT NOT NULL, legend TEXT NOT NULL, code TEXT NOT NULL,
            category TEXT NOT NULL, quantity INTEGER NOT NULL, posting_date INTEGER NOT NULL, fk_reservation_reference TEXT,
            fk_amended_reservation_reference TEXT, unit_cost_amount REAL NOT NULL, unit_cost_currency TEXT NOT NULL,
            FOREIGN KEY(fk_amended_reservation_reference) REFERENCES amended_reservation(amended_reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE ,
            FOREIGN KEY(fk_reservation_reference) REFERENCES reservation(reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE )"""
        )

        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_criteria_fk_reservation_reference_room_id ON room_criteria (fk_reservation_reference, room_id)""")
        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_criteria_fk_amended_reservation_reference_room_id ON room_criteria (fk_amended_reservation_reference, room_id)""")

        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_guest_fk_reservation_reference_room_id ON room_guest (fk_reservation_reference, room_id)""")
        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_guest_fk_amended_reservation_reference_room_id ON room_guest (fk_amended_reservation_reference, room_id)""")

        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_upsell_fk_reservation_reference ON room_upsell (fk_reservation_reference)""")
        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_upsell_fk_amended_reservation_reference ON room_upsell (fk_amended_reservation_reference)""")
    }
}
val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_upsell_fk_reservation_reference_room_id_code_posting_date ON room_upsell (fk_reservation_reference, room_id, code, posting_date)""")
        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_upsell_fk_amended_reservation_reference_room_id_code_posting_date ON room_upsell (fk_amended_reservation_reference, room_id, code, posting_date)""")

        database.execSQL(
                """CREATE TABLE IF NOT EXISTS room_breakdown 
            (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, room_id TEXT NOT NULL,
            unit_cost_amount REAL NOT NULL, unit_cost_currency TEXT NOT NULL,
            fk_reservation_reference TEXT, fk_amended_reservation_reference TEXT, 
            FOREIGN KEY(fk_amended_reservation_reference) REFERENCES amended_reservation(amended_reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE , 
            FOREIGN KEY(fk_reservation_reference) REFERENCES reservation(reservation_reference) ON UPDATE CASCADE ON DELETE CASCADE )"""
        )

        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_breakdown_fk_reservation_reference_room_id ON room_breakdown (fk_reservation_reference, room_id)""")
        database.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS index_room_breakdown_fk_amended_reservation_reference_room_id ON room_breakdown (fk_amended_reservation_reference, room_id)""")

        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_breakdown_fk_reservation_reference ON room_breakdown (fk_reservation_reference)""")
        database.execSQL("""CREATE INDEX IF NOT EXISTS index_room_breakdown_fk_amended_reservation_reference ON room_breakdown (fk_amended_reservation_reference)""")
    }
}
val MIGRATION_7_8: Migration = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE IF NOT EXISTS upsell_item_available
            (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, description TEXT NOT NULL, foodUpsell INTEGER NOT NULL, 
            freeBreakfastTrigger INTEGER NOT NULL, availableForChildren INTEGER NOT NULL, code TEXT NOT NULL, 
            freeBreakfastCode TEXT NOT NULL, legend TEXT NOT NULL, freeBreakfastOption INTEGER NOT NULL, 
            attachments TEXT NOT NULL, unit_cost_amount REAL NOT NULL, unit_cost_currency TEXT NOT NULL)""")
    }
}

val MIGRATION_8_9: Migration = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                """CREATE TABLE IF NOT EXISTS dashboard
                    (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    type TEXT NOT NULL, content_hotel_image TEXT, content_hotel_name TEXT, content_hotel_code TEXT,
                    content_checked_in INTEGER, content_confirmation_number TEXT, content_arrival_date INTEGER, 
                    content_departure_date INTEGER, content_rooms TEXT, content_guests INTEGER, 
                    content_actions TEXT, content_map_latitude REAL, content_map_longitude REAL)""")

        database.execSQL("""CREATE TABLE IF NOT EXISTS dashboard_recent_search 
            (search_term TEXT NOT NULL, hotel_code TEXT NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL, hotel_brand TEXT NOT NULL, 
             arrival_date INTEGER NOT NULL, departure_date INTEGER NOT NULL, rooms_count INTEGER NOT NULL, adults TEXT NOT NULL, 
             children TEXT NOT NULL, infants TEXT NOT NULL, cots TEXT NOT NULL, room_type_codes TEXT NOT NULL, date_created INTEGER NOT NULL, 
             PRIMARY KEY(search_term, hotel_code, latitude, longitude, hotel_brand, arrival_date, departure_date, rooms_count, adults, children, infants, cots, room_type_codes))""")
    }
}

val MIGRATION_9_10: Migration = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE dashboard ADD COLUMN content_frequent_bookings TEXT""")
    }
}

val MIGRATION_10_11: Migration = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN business_booker INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_11_12: Migration = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE room ADD COLUMN numberOfChildren INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_12_13: Migration = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE room ADD COLUMN lettingType TEXT NOT NULL DEFAULT '' """)
    }
}

val MIGRATION_13_14: Migration = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN booking_status TEXT NOT NULL DEFAULT '' """)
    }
}

val MIGRATION_14_15: Migration = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN is_check_in_online_available INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_15_16: Migration = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN basket_status TEXT""")
    }
}

val MIGRATION_16_17: Migration = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN is_check_out_online_available INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_17_18: Migration = object : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN hotel_country TEXT""")
    }
}


val MIGRATION_18_19: Migration = object : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN employee_booking INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_19_20: Migration = object : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE booking ADD COLUMN cancellable INTEGER NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE booking ADD COLUMN is_third_party_booking INTEGER NOT NULL DEFAULT 0""")
    }
}

val MIGRATION_20_21: Migration = object : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE room_guest ADD COLUMN email_address TEXT""")
    }
}

