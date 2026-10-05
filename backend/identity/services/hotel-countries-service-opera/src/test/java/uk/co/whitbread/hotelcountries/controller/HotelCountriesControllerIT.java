package uk.co.whitbread.hotelcountries.controller;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotelcountries.model.CountriesResponse;
import uk.co.whitbread.hotelcountries.model.Country;
import uk.co.whitbread.hotelcountries.service.HotelCountriesService;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"disable-caching"})
@ExtendWith(SpringExtension.class)
class HotelCountriesControllerIT {

    private static final String PARAM_COUNTRY = "country";
    private static final String PARAM_LANGUAGE = "language";
    private static final String VALUE_COUNTRY = "gb";
    private static final String VALUE_LANGUAGE = "en";
    private static final String VALUE_COUNTRY_DE = "de";
    private static final String VALUE_LANGUAGE_DE = "de";
    private static final String VALUE_COUNTRY_UPPERCASE = "GB";
    private static final String VALUE_LANGUAGE_UPPERCASE = "EN";
    private static final String AUSTRIA = "Austria";
    private static final String AUSTRIA_DE = "Österreich";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private HotelCountriesService service;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
        when(service.getCountries(VALUE_COUNTRY, VALUE_LANGUAGE)).thenReturn(getCountriesResponseEnglish());
        when(service.getCountries(VALUE_COUNTRY_DE, VALUE_LANGUAGE_DE)).thenReturn(getCountriesResponseGerman());
    }

    @Test
    void retrieveCountriesWithHeadersEnglish() throws Exception {
        HttpResponse<String> response = sendCountriesRequest(null, getHeadersEnglish());
        assertCountryLegend(response, AUSTRIA);
    }

    @Test
    void retrieveCountriesWithHeadersGerman() throws Exception {
        HttpResponse<String> response = sendCountriesRequest(null, getHeadersGerman());
        assertCountryLegend(response, AUSTRIA_DE);
    }

    @Test
    void retrieveCountriesWithQueryParamsEnglish() throws Exception {
        HttpResponse<String> response = sendCountriesRequest(getParamsEnglish(), null);
        assertCountryLegend(response, AUSTRIA);
    }

    @Test
    void retrieveCountriesWithQueryParamsGerman() throws Exception {
        HttpResponse<String> response = sendCountriesRequest(getParamsGerman(), null);
        assertCountryLegend(response, AUSTRIA_DE);
    }

    @Test
    void retrieveCountriesWithQueryParamsUppercase() throws Exception {
        HttpResponse<String> response = sendCountriesRequest(getParamsUppercase(), null);
        assertCountryLegend(response, AUSTRIA);
    }

    @Test
    void retrieveCountriesWithHeadersUppercase() throws Exception {
        when(service.getCountries(VALUE_COUNTRY, VALUE_LANGUAGE)).thenReturn(getCountriesResponseEnglish());

        Map<String, String> headers = new HashMap<>();
        headers.put(PARAM_COUNTRY, VALUE_COUNTRY_UPPERCASE);
        headers.put(PARAM_LANGUAGE, VALUE_LANGUAGE_UPPERCASE);

        HttpResponse<String> response = sendCountriesRequest(null, headers);
        assertCountryLegend(response, AUSTRIA);
    }

    @Test
    void retrieveCountriesWithMixedCaseQueryParams() throws Exception {
        when(service.getCountries(VALUE_COUNTRY_DE, VALUE_LANGUAGE_DE)).thenReturn(getCountriesResponseGerman());

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_COUNTRY, "De"); // Mixed case
        params.put(PARAM_LANGUAGE, "De"); // Mixed case

        HttpResponse<String> response = sendCountriesRequest(params, null);
        assertCountryLegend(response, AUSTRIA_DE);
    }

    @Test
    void retrieveCountriesWithMixedCaseHeaders() throws Exception {
        when(service.getCountries(VALUE_COUNTRY_DE, VALUE_LANGUAGE_DE)).thenReturn(getCountriesResponseGerman());

        Map<String, String> headers = new HashMap<>();
        headers.put(PARAM_COUNTRY, "De"); // Mixed case
        headers.put(PARAM_LANGUAGE, "De"); // Mixed case

        HttpResponse<String> response = sendCountriesRequest(null, headers);
        assertCountryLegend(response, AUSTRIA_DE);
    }

    private HttpResponse<String> sendCountriesRequest(Map<String, String> queryParams,
                                                      Map<String, String> headers) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(buildUri(queryParams)).GET();
        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }
        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI buildUri(Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder("http://localhost:")
                .append(serverPort)
                .append("/countries");

        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            queryParams.forEach((key, value) -> {
                if (url.charAt(url.length() - 1) != '?') {
                    url.append("&");
                }
                url.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                url.append("=");
                url.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            });
        }

        return URI.create(url.toString());
    }

    private void assertCountryLegend(HttpResponse<String> response, String expectedCountryLegend) throws Exception {
        assertEquals(HttpStatus.SC_OK, response.statusCode());

        JsonNode root = OBJECT_MAPPER.readTree(response.body());
        String actualCountryLegend = root.path("countries").path(0).path("countryLegend").asText();

        assertEquals(expectedCountryLegend, actualCountryLegend);
    }

    private Map<String, String> getHeadersEnglish() {
        Map<String, String> headers = new HashMap<>();
        headers.put(PARAM_COUNTRY, VALUE_COUNTRY);
        headers.put(PARAM_LANGUAGE, VALUE_LANGUAGE);
        return headers;
    }

    private Map<String, String> getHeadersGerman() {
        Map<String, String> headers = new HashMap<>();
        headers.put(PARAM_COUNTRY, VALUE_COUNTRY_DE);
        headers.put(PARAM_LANGUAGE, VALUE_LANGUAGE_DE);
        return headers;
    }

    private Map<String, String> getParamsEnglish() {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_COUNTRY, VALUE_COUNTRY);
        params.put(PARAM_LANGUAGE, VALUE_LANGUAGE);
        return params;
    }

    private Map<String, String> getParamsGerman() {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_COUNTRY, VALUE_COUNTRY_DE);
        params.put(PARAM_LANGUAGE, VALUE_LANGUAGE_DE);
        return params;
    }

    private Map<String, String> getParamsUppercase() {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_COUNTRY, VALUE_COUNTRY_UPPERCASE);
        params.put(PARAM_LANGUAGE, VALUE_LANGUAGE_UPPERCASE);
        return params;
    }

    private CountriesResponse getCountriesResponseEnglish() {
        List<Country> countries = new ArrayList<>();
        Country country = new Country();
        country.setCountryCode("A");
        country.setCountryCodeISO("AT");
        country.setCountryLegend(AUSTRIA);
        country.setPassportRequired(true);
        country.setDialingCode("+43");
        country.setFlagImg("/content/dam/global/flags/Austria.png");
        countries.add(country);
        return new CountriesResponse(countries);
    }

    private CountriesResponse getCountriesResponseGerman() {
        List<Country> countries = new ArrayList<>();
        Country country = new Country();
        country.setCountryCode("A");
        country.setCountryCodeISO("AT");
        country.setCountryLegend(AUSTRIA_DE);
        country.setPassportRequired(true);
        country.setDialingCode("+43");
        country.setFlagImg("/content/dam/global/flags/Austria.png");
        countries.add(country);
        return new CountriesResponse(countries);
    }

}
