package uk.co.whitbread.hotel.info.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.info.exception.AemInternalError;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;
import uk.co.whitbread.hotel.info.model.domain.Language;
import uk.co.whitbread.hotel.info.utils.TestUtils;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
public class HotelKeyServiceIT {
    private static final String hotelCode = "LONBLA";

    private static final String HOTEL_KEY_URL_LONBLA = "/gb/en/hoteldirectory/L/LONBLA.summary.data";
    private static final String INFO_ERROR_MESSAGE = "Unable to get info for LONBLA. Reason: ";
    private static final String KEY_ERROR_MESSAGE = "Unable to get key for LONBLA. Reason: ";

    @InjectMocks
    private HotelKeyService sut;

    @Mock
    private HotelInfoService hotelInfoService;

    @BeforeEach
    public void init() {
        sut = new HotelKeyService(hotelInfoService, JsonMapper.builder().findAndAddModules().build());
    }

    @Test
    public void shouldGetHotelKey() throws Exception {
        final String fileName = "hotel-info-LONBLA.summary.json";
        final String aemResult = TestUtils.getDataFileContent(fileName);
        when(hotelInfoService.getHotelInfo(anyString(), any(), any(), any())).thenReturn(aemResult);

        HotelKeyData hotelKeyInfo = sut.getHotelKeyData(Language.EN, hotelCode);

        assertNotNull(hotelKeyInfo);

        assertEquals(hotelCode, hotelKeyInfo.getCode());
        assertEquals(BartHotelBrandCode.PI, hotelKeyInfo.getBrand());
        assertThat(hotelKeyInfo.getName(), is("Alpha2 London Blackfriars (Fleet Street)") );
    }

    @Test
    public void shouldThrowExceptionWhenResponseIsInvalid() {
        String reason = "Invalid JSON response.";
        when(hotelInfoService.getHotelInfo(anyString(), any(), any(), any())).thenThrow(
            new RemoteCallException(String.format(KEY_ERROR_MESSAGE, hotelCode, reason), new Throwable()));

        assertThrows(RemoteCallException.class,
                () -> sut.getHotelKeyData(Language.EN, hotelCode),
                KEY_ERROR_MESSAGE + "Invalid JSON response.");
    }

    @Test
    public void shouldHandleAEMInternalErrors() {
        String reason = "500 Server Error";
        when(hotelInfoService.getHotelInfo(anyString(), any(), any(), any())).thenThrow(
            new AemInternalError(String.format(INFO_ERROR_MESSAGE, hotelCode, reason), new Throwable()));

        assertThrows(AemInternalError.class,
                () -> sut.getHotelKeyData(Language.EN, hotelCode),
                INFO_ERROR_MESSAGE + "500 Server Error");
    }

    @Test
    public void shouldHandleAEMForbidden() {
        String reason = "403 Forbidden";
        when(hotelInfoService.getHotelInfo(anyString(), any(), any(), any())).thenThrow(
            new AemInternalError(String.format(INFO_ERROR_MESSAGE, hotelCode, reason), new Throwable()));

        assertThrows(AemInternalError.class,
                () -> sut.getHotelKeyData(Language.EN, hotelCode),
                INFO_ERROR_MESSAGE + "403 Forbidden");
    }

    @Test
    public void shouldHandleRestClientExceptionForKey() {
        String reason = "404 Not Found";
        when(hotelInfoService.getHotelInfo(anyString(), any(), any(), any())).thenThrow(
            new RemoteCallException(String.format(INFO_ERROR_MESSAGE, hotelCode, reason), new Throwable()));

        assertThrows(RemoteCallException.class,
                () -> sut.getHotelKeyData(Language.EN, hotelCode),
                INFO_ERROR_MESSAGE + "404 Not Found");

    }

}
