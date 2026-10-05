package com.whitbread.premierinn.domain.recentsearch.repository

import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import io.reactivex.Completable
import io.reactivex.Observable

interface RecentSearchRepository {
    fun saveRecentSearch(recentSearch: RecentSearch): Completable
    fun getRecentSearchesWithinAWeek(): Observable<List<RecentSearch>>
    fun getRecentSearches(): Observable<List<RecentSearch>>
    fun deleteRecentSearches(): Completable
    fun deleteBookedRecentSearch(recentSearch: RecentSearch): Completable
}