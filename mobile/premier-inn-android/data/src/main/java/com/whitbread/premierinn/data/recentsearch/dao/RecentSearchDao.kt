package com.whitbread.premierinn.data.recentsearch.dao

import androidx.room.*
import com.whitbread.premierinn.data.common.persistence.EntityDao
import com.whitbread.premierinn.data.recentsearch.entity.RecentSearchEntity
import org.threeten.bp.LocalDate
import java.util.*

private const val DASHBOARD_MAX_RESULTS = 2

@Dao
abstract class RecentSearchDao : EntityDao<RecentSearchEntity> {

    @Query("SELECT COUNT(*) FROM dashboard_recent_search")
    abstract fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrReplace(entity: RecentSearchEntity)

    @Query("""DELETE FROM dashboard_recent_search WHERE date_created NOT IN 
                     (SELECT date_created FROM dashboard_recent_search ORDER BY date_created DESC LIMIT 10)""")
    abstract fun deleteOldSearches()

    @Transaction
    open fun insertRecentSearch(entity: RecentSearchEntity) {
        insertOrReplace(entity.copy(dateCreated = Date().time))
        deleteOldSearches()
    }

    @Query("SELECT * FROM dashboard_recent_search WHERE arrival_date >= :todayDate ORDER BY date_created DESC LIMIT $DASHBOARD_MAX_RESULTS")
    abstract fun getRecentSearches(todayDate: Long = LocalDate.now().toEpochDay()): List<RecentSearchEntity>

    @Query("SELECT * FROM dashboard_recent_search WHERE arrival_date >= :todayDate AND arrival_date <= :oneWeek ORDER BY date_created DESC LIMIT $DASHBOARD_MAX_RESULTS")
    abstract fun getRecentSearchesWithinAWeek(todayDate: Long = LocalDate.now().toEpochDay(), oneWeek: Long =  LocalDate.now().plusWeeks(1).toEpochDay()): List<RecentSearchEntity>

    @Query("DELETE FROM dashboard_recent_search")
    abstract fun deleteAllRecentSearches()

    @Query("""SELECT * FROM dashboard_recent_search WHERE hotel_code = :hotelCode 
                     AND arrival_date = :arrivalDate AND departure_date = :departureDate 
                     AND rooms_count = :roomsCount AND adults = :adults AND children = :children 
                     AND infants = :infants AND cots = :cots""")
    abstract fun getBookedRecentSearch(hotelCode: String, arrivalDate: Long, departureDate: Long, roomsCount: Int,
                                       adults: String, children: String, infants: String, cots: String): RecentSearchEntity?
}