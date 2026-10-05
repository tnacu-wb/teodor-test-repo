package com.whitbread.premierinn.domain.search.usecase

import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 *
 */
class GetDefaultSearchItemsTest {


    @Mock
    lateinit var repository: SearchItemRepository
    lateinit var useCase: GetDefaultSearchItems

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = GetDefaultSearchItems(repository)
    }

    @Test
    fun `GIVEN 0 recent items AND topDestinations items exist THEN emit just TopDestinations`() {

        val expectedItems = listOf(
                SearchSuggetionItem(name = "London", location = Location(1.toDouble(), 2.toDouble()), type = SearchSuggetionItem.Type.LOCATION),
                SearchSuggetionItem(name = "Brighton", location = Location(2.toDouble(), 2.toDouble()), type = SearchSuggetionItem.Type.LOCATION)
        )

        `when`(repository.getRecent()).thenReturn(Single.just(emptyList()))
        `when`(repository.getTopDestinations()).thenReturn(Single.just(expectedItems))

        useCase.execute().test()
                .assertValue(expectedItems)
                .assertComplete()
                .assertNoErrors()
    }


    @Test
    fun `GIVEN some recent items  AND topDestinations items exist THEN emit just recent items`() {

        val expectedRecentItems = listOf(
                SearchSuggetionItem(name = "London", location = Location(1.toDouble(), 2.toDouble()), type = SearchSuggetionItem.Type.LOCATION),
                SearchSuggetionItem(name = "Brighton", location = Location(2.toDouble(), 2.toDouble()), type = SearchSuggetionItem.Type.LOCATION)
        )

        val expectedTopDestinationsItems = listOf(
                SearchSuggetionItem(name = "Bristol", location = Location(1.toDouble(), 2.toDouble()), type = SearchSuggetionItem.Type.LOCATION))

        `when`(repository.getRecent()).thenReturn(Single.just(expectedRecentItems))
        `when`(repository.getTopDestinations()).thenReturn(Single.just(expectedTopDestinationsItems))

        useCase.execute().test()
                .assertValue(expectedRecentItems)
                .assertComplete()
                .assertNoErrors()
    }


    @Test
    fun `GIVEN 0 recent items AND 0 topDestinations items THEN emit Error`() {

        `when`(repository.getRecent()).thenReturn(Single.just(emptyList()))
        `when`(repository.getTopDestinations()).thenReturn(Single.just(emptyList()))

        useCase.execute().test()
                .assertNotComplete()
                .assertError { error -> error is NoSuchElementException }
    }
}