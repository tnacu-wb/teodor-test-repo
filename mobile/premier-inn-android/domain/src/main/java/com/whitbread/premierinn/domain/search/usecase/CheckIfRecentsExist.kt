package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import javax.inject.Inject

/**
 *
 */
class CheckIfRecentsExist @Inject constructor(private val repository: SearchItemRepository) {

    fun execute(): Boolean {
        return repository.haseRecents()
    }
}