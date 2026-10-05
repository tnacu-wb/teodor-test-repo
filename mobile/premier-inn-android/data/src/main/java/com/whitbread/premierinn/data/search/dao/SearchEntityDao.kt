package com.whitbread.premierinn.data.search.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whitbread.premierinn.data.search.entity.SearchEntity
import io.reactivex.Completable

import io.reactivex.Maybe

const val MAX_RESULTS = 5

@Dao
interface SearchEntityDao {

    @Query("SELECT COUNT(*) FROM recent_search")
    fun count(): Int

    @Query("SELECT * FROM recent_search ORDER BY date_created DESC LIMIT ${MAX_RESULTS} ")
    fun getAll(): Maybe<List<SearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: SearchEntity): Long

    @Query("DELETE FROM recent_search")
    fun deleteAll()
}
