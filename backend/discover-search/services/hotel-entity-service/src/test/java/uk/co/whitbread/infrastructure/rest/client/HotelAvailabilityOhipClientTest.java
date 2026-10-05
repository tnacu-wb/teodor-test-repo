package uk.co.whitbread.infrastructure.rest.client;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.domain.exceptions.HotelAvailabilityBadReqException;
import uk.co.whitbread.domain.model.availability.in.MultiHotelRestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.domain.model.availability.out.RestrictionStatus;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.*;
import uk.co.whitbread.infrastructure.config.OhipProperties;
import uk.co.whitbread.infrastructure.rest.client.availability.model.*;
import uk.co.whitbread.infrastructure.rest.client.ohip.exceptions.OhipClientException;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomRateDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RoomTypeDto;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class HotelAvailabilityOhipClientTest {

  public static final String HOTEL_ID = "TKINPT";
  public static final String ARRIVAL_DATE = "2022-03-01";
  public static final String OPERA_END_DATE = "2022-03-02";
  public static final String DEPARTURE_DATE = "2022-03-03";
  public static final List<Integer> ADULTS = of(1, 2);
  public static final List<String> ROOM_TYPES = Collections.singletonList("DB");
  private static final String ERROR_WHILE_TRYING_TO_GET_LIGHTWEIGHT_RESERVATION = "Error while trying to get lightweight reservation!";
  private static final String OHIP_ADAPTER_ERROR_MESSAGE = "An error was returned by OHIP Adapter !!";
  private static final String RES_1 = "RES1";
  private static final String RES_2 = "RES2";

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private OhipProperties ohipProperties;
  @InjectMocks
  private OhipClient ohipClient;

  @Test
  void getHotelAvailability__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityDto.class)).thenReturn(mockAvailabilityResponseV1());

    // Act
    HotelAvailabilityDto response = ohipClient.getHotelAvailability(
        getHotelAvailabilityRequestOhipDto());

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HOTEL_ID, response.getHotelId());
    Assertions.assertEquals(ARRIVAL_DATE, response.getStartDate());
    Assertions.assertEquals(OPERA_END_DATE, response.getEndDate());
    Assertions.assertTrue(response.isAvailable());
  }


  @Test
  void getRatePlans__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RatePlansResponseDto.class)).thenReturn(mockRatePlansResponseDto());

    // Act
    var response = ohipClient.getRatePlans(Collections.singletonList("FLEXRATE"), "LONEUS")
        .block();

    // Assert
    Assertions.assertNotNull(response);
  }

  private Mono<RatePlansResponseDto> mockRatePlansResponseDto() {
    var ratePlansResponseDto = new RatePlansResponseDto();
    ratePlansResponseDto.setRatePlans(Collections.singletonList(mockRatePlanDto()));
    return Mono.just(ratePlansResponseDto);
  }

  private RatePlanDto mockRatePlanDto() {
    var ratePlanDto = new RatePlanDto();
    ratePlanDto.setHotelId("LONEUS");
    return ratePlanDto;
  }

  @Test
  void getHotelAvailability__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelAvailabilityDto.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelAvailability(getHotelAvailabilityRequestOhipDto()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelRoomsInventory__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInventoryRoomTypeDto.class)).thenReturn(mockInventoryResponse());

    // Act
    HotelInventoryRoomTypeDto response = ohipClient.getHotelRoomsInventory(
        createHotelInventoryRequest());

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getRoomTypeInventories(), hasSize(1));
    assertThat(response.getRoomTypeInventories().get(0).getAvailableCount(), is(42));
    assertThat(response.getRoomTypeInventories().get(0).getCode(), is("DOUBLE"));
  }

  @Test
  void getHotelRoomsInventory__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInventoryRoomTypeDto.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelRoomsInventory(createHotelInventoryRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRoomPriceBreakdown__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RoomPriceBreakdownResult.class))
            .thenThrow(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
            () -> ohipClient.getRoomPriceBreakdown("hotelId", createRoomPriceBreakdownRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRoomPriceBreakdown__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomPriceBreakdownResult.class)).thenReturn(mockRoomPriceBreakdownResult());

    // Act
    RoomPriceBreakdownResult response = ohipClient
        .getRoomPriceBreakdown("TEST", createRoomPriceBreakdownRequest()).block();

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getPriceBreakdown().get(0).getCurrencyCode(), is("EUR"));
    assertThat(response.getPriceBreakdown().get(0).getTotalNetAmount(),
        is(BigDecimal.valueOf(500)));
  }

  private Mono<RoomPriceBreakdownResult> mockRoomPriceBreakdownResult() {
    return Mono.just(RoomPriceBreakdownResult.builder()
        .priceBreakdown(of(RoomPriceBreakdown.builder()
            .totalNetAmount(BigDecimal.valueOf(500))
            .currencyCode("EUR")
            .build()))
        .build());
  }

  private RoomPriceBreakdownRequest createRoomPriceBreakdownRequest() {
    return RoomPriceBreakdownRequest.builder()
        .roomTypes(List.of("DOUBLE"))
        .adultsNo(List.of(1))
        .childrenNo(List.of(1))
        .build();
  }

  @Test
  void getRateCodePricing__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RateCodePricingResult.class)).thenReturn(mockRateCodePricing());

    // Act
    RateCodePricingResult response = ohipClient.getRateCodePricing(createRateCodeRequest());

    // Assert
    Assertions.assertNotNull(response);
    assertThat(response.getRatePlanCode(), is("FLEXRATE"));
    assertThat(response.getCurrencyCode(), is("EUR"));
    assertThat(response.getTotalNetAmount(), is(BigDecimal.valueOf(1280)));
  }

  @Test
  void getRateCodePricing__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RateCodePricingResult.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getRateCodePricing(createRateCodeRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelAvailabilityByIds__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityByIdsDto.class)).thenReturn(mockAvailabilityByIdsResponse());

    // Act
    HotelAvailabilityByIdsDto response = ohipClient.getHotelAvailabilityByIds(
        getHotelAvailabilityByIdsRequestOhipDto());

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HOTEL_ID, response.getHotelAvailability().get(0).getHotelId());
    Assertions.assertEquals(ARRIVAL_DATE, response.getHotelAvailability().get(0).getStartDate());
    Assertions.assertEquals(OPERA_END_DATE, response.getHotelAvailability().get(0).getEndDate());
    Assertions.assertTrue(response.getHotelAvailability().get(0).isAvailable());
  }

  @Test
  void getHotelAvailabilityByIds_shouldReturnException(){
    String errorMessage = String.format("Error while trying to create HotelAvailabilityByIds for hotelAvailabilityByIdsRequestOhipDto=%s",
            getHotelAvailabilityByIdsRequestOhipDto());
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelAvailabilityByIdsDto.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
            () -> ohipClient.getHotelAvailabilityByIds(getHotelAvailabilityByIdsRequestOhipDto()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelInfo_shouldReturnException(){
    String errorMessage = "Error while trying to get hotel information!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelInfo("hotelId"));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRoomTypesInfo_shouldReturnException(){
    String errorMessage = "Error while trying to get room types info from OHIP!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RoomTypesInfoDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getRoomTypesInfo("hotelId"));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getMultiHotelAvailability_shouldReturnException(){
    String errorMessage = "Error while trying to get multi hotel availabilities from OHIP";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(MultiAvailabilityResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getMultiHotelAvailability(new MultiHotelAvaSearchCriteriaOhip()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getListOfCancellationReasons_shouldReturnException(){
    String errorMessage = "Error while trying to get list of cancellation reasons from OHIP";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(CancellationReasonsResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getListOfCancellationReasons("hotelId"));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getPostMultiHotelAvailability_shouldReturnException(){
    String errorMessage = "Error while trying to create reservation guests";
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipProperties.getMultiHotelAvailabilitiesEndpoint2()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(MultiAvailabilityResponseV2Dto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getPostMultiHotelAvailability(new MultiAvailabilityRequestV2Dto()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelAvailabilityByIdsV2_shouldReturnException(){
    String errorMessage = "Error while trying to get availability by ids v2";
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipProperties.getAvailabilityByIdsEndpointV2()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(AvailabilityByIdsResponseV2Dto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelAvailabilityByIdsV2(HotelAvailabilityByIdsRequestOhipV2Dto.builder().build()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRatePlanInfo__shouldReturnOk() {

    // Arrange
    var ratePlanCode = "FLEXRATE";
    var hotelId = "LONEUS";
    var expectedResponse = new RatePlanInfoResponseDto();
    expectedResponse.setRatePlanInfo(Collections.emptyList());

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RatePlanInfoResponseDto.class)).thenReturn(Mono.just(expectedResponse));

    // Act
    var response = ohipClient.getRatePlanInfo(ratePlanCode, hotelId);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getRatePlanInfo());
  }

  @Test
  void getRatePlanInfo__shouldReturnException() {

    // Arrange
    var ratePlanCode = "FLEXRATE";
    var hotelId = "LONEUS";
    String errorMessage = String.format(
        "Error while trying to get rate plan info for ratePlanCode=%s, hotelId=%s",
        ratePlanCode, hotelId);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RatePlanInfoResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getRatePlanInfo(ratePlanCode, hotelId));

    // Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRatePlans_shouldReturnException(){
    String errorMessage = "Error while trying to get rate plans!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RatePlansResponseDto.class))
        .thenThrow(new OhipClientException("message",
            errorMessage, new Exception(), 1));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getRatePlans(List.of("1","2"), "hotelId"));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRestrictionsByDateRange_When5xx_ThenExceptionIsThrown(){
    String errorMessage = "Error while trying to get restrictions by date range";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RestrictionsByDateRangeResult.class))
          .thenThrow(new OhipClientException("message", errorMessage, new Exception(), 1));

    var thrownException = assertThrowsExactly(OhipClientException.class,
          () -> ohipClient.getRestrictionsByDateRange(RestrictionsByDateRangeRequest.builder()
                      .startDate("2025-01-01")
                      .endDate("2025-01-10")
                      .hotelId("HEAPTI")
                .build()));

    Assertions.assertEquals(errorMessage, thrownException.getDebugMessage());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getRestrictionsByDateRange_WhenResponseReceived_ThenParsedCorrectly() {
    var expected = RestrictionsByDateRangeResult.builder()
          .hotelId("HEAPTI")
          .restrictionSets(of(RestrictionSets.builder()
                .start("2025-01-01")
                .end("2025-01-10")
                .restrictionStatus(RestrictionStatus.builder()
                      .code("MinimumLengthOfStay")
                      .unit(2)
                      .build())
                .build()))
          .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RestrictionsByDateRangeResult.class)).thenReturn(Mono.just(expected));

    var actual = ohipClient
          .getRestrictionsByDateRange(RestrictionsByDateRangeRequest
                .builder()
                .startDate("2025-01-01")
                .endDate("2025-01-10")
                .hotelId("HEAPTI")
                .build());

    Assertions.assertNotNull(actual);
    assertEquals(expected.getHotelId(), actual.getHotelId());
    var actualRestrictionSets = actual.getRestrictionSets().get(0);
    var expectedRestrictionSets = expected.getRestrictionSets().get(0);
    assertEquals(expectedRestrictionSets.getStart(), actualRestrictionSets.getStart());
    assertEquals(expectedRestrictionSets.getEnd(), actualRestrictionSets.getEnd());
    assertEquals(expectedRestrictionSets.getRestrictionStatus().getCode(), actualRestrictionSets.getRestrictionStatus().getCode());
    assertEquals(expectedRestrictionSets.getRestrictionStatus().getUnit(), actualRestrictionSets.getRestrictionStatus().getUnit());
  }

  @Test
  void getMultiHotelRestrictionsByDateRange_WhenResponseReceived_ThenParsedCorrectly() {
    var expected = RestrictionsByDateRangeResult.builder()
        .hotelId("HEAPTI")
        .restrictionSets(of(RestrictionSets.builder()
            .start("2025-01-01")
            .end("2025-01-10")
            .restrictionStatus(RestrictionStatus.builder()
                .code("MinimumLengthOfStay")
                .unit(2)
                .build())
            .build()))
        .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
        .thenReturn(Mono.just(List.of(expected)));

    var actual = ohipClient
        .getMultiHotelRestrictionsByDateRange(MultiHotelRestrictionsByDateRangeRequest
            .builder()
            .startDate("2025-01-01")
            .endDate("2025-01-10")
            .hotelIds(List.of("HEAPTI","LONEUS"))
            .build());

    Assertions.assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(expected.getHotelId(), actual.get(0).getHotelId());
    var actualRestrictionSets = actual.get(0).getRestrictionSets().get(0);
    var expectedRestrictionSets = expected.getRestrictionSets().get(0);
    assertEquals(expectedRestrictionSets.getStart(), actualRestrictionSets.getStart());
    assertEquals(expectedRestrictionSets.getEnd(), actualRestrictionSets.getEnd());
    assertEquals(expectedRestrictionSets.getRestrictionStatus().getCode(), actualRestrictionSets.getRestrictionStatus().getCode());
    assertEquals(expectedRestrictionSets.getRestrictionStatus().getUnit(), actualRestrictionSets.getRestrictionStatus().getUnit());
  }

  @Test
  void getMultiHotelRestrictionsByDateRange_When5xx_ThenExceptionIsThrown() {
    //Arrange
    String error = "Error while trying to get preference labels";
    MultiHotelRestrictionsByDateRangeRequest request = MultiHotelRestrictionsByDateRangeRequest.builder()
        .startDate("2025-01-01")
        .endDate("2025-01-10")
        .hotelIds(List.of("HEAPTI", "LONEUS"))
        .build();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<RestrictionsByDateRangeResult>>() {}))
        .thenReturn(Mono.error(new OhipClientException("message", error, new Exception(), 1)));

    // Act & Assert
    OhipClientException thrownException = assertThrowsExactly(OhipClientException.class, () -> {
      ohipClient.getMultiHotelRestrictionsByDateRange(request);
    });

    //Assert
    verifyNoMoreInteractions(webClient);
    Assertions.assertEquals(thrownException.getMessage(), error);
  }

  @Test
  void getLightweightReservations_shouldReturnOk() {
    // Arrange
    var hotelId = HOTEL_ID;
    var reservationIds = Set.of(RES_1, RES_2);
    var expectedResponse = mockReservationLightweightResponseDto();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class))
        .thenReturn(Mono.just(expectedResponse));

    // Act
    var actualResponse = ohipClient.getLightweightReservations(hotelId, reservationIds).block();

    // Assert
    Assertions.assertNotNull(actualResponse);
    assertEquals(2, expectedResponse.getReservationByIdList().size());
  }

  @Test
  void getLightweightReservations_shouldThrowException() {
    // Arrange
    var hotelId = HOTEL_ID;
    var reservationIds = Set.of(RES_1, RES_2);
    var error = ERROR_WHILE_TRYING_TO_GET_LIGHTWEIGHT_RESERVATION;

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message", error, new Exception(), 1)));

    // Act & Assert
    OhipClientException thrownException = assertThrowsExactly(OhipClientException.class, () -> {
      ohipClient.getLightweightReservations(hotelId, reservationIds).block();
    });

    Assertions.assertEquals(error, thrownException.getMessage());
  }

  private Mono<HotelAvailabilityByIdsDto> mockAvailabilityByIdsResponse() {
    var response = new HotelAvailabilityByIdsDto();
    response.setHotelAvailability(List.of(mockAvailabilityResponse().block()));
    return Mono.just(response);
  }

  private HotelAvailabilityByIdsRequestOhipDto getHotelAvailabilityByIdsRequestOhipDto() {
    return HotelAvailabilityByIdsRequestOhipDto.builder()
        .hotelIds(List.of(HOTEL_ID))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .adultsNumber(ADULTS)
        .roomTypes(ROOM_TYPES)
        .childrenNumber(of())
        .cotsRequired(of())
        .build();
  }

  private RateCodeCriteria createRateCodeRequest() {
    return RateCodeCriteria.builder()
        .hotelId("MANOLD")
        .arrivalDate("2022-10-20")
        .departureDate("2022-10-22")
        .adultsNo(List.of(2))
        .childrenNo(List.of(0))
        .ratePlanCode("FLEXRATE")
        .roomTypes(List.of("DOUBLE"))
        .build();
  }

  private Mono<RateCodePricingResult> mockRateCodePricing() {
    return Mono.just(RateCodePricingResult.builder()
        .ratePlanCode("FLEXRATE")
        .totalNetAmount(BigDecimal.valueOf(1280))
        .currencyCode("EUR")
        .build());
  }

  private HotelAvailabilityRequestOhipDto getHotelAvailabilityRequestOhipDto() {
    return HotelAvailabilityRequestOhipDto.builder()
        .hotelId(HOTEL_ID)
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .adultsNumber(ADULTS)
        .roomTypes(ROOM_TYPES)
        .childrenNumber(of())
        .cotsRequired(of())
        .build();
  }

  private Mono<HotelAvailabilityDto> mockAvailabilityResponseV1() {
    var roomType = new RoomTypeDto();
    roomType.setRoomType("DB");

    var roomRate = new RoomRateDto();
    roomRate.setRatePlanCode("FLEXRATE");
    roomRate.setRoomTypes(of(roomType));

    var response = new HotelAvailabilityDto();
    response.setHotelId("TKINPT");
    response.setStartDate("2022-03-01");
    response.setEndDate("2022-03-02");
    response.setAvailable(true);
    response.setRoomRates(of(roomRate));

    return Mono.just(response);
  }

  private Mono<HotelAvailabilityResponseDto> mockAvailabilityResponse() {
    var roomType = new RoomTypeDto();
    roomType.setRoomType("DB");

    var roomRate = new RoomRateDto();
    roomRate.setRatePlanCode("FLEXRATE");
    roomRate.setRoomTypes(of(roomType));

    var response = new HotelAvailabilityResponseDto();
    response.setHotelId("TKINPT");
    response.setStartDate("2022-03-01");
    response.setEndDate("2022-03-02");
    response.setAvailable(true);
    response.setRoomRates(of(roomRate));

    return Mono.just(response);
  }

  private HotelInventoryRequestOhipDto createHotelInventoryRequest() {
    return HotelInventoryRequestOhipDto.builder().hotelId("MANOLD").dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30").build();
  }

  private Mono<HotelInventoryRoomTypeDto> mockInventoryResponse() {

    RoomLevelInventoryDto rl = new RoomLevelInventoryDto();
    rl.setAvailableCount(42);
    rl.setCode("DOUBLE");

    var response = new HotelInventoryRoomTypeDto();
    response.setRoomTypeInventories(List.of(rl));

    return Mono.just(response);
  }

  private static ReservationLightweightResponseDto mockReservationLightweightResponseDto() {
    var expectedResponse = new ReservationLightweightResponseDto();
    var res1 = new LightweightReservationByIdDto();
    res1.setReservationId(RES_1);
    var res2 = new LightweightReservationByIdDto();
    res1.setReservationId(RES_2);
    expectedResponse.setReservationByIdList(List.of(res1, res2));
    return expectedResponse;
  }

  @Test
  void getHotelAvailabilityByIdsV3_shouldReturnException() {
    String errorMessage = "Error while trying to get availability by ids v2";
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipProperties.getAvailabilityByIdsEndpointV3()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(AvailabilityByIdsResponseV2Dto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelAvailabilityByIdsV3(HotelAvailabilityByIdsRequestOhipV3Dto.builder().build()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
  }

  @Test
  void getHotelAvailability__shouldThrowBadRequestException_when4xxError() {
    // Arrange
    String badRequestMessage = "Hotel not found";
    String debugMessage = "The hotel code provided does not exist";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelAvailabilityDto.class))
        .thenReturn(Mono.error(new HotelAvailabilityBadReqException(badRequestMessage, debugMessage, null, 404)));

    // Act
    var thrownException = assertThrowsExactly(
        HotelAvailabilityBadReqException.class,
        () -> ohipClient.getHotelAvailability(getHotelAvailabilityRequestOhipDto()));

    // Assert
    Assertions.assertEquals(debugMessage, thrownException.getDebugMessage());
    Assertions.assertEquals(404, thrownException.getErrorCode());
  }

  @Test
  void fetchReservationPreferences__shouldReturnOk() {
    // Arrange
    var expected = new KioskReservationPreferencesDto();
    var capturedUri = new AtomicReference<URI>();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      capturedUri.set(uriFunction.apply(UriComponentsBuilder.fromUriString("http://localhost")));
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(KioskReservationPreferencesDto.class)).thenReturn(Mono.just(expected));

    // Act
    var actual = ohipClient.fetchReservationPreferences(HOTEL_ID, "RES123");

    // Assert
    assertThat(actual, notNullValue());
    Assertions.assertNotNull(capturedUri.get());
    var query = capturedUri.get().getQuery();
    Assertions.assertNotNull(query);
    Assertions.assertTrue(query.contains("hotelId=" + HOTEL_ID));
    Assertions.assertTrue(query.contains("reservationId=RES123"));
  }

  @Test
  void fetchReservationPreferences__shouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(KioskReservationPreferencesDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.fetchReservationPreferences(HOTEL_ID, "RES123"));

    // Assert
    assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, thrownException.getDebugMessage());
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void getVacantRooms__shouldReturnOk() {
    // Arrange
    var expected = new VacantRoomResponseDto();
    var capturedUri = new AtomicReference<URI>();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      capturedUri.set(uriFunction.apply(UriComponentsBuilder.fromUriString("http://localhost")));
      return requestBodySpec;
    });
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VacantRoomResponseDto.class)).thenReturn(Mono.just(expected));

    // Act
    var actual = ohipClient.getVacantRooms(HOTEL_ID, "DB");

    // Assert
    assertThat(actual, notNullValue());
    Assertions.assertNotNull(capturedUri.get());
    var query = capturedUri.get().getQuery();
    Assertions.assertNotNull(query);
    Assertions.assertTrue(query.contains("hotelId=" + HOTEL_ID));
    Assertions.assertTrue(query.contains("roomType=DB"));
  }

  @Test
  void getVacantRooms__shouldReturnException() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(VacantRoomResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getVacantRooms(HOTEL_ID, "DB"));

    // Assert
    assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, thrownException.getDebugMessage());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getOnSaleFlagFromOpera__shouldThrowBadRequestException_when4xxError() {
    // Arrange
    String badRequestMessage = "Invalid hotel IDs";
    String debugMessage = "One or more hotel IDs are invalid";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
        .thenReturn(Mono.error(new HotelAvailabilityBadReqException(badRequestMessage, debugMessage, null, 400)));

    // Act
    var thrownException = assertThrowsExactly(
        HotelAvailabilityBadReqException.class,
        () -> ohipClient.getOnSaleFlagFromOpera(List.of("INVALID1", "INVALID2")));

    // Assert
    Assertions.assertEquals(debugMessage, thrownException.getDebugMessage());
    Assertions.assertEquals(400, thrownException.getErrorCode());
  }

  @Test
  void getReservationInfo__shouldReturnOk() {
    // Arrange
    var expected = new ReservationByBasketRefResponseDto();
    var capturedUri = new AtomicReference<URI>();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      capturedUri.set(uriFunction.apply(UriComponentsBuilder.fromUriString("http://localhost")));
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(Mono.just(expected));

    // Act
    var actual = ohipClient.getReservationInfo(
        HOTEL_ID,
        List.of("RES1", "RES2"),
        true,
        false,
        true);

    // Assert
    assertThat(actual, notNullValue());
    Assertions.assertNotNull(capturedUri.get());
    var query = capturedUri.get().getQuery();
    Assertions.assertNotNull(query);
    Assertions.assertTrue(query.contains("hotelId=" + HOTEL_ID));
    Assertions.assertTrue(query.contains("priceBreakdownNeeded=true"));
    Assertions.assertTrue(query.contains("operaUiCreatedRsv=false"));
    Assertions.assertTrue(query.contains("rateInfoNeeded=true"));
    Assertions.assertTrue(query.contains("reservationIds=RES1") && query.contains("RES2"));
  }

  @Test
  void getReservationInfo__shouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(ReservationByBasketRefResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getReservationInfo(
            HOTEL_ID,
            List.of("RES1", "RES2"),
            true,
            false,
            true));

    // Assert
    assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, thrownException.getDebugMessage());
    verifyNoMoreInteractions(webClient);
  }
}
