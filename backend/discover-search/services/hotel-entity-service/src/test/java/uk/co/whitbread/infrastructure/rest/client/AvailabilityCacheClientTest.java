package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.exception.AvailabilityCacheException;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.in.AvailabilityCacheRequest;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.PriceDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.RatePlanDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.RoomDto;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class AvailabilityCacheClientTest {

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
  private AvailabilityCacheClient availabilityCacheClient;

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
    var avaCacheResponse = availabilityCacheClient.getAvailabilitiesFromCache(avaCacheRequest);

    //Assert
    assertThat(avaCacheResponse, notNullValue());
    assertThat(avaCacheResponse.getHotelAvailabilities().get(0).getHotelCode(), is("LONLEI"));
    assertThat(avaCacheResponse.getHotelAvailabilities().get(0).getHotelName(),
        is("Test Hotel Name"));
    assertThat(avaCacheResponse.getHotelAvailabilities()
        .get(0).getRates().get(0).getName(), is("Flex"));
    assertThat(avaCacheResponse.getHotelAvailabilities()
        .get(0).getRates().get(0).getRooms().get(0).getType(), is("DB"));
  }

  @Test
  void getAvailabilitiesFromCache__shouldReturnException() {
    String errorMessage = "An error was returned while trying to create hotel availabilities from cache!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(AvailabilityCacheException.class,
        () -> availabilityCacheClient.getAvailabilitiesFromCache(mockAvaCacheRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }


  private AvailabilityCacheRequest mockAvaCacheRequest() {
    return AvailabilityCacheRequest.builder()
        .hotelCodes(Arrays.asList("LONLEI", "LONEUS"))
        .arrival("2022-01-01")
        .departure("2022-01-05")
        .language("en")
        .country("gb")
        .rooms(1)
        .adults(new int[] {1})
        .children(new int[] {0})
        .type(new String[] {"DB"})
        .page(1)
        .size(40)
        .build();
  }

  private Mono<HotelAvailabilitiesDto> mockHotelAvailabilitiesDto() {
    return Mono.just(createAvailabilityCacheResponse());
  }

  private HotelAvailabilitiesDto createAvailabilityCacheResponse() {
    return new HotelAvailabilitiesDto(1, 1, 1, createHotelAvailabilities());
  }

  private List<HotelDto> createHotelAvailabilities() {
    List<HotelDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, createRatesList()));
    return hotelDtoList;
  }

  private List<RatePlanDto> createRatesList() {
    List<RatePlanDto> ratePlanDtoList = new LinkedList<>();
    ratePlanDtoList.add(new RatePlanDto("FLEXRATE", "Flex",
        "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
        "A", "1", new PriceDto(new BigDecimal("60.00"), "EUR"), createRooms()));
    return ratePlanDtoList;
  }

  private List<RoomDto> createRooms() {
    List<RoomDto> roomDtoList = new LinkedList<>();
    roomDtoList.add(new RoomDto("DB", 1, 0, new PriceDto(new BigDecimal("60.00"), "EUR")));
    return roomDtoList;
  }

}