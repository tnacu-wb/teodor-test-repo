package uk.co.whitbread.hotel.info.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.hotel.info.model.aem.AEMHotelBasicDescription;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.service.utils.AEMUrlProvider;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.DE;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AllHotelsCacheProviderTest {

    @Mock
    private AEMUrlProvider urlProviderMock;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private JsonMapper jsonMapperMock;
    @Mock
    private Map<String, List<String>> lastSuccessResultsMock;


    @Spy
    @InjectMocks
    AllHotelsCacheProvider testObj;

    private static final String HOTEL_CODE_1 = "hotel-code-test-b1";
    private static final String HOTEL_CODE_2 = "hotel-code-test-a2";
    private static final String TITLE_1 = "title-test-1";
    private static final String TITLE_2 = "title-test-2";
    private static final String BRAND_1 = "brand-test-1";
    private static final String BRAND_2 = "brand-test-2";
    private static final AEMHotelBasicDescription HOTEL_1 =
            AEMHotelBasicDescription.builder().code(HOTEL_CODE_1).title(TITLE_1).brand(BRAND_1).build();
    private static final AEMHotelBasicDescription HOTEL_2 =
            AEMHotelBasicDescription.builder().code(HOTEL_CODE_2).title(TITLE_2).brand(BRAND_2).build();
    private static final AEMHotelBasicDescription HOTEL_1_DUPLICATE =
            AEMHotelBasicDescription.builder().code(HOTEL_CODE_1+"-duplicate").title(TITLE_1).brand(BRAND_1+"-duplicate").build();
    private static final String RAW_JSON_1 = "raw-json-test-1";
    private static final String RAW_JSON_2 = "raw-json-test-2";
    private static final List<String> RAW_JSONS = asList(RAW_JSON_1, RAW_JSON_2);
    private static final String ALL_HOTELS_URL_GB = "all-hotels-url-gb-test";
    private static final String ALL_HOTELS_URL_DE = "all-hotels-url-de-test";

    @BeforeEach
    public void setUp() throws Exception {

        when(jsonMapperMock.writeValueAsString(HOTEL_1)).thenReturn(RAW_JSON_1);
        when(jsonMapperMock.writeValueAsString(HOTEL_2)).thenReturn(RAW_JSON_2);
        when(urlProviderMock.getAllHotelsUrl(GB, EN)).thenReturn(ALL_HOTELS_URL_GB);
        when(urlProviderMock.getAllHotelsUrl(Country.DE, DE)).thenReturn(ALL_HOTELS_URL_DE);
        when(restTemplateMock.getForObject(ALL_HOTELS_URL_GB, AEMHotelBasicDescription[].class))
                .thenReturn(new AEMHotelBasicDescription[]{HOTEL_1, HOTEL_2});
        testObj.getLastSuccessResults().clear();
        when(testObj.getLastSuccessResults()).thenReturn(lastSuccessResultsMock);
    }

    @Test
    public void shouldCallGetNonCachedWhenNotInCache() {

        doReturn(RAW_JSONS).when(testObj).getNonCached(GB, EN);
        List<String> result = testObj.getCached(GB, EN);
        assertEquals(RAW_JSONS, result);
    }

    @Test
    public void shouldConvertHotel() {

        String json = testObj.convert(HOTEL_1);
        assertEquals(RAW_JSON_1, json);
    }

    @Test
    public void shouldRaiseRuntimeExceptionWhenConvertionError() throws JacksonException {

        when(jsonMapperMock.writeValueAsString(HOTEL_1)).thenThrow(mock(JacksonException.class));

        assertThrows(RuntimeException.class,
                () -> testObj.convert(HOTEL_1));
    }

    @Test
    public void shouldGetNonCached() {

        List<String> expectedResult = asList(RAW_JSON_1, RAW_JSON_2);
        List<String> result = testObj.getNonCached(GB, EN);
        assertEquals(expectedResult, result);
        verify(testObj).setLastSuccessResult(expectedResult, GB, EN);
    }

    @Test
    public void shouldConsiderTwoAEMHotelBasicDescriptionEqualByCode() {

        assertTrue(HOTEL_1.equals(HOTEL_1_DUPLICATE));

    }

    @Test
    public void shouldGetNonCachedAndRemoveDuplicates() {

        when(restTemplateMock.getForObject(ALL_HOTELS_URL_GB, AEMHotelBasicDescription[].class))
                .thenReturn(new AEMHotelBasicDescription[]{HOTEL_1, HOTEL_2, HOTEL_1_DUPLICATE});

        List<String> expectedResult = asList(RAW_JSON_1, RAW_JSON_2);
        List<String> result = testObj.getNonCached(GB, EN);
        assertEquals(expectedResult, result);
        verify(testObj).setLastSuccessResult(expectedResult, GB, EN);

    }

    @Test
    public void shouldGetLastSuccessResultWhenError() {

        when(restTemplateMock.getForObject(ALL_HOTELS_URL_GB, AEMHotelBasicDescription[].class))
                .thenThrow(RuntimeException.class);

        List<String> lastSuccess = asList("lastSuccessRawJson1", "lastSuccessRawJson2");
        doReturn(lastSuccess).when(testObj).getLastSuccessResult(GB, EN);

        List<String> result = testObj.getNonCached(GB, EN);
        assertEquals(lastSuccess, result);
    }

    @Test
    public void shouldThrowExceptionWhenNoLastSuccessResultAndError() {

        when(restTemplateMock.getForObject(ALL_HOTELS_URL_GB, AEMHotelBasicDescription[].class))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> testObj.getNonCached(GB, EN));
    }

    @Test
    public void shouldThrowExceptionWhenNoLastSuccessResultForCountryAndError() {

        when(restTemplateMock.getForObject(ALL_HOTELS_URL_GB, AEMHotelBasicDescription[].class))
                .thenThrow(new RuntimeException("Error GB"));
        when(restTemplateMock.getForObject(ALL_HOTELS_URL_DE, AEMHotelBasicDescription[].class))
                .thenThrow(new RuntimeException("Error DE"));

        List<String> lastSuccess = asList("lastSuccessRawJson1", "lastSuccessRawJson2");
        doReturn(lastSuccess).when(testObj).getLastSuccessResult(GB, EN);

        List<String> result = testObj.getNonCached(GB, EN);
        assertEquals(lastSuccess, result);

        assertThrows(RuntimeException.class,
                () -> testObj.getNonCached(Country.DE, DE),
                "Error DE");
    }

    @Test
    public void shouldGetLocalCacheKeyGB() {

        String result = testObj.getLocalCacheKey(GB, EN);
        assertEquals("GB-EN", result);
    }

    @Test
    public void shouldGetLocalCacheKeyDE() {

        String result = testObj.getLocalCacheKey(Country.DE, DE);
        assertEquals("DE-DE", result);
    }

    @Test
    public void shouldSetLastSuccessfulResults() {

        List<String> lastSuccessfulResults = asList("res1", "res2");
        testObj.setLastSuccessResult(lastSuccessfulResults, GB, EN);
        verify(lastSuccessResultsMock).put("GB-EN", lastSuccessfulResults);
    }

    @Test
    public void shouldGetLastSuccessfulResults() {

        List<String> lastSuccessfulResults = asList("res1", "res2");
        when(lastSuccessResultsMock.get("GB-EN")).thenReturn(lastSuccessfulResults);

        List<String> result = testObj.getLastSuccessResult(GB, EN);

        assertEquals(lastSuccessfulResults, result);
    }
}