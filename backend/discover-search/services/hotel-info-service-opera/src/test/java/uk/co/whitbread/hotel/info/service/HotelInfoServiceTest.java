package uk.co.whitbread.hotel.info.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.hotel.info.cache.AllHotelsCacheProvider;
import uk.co.whitbread.hotel.info.cache.HotelInfoCacheProvider;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

@ExtendWith(MockitoExtension.class)
public class HotelInfoServiceTest {

    @Mock
    private HotelInfoCacheProvider hotelInfoCacheProvider;
    @Mock
    private AllHotelsCacheProvider allHotelsCacheProvider;

    private HotelInfoService hotelInfoService;

    @Mock
    private AbstractMALException malException;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setup() {
        Mockito.reset(hotelInfoCacheProvider);
        hotelInfoService = spy(new HotelInfoService(allHotelsCacheProvider, hotelInfoCacheProvider, executor));
    }

    @Test
    public void should_get_hotel_info_from_cache() {

        when(hotelInfoCacheProvider.getCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info");

        String result = hotelInfoService.getHotelInfo("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);

        assertThat("Not Null", result, equalTo("Hotel Info"));

        verify(hotelInfoCacheProvider).getCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);
        verify(hotelInfoCacheProvider, never()).getNonCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);
    }

    @Test
    public void should_get_hotel_info_from_non_cache() {

        when(hotelInfoCacheProvider.getCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenThrow(new RedisConnectionFailureException("Dummy Failure"));
        when(hotelInfoCacheProvider.getNonCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info");

        String result = hotelInfoService.getHotelInfo("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);

        assertThat("Not Null", result, equalTo("Hotel Info"));

        verify(hotelInfoCacheProvider).getCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);
        verify(hotelInfoCacheProvider).getNonCached("LONBLA", Country.GB, Language.EN, HotelInfoFormat.LONG);
    }

    @Test
    public void empty_key_list_returns_empty_list() {
        assertThat(hotelInfoService.getHotelInfo(Collections.emptyList(), Country.GB, Language.EN, HotelInfoFormat.LONG), hasSize(0));
    }

    @Test
    public void null_key_list_returns_empty_list() {
        assertThat(hotelInfoService.getHotelInfo((List<String>) null, Country.GB, Language.EN, HotelInfoFormat.LONG), hasSize(0));
    }

    @Test
    public void all_null_doesnt_explode() {
        assertThat(hotelInfoService.getHotelInfo((List<String>) null, null, null, null), hasSize(0));
    }

    @Test
    public void should_get_multiple_hotel_info_from_cache() {
        when(hotelInfoCacheProvider.getCached("FOO", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info");
        when(hotelInfoCacheProvider.getCached("BAR", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info 2");

        List<String> result = hotelInfoService.getHotelInfo(asList("FOO", "BAR"), Country.GB, Language.EN, HotelInfoFormat.LONG);

        assertThat(result, hasItems("Hotel Info", "Hotel Info 2"));

        verify(hotelInfoService).getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "BAR");
        verify(hotelInfoService).getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "FOO");

        verify(hotelInfoCacheProvider, times(2)).getCached(any(), any(), any(), any());
        verify(hotelInfoCacheProvider, never()).getNonCached(any(), any(), any(), any());
    }

    @Test
    public void should_get_multiple_hotel_info_from_non_cache() {

        when(hotelInfoCacheProvider.getCached(any(), any(), any(), any())).thenThrow(new RedisConnectionFailureException("Dummy Failure"));

        when(hotelInfoCacheProvider.getNonCached("FOO", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info");
        when(hotelInfoCacheProvider.getNonCached("BAR", Country.GB, Language.EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info 2");


        List<String> result = hotelInfoService.getHotelInfo(asList("FOO", "BAR"), Country.GB, Language.EN, HotelInfoFormat.LONG);

        assertThat(result, hasItems("Hotel Info", "Hotel Info 2"));

        verify(hotelInfoService).getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "BAR");
        verify(hotelInfoService).getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "FOO");

        verify(hotelInfoCacheProvider, times(2)).getCached(any(), any(), any(), any());
        verify(hotelInfoCacheProvider, times(2)).getNonCached(any(), any(), any(), any());
    }

    @Test
    public void should_not_get_null_hotel_info() {

        doReturn("Hotel Info").when(hotelInfoService).getHotelInfo("FOO", Country.GB, Language.EN, HotelInfoFormat.LONG);
        doReturn(null).when(hotelInfoService).getHotelInfo("BAR", Country.GB, Language.EN, HotelInfoFormat.LONG);

        List<String> result = hotelInfoService.getHotelInfo(asList("FOO", "BAR"), Country.GB, Language.EN, HotelInfoFormat.LONG);

        assertEquals(List.of("Hotel Info"), result);
    }

    @Test
    public void should_get_hotel_info_when_present() {

        doReturn("Hotel Info").when(hotelInfoService).getHotelInfo("FOO", Country.GB, Language.EN, HotelInfoFormat.LONG);
        String result = hotelInfoService.getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "FOO");
        assertEquals("Hotel Info", result);
    }

    @Test
    public void should_return_empty_hotel_info_when_not_present() {

        doThrow(malException).when(hotelInfoService).getHotelInfo("FOO", Country.GB, Language.EN, HotelInfoFormat.LONG);
        String result = hotelInfoService.getHotelInfoIfPresent(Country.GB, Language.EN, HotelInfoFormat.LONG, "FOO");
        assertNull(result);
    }

    @Test
    public void should_get_all_hotels() {
        when(allHotelsCacheProvider.getCached(GB, EN)).thenReturn(asList("Hotel 1", "Hotel 2"));
        List<String> result = hotelInfoService.getAllHotels(GB, EN);
        assertEquals(asList("Hotel 1", "Hotel 2"), result);
    }
}