package uk.co.whitbread.hotel.info.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;
import uk.co.whitbread.hotel.info.model.domain.Language;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class HotelKeyServiceTest {

    private HotelInfoService hotelInfoService;
    private JsonMapper jsonMapper;
    private HotelKeyService hotelKeyService;
    private AbstractMALException malException;

    @BeforeEach
    public void setup() {
        hotelInfoService = mock(HotelInfoService.class);
        jsonMapper = mock(JsonMapper.class);
        malException = mock(AbstractMALException.class);
        hotelKeyService = spy(new HotelKeyService(hotelInfoService, jsonMapper));
    }

    @Test
    public void should_get_multi_hotel_key_data() throws Exception {
        HotelKeyData hotelKeyData = new HotelKeyData();
        HotelKeyData hotelKeyData2 = new HotelKeyData();

        when(hotelInfoService.getHotelInfo(eq("foo"), any(Country.class), any(), any(HotelInfoFormat.class)))
                .thenReturn("serializedFoo");
        when(hotelInfoService.getHotelInfo(eq("foo2"), any(Country.class), any(), any(HotelInfoFormat.class)))
                .thenReturn("serializedFoo2");

        when(jsonMapper.readValue("serializedFoo", HotelKeyData.class)).thenReturn(hotelKeyData);
        when(jsonMapper.readValue("serializedFoo2", HotelKeyData.class)).thenReturn(hotelKeyData2);

        List<HotelKeyData> result = hotelKeyService.getHotelKeyData(Language.EN, asList("foo", "foo2"));

        verify(hotelKeyService).getHotelKeyDataIfPresent(Language.EN, "foo");
        verify(hotelKeyService).getHotelKeyDataIfPresent(Language.EN, "foo2");

        assertThat(result, hasItems(hotelKeyData, hotelKeyData2));
    }

    @Test
    public void should_get_hotel_key_data() throws Exception {
        HotelKeyData hotelKeyData = new HotelKeyData();

        when(hotelInfoService.getHotelInfo(eq("foo"), any(Country.class), any(), any(HotelInfoFormat.class)))
                .thenReturn("serializedFoo");
        when(jsonMapper.readValue("serializedFoo", HotelKeyData.class)).thenReturn(hotelKeyData);

        HotelKeyData result = hotelKeyService.getHotelKeyData(Language.EN, "foo");

        assertEquals(hotelKeyData, result);
    }

    @Test
    public void should_throw_exception_when_info_response_is_invalid() throws Exception {

        when(hotelInfoService.getHotelInfo(eq("foo"), any(Country.class), any(), any(HotelInfoFormat.class)))
                .thenReturn("serializedFoo");
        when(jsonMapper.readValue("serializedFoo", HotelKeyData.class)).thenThrow(mock(JacksonException.class));

        assertThrows(RemoteCallException.class,
                () -> hotelKeyService.getHotelKeyData(Language.EN, "foo"),
                "Unable to get key for foo. Reason: Invalid JSON response.");
    }

    @Test
    public void should_not_get_null_hotel_key_data() throws Exception {
        HotelKeyData hotelKeyData = new HotelKeyData();

        doReturn(hotelKeyData).when(hotelKeyService).getHotelKeyDataIfPresent(Language.EN, "foo");
        doReturn(null).when(hotelKeyService).getHotelKeyDataIfPresent(Language.EN, "foo2");

        List<HotelKeyData> result = hotelKeyService.getHotelKeyData(Language.EN, asList("foo", "foo2"));

        assertEquals(asList(hotelKeyData), result);
    }

    @Test
    public void should_get_hotel_key_data_when_present() {
        HotelKeyData hotelKeyData = new HotelKeyData();

        doReturn(hotelKeyData).when(hotelKeyService).getHotelKeyData(Language.EN, "foo");
        HotelKeyData result = hotelKeyService.getHotelKeyDataIfPresent(Language.EN, "foo");

        assertEquals(hotelKeyData, result);
    }

    @Test
    public void should_return_empty_hotel_key_data_when_not_present() {

        doThrow(malException).when(hotelKeyService).getHotelKeyData(Language.EN, "foo");
        HotelKeyData result = hotelKeyService.getHotelKeyDataIfPresent(Language.EN, "foo");
        assertNull(result);
    }

}