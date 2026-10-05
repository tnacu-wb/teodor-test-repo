package uk.co.whitbread.booking.infrastructure.rest.client.basket.service;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.model.out.BasketForBookingReferencesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.BaseOperaEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBasketOptionsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.utils.CustomTestResponseSpec;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;

import java.util.function.Function;
import java.util.function.Predicate;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class BasketClientTest {

  private static final String BASKET_REFERENCE = "basketReference";
  private static final String BOOKING_REFERENCE = "bookingReference";

  @InjectMocks
  private BasketClient basketClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
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
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  BasketProperties properties;

  @Test
  void getReservationBasketOptions_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationBasketOptionsResponseDto.class)).thenReturn(
            Mono.just(new ReservationBasketOptionsResponseDto()));

    // Act
    var response = basketClient.getBasketOptions(BASKET_REFERENCE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationBasketOptions_throwsBasketException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationBasketOptionsResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));

    // Assert
    assertThrows(InternalBasketException.class,
            () -> basketClient.getBasketOptions(BASKET_REFERENCE));
  }

  @Test
  void getBasketByBookingReference_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationBasketOptionsResponseDto.class)).thenReturn(
            Mono.just(new ReservationBasketOptionsResponseDto()));

    // Act
    var response = basketClient.getBasketByBookingReference(BOOKING_REFERENCE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getBasketByBookingReference_throwsBasketException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(ReservationBasketOptionsResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));
    // Assert
    assertThrows(InternalBasketException.class,
            () -> basketClient.getBasketByBookingReference(BOOKING_REFERENCE));
  }

  @Test
  void sendReservationConfirmationOrInvoiceEmail_success() {
    // Arrange
    var mockReservationBaseEmailRequest = BaseOperaEmailRequestDto.builder().build();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getOperaResendEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    var response =basketClient.sendBookingConfirmationOrInvoiceEmail(mockReservationBaseEmailRequest);

    // Assert
    assertNull(response);
  }

  @Test
  void sendReservationConfirmationOrInvoiceEmail_throwsBasketException() {
    var mockReservationBaseEmailRequest = BaseOperaEmailRequestDto.builder().build();
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getOperaResendEmailEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.toBodilessEntity())
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));

    // Assert
    assertThrows(InternalBasketException.class,
            () -> basketClient.sendBookingConfirmationOrInvoiceEmail(mockReservationBaseEmailRequest));
  }

  @Test
  void getBasketsStatusesByBookingRefs_success() {
    // Arrange
    var basketStatusRefs = new BasketForBookingReferencesDto(BasketStatus.PRE_CHECKED_IN, "bookingRef",
        List.of(new BasketItemDto()));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(BasketForBookingReferencesDto.class)).thenReturn(
        Flux.just(basketStatusRefs));

    // Act
    var response =basketClient.getBasketsForBookingReferences(Set.of("bookingRefs1", "bookingRefs2"));

    // Assert
    assertNotNull(response);
    assertEquals(BasketStatus.PRE_CHECKED_IN,response.get(0).getStatus());
    assertEquals("bookingRef",response.get(0).getBookingReference());
  }

  @Test
  void sendGetBasketByReference_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(
        Mono.just(ResponseEntity.ok(new BasketDto())));

    // Act
    var response = basketClient.sendGetBasketByReference("TestBasketReference");

    // Assert
    assertNotNull(response);
  }

  @Test
  void sendGetBasketByReference_NotFound() {
    // Arrange
    InternalBasketException ex = mock(InternalBasketException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(responseSpecMock.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(InternalBasketException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

  @Test
  void sendGetBasketByReference_BadRequest() {
    // Arrange
    InternalBasketException ex = mock(InternalBasketException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(responseSpecMock.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(InternalBasketException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

  @Test
  void sendGetBasketByReference_InternalServerError() {
    // Arrange
    InternalBasketException ex = mock(InternalBasketException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(responseSpecMock.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(InternalBasketException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

}
