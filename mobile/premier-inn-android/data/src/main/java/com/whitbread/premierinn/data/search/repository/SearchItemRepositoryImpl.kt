package com.whitbread.premierinn.data.search.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.remote.SearchItemApi
import com.whitbread.premierinn.data.remote.SearchTopDestinationItem
import com.whitbread.premierinn.data.search.dao.SearchEntityDao
import com.whitbread.premierinn.data.search.toDomain
import com.whitbread.premierinn.data.search.toSearchEntity
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.TOP_DESTINATIONS
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 *
 */
class SearchItemRepositoryImpl @Inject constructor(
    val searchApi: SearchItemApi,
    val resourceRepository: ContentManagedResourceRepository,
    val gson: Gson,
    val dao: SearchEntityDao) : SearchItemRepository {

    override fun haseRecents(): Boolean {
        return dao.count() > 0
    }

    override fun search(query: String): Single<List<SearchSuggetionItem>> {
        return searchApi.search(query)
                .flatMap { list ->
                    val managedPlacesList = list.managedPlaces.asSequence().take(3).map { it.toDomain() }.filterNotNull().toList()
                    val googlePlacesList = list.places.asSequence().take(2).filter { !it.placeId.isNullOrEmpty() }.map { it.toDomain() }.toList()
                    val hotelList = list.properties.asSequence().take(5).map { it.toDomain() }.toList()

                    Single.just(listOf(managedPlacesList, googlePlacesList, hotelList).flatten())
                }
    }

    override fun getTopDestinations(): Single<List<SearchSuggetionItem>> {
        return resourceRepository.getStringSingle(TOP_DESTINATIONS.value)
                .map { jsonString ->
                    if (jsonString.isNotEmpty()) {
                        val listType = object : TypeToken<List<SearchTopDestinationItem>>() {}.type
                        gson.fromJson<List<SearchTopDestinationItem>>(jsonString, listType)
                    } else emptyList()
                }
                .toObservable()
                .flatMapIterable { it }
                .map { it.toDomain() }
                .toList()
    }

    override fun getRecent(): Single<List<SearchSuggetionItem>> {
        return dao.getAll().toObservable()
                .flatMapIterable { entities -> entities }
                .map { it.toDomain() }
                .toList()
    }

    override fun store(item: SearchSuggetionItem, dateTime: Long): Completable {
        return Completable.fromCallable { dao.insertOrReplace(toSearchEntity(item, dateTime)) }
    }

    override fun removeRecent() {
        dao.deleteAll()
    }
}