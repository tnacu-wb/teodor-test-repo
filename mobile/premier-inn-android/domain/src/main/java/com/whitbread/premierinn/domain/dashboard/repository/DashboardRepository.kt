package com.whitbread.premierinn.domain.dashboard.repository

import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.LocalDate

interface DashboardRepository {
    fun getDashboardFromApi(authorization: String?, reservationReference: String?, surname: String?,
                            arrivalDate: LocalDate?, business: Boolean, hasRecentSearches: Boolean) : Completable
    fun getDashboardFromDb() : Observable<List<DashboardItem>>
    fun clearDashboard()
}