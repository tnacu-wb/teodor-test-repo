package uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESTRICTIONS_BY_DATE_END_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESTRICTIONS_BY_DATE_START_DATE;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremockOhipAvailabilityClientTest {

  @Mock
  private AvailabilityOhipProperties availabilityOhipProperties;
  private WireMockServer wm;
  private String path;
  private WebClient webClient;
  private final List<String> stringList = List.of("type");

  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
    webClient = WebClient.create(path);
  }

  @BeforeEach
  void setUp() {
    wm.resetAll();

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipClientAvailabilities_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    when(availabilityOhipProperties.getAvailabilitiesEndpoint()).thenReturn(
        "/par/v1/hotels/{hotelId}/availability");

    //Act
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getRoomTypes("hotelId"));
  }

  @Test
  void testOhipClientRateInfo_ShouldReturnException() {
    final ItemInventoryRequestDto item = new ItemInventoryRequestDto();
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    when(availabilityOhipProperties.getRateInfoEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/rateInfo");

    //Act
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getItemsInventory(item));
  }

  @Test
  void testOhipClientItemsInventory_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);

    when(availabilityOhipProperties.getItemsInventoryEndpoint()).thenReturn(
        "/inv/v1/hotels/{hotelId}/itemInventory");
    //Act
    var response = ohipClient.getHotelInventory("hotelId", "startDate", "endDate",
        1);
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientItemInventoryHold_ShouldReturnException() {
    HoldItemInfoDto item = HoldItemInfoDto.builder().hotelId("1").build();
    HoldItemInventoryRequestDto req = HoldItemInventoryRequestDto.builder().holdItemInfo(item)
        .build();
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);

    when(availabilityOhipProperties.getItemInventoryHoldEndpoint()).thenReturn(
        "/inv/v1/hotels/{hotelId}/itemInventoryHold");
    //Act

    var response = ohipClient.itemInventoryHold(req);
    assertThrows(HotelAvailabilityException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientHotelAvailability_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);

    when(availabilityOhipProperties.getHotelInventoryEndpoint()).thenReturn(
        "/inv/v1/hotels/{hotelId}/hotelInventory");

    //Act
    var response = ohipClient.getHotelAvailabilityRequest(
        AvailabilityRequestDto.builder().roomTypes(stringList).build());
    assertThrows(HotelAvailabilityException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientInventoryStatistics_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    when(availabilityOhipProperties.getAvailabilitiesEndpoint()).thenReturn(
        "/par/v1/hotels/{hotelId}/availability");

    //Act
    var response = ohipClient.getHotelAvailabilityByIdsRequestV2(
        AvailabilityByIdsSearchCriteriaV2Dto.builder().hotelIds(stringList)
            .build());
    assertThrows(HotelAvailabilityException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientAvailabilityByIdsV2_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);

    //Act
    var response = ohipClient.getHotelAvailabilityByIdsRequestV2(
        AvailabilityByIdsSearchCriteriaV2Dto.builder().hotelIds(stringList).build());
    assertThrows(HotelAvailabilityException.class, () -> response.block());
  }

  @Test
  void testOhipClientRateCodePricing_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    final RateCodeRoomInfoCriteria request = RateCodeRoomInfoCriteria.builder()
        .adultsNo(1)
        .childrenNo(1)
        .build();
    final RateCodeCriteria rateCodeCriteria = new RateCodeCriteria();
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getRateCodePricing(rateCodeCriteria, request));
  }

  @Test
  void testOhipClientMultiHotelAva_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);

    when(availabilityOhipProperties.getMultiHotelAvaEndpoint()).thenReturn("/par/v1/availability");

    //Act
    var response = ohipClient.getHotelAvailabilityByIdsRequest
        (MultiHotelAvailabilityRequestDto.builder()
            .hotelIds(stringList)
            .roomTypes(stringList)
            .roomStayQuantity(1)
            .build());
    assertThrows(HotelAvailabilityException.class, () -> response.block());
  }

  @Test
  void testOhipClientMultiHotelAvailabilityRequestV2_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    when(availabilityOhipProperties.getMinimumRateAvaEndpoint()).thenReturn(
        "/parext/v1/hotels/minimumRateAvailability");
    var response = ohipClient.getMultiHotelAvailabilityRequestV2(
        MultiHotelAvailabilityRequestV2Dto.builder()
            .hotelIds(stringList)
            .build());
    assertThrows(HotelReservationException.class, () -> response.block());
  }

  @Test
  void testOhipClientMultiHotelAvailabilityRequest_ShouldReturnException() {
    var req = MultiHotelAvailabilityRequestDto.builder()
        .hotelIds(stringList)
        .ratePlanCodes(List.of("rate"))
        .ratePlanSet("rate")
        .roomStayEndDate("01/01/2000")
        .roomStayEndDate("01/01/2000")
        .roomTypes(List.of("type"))
        .roomStayQuantity(1)
        .companyId("1")
        .build();
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    when(availabilityOhipProperties.getMultiHotelAvaEndpoint()).thenReturn(
        "/par/v1/availability");
    var response = ohipClient.getMultiHotelAvailabilityRequest(req);
    assertThrows(HotelAvailabilityException.class, () -> response.block());
  }

  @Test
  void testOhipClientRateAvaEndpoint_ShouldReturnException() {
    final OhipAvailabilityClient ohipClient = new OhipAvailabilityClient(webClient,
        availabilityOhipProperties);
    final AvailabilityRequestDto request = AvailabilityRequestDto.builder()
        .hotelId("id")
        .roomTypes(stringList)
        .build();
    when(availabilityOhipProperties.getMultiRoomRateAvaEndpoint()).thenReturn(
        "/parext/v1/hotels/multiRoomRateAvailability");

    //Act
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getHotelInventoryStatistics(request));
  }

  @Test
  void getRestrictionsByDateRange_WhenServerError_ThenThrowException() {
    var request = RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("id")
        .startDate("2025-01-01")
        .endDate("2025-01-30")
        .build();
    when(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint()).thenReturn(
        "/par/v1/hotels/{hotelId}/restrictions");
    final var ohipClient = new OhipAvailabilityClient(webClient, availabilityOhipProperties);

    try {
      ohipClient.getRestrictionsByDateRange(request);
      fail("Exception expected.");
    } catch (HotelAvailabilityException exc) {
      assertEquals(ErrorCode.OHIP_GET_RESTRICTIONS_BY_DATE_RANGE_EXCEPTION.getCode(), exc.getErrorCode());
    } catch (Exception e) {
      fail("Wrong exception type.");
    }
  }

  @Test
  void getRestrictionsByDateRange_WhenResponseReceived_ThenParsedCorrectly() {
    var request = RestrictionsByDateRangeSearchCriteria.builder()
        .hotelId("id")
        .startDate("2025-01-01")
        .endDate("2025-01-30")
        .build();
    when(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint()).thenReturn(
        "/par/v1/hotels/{hotelId}/restrictions");
    wm.stubFor(get(String.format("/par/v1/hotels/id/restrictions?%s=%s&%s=%s",RESTRICTIONS_BY_DATE_START_DATE, request.getStartDate(), RESTRICTIONS_BY_DATE_END_DATE, request.getEndDate()))
        .withQueryParam(RESTRICTIONS_BY_DATE_START_DATE, equalToIgnoreCase(request.getStartDate()))
        .withQueryParam(RESTRICTIONS_BY_DATE_END_DATE, equalToIgnoreCase(request.getEndDate()))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"restrictionsByDateRange\":{\"restrictionsByDateRange\":{\"restrictionSets\":[{\"restrictionControl\":{\"house\":true,\"roomType\":\"FMTRPL\",\"ratePlanCode\":\"STANDARD\",\"roomClass\":\"U\",\"ratePlanCategory\":\"CAT\"},\"restrictionStatus\":{\"code\":\"MinimumLengthOfStay\",\"unit\":2},\"actualTimeSpan\":{\"startDate\":\"2025-01-10\",\"endDate\":\"2025-01-12\"},\"onRequest\":false,\"start\":\"2025-01-10\",\"end\":\"2025-01-12\",\"sunday\":true,\"monday\":false,\"tuesday\":false,\"wednesday\":false,\"thursday\":false,\"friday\":true,\"saturday\":true}],\"hotelId\":\"HEAPTI\",\"hasMore\":false}},\"links\":[{\"href\":\"href\",\"rel\":\"self\",\"templated\":false,\"method\":\"POST\",\"operationId\":\"postRestriction\"}]}")));
    final var ohipClient = new OhipAvailabilityClient(webClient, availabilityOhipProperties);

    var result = ohipClient.getRestrictionsByDateRange(request);

    var resultRestrictionsByDateRange = result.getRestrictionsByDateRange().getRestrictionsByDateRange();
    var resultRestrictionSet = result.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets().get(0);
    assertEquals("HEAPTI", resultRestrictionsByDateRange.getHotelId());
    assertEquals(false, resultRestrictionsByDateRange.isHasMore());
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
    assertEquals(1, result.getLinks().size());
    assertEquals("href", result.getLinks().get(0).getHref());
    assertEquals("POST", result.getLinks().get(0).getMethod());
    assertEquals("self", result.getLinks().get(0).getRel());
    assertEquals(false, result.getLinks().get(0).isTemplated());
    assertEquals("postRestriction", result.getLinks().get(0).getOperationId());
  }

  @Test
  void getRestrictionsByDateRange_WhenResponseLacksObject_ThenParsedCorrectly() {
    var request = RestrictionsByDateRangeSearchCriteria.builder()
          .hotelId("id")
          .startDate("2025-01-01")
          .endDate("2025-01-30")
          .build();
    when(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint()).thenReturn(
          "/par/v1/hotels/{hotelId}/restrictions");
    wm.stubFor(get(String.format("/par/v1/hotels/id/restrictions?%s=%s&%s=%s",RESTRICTIONS_BY_DATE_START_DATE, request.getStartDate(), RESTRICTIONS_BY_DATE_END_DATE, request.getEndDate()))
          .withQueryParam(RESTRICTIONS_BY_DATE_START_DATE, equalToIgnoreCase(request.getStartDate()))
          .withQueryParam(RESTRICTIONS_BY_DATE_END_DATE, equalToIgnoreCase(request.getEndDate()))
          .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"links\":[{\"href\":\"href\",\"rel\":\"self\",\"templated\":false,\"method\":\"POST\",\"operationId\":\"postRestriction\"}]}")));
    final var ohipClient = new OhipAvailabilityClient(webClient, availabilityOhipProperties);

    var result = ohipClient.getRestrictionsByDateRange(request);

    assertNull(result.getRestrictionsByDateRange());
    assertEquals(1, result.getLinks().size());
    assertEquals("href", result.getLinks().get(0).getHref());
    assertEquals("POST", result.getLinks().get(0).getMethod());
    assertEquals("self", result.getLinks().get(0).getRel());
    assertEquals(false, result.getLinks().get(0).isTemplated());
    assertEquals("postRestriction", result.getLinks().get(0).getOperationId());
  }
}
