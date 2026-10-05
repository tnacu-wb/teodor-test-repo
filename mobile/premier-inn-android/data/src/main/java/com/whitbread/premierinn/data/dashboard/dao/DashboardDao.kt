package com.whitbread.premierinn.data.dashboard.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.whitbread.premierinn.data.common.persistence.EntityDao
import com.whitbread.premierinn.data.dashboard.entity.DashboardEntity
import io.reactivex.Observable

@Dao
abstract class DashboardDao : EntityDao<DashboardEntity> {

    @Transaction
    @Query("SELECT * FROM dashboard")
    abstract fun getDashboard(): Observable<List<DashboardEntity>>

    @Query("DELETE FROM dashboard")
    abstract fun deleteDashboard()

    fun deleteAndInsert(dashboardEntity: List<DashboardEntity>) {
        deleteDashboard()
        insertAll(dashboardEntity)
    }
}