package uk.co.whitbread.basket.infrastructure.rest.client.hotel.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.generated.models.hotel.HotelInfoDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@ExtendWith(MockitoExtension.class)
class HotelInfoClientTest {

  @InjectMocks
  private HotelInfoClient hotelInfoClient;
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
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void getHotelInfo_shouldReturnOk() {
    // Arrange

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class)).thenReturn(
        mockHotelInfoResponse());

    // Act
    var response = hotelInfoClient.getHotelInfo("TSTTST");

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getThreeLetterId(), is("ABC"));
  }

  @Test
  void getHotelInfo_shouldReturnHotelReservationException() {

    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class)).thenReturn(
        Mono.error(new HotelReservationException("message",
            "An error was returned calling the Hotel service",new Exception(),1)));
    //Act
    Exception exception =  assertThrows(HotelReservationException.class,
        () -> hotelInfoClient.getHotelInfo("TSTTST"));

    //Assert
    String expectedMessage = "An error was returned calling the Hotel service";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  private Mono<HotelInfoDto> mockHotelInfoResponse() {
    var hotelInfoDto = new HotelInfoDto();
    hotelInfoDto.setThreeLetterId("ABC");

    return Mono.just(hotelInfoDto);
  }

}
