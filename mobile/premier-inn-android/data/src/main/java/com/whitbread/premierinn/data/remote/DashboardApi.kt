package com.whitbread.premierinn.data.remote

import io.reactivex.Single
import org.threeten.bp.LocalDate
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// TODO: Bart remove after migration as we dont have dashboard migrated yet
interface DashboardApi {
    @GET("/dashboard")
    fun getDashboard(@Header("Authorization") authorization: String?,
                     @Query("confirmationNumber") confirmationNumber: String?,
                     @Query("surname") surname: String?,
                     @Query("arrivalDate") arrivalDate: LocalDate?,
                     @Query("business") business: Boolean,
                     @Query("language") language: String,
                     @Query("hasRecentSearches") hasRecentSearches: Boolean): Single<List<DashboardApiContract.DashboardResponse>>
}