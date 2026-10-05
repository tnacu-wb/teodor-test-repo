package com.whitbread.premierinn.domain.search.repository

import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import io.reactivex.Completable
import io.reactivex.Single

/**
 *
 */
interface SearchItemRepository {
    fun search(query: String): Single<List<SearchSuggetionItem>>
    fun getTopDestinations(): Single<List<SearchSuggetionItem>>
    fun getRecent(): Single<List<SearchSuggetionItem>>
    fun haseRecents(): Boolean
    fun store(item: SearchSuggetionItem, dateTime: Long): Completable
    fun removeRecent()
}