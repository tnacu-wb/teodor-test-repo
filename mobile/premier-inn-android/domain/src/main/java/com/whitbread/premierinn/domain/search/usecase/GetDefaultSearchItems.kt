package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 *
 */
class GetDefaultSearchItems @Inject constructor(private val repository: SearchItemRepository) {

    fun execute(): Single<List<SearchSuggetionItem>> {
        return getRecentItemsIfExistOtherWiseTopDestinations()
    }

    private fun getRecentItemsIfExistOtherWiseTopDestinations(): Single<List<SearchSuggetionItem>> {
        return Single.concat(
                repository.getRecent(),
                repository.getTopDestinations()
        )
                .filter { recentItems -> recentItems.isNotEmpty() }.firstElement()
                .toSingle()
    }
}