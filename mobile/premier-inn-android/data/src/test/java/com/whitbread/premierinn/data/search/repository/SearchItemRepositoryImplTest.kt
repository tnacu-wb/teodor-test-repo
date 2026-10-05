package com.whitbread.premierinn.data.search.repository

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.remote.SearchItemApi
import com.whitbread.premierinn.data.remote.SnowDropApiContract
import com.whitbread.premierinn.data.search.dao.SearchEntityDao
import com.whitbread.premierinn.data.search.entity.SearchEntity
import com.whitbread.premierinn.data.search.toDomain
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.TOP_DESTINATIONS
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response


/**
 *
 */
class SearchItemRepositoryImplTest {

    @Mock
    lateinit var searchApi: SearchItemApi
    @Mock
    lateinit var resourceRepository: ContentManagedResourceRepository
    @Mock
    lateinit var dao: SearchEntityDao

    val gson = GsonFactory.create()

    lateinit var repo: SearchItemRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repo = SearchItemRepositoryImpl(searchApi, resourceRepository, gson, dao)
    }

    @Test
    fun getSuggestions_success() {
        val searchTerm = "London"

        val response = FileUtils.loadFileFromResource("api/mw-responses/suggestions/edinburgh.json")

        `when`(searchApi.search(searchTerm)).thenReturn(Single.just(gson.fromJson(response, SnowDropApiContract.AutocompleteSuggestions::class.java)))

        val expectedItemAt0 = SearchSuggetionItem(name= "Edinburgh, UK", code = "ChIJIyaYpQC4h0gRJxfnfHsU8mQ", type = SearchSuggetionItem.Type.GOOGLE_PLACE)

        repo.search(searchTerm)
                .test()
                .assertNoErrors()
                .assertValueAt(0) {  it.contains(expectedItemAt0) }
                .assertComplete()
    }

    @Test
    fun getSuggestions_success_no_results() {
        val searchTerm = "London"

        val response = FileUtils.loadFileFromResource("api/mw-responses/suggestions/no-results.json")

        `when`(searchApi.search(searchTerm)).thenReturn(Single.just(gson.fromJson(response, SnowDropApiContract.AutocompleteSuggestions::class.java)))

        repo.search(searchTerm)
                .test()
                .assertNoErrors()
                .assertValue { it.isEmpty() }
                .assertComplete()
    }

    @Test
    fun getSuggestions_httpError() {
        val searchTerm = "London"

        `when`(searchApi.search(searchTerm)).thenReturn(Single.error(HttpException(Response.error<SnowDropApiContract.AutocompleteSuggestions>(400,
                ResponseBody.create(MediaType.parse("UTF-8"), "")))))

        repo.search(searchTerm)
                .test()
                .assertError { it is HttpException }
                .assertNotComplete()
    }

    @Test
    fun getAllRecents() {
        val date11APR20181700 = 1523466091000L
        val date11APR20181707 = 1523466472000L

        val searchEntity1 = SearchEntity("London", 1.0, 1.0, date11APR20181700)
        val searchEntity2 = SearchEntity("LondonHotel", 1.0, 1.0, date11APR20181707, "LONMON", "PI")
        val searchEntity3 = SearchEntity("LondonHotel", 1.0, 1.0, date11APR20181707, "LONMON", null)

        `when`(dao.getAll()).thenReturn(Maybe.just(listOf(searchEntity1, searchEntity2, searchEntity3)))

        val expectedItemAt1 = searchEntity1.toDomain()
        val expectedItemAt2 = searchEntity2.toDomain()
        val expectedItemAt3 = searchEntity3.toDomain()

        repo.getRecent()
                .test()
                .assertValue(listOf(expectedItemAt1, expectedItemAt2, expectedItemAt3))
                .assertComplete()
                .assertNoErrors()
    }

    @Test
    fun getAllRecents_no_results() {

        `when`(dao.getAll()).thenReturn(Maybe.empty<List<SearchEntity>>())

        repo.getRecent()
                .test()
                .assertValue(emptyList())
                .assertComplete()
    }

    @Test
    fun store() {
        val date11APR20181700 = 1523466091000L
        val searchEntity1 = SearchEntity("London", 1.0, 1.0, date11APR20181700)

        repo.store(searchEntity1.toDomain(), date11APR20181700).test()

        verify(dao).insertOrReplace(searchEntity1)
    }

    @Test
    fun clearRecents() {

        repo.removeRecent()

        verify(dao).deleteAll()
    }

    @Test
    fun topDestination_success() {

        val json = """
            [{"name": "London", "lat": 51.512238, "long": -0.1059152}, 
             {"name": "Edinburgh", "lat": 55.950691, "long": -3.192125}, 
             {"name": "York", "lat": 53.96206, "long": -1.07888}]
        """.trimIndent()

        whenever(resourceRepository.getStringSingle(TOP_DESTINATIONS.value)).doReturn(Single.just(json))

        repo.getTopDestinations().test().assertNoErrors().assertValue(listOf(
                SearchSuggetionItem(name = "London",
                        location = Location(51.512237548828125, -0.10591520369052887),
                        type = SearchSuggetionItem.Type.LOCATION),
                SearchSuggetionItem(name = "Edinburgh",
                        location = Location(55.95069122314453, -3.192125082015991),
                        type = SearchSuggetionItem.Type.LOCATION),
                SearchSuggetionItem(name = "York",
                        location = Location(53.962059020996094, -1.078879952430725),
                        type = SearchSuggetionItem.Type.LOCATION)
        ))

    }

    @Test
    fun topDestination_empty() {

        val json = ""

        whenever(resourceRepository.getStringSingle(TOP_DESTINATIONS.value)).doReturn(Single.just(json))

        repo.getTopDestinations().test().assertNoErrors().assertValue(emptyList())

    }
}