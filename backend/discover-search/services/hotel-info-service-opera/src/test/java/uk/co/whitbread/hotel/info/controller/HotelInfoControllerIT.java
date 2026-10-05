package uk.co.whitbread.hotel.info.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.whitbread.hotel.info.Application;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;
import uk.co.whitbread.hotel.info.model.domain.Language;
import uk.co.whitbread.hotel.info.service.HotelInfoService;
import uk.co.whitbread.hotel.info.service.HotelKeyService;
import uk.co.whitbread.hotel.info.utils.TestUtils;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.whitbread.common.exceptions.ErrorCodes.UNCLASSIFIED_ERROR_CODE;
import static uk.co.whitbread.common.exceptions.ErrorCodes.VALIDATION_ERROR_CODE;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.DE;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class HotelInfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelInfoService mockHotelInfoService;

    @MockitoBean
    private HotelKeyService mockHotelKeyService;

    private static final Country DEFAULT_COUNTRY = Country.GB;
    private static final Language DEFAULT_LANGUAGE = Language.EN;
    private static final HotelInfoFormat DEFAULT_FORMAT = HotelInfoFormat.LONG;
    private static final String INPUT_HOTEL_CODE = "ABCD";

    @Test
    public void shouldGetOkResponse() throws Exception {
        when(mockHotelInfoService.getHotelInfo(eq(INPUT_HOTEL_CODE), any(Country.class), any(Language.class), any(HotelInfoFormat.class)))
                .thenReturn("{\"test\":\"sample\"}");

        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("country", "GB")
                        .queryParam("language", "EN")
                        .queryParam("brand", "PI")
                        .queryParam("format", "LONG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test").value("sample"));
    }

    @Test
    public void shouldGetOkResponseNullBrand() throws Exception {
        when(mockHotelInfoService.getHotelInfo(eq(INPUT_HOTEL_CODE), any(Country.class), any(Language.class), any(HotelInfoFormat.class)))
                .thenReturn("{\"test\":\"sample\"}");

        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("country", "GB")
                        .queryParam("language", "EN")
                        .queryParam("format", "LONG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test").value("sample"));
    }

    @Test
    public void shouldGetHotelKeyInfoOkResponse() throws Exception {
        HotelKeyData hotelKeyData = new HotelKeyData();
        hotelKeyData.setCode("LONBVLA");
        hotelKeyData.setName("London Blackfriars (Fleet Street)");
        hotelKeyData.setBrand(BartHotelBrandCode.PI);

        when(mockHotelKeyService.getHotelKeyData(any(Language.class), any(String.class)))
                .thenReturn(hotelKeyData);

        mockMvc.perform(get("/hotels/{hotel-code}/key", INPUT_HOTEL_CODE)
                        .queryParam("hotelCode", INPUT_HOTEL_CODE)
                        .queryParam("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LONBVLA"))
                .andExpect(jsonPath("$.brand").value("PI"))
                .andExpect(jsonPath("$.name").value("London Blackfriars (Fleet Street)"));
    }

    @Test
    public void shouldGetNokResponse() throws Exception {
        when(mockHotelInfoService.getHotelInfo(anyString(), any(Country.class), any(Language.class), any(HotelInfoFormat.class)))
                .thenThrow(new RemoteCallException("Remote call reason"));

        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("country", "GB")
                        .queryParam("language", "EN")
                        .queryParam("brand", "PI")
                        .queryParam("format", "LONG"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(RemoteCallException.REMOTE_CALL_ERROR_CODE))
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", containsInAnyOrder("Remote call reason")));
    }

    @Test
    public void shouldHandleInternalServerError() throws Exception {
        when(mockHotelInfoService.getHotelInfo(anyString(), any(Country.class), any(Language.class), any(HotelInfoFormat.class)))
                .thenThrow(new RuntimeException("Remote call reason"));

        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("country", "GB")
                        .queryParam("language", "EN")
                        .queryParam("brand", "PI")
                        .queryParam("format", "LONG"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(UNCLASSIFIED_ERROR_CODE.getCode()))
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details", containsInAnyOrder("Remote call reason")));
    }

    @Test
    public void shouldAcceptNullForOptionalParameters() throws Exception {
        when(mockHotelInfoService.getHotelInfo(INPUT_HOTEL_CODE, DEFAULT_COUNTRY, DEFAULT_LANGUAGE, DEFAULT_FORMAT))
                .thenReturn("{\"test\":\"sample\"}");

        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test").value("sample"));
    }

    @Test
    public void shouldHandleInvalidFormat() throws Exception {
        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("format", "YYY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(VALIDATION_ERROR_CODE.getCode()))
                .andExpect(jsonPath("$.details", containsInAnyOrder("format must be an acceptable value. Acceptable values: [SHORT, LONG]")));
    }

    @Test
    public void shouldHandleInvalidCountry() throws Exception {
        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("country", "ZZZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(VALIDATION_ERROR_CODE.getCode()))
                .andExpect(jsonPath("$.details", containsInAnyOrder("country must be an acceptable value. Acceptable values: [GB, DE]")));
    }

    @Test
    public void shouldHandleInvalidLanguage() throws Exception {
        mockMvc.perform(get("/hotels/{hotel-code}", INPUT_HOTEL_CODE)
                        .queryParam("language", "RRR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(VALIDATION_ERROR_CODE.getCode()))
                .andExpect(jsonPath("$.details", containsInAnyOrder("language must be an acceptable value. Acceptable values: [EN, DE]")));
    }

    @Test
    public void shouldRejectNullListInMultiHotelKeyDataRequest() throws Exception {
        mockMvc.perform(get("/hotels/key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(VALIDATION_ERROR_CODE.getCode()))
                .andExpect(jsonPath("$.details", containsInAnyOrder("hotelCodes must not be null")));
    }

    @Test
        public void shouldRejectListWithoutValuesInMultiHotelKeyDataRequest() throws Exception {
        mockMvc.perform(get("/hotels/key")
                        .queryParam("hotelCodes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(VALIDATION_ERROR_CODE.getCode()))
                                .andExpect(jsonPath("$.details", containsInAnyOrder("hotelCodes must not be null")));
    }

    @Test
    public void shouldGetOkResponseWhenGettingAllHotelsWithDefaultCountryAndLanguage() throws Exception {
        when(mockHotelInfoService.getAllHotels(GB, EN))
                .thenReturn(asList(TestUtils.getDataFileContent("all-hotels-expected-1-GB.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-2-GB.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-3-GB.json")));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.getDataFileContent("all-hotels-expected-GB.json"), true));
    }

    @Test
    public void shouldGetOkResponseWhenGettingAllHotelsGB() throws Exception {
        when(mockHotelInfoService.getAllHotels(GB, EN))
                .thenReturn(asList(TestUtils.getDataFileContent("all-hotels-expected-1-GB.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-2-GB.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-3-GB.json")));

        mockMvc.perform(get("/hotels")
                        .queryParam("country", "GB")
                        .queryParam("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.getDataFileContent("all-hotels-expected-GB.json"), true));
    }

    @Test
    public void shouldGetOkResponseWhenGettingAllHotelsDE() throws Exception {
        when(mockHotelInfoService.getAllHotels(Country.DE, DE))
                .thenReturn(asList(TestUtils.getDataFileContent("all-hotels-expected-1-DE.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-2-DE.json"),
                        TestUtils.getDataFileContent("all-hotels-expected-3-DE.json")));

        mockMvc.perform(get("/hotels")
                        .queryParam("country", "DE")
                        .queryParam("language", "DE"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtils.getDataFileContent("all-hotels-expected-DE.json"), true));
    }
}
