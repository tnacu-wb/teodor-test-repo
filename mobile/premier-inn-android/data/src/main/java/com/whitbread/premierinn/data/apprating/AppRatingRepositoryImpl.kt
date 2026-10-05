package com.whitbread.premierinn.data.apprating

import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import javax.inject.Inject

class AppRatingRepositoryImpl @Inject constructor(private val persistance: SimplePersistenceManager) : AppRatingRepository {

    override fun isAppRated(): Boolean = persistance.isAppRated()

    override fun setAsAppRated(value: Boolean) = persistance.setAppAsRated(value)

    override fun numberOfBookingsSinceAppRating(): Int = persistance.numberOfBookingsSinceAppRating()

    override fun incrementNumberOfBookings() = persistance.incrementNumberOfBookings()

}