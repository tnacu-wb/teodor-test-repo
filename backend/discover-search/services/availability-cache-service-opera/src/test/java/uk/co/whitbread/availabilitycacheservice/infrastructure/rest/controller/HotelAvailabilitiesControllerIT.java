package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static java.nio.charset.Charset.defaultCharset;
import static mocks.HotelMock.buildHotels;
import static mocks.HotelMock.buildHotelsWithRates;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.util.StreamUtils.copyToString;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.ContentServiceMockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.ContentServiceWireMockConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;

@Disabled("Not compatible with Kaniko executor")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
@ActiveProfiles(profiles = {"increase-batch-size"})
@Import(ContentServiceWireMockConfig.class)
@AutoConfigureTestRestTemplate
class HotelAvailabilitiesControllerIT {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private HotelAvailabilitiesPort hotelAvailabilitiesPort;

  @MockitoBean
  private HotelAvailabilitiesPersistencePort hotelAvailabilitiesPersistencePort;

  @Autowired
  private WireMockServer mockContentService;

  private String availabilitiesUrl;

  private SearchCriteria searchCriteria;

  @BeforeEach
  void setUp() throws IOException {
    searchCriteria = buildSearchCriteria();
    ContentServiceMockResponse.setupMockRateClassification(mockContentService);
    availabilitiesUrl = "http://localhost:" + port + "/search/hotels/availabilities";
  }

  @Test
  void shouldReturn200Response() throws Exception {
    List<Hotel> hotelsPersistanceResponse = buildHotelsWithRates(Arrays.asList("LONLEI", "BASQUA", "COVCRO"));
    when(hotelAvailabilitiesPersistencePort.getHotelsByCodeAndAvailDateBetween(any(SearchCriteria.class))).thenReturn(
        hotelsPersistanceResponse);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(availabilitiesUrl)
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("type", "DB")
        .queryParam("rooms", "1")
        .queryParam("sort", "DISTANCE");

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
        httpEntity, String.class);

    String availabilitiesResponse = copyToString(ContentServiceMockResponse.class.getClassLoader()
        .getResourceAsStream("stubs/availabilities/availabilitiesresponse_it.json"), defaultCharset());

    assertThat(response.getBody()).isEqualToIgnoringWhitespace(availabilitiesResponse);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void shouldReturn200ResponseForNoValuesPassedOnPageAndSizeAsQueryParams() throws Exception {
    List<Hotel> hotelsPersistanceResponse = buildHotelsWithRates(Arrays.asList("LONLEI", "BASQUA", "COVCRO"));
    when(hotelAvailabilitiesPersistencePort.getHotelsByCodeAndAvailDateBetween(any(SearchCriteria.class))).thenReturn(
        hotelsPersistanceResponse);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(availabilitiesUrl)
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("type", "DB")
        .queryParam("rooms", "1")
        .queryParam("sort", "DISTANCE");

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
        httpEntity, String.class);

    String availabilitiesResponse = copyToString(ContentServiceMockResponse.class.getClassLoader()
        .getResourceAsStream("stubs/availabilities/availabilitiesresponse_emptypageandsize.json"), defaultCharset());

    assertThat(response.getBody()).isEqualToIgnoringWhitespace(availabilitiesResponse);
    assertEquals(HttpStatus.OK, response.getStatusCode());  }

  @Test
  void shouldReturn400BadRequest() throws Exception {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(availabilitiesUrl)
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("type", "DB")
        .queryParam("rooms", "1")
        .queryParam("sort", "");

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertThat(response.getBody()).contains(
        "searchCriteria: Invalid Sort Type. Sort Type should be either DISTANCE or PRICE.");
  }

  @Test
  void shouldReturn200WithEmptyHotelAvailabilities() throws Exception {
    List<Hotel> hotelsRequest = buildHotels(Arrays.asList("LONLEI", "BASQUA", "COVCRO"));

    when(hotelAvailabilitiesPersistencePort.getHotelsByCodeAndAvailDateBetween(any(SearchCriteria.class))).thenReturn(
        Collections.emptyList());

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(availabilitiesUrl)
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("type", "DB")
        .queryParam("rooms", "1")
        .queryParam("sort", "DISTANCE");

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody()).contains("{\"total\":0,\"page\":1,\"pageSize\":40,\"hotelAvailabilities\":[]}");
  }

  // this requires fix to work with the mock persistance response.
  // Also the mock won't be required once the actual test database container is configured.
  private SearchCriteria buildSearchCriteria() {
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONLEI,BASQUA,COVCRO"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .page(1)
        .size(40)
        .adults(new int[]{2})
        .children(new int[]{0})
        .cot(false)
        .type(new String[]{RoomType.DB.name()})
        .rooms(1)
        .sort(SortType.DISTANCE)
        .type(new String[]{RoomType.SB.name()})
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

}
