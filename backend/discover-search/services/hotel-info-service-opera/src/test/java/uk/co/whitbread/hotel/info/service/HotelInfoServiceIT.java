package uk.co.whitbread.hotel.info.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.DE;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.info.cache.AllHotelsCacheProvider;
import uk.co.whitbread.hotel.info.cache.HotelInfoCacheProvider;
import uk.co.whitbread.hotel.info.exception.AemInternalError;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.utils.TestUtils;

/**
 * Test for the {@link HotelInfoService class}
 */
@ExtendWith(MockitoExtension.class)
public class HotelInfoServiceIT {
    private static final String UNABLE_TO_GET_INFO_FOR_S_REASON_S = "Unable to get info for %s. Reason: %s";

    @InjectMocks
    private HotelInfoService sut;

    @Mock
    private HotelInfoCacheProvider hotelInfoCacheProvider;
    @Mock
    private AllHotelsCacheProvider allHotelsCacheProvider;

    @Test
    public void shouldGetHotelInfoLong() throws Exception {
        final String hotelCode = "LONBLA";
        final String fileName = "hotel-info-LONBLA.full.json";
        String result = TestUtils.getDataFileContent(fileName);
        when(hotelInfoCacheProvider.getCached(anyString(),any(), any(), any())).thenReturn(result);

        //When
        String infoJson = sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.LONG);

        //Then
        assertNotNull(infoJson);
        assertEquals(TestUtils.getDataFileContent(fileName), infoJson);
    }

    @Test
    public void shouldGetHotelInfoShort() throws Exception {
        final String hotelCode = "LONBLA";
        final String fileName = "hotel-info-LONBLA.summary.json";
        String result = TestUtils.getDataFileContent(fileName);
        when(hotelInfoCacheProvider.getCached(anyString(),any(), any(), any())).thenReturn(result);

        //When
        String infoJson = sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.SHORT);

        //Then
        assertNotNull(infoJson);
        assertEquals(TestUtils.getDataFileContent(fileName), infoJson);
    }

    @Test
    public void shouldGetHotelInfoPIDBrandDefaultstoPI() throws Exception {
        final String hotelCode = "FRAMTI";
        final String fileName = "hotel-info-FRAMTI.summary.json";
        String result = TestUtils.getDataFileContent(fileName);
        when(hotelInfoCacheProvider.getCached(anyString(),any(), any(), any())).thenReturn(result);

        //When
        String infoJson = sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.SHORT);

        //Then
        assertNotNull(infoJson);
        assertEquals(TestUtils.getDataFileContent(fileName), infoJson);
    }

    @Test
    public void shouldGetHotelInfoZIPBrandDefaultsToPI() throws Exception {
        final String hotelCode = "CARROA";
        final String fileName = "hotel-info-CARROA.summary.json";
        String result = TestUtils.getDataFileContent(fileName);
        when(hotelInfoCacheProvider.getCached(anyString(),any(), any(), any())).thenReturn(result);

        //When
        String infoJson = sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.SHORT);

        //Then
        assertNotNull(infoJson);
        assertEquals(TestUtils.getDataFileContent(fileName), infoJson);
    }

    @Test
    public void shouldHandleAEMInternalErrors() {
        final String hotelCode = "LONBLA";
        String reason = "500 Server Error";
        when(hotelInfoCacheProvider.getCached(anyString(), any(), any(), any())).thenThrow(
            new AemInternalError(String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, reason), new Throwable()));

        Throwable thrown = catchThrowable(() -> sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.LONG));

        assertThat(thrown).isInstanceOf(AemInternalError.class)
                .hasMessageContaining("Unable to get info for LONBLA. Reason: 500 Server Error");

    }

    @Test
    public void shouldHandleAEMForbiddenErrors() {
        final String hotelCode = "LONBLA";
        String reason = "403 Forbidden";

        when(hotelInfoCacheProvider.getCached(anyString(), any(), any(), any())).thenThrow(
            new AemInternalError(String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, reason), new Throwable()));

        Throwable thrown = catchThrowable(() -> sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.LONG));

        assertThat(thrown).isInstanceOf(AemInternalError.class)
                .hasMessageContaining("Unable to get info for LONBLA. Reason: 403 Forbidden");

    }

    @Test
    public void shouldHandleHotelNotFound() {
        final String hotelCode = "LONBLA";
        String reason = "404 Not Found";

        when(hotelInfoCacheProvider.getCached(anyString(), any(), any(), any())).thenThrow(
            new RemoteCallException(String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, reason), new Throwable()));

        Throwable thrown = catchThrowable(() -> sut.getHotelInfo(hotelCode, GB, EN, HotelInfoFormat.LONG));

        assertThat(thrown).isInstanceOf(RemoteCallException.class)
                .hasMessageContaining("Unable to get info for LONBLA. Reason: 404 Not Found");

    }

    @Test
    public void shouldGetAllHotelsGB() throws Exception {
        final String fileName = "all-hotels-aem-GB.json";
        List<String> result = TestUtils.getDataFileListContent(fileName);
        when(allHotelsCacheProvider.getCached(any(), any())).thenReturn(result);

        //When
        List<String> allHotels = sut.getAllHotels(GB, EN);

        //Then
        assertNotNull(allHotels);
        assertFalse(allHotels.isEmpty());
        assertEquals(3, allHotels.size());
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-1-GB.json"), allHotels.get(0));
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-2-GB.json"), allHotels.get(1));
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-3-GB.json"), allHotels.get(2));
    }

    @Test
    public void shouldGetAllHotelsDE() throws Exception {
        final String fileName = "all-hotels-aem-DE.json";
        List<String> result = TestUtils.getDataFileListContent(fileName);
        when(allHotelsCacheProvider.getCached(any(), any())).thenReturn(result);

        //When
        List<String> allHotels = sut.getAllHotels(Country.DE, DE);

        //Then
        assertNotNull(allHotels);
        assertFalse(allHotels.isEmpty());
        assertEquals(3, allHotels.size());
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-1-DE.json"), allHotels.get(0));
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-2-DE.json"), allHotels.get(1));
        assertEquals(TestUtils.getDataFileContent("all-hotels-expected-3-DE.json"), allHotels.get(2));
    }


}