package com.whitbread.premierinn.domain.dashboard.usecase

import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetDashboard @Inject constructor(
        private val repository: DashboardRepository
) {
    operator fun invoke() : Observable<List<DashboardItem>> {
        return repository.getDashboardFromDb()
    }
}