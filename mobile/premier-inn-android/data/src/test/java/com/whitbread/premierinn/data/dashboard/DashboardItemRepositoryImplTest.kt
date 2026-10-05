package com.whitbread.premierinn.data.dashboard

import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.dashboard.dao.DashboardDao
import com.whitbread.premierinn.data.dashboard.repository.DashboardRepositoryImpl
import com.whitbread.premierinn.data.dashboard.repository.mapToDashboardEntity
import com.whitbread.premierinn.data.model.DashboardEntityFixture
import com.whitbread.premierinn.data.remote.DashboardApi
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

val DASHBOARD = DashboardEntityFixture.aDashboardItem()

class DashboardItemRepositoryImplTest {

    private val dashboardApi: DashboardApi = mockk()
    private val dashboardDao: DashboardDao = mockk()
    private val errorLogger: ErrorLogger = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    lateinit var repository: DashboardRepository

    @Before
    fun setUp() {
        repository = DashboardRepositoryImpl(dashboardApi, dashboardDao, errorLogger, deviceLocaleProvider)
    }

    @Test
    fun `should delete and insert dashboard to db on trigger success`() {
        every { dashboardApi.getDashboard(any(), any(), any(), any(), any(), any(), any()) } returns Single.just(listOf(DASHBOARD))
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        repository.getDashboardFromApi(authorization = null, reservationReference = "GAGR8211", surname = "Onabanjo",
                arrivalDate = LocalDate.parse("2020-11-26"), business = false, hasRecentSearches = true).test()

        verify {
            dashboardDao.deleteAndInsert(listOf(DASHBOARD.mapToDashboardEntity()))
        }
    }
}