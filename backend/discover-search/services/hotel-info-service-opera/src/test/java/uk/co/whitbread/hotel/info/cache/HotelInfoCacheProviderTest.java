package uk.co.whitbread.hotel.info.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.hotel.info.Application;
import uk.co.whitbread.hotel.info.command.CacheFallbackCommand;
import uk.co.whitbread.hotel.info.exception.AemInternalError;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat.LONG;
import static uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat.SHORT;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class HotelInfoCacheProviderTest {

    public static final String HOTEL_CODE_LONBLA = "LONBLA";
    public static final String HOTEL_CODE_FRAMTI = "FRAMTI";
    public static final String INFO_HOTEL_LONBLA_GB_EN_LONG = "INFO_LONBLA_GB_EN_LONG";
    public static final String INFO_HOTEL_FRAMTI_GB_EN_LONG = "INFO_FRAMTI_GB_EN_LONG";
    public static final String INFO_HOTEL_LONBLA_DE_EN_LONG = "INFO_LONBLA_DE_EN_LONG";
    public static final String INFO_HOTEL_LONBLA_GB_DE_LONG = "INFO_LONBLA_GB_DE_LONG";
    public static final String INFO_HOTEL_LONBLA_GB_EN_SHORT = "INFO_LONBLA_GB_EN_SHORT";

    @Autowired
    HotelInfoCacheProvider hotelInfoCacheProvider;

    @MockitoBean
    RestTemplate restTemplate;

    @MockitoBean
    CacheManager cacheManager;

    ConcurrentMapCache hotelInfoCache;

    @BeforeEach
    public void setup() {

        mapUrlAndHotelInfo("/gb/en/hoteldirectory/L/LONBLA.complete.data", INFO_HOTEL_LONBLA_GB_EN_LONG);
        mapUrlAndHotelInfo("/gb/en/hoteldirectory/F/FRAMTI.complete.data", INFO_HOTEL_FRAMTI_GB_EN_LONG);
        mapUrlAndHotelInfo("/de/en/hoteldirectory/L/LONBLA.complete.data", INFO_HOTEL_LONBLA_DE_EN_LONG);
        mapUrlAndHotelInfo("/gb/de/hoteldirectory/L/LONBLA.complete.data", INFO_HOTEL_LONBLA_GB_DE_LONG);
        mapUrlAndHotelInfo("/gb/en/hoteldirectory/L/LONBLA.summary.data", INFO_HOTEL_LONBLA_GB_EN_SHORT);

        hotelInfoCache = spy(new ConcurrentMapCache("hotelInfo"));

        when(cacheManager.getCache("hotelInfo")).thenReturn(hotelInfoCache);

    }

    private void mapUrlAndHotelInfo(String uri, String hotelInfo) {
        when(restTemplate.getForObject(endsWith(uri), Mockito.<Class<String>> any())).thenReturn(hotelInfo);
    }

    @Test
    public void cacheShouldReturnSameObject() {

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);

        assertThat(result1, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));
        assertThat(result2, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));

        verify(restTemplate).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");
        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-LONG", INFO_HOTEL_LONBLA_GB_EN_LONG);
    }

    @Test
    public void cacheShouldNotReturnSameObjectForDifferentHotelCodes() {

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_FRAMTI, GB, EN, LONG);

        assertThat(result1, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));
        assertThat(result2, sameInstance(INFO_HOTEL_FRAMTI_GB_EN_LONG));

        verify(restTemplate, times(2)).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");

        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-LONG", INFO_HOTEL_LONBLA_GB_EN_LONG);
        verify(hotelInfoCache).put("hotelInfoCache-FRAMTI-GB-EN-LONG", INFO_HOTEL_FRAMTI_GB_EN_LONG);
    }

    @Test
    public void cacheShouldNotReturnSameObjectForDifferentCountries() {

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_LONBLA, Country.DE, EN, LONG);

        assertThat(result1, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));
        assertThat(result2, sameInstance(INFO_HOTEL_LONBLA_DE_EN_LONG));

        verify(restTemplate, times(2)).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");

        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-LONG", INFO_HOTEL_LONBLA_GB_EN_LONG);
        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-DE-EN-LONG", INFO_HOTEL_LONBLA_DE_EN_LONG);
    }

    @Test
    public void cacheShouldNotReturnSameObjectForDifferentLanguages() {

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, Language.EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, Language.DE, LONG);

        assertThat(result1, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));
        assertThat(result2, sameInstance(INFO_HOTEL_LONBLA_GB_DE_LONG));

        verify(restTemplate, times(2)).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");

        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-LONG", INFO_HOTEL_LONBLA_GB_EN_LONG);
        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-DE-LONG", INFO_HOTEL_LONBLA_GB_DE_LONG);
    }

    @Test
    public void cacheShouldNotReturnSameObjectForDifferentFormats() {

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, Language.EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, Language.EN, SHORT);

        assertThat(result1, sameInstance(INFO_HOTEL_LONBLA_GB_EN_LONG));
        assertThat(result2, sameInstance(INFO_HOTEL_LONBLA_GB_EN_SHORT));

        verify(restTemplate, times(2)).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");

        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-LONG", INFO_HOTEL_LONBLA_GB_EN_LONG);
        verify(hotelInfoCache).put("hotelInfoCache-LONBLA-GB-EN-SHORT", INFO_HOTEL_LONBLA_GB_EN_SHORT);
    }

    @Test
    public void canHandleHttpClientErrorException() {

        when(restTemplate.getForObject(anyString(), Mockito.<Class<String>> any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RemoteCallException.class,
                () -> generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG),
                "Unable to get info for LONBLA.");
    }

    @Test
    public void canHandleRestClientException() {

        when(restTemplate.getForObject(anyString(), Mockito.<Class<String>> any()))
                .thenThrow(new RestClientException("Dummy Rest Client Exception"));

        assertThrows(AemInternalError.class,
                () -> generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG),
                "Unable to get info for LONBLA.");
    }

    @Test
    public void canHandleHttpStatus_Not404() {


        when(restTemplate.getForObject(anyString(), Mockito.<Class<String>> any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(AemInternalError.class,
                () -> generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG),
                "Unable to get info for LONBLA.");
    }

    @Test
    public void getCached_whenCacheIsNotAvailableReturnDifferentObject() {
        when(cacheManager.getCache("hotelInfo")).thenThrow(new RedisConnectionFailureException("Fake DataAccessConnection"));

        when(restTemplate.getForObject(anyString(), Mockito.<Class<String>> any()))
                .thenReturn("Some Hotel Info 1")
                .thenReturn("Some Hotel Info 2");

        String result1 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);
        String result2 = generateHotelInfo(HOTEL_CODE_LONBLA, GB, EN, LONG);

        assertThat("Result", result1, not(sameInstance(result2)));

        verify(restTemplate, times(2)).getForObject(anyString(), Mockito.<Class<String>> any());
        verify(cacheManager, atLeastOnce()).getCache("hotelInfo");

    }

    private String generateHotelInfo(String hotelCode, Country country, Language language, HotelInfoFormat format) {
        return CacheFallbackCommand.execute("CacheGroup", "hotelinfo", "hotelinfo",
                    () -> hotelInfoCacheProvider.getCached(hotelCode, country, language, format),
                    () -> hotelInfoCacheProvider.getNonCached(hotelCode, country, language, format));
    }
}