package com.whitbread.premierinn.domain.apprating.repository

/**
 *
 */
interface AppRatingRepository {
    fun isAppRated(): Boolean
    fun setAsAppRated(value: Boolean)
    fun numberOfBookingsSinceAppRating(): Int
    fun incrementNumberOfBookings()
}