package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 *
 */
class StoreSearchItem @Inject constructor(private val repository: SearchItemRepository) {

    fun execute(item: SearchSuggetionItem, dateTime: Long): Completable {
        return repository.store(item, dateTime)
    }
}