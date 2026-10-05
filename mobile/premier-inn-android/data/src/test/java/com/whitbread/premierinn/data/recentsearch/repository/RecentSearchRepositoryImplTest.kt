package com.whitbread.premierinn.data.recentsearch.repository

import com.whitbread.premierinn.data.recentsearch.dao.RecentSearchDao
import com.whitbread.premierinn.data.recentsearch.entity.RecentSearchEntity
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.repository.RecentSearchRepository
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class RecentSearchRepositoryImplTest {

    private val recentSearchDao: RecentSearchDao = mockk()
    private lateinit var recentSearch: RecentSearch
    private lateinit var recentSearchEntity: RecentSearchEntity
    private lateinit var repository: RecentSearchRepository

    @Before
    fun setUp() {
        repository = RecentSearchRepositoryImpl(recentSearchDao)

        recentSearch = RecentSearch(
                searchTerm = "Holborn", hotelCode = "LONHOL", latitude = 51.555553F, longitude = 0.0949867F,
                hotelBrand = "PI", arrivalDate = LocalDate.parse("2020-12-11"), departureDate = LocalDate.parse("2020-12-12"), roomsCount = 1,
                adults = listOf(1), children = listOf(0), infants = listOf(0), cots = listOf(false), roomTypeCodes = listOf("DB"))
        recentSearchEntity = RecentSearchEntity(
                searchTerm = "Holborn", hotelCode = "LONHOL", latitude = 51.555553F, longitude = 0.0949867F,
                hotelBrand = "PI", arrivalDate = LocalDate.parse("2020-12-11"), departureDate = LocalDate.parse("2020-12-12"), roomsCount = 1,
                adults = listOf(1), children = listOf(0), infants = listOf(0), cots = listOf(false), roomTypeCodes = listOf("DB"), dateCreated = 0L)
    }

    @Test
    fun `when save search is called then recent search payload is inserted in db`() {
        every { recentSearchDao.insertRecentSearch(any()) } just Runs

        repository.saveRecentSearch(recentSearch).test().assertNoErrors().assertComplete()

        verify(exactly = 1) { recentSearchDao.insertRecentSearch(recentSearchEntity) }
    }

    @Test
    fun `when db is empty then return zero recent searches`() {
        every { recentSearchDao.count() } returns 0

        repository.getRecentSearches()

        verify(exactly = 0) { recentSearchDao.getRecentSearches() }
    }

    @Test
    fun `when get recent searches is called then return result from db`() {
        every { recentSearchDao.count() } returns 1
        every { recentSearchDao.getRecentSearches() } returns listOf(recentSearchEntity)

        val testObserver = repository.getRecentSearches().test()

        testObserver.assertValue(listOf(recentSearch))
        verify(exactly = 1) { recentSearchDao.getRecentSearches() }
    }

    @Test
    fun `when delete recent searches is called then all recent searches are cleared from db`() {
        every { recentSearchDao.deleteAllRecentSearches() } just Runs

        repository.deleteRecentSearches().test().assertNoErrors().assertComplete()

        verify(exactly = 1) { recentSearchDao.deleteAllRecentSearches() }
    }

    @Test
    fun `when recent hotel search is booked then delete the entry from db table`() {
        every { recentSearchDao.getBookedRecentSearch(any(), any(), any(), any(), any(), any(), any(), any()) } returns recentSearchEntity

        repository.deleteBookedRecentSearch(recentSearch).test().assertNoErrors().assertComplete()

        verify(exactly = 1) { recentSearchDao.delete(recentSearchEntity) }
    }

    @Test
    fun `when recent place search is done then the entry is not deleted from db table`() {
        every { recentSearchDao.getBookedRecentSearch(any(), any(), any(), any(), any(), any(), any(), any()) } returns null

        repository.deleteBookedRecentSearch(recentSearch).test().assertNoErrors().assertComplete()

        verify(exactly = 0) { recentSearchDao.delete(any()) }
    }
}