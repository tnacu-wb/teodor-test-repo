package com.whitbread.premierinn.domain.recentsearch.usecase

import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.repository.RecentSearchRepository
import io.reactivex.Completable
import javax.inject.Inject

class StoreRecentSearch @Inject constructor(private val repository: RecentSearchRepository) {
    fun execute(recentSearch: RecentSearch): Completable {
        return repository.saveRecentSearch(recentSearch)
    }
}