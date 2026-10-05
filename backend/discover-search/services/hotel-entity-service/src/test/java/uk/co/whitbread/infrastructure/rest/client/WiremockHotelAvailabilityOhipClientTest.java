package uk.co.whitbread.infrastructure.rest.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;
import static uk.co.whitbread.infrastructure.rest.client.OhipClient.END_DATE_PARAM;
import static uk.co.whitbread.infrastructure.rest.client.OhipClient.START_DATE_PARAM;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.infrastructure.config.OhipProperties;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockHotelAvailabilityOhipClientTest {
  @Mock
  private OhipProperties availabilityOhipProperties = mock(OhipProperties.class);
  private WireMockServer server;
  private final WebClient webClient = WebClient.create("http://localhost:8080");

  private OhipClient ohipClient;

  @BeforeAll
  void setUp() {
    server = new WireMockServer(options().port(8080));
    server.start();
    this.ohipClient = new OhipClient(webClient, availabilityOhipProperties);
  }

  @AfterAll
  void tearDown() {
    server.stop();
  }

  @Test
  void getRestrictionsByDateRange_WhenResponseReceived_ThenParsedCorrectly() {
    var request = RestrictionsByDateRangeRequest.builder()
          .hotelId("HEAPTI")
          .startDate("2025-01-01")
          .endDate("2025-01-30")
          .build();
    when(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint()).thenReturn(
          "/hotels/{hotelId}/restrictions");
    server.stubFor(get(String.format("/hotels/%s/restrictions?%s=%s&%s=%s",
          request.getHotelId(),
          START_DATE_PARAM, request.getStartDate(),
          END_DATE_PARAM, request.getEndDate()))
          .withQueryParam(START_DATE_PARAM, equalToIgnoreCase(request.getStartDate()))
          .withQueryParam(END_DATE_PARAM, equalToIgnoreCase(request.getEndDate()))
          .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"restrictionSets\":[{\"restrictionControl\":{\"house\":true,\"roomType\":\"FMTRPL\",\"ratePlanCode\":\"STANDARD\",\"roomClass\":\"U\",\"ratePlanCategory\":\"CAT\"},\"restrictionStatus\":{\"code\":\"MinimumLengthOfStay\",\"unit\":2},\"actualTimeSpan\":{\"startDate\":\"2025-01-10\",\"endDate\":\"2025-01-12\"},\"onRequest\":false,\"start\":\"2025-01-10\",\"end\":\"2025-01-12\",\"sunday\":true,\"monday\":false,\"tuesday\":false,\"wednesday\":false,\"thursday\":false,\"friday\":true,\"saturday\":true}],\"hotelId\":\"HEAPTI\",\"hasMore\":false}")));

    var result = ohipClient.getRestrictionsByDateRange(request);

    Assertions.assertNotNull(result);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertEquals("HEAPTI", result.getHotelId());
    assertEquals(false, result.isHasMore());
    assertEquals(true, resultRestrictionSet.getRestrictionControl().isHouse());
    assertEquals("STANDARD", resultRestrictionSet.getRestrictionControl().getRatePlanCode());
    assertEquals("FMTRPL", resultRestrictionSet.getRestrictionControl().getRoomType());
    assertEquals("CAT", resultRestrictionSet.getRestrictionControl().getRatePlanCategory());
    assertEquals("U", resultRestrictionSet.getRestrictionControl().getRoomClass());
    assertEquals(2, resultRestrictionSet.getRestrictionStatus().getUnit());
    assertEquals("MinimumLengthOfStay", resultRestrictionSet.getRestrictionStatus().getCode());
    assertEquals("2025-01-10", resultRestrictionSet.getActualTimeSpan().getStartDate());
    assertEquals("2025-01-12", resultRestrictionSet.getActualTimeSpan().getEndDate());
    assertEquals(false, resultRestrictionSet.isOnRequest());
    assertEquals("2025-01-10", resultRestrictionSet.getStart());
    assertEquals("2025-01-12", resultRestrictionSet.getEnd());
    assertEquals(false, resultRestrictionSet.isMonday());
    assertEquals(false, resultRestrictionSet.isTuesday());
    assertEquals(false, resultRestrictionSet.isWednesday());
    assertEquals(false, resultRestrictionSet.isThursday());
    assertEquals(true, resultRestrictionSet.isFriday());
    assertEquals(true, resultRestrictionSet.isSaturday());
    assertEquals(true, resultRestrictionSet.isSunday());
  }

  @Test
  void getRestrictionsByDateRange_WhenResponseIsNotComplete_ThenParsedCorrectly() {
    var request = RestrictionsByDateRangeRequest.builder()
          .hotelId("HEAPTI")
          .startDate("2025-01-02")
          .endDate("2025-01-30")
          .build();
    when(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint()).thenReturn(
          "/hotels/{hotelId}/restrictions");
    server.stubFor(get(String.format("/hotels/%s/restrictions?%s=%s&%s=%s",
          request.getHotelId(),
          START_DATE_PARAM, request.getStartDate(),
          END_DATE_PARAM, request.getEndDate()))
          .withQueryParam(START_DATE_PARAM, equalToIgnoreCase(request.getStartDate()))
          .withQueryParam(END_DATE_PARAM, equalToIgnoreCase(request.getEndDate()))
          .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"restrictionSets\":[{\"restrictionStatus\":{\"code\":\"MinimumLengthOfStay\",\"unit\":2},\"actualTimeSpan\":{\"startDate\":\"2025-01-10\",\"endDate\":\"2025-01-12\"},\"onRequest\":false,\"start\":\"2025-01-10\",\"end\":\"2025-01-12\",\"sunday\":true,\"monday\":false,\"tuesday\":false,\"wednesday\":false,\"thursday\":false,\"friday\":true,\"saturday\":true}],\"hotelId\":\"HEAPTI\",\"hasMore\":false}")));

    var result = ohipClient.getRestrictionsByDateRange(request);

    Assertions.assertNotNull(result);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertEquals("HEAPTI", result.getHotelId());
    assertEquals(false, result.isHasMore());
    assertNull(resultRestrictionSet.getRestrictionControl());
    assertEquals(2, resultRestrictionSet.getRestrictionStatus().getUnit());
    assertEquals("MinimumLengthOfStay", resultRestrictionSet.getRestrictionStatus().getCode());
    assertEquals("2025-01-10", resultRestrictionSet.getActualTimeSpan().getStartDate());
    assertEquals("2025-01-12", resultRestrictionSet.getActualTimeSpan().getEndDate());
    assertEquals(false, resultRestrictionSet.isOnRequest());
    assertEquals("2025-01-10", resultRestrictionSet.getStart());
    assertEquals("2025-01-12", resultRestrictionSet.getEnd());
    assertEquals(false, resultRestrictionSet.isMonday());
    assertEquals(false, resultRestrictionSet.isTuesday());
    assertEquals(false, resultRestrictionSet.isWednesday());
    assertEquals(false, resultRestrictionSet.isThursday());
    assertEquals(true, resultRestrictionSet.isFriday());
    assertEquals(true, resultRestrictionSet.isSaturday());
    assertEquals(true, resultRestrictionSet.isSunday());
  }
}
