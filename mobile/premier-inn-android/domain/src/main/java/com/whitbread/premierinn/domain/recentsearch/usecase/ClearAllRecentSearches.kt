package com.whitbread.premierinn.domain.recentsearch.usecase

import com.whitbread.premierinn.domain.recentsearch.repository.RecentSearchRepository
import io.reactivex.Completable
import javax.inject.Inject

class ClearAllRecentSearches @Inject constructor(private val repository: RecentSearchRepository) {
    fun execute(): Completable {
        return repository.deleteRecentSearches()
    }
}