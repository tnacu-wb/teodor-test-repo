package uk.co.whitbread.hotel.info.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.info.model.domain.*;
import uk.co.whitbread.hotel.info.service.HotelInfoService;
import uk.co.whitbread.hotel.info.service.HotelKeyService;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

@ExtendWith(MockitoExtension.class)
public class HotelInfoControllerTest {

    @Mock
    private HotelInfoService hotelInfoService;
    @Mock
    private HotelKeyService hotelKeyService;
    @InjectMocks
    private HotelInfoController hotelInfoController;

    @Test
    public void should_get_hotel_info() {
        when(hotelInfoService.getHotelInfo("foo", GB, EN, HotelInfoFormat.LONG)).thenReturn("Hotel Info");

        HotelInfoRequest hotelInfoRequest = new HotelInfoRequest();
        hotelInfoRequest.setCountry(GB);
        hotelInfoRequest.setLanguage(EN);
        hotelInfoRequest.setFormat(HotelInfoFormat.LONG);

        String result = hotelInfoController.getHotelInfo("foo", hotelInfoRequest);

        assertThat(result, equalTo("Hotel Info"));

        verify(hotelInfoService).getHotelInfo("foo", GB, EN, HotelInfoFormat.LONG);
    }

    @Test
    public void should_get_multi_hotel_info() {
        when(hotelInfoService.getHotelInfo(Arrays.asList("foo", "bar"), GB, EN, HotelInfoFormat.LONG)).thenReturn(Arrays.asList("Hotel Info", "Hotel Info 2"));

        MultiHotelInfoRequest multiHotelInfoRequest = new MultiHotelInfoRequest();
        multiHotelInfoRequest.setCountry(GB);
        multiHotelInfoRequest.setLanguage(EN);
        multiHotelInfoRequest.setFormat(HotelInfoFormat.LONG);
        multiHotelInfoRequest.setHotelCodes(Arrays.asList("foo", "bar"));

        MultiHotelInfoResponse result = hotelInfoController.getHotelInfo(multiHotelInfoRequest);

        assertThat(result.getHotels(), contains("Hotel Info", "Hotel Info 2"));
        assertThat(result.getTotal(), is(2));
    }

    @Test
    public void should_get_all_hotels() {
        when(hotelInfoService.getAllHotels(GB, EN)).thenReturn(Arrays.asList("All Hotel 1", "All Hotel 2"));

        MultiHotelInfoRequest multiHotelInfoRequest = new MultiHotelInfoRequest();

        MultiHotelInfoResponse result = hotelInfoController.getHotelInfo(multiHotelInfoRequest);

        assertThat(result.getHotels(), contains("All Hotel 1", "All Hotel 2"));
        assertThat(result.getTotal(), is(2));
    }

    @Test
    public void should_get_hotel_key_data() {
        HotelKeyData hotelKeyData = new HotelKeyData();
        hotelKeyData.setCode("foo");
        hotelKeyData.setName("cat");
        hotelKeyData.setBrand(BartHotelBrandCode.PI);

        when(hotelKeyService.getHotelKeyData(EN, "foo")).thenReturn(hotelKeyData);

        HotelKeyDataRequest hotelKeyDataRequest = new HotelKeyDataRequest();
        hotelKeyDataRequest.setLanguage(EN);

        HotelKeyData result = hotelInfoController.getHotelKeyData("foo", hotelKeyDataRequest);

        assertThat(result, equalTo(hotelKeyData));
    }

    @Test
    public void should_get_multi_hotel_key_data() {
        HotelKeyData hotelKeyData = new HotelKeyData();
        HotelKeyData hotelKeyData2 = new HotelKeyData();

        when(hotelKeyService.getHotelKeyData(EN, Arrays.asList("foo", "bar"))).thenReturn(Arrays.asList(hotelKeyData, hotelKeyData2));

        MultiHotelKeyDataRequest multiHotelKeyDataRequest = new MultiHotelKeyDataRequest();
        multiHotelKeyDataRequest.setLanguage(EN);
        multiHotelKeyDataRequest.setHotelCodes(Arrays.asList("foo", "bar"));

        MultiHotelKeyDataResponse result = hotelInfoController.getHotelKeyData(multiHotelKeyDataRequest);

        assertThat(result.getKeyData(), hasItems(hotelKeyData, hotelKeyData2));
    }
}