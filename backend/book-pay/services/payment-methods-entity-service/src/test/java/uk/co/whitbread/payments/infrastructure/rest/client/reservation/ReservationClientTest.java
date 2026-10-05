package uk.co.whitbread.payments.infrastructure.rest.client.reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.payments.infrastructure.rest.client.service.CustomTestResponseSpec;
import uk.co.whitbread.payments.infrastructure.rest.client.util.CacheHelper;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ReservationClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  private CacheHelper cacheHelper;
  @InjectMocks
  private ReservationClient reservationClient;


  @Test
  void getDepositFolios_success() {
    // Arrange
    DepositsResponseDto expected = new DepositsResponseDto(
        java.util.List.of(new DepositsDto("payment-ref", null)));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(DepositsResponseDto.class)).thenReturn(Mono.just(expected));

    // Act
    DepositsResponseDto result = reservationClient.getDepositFolios("HOTEL123", "RES123");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getDeposits().size());
    assertEquals("payment-ref", result.getDeposits().get(0).getPaymentReference());
  }

  @Test
  void getDepositFolios_throwsException() {
    // Arrange
    String errorMessage = "Error while trying to get deposit folios";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(DepositsResponseDto.class))
        .thenReturn(Mono.error(new HotelReservationException("message",
            errorMessage, new Exception(), 1)));

    // Act & Assert
    Exception exception = assertThrows(HotelReservationException.class,
        () -> reservationClient.getDepositFolios("HOTEL123", "RES123"));
    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void findReservations_throwsException() {
    String errorMessage = "Error while trying to find reservations";
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationListDto.class))
        .thenReturn(Mono.error(new HotelReservationException("message",
            errorMessage, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(HotelReservationException.class,
        () -> reservationClient.findReservations("basketReference"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  @Test
  void getCachedReservations_cacheHit_returnsCachedValue() {
    // Arrange
    String basketReference = "basketRef123";
    ReservationListDto expectedDto = new ReservationListDto();

    when(cacheHelper.getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class))
        .thenReturn(expectedDto);

    // Act
    ReservationListDto result = reservationClient.getCachedReservations(basketReference);

    // Assert
    assertNotNull(result);
    assertEquals(expectedDto, result);
    verify(cacheHelper).getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class);
    verify(webClient, never()).get();
  }

  @Test
  void getCachedReservations_cacheMiss_fetchesFromService() {
    // Arrange
    String basketReference = "basketRef456";
    ReservationListDto expectedDto = new ReservationListDto();

    when(cacheHelper.getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class))
        .thenReturn(null);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(ReservationListDto.class)).thenReturn(Mono.just(expectedDto));

    // Act
    ReservationListDto result = reservationClient.getCachedReservations(basketReference);

    // Assert
    assertNotNull(result);
    assertEquals(expectedDto, result);
    verify(cacheHelper).getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class);
    verify(webClient).get();
  }

  @Test
  void getCachedReservations_cacheMiss_throwsExceptionWhenServiceFails() {
    // Arrange
    String basketReference = "basketRef789";
    String errorMessage = "Service unavailable";

    when(cacheHelper.getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class))
        .thenReturn(null);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationListDto.class))
        .thenReturn(Mono.error(new HotelReservationException("message", errorMessage, new Exception(), 1)));

    // Act & Assert
    Exception exception = assertThrows(HotelReservationException.class,
        () -> reservationClient.getCachedReservations(basketReference));

    assertTrue(exception.getMessage().contains(errorMessage));
    verify(cacheHelper).getCacheValue(ReservationClient.CACHE_NAME, basketReference, ReservationListDto.class);
    verify(webClient).get();
  }
}
