package com.whitbread.premierinn.data.upsellavailable

import androidx.room.*
import com.whitbread.premierinn.data.common.persistence.EntityDao
import io.reactivex.Observable

@Dao
abstract class UpsellItemAvailableDao : EntityDao<UpsellItemAvailableEntity> {

    @Transaction
    @Query("DELETE FROM upsell_item_available")
    abstract fun deleteUpsellItemsAvailable()

    @Transaction
    @Query("SELECT * FROM upsell_item_available")
    abstract fun getUpsellItemsAvailable(): Observable<List<UpsellItemAvailableEntity>>

    /**
     * Delete and insert upsellAvailable list data
     * **/
    @Transaction
    open fun updateUpsellItemsAvailable(listOfItemsAvailable: MutableList<UpsellItemAvailableEntity>) {
        deleteUpsellItemsAvailable()
        insertAll(listOfItemsAvailable)
    }
    @Query("DELETE FROM upsell_item_available")
    abstract fun deleteAll()
}