package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.config.AvailabilityCacheProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.AvailabilityCacheV1Exception;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.AvailableCostsDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelAvailabilitiesDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RatePlanOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RoomOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in.AvailabilityCacheRequestV1;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RatePlanOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RoomOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.TotalCostDto;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class AvailabilityCacheV1ClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @InjectMocks
  private AvailabilityCacheV1Client availabilityCacheClient;
  @Mock
  private AvailabilityCacheProperties properties;

  @Test
  void getAvailabilitiesResponseFromCacheV1Distr__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilitiesDistrDto.class)).thenReturn(mockHotelAvailabilitiesDistrDto());
    var avaCacheRequest = mockAvaCacheRequest();

    //Act
    var avaCacheResponse = this.availabilityCacheClient
        .getAvailabilitiesResponseFromCacheV1Distr(avaCacheRequest,Collections.emptySet());

    //Assert
    assertThat(avaCacheResponse, notNullValue());
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelCode(), is("LONLEI"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelName(),
        is("Test Hotel Name"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getName(), is("Flex"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getRooms().get(0).getRoomType(), is("DB"));
  }

  @Test
  void getAvailabilitiesResponseFromCacheV1DistrWithRates__ShouldReturnOK() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilitiesDistrDto.class)).thenReturn(mockHotelAvailabilitiesDistrDto());
    when(properties.getDistrAvailabilityCacheEndpoint()).thenReturn("/availability/v1/distr");

    var uriBuilderFactory = new DefaultUriBuilderFactory();
    var uriBuilder = uriBuilderFactory.builder().scheme("http").host("localhost");

    var avaCacheRequest = mockAvaCacheRequest();

    // Act
    var avaCacheResponse = this.availabilityCacheClient
        .getAvailabilitiesResponseFromCacheV1Distr(avaCacheRequest, Set.of("FLEXRATE"));

    // Assert
    verify(requestHeadersUriSpec).uri(uriCaptor.capture());
    URI capturedUri = uriCaptor.getValue().apply(uriBuilder);
    assertThat(capturedUri.toString(), is(notNullValue()));
    assertThat(capturedUri.getQuery(), is(notNullValue()));
    assertTrue(capturedUri.getQuery().contains("ratePlanCodes=FLEXRATE"));
    assertThat(avaCacheResponse, notNullValue());
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelCode(), is("LONLEI"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelName(),
        is("Test Hotel Name"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getName(), is("Flex"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getRooms().get(0).getRoomType(), is("DB"));
  }

  @Test
  void getAvailabilitiesResponseFromCacheV1Distr__shouldReturnException() {
    String errorMessage = "An error was returned while trying to create hotel availabilities from cacheV1 distr!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(AvailabilityCacheV1Exception.class,
        () -> availabilityCacheClient.getAvailabilitiesResponseFromCacheV1Distr(mockAvaCacheRequest(), Collections.emptySet()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getAvailabilitiesFromCache__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilitiesDto.class)).thenReturn(mockHotelAvailabilitiesDto());
    var avaCacheRequest = mockAvaCacheRequest();

    //Act
    var avaCacheResponse = this.availabilityCacheClient.getAvailabilitiesResponseFromCacheV1(avaCacheRequest);

    //Assert
    assertThat(avaCacheResponse, notNullValue());
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelCode(), is("LONLEI"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities().get(0).getHotelName(),
        is("Test Hotel Name"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getName(), is("Flex"));
    assertThat(avaCacheResponse.getOperaHotelAvailabilities()
        .get(0).getRates().get(0).getRooms().get(0).get(0).getType(), is("DB"));
  }

  @Test
  void getAvailabilitiesFromCache__shouldReturnException() {
    String errorMessage = "An error was returned while trying to create hotel availabilities from cacheV1!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(AvailabilityCacheV1Exception.class,
        () -> availabilityCacheClient.getAvailabilitiesResponseFromCacheV1(mockAvaCacheRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }


  private AvailabilityCacheRequestV1 mockAvaCacheRequest() {
    return AvailabilityCacheRequestV1.builder()
        .hotelCodes(Arrays.asList("LONLEI", "LONEUS"))
        .arrival("2022-01-01")
        .departure("2022-01-05")
        .language("en")
        .country("gb")
        .rooms(1)
        .adults(List.of(1))
        .children(List.of(0))
        .roomTypesList(List.of(List.of("DB")))
        .roomQty(List.of(1))
        .build();
  }

  private Mono<HotelAvailabilitiesDistrDto> mockHotelAvailabilitiesDistrDto() {
    return Mono.just(createAvailabilityCacheResponseDistr());
  }

  private HotelAvailabilitiesDistrDto createAvailabilityCacheResponseDistr() {
    return new HotelAvailabilitiesDistrDto(1, createHotelAvailabilitiesDistr());
  }

  private List<HotelOperaDistrDto> createHotelAvailabilitiesDistr() {
    List<HotelOperaDistrDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelOperaDistrDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, false, "OP", createRatesListDistr()));
    return hotelDtoList;
  }

  private List<RatePlanOperaDistrDto> createRatesListDistr() {
    List<RatePlanOperaDistrDto> ratePlanDtoList = new LinkedList<>();
    ratePlanDtoList.add(new RatePlanOperaDistrDto("FLEXRATE", "A",
        "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
        "Flex", "1", createRoomsDistr()));
    return ratePlanDtoList;
  }

  private List<RoomOperaDistrDto> createRoomsDistr() {
    List<RoomOperaDistrDto> roomDtoList = new LinkedList<>();
    roomDtoList.add(new RoomOperaDistrDto("DB", false, 1,
        List.of(new AvailableCostsDistrDto("2023-06-01",
            new BigDecimal("60.00"), "EUR", 1))));
    return roomDtoList;
  }

  private Mono<HotelAvailabilitiesDto> mockHotelAvailabilitiesDto() {
    return Mono.just(createAvailabilityCacheResponse());
  }

  private HotelAvailabilitiesDto createAvailabilityCacheResponse() {
    return new HotelAvailabilitiesDto(1, createHotelAvailabilities());
  }

  private List<HotelOperaDto> createHotelAvailabilities() {
    List<HotelOperaDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelOperaDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, false, "OP", createRatesList(), "EMP01", false, 1));
    return hotelDtoList;
  }

  private List<RatePlanOperaDto> createRatesList() {
    List<RatePlanOperaDto> ratePlanDtoList = new LinkedList<>();
    ratePlanDtoList.add(new RatePlanOperaDto("FLEXRATE", "A",
        "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
        "Flex", "1", List.of(createRooms())));
    return ratePlanDtoList;
  }

  private List<RoomOperaDto> createRooms() {
    List<RoomOperaDto> roomDtoList = new LinkedList<>();
    roomDtoList.add(new RoomOperaDto("DB", false, 1, 0, new TotalCostDto(new BigDecimal("60.00"), "EUR")));
    return roomDtoList;
  }

}