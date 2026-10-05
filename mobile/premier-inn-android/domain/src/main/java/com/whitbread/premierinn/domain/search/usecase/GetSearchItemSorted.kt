package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Use case that exposes an ordered list of @link{com.whitbread.premierinn.domain.search.entity.SearchItem}
 * Api is responsible for setting the order. Places first followed by Hotels
 */
class GetSearchItemSorted @Inject constructor(val repository: SearchItemRepository) {

    fun execute(query: String): Single<List<SearchSuggetionItem>> {
        return repository.search(query)
    }
}