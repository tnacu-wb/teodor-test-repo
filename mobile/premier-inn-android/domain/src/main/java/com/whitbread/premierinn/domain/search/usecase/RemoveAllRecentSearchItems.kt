package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import javax.inject.Inject

/**
 *
 */
class RemoveAllRecentSearchItems @Inject constructor(private val repository: SearchItemRepository) {

    fun execute() {
        repository.removeRecent()
    }
}