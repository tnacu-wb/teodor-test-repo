package com.whitbread.premierinn.data.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.booking.entity.BookingEntity
import com.whitbread.premierinn.data.booking.entity.RoomEntity
import com.whitbread.premierinn.data.dashboard.dao.DashboardDao
import com.whitbread.premierinn.data.dashboard.entity.DashboardEntity
import com.whitbread.premierinn.data.recentsearch.dao.RecentSearchDao
import com.whitbread.premierinn.data.recentsearch.entity.RecentSearchEntity
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.reservation.dao.ReservationDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownDao
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownEntity
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity
import com.whitbread.premierinn.data.roomupsell.RoomUpsellDao
import com.whitbread.premierinn.data.roomupsell.RoomUpsellEntity
import com.whitbread.premierinn.data.search.dao.SearchEntityDao
import com.whitbread.premierinn.data.search.entity.SearchEntity
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableDao
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableEntity

/**
 *
 */
@TypeConverters(DbLocalDateConverter::class, DbRoomTypeConverter::class, UpsellAttachmentsListConverter::class,
        DashboardRoomConverter::class, DashboardActionConverter::class, RecentSearchDataConverter::class, DashboardFrequentBookingConverter::class)
@Database(entities = [SearchEntity::class, BookingEntity::class, RoomEntity::class,
    ReservationEntity::class, AmendedReservationEntity::class,
    RoomCriteriaEntity::class, RoomGuestEntity::class, RoomUpsellEntity::class, RoomBreakdownEntity::class, UpsellItemAvailableEntity::class,
    DashboardEntity::class, RecentSearchEntity::class], version = 21)
abstract class PremierInnDatabase : RoomDatabase() {
    abstract fun searchEntityDao(): SearchEntityDao
    abstract fun bookingEntityDao(): BookingDao
    abstract fun reservationDao(): ReservationDao
    abstract fun amendReservationDao(): AmendedReservationDao
    abstract fun roomCriteriaDao(): RoomCriteriaDao
    abstract fun roomGuestDao(): RoomGuestDao
    abstract fun roomUpsellDao(): RoomUpsellDao
    abstract fun roomBreakdownDao(): RoomBreakdownDao
    abstract fun upsellItemAvailableDao(): UpsellItemAvailableDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun recentSearchDao(): RecentSearchDao
}