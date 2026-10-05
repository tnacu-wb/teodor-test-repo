package com.whitbread.premierinn.data.recentsearch.repository

import com.whitbread.premierinn.data.recentsearch.dao.RecentSearchDao
import com.whitbread.premierinn.data.recentsearch.mapper.toDomain
import com.whitbread.premierinn.data.recentsearch.mapper.toEntity
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.repository.RecentSearchRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(val dao: RecentSearchDao) : RecentSearchRepository {
    override fun saveRecentSearch(recentSearch: RecentSearch): Completable {
        return Completable.fromCallable { dao.insertRecentSearch(recentSearch.toEntity()) }
    }

    override fun getRecentSearches(): Observable<List<RecentSearch>> {
        return Observable.fromCallable {
            if (dao.count() > 0) {
                dao.getRecentSearches()
                        .map { it.toDomain() }
            } else emptyList()
        }
    }

    override fun getRecentSearchesWithinAWeek(): Observable<List<RecentSearch>> {
        return Observable.fromCallable {
            if (dao.count() > 0) {
                dao.getRecentSearchesWithinAWeek()
                        .map { it.toDomain() }
            } else emptyList()
        }
    }

    override fun deleteRecentSearches(): Completable {
        return Completable.fromCallable { dao.deleteAllRecentSearches() }
    }

    override fun deleteBookedRecentSearch(recentSearch: RecentSearch): Completable {
        return Completable.fromCallable {
            dao.getBookedRecentSearch(
                    hotelCode = recentSearch.hotelCode!!,
                    arrivalDate = recentSearch.arrivalDate.toEpochDay(),
                    departureDate = recentSearch.departureDate.toEpochDay(),
                    roomsCount = recentSearch.roomsCount,
                    adults = recentSearch.adults.joinToString(),
                    children = recentSearch.children.joinToString(),
                    infants = recentSearch.infants.joinToString(),
                    cots = recentSearch.cots.joinToString()
            )?.let { dao.delete(it) }
        }.onErrorComplete()
    }
}