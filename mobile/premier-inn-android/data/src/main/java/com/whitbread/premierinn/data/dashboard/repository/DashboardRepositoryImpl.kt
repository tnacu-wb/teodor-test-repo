package com.whitbread.premierinn.data.dashboard.repository

import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.throwApiExceptionIfNullable
import com.whitbread.premierinn.data.dashboard.dao.DashboardDao
import com.whitbread.premierinn.data.remote.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.DashboardApi
import com.whitbread.premierinn.data.remote.DashboardApiContract
import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
        private val dashboardApi: DashboardApi,
        private val dashboardDao: DashboardDao,
        private val errorLogger: ErrorLogger,
        private val deviceLocaleProvider: DeviceLocaleProvider
) : DashboardRepository {

    override fun getDashboardFromApi(authorization: String?, reservationReference: String?, surname: String?,
                                     arrivalDate: LocalDate?, business: Boolean, hasRecentSearches: Boolean): Completable {
        return dashboardApi.getDashboard(
            if (authorization != null) "$AUTHORIZATION_BEARER $authorization" else null,
            reservationReference, surname, arrivalDate, business, deviceLocaleProvider.getDeviceLanguage(), hasRecentSearches)
            .doOnSuccess { dashboard ->
                throwApiExceptionIfNullable(dashboard)
                storeDashboard(dashboard)
            }
            .doOnError { errorLogger.logException(it) }
            .ignoreElement()
    }

    private fun storeDashboard(dashboard: List<DashboardApiContract.DashboardResponse>) {
        dashboardDao.deleteAndInsert(dashboard.toEntity())
    }

    override fun getDashboardFromDb(): Observable<List<DashboardItem>> {
        return dashboardDao.getDashboard().map { it.toDashboardItems() }
    }

    override fun clearDashboard() {
        dashboardDao.deleteDashboard()
    }
}