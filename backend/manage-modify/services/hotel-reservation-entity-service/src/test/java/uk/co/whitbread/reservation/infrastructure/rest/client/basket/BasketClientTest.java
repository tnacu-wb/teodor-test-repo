package uk.co.whitbread.reservation.infrastructure.rest.client.basket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddAmendedReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CancelBasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketIdContextDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketStatusDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EmailRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ErroredBookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.InitiatePaymentResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ProcessAmendRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromotionsInformationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateAllowancesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UserInfoDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BasketClientTest {

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
  private BasketProperties basketProperties;
  @InjectMocks
  private BasketClient basketClient;

  @Test
  void sendGetBasket_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123\"");
    var responseEntity =
        new ResponseEntity<>(new BasketDto(), headers, HttpStatusCode.valueOf(200));
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));

    // Act
    var response = basketClient.sendGetBasket("TestBasketReference");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
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
  void sendCreateBasket_success() {
    // Arrange
    HttpHeaders header = new HttpHeaders();
    header.set("ETag", "123");
    ResponseEntity<BasketDto> responseEntity = new ResponseEntity<>(
        new BasketDto(),
        header,
        HttpStatus.OK);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = basketClient.createBasket(mock(CreateBasketRequestDto.class));

    // Assert
    assertNotNull(response);
    assertThat(response.getT2(), is("123"));
  }

  @Test
  void updateOccupancySupplement_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenReturn(customResponseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123\"");
    var responseEntity = new ResponseEntity<Void>(headers, HttpStatusCode.valueOf(200));
    when(customResponseSpec.toBodilessEntity()).thenReturn(Mono.just(responseEntity));

    // Act
    var response =
        basketClient.updateOccupancySupplement("TestBasketReference", new UpdateBasketItemOccupancyRequestDto(),
            "eTag");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateOccupancySupplement_failure() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.updateOccupancySupplement("TestBasketReference", new UpdateBasketItemOccupancyRequestDto(),
            "eTag"));
  }

  @Test
  void addBasketItems_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123:dtagent\"");
    var responseEntity =
            new ResponseEntity<>(new BasketDto(), headers, HttpStatusCode.valueOf(200));
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));

    // Act
    var response = basketClient.addBasketItems("TestBasketReference", "123",
        new AddBasketItemRequestDto());

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void removeItem_success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123\"");
    var responseEntity =
        new ResponseEntity<>(new BasketDto(), headers, HttpStatusCode.valueOf(200));
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));

    // Act
    var response = basketClient.removeItem("TestBasketReference", "123", "123");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void removeItems_success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123\"");
    var responseEntity =
        new ResponseEntity<>(new BasketDto(), headers, HttpStatusCode.valueOf(200));
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));

    // Act
    var response = basketClient.removeItems("TestBasketReference", List.of("123"), "123");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void triggerRefund_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RefundResponseDto.class)).thenReturn(
        Mono.just(new RefundResponseDto()));

    // Act
    basketClient.triggerRefund("TestBasketReference", new RefundRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteBasket_success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toEntity(Void.class)).thenReturn(Mono.empty());
//    // Act
    basketClient.deleteBasket("TestBasketReference", "123");

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void saveCharges_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    PrepaidDepositDto prepaidDepositDto = new PrepaidDepositDto();
    prepaidDepositDto.setPaymentNo(1L);
    prepaidDepositDto.setReservationId("11111");
    PrepaidDepositsRequestDto prepaidDepositsDto = new PrepaidDepositsRequestDto();
    prepaidDepositsDto.addPrepaidDepositsItem(prepaidDepositDto);

    // Act
    basketClient.saveCharges(prepaidDepositsDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void getCharges_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PrepaidDepositsDto.class)).thenReturn(
        Mono.just(new PrepaidDepositsDto()));

    // Act
    var response = basketClient.getCharges("reservationId");

    // Assert
    assertNotNull(response);
  }

  @Test
  void sendGetBasketByReference_NotFound() {
    // Arrange
    BasketNotFoundException ex = mock(BasketNotFoundException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketNotFoundException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

  @Test
  void sendGetBasketByReference_BadRequest() {
    // Arrange
    BasketNotFoundException ex = mock(BasketNotFoundException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketNotFoundException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

  @Test
  void sendGetBasketByReference_InternalServerError() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendGetBasketByReference("TestBasketReference"));
  }

  @Test
  void createBasket_throwsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.createBasket(new CreateBasketRequestDto()));
  }

  @Test
  void createBasket_ThrowsException2() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.OK);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(
        Mono.just(ResponseEntity.ok(new BasketDto())));
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    CreateBasketRequestDto createBasketRequestDto = new CreateBasketRequestDto();

    // Act & Assert
    assertThrows(BasketDigitalException.class,
        () -> basketClient.createBasket(createBasketRequestDto));
  }

  @Test
  void sendGetBasketByReference_ThrowsException3() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.OK);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(
        Mono.just(ResponseEntity.ok(new BasketDto())));
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    CreateBasketRequestDto createBasketRequestDto = new CreateBasketRequestDto();

    // Act & Assert
    assertThrows(BasketDigitalException.class,
        () -> basketClient.createBasket(createBasketRequestDto));
  }

  @Test
  void addBasketItems_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header("If-Match", "etag")).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.addBasketItems("basketRef", "etag", new AddBasketItemRequestDto()));
  }

  @Test
  void linkAmendReservationsInBasket_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<Object>> ent = Mono.error(ex);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header("If-Match", "etag")).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.toEntity((Class<Object>) any())).thenReturn(ent);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.linkAmendReservationsInBasket("basketRef", "etag",
            new AddAmendedReservationsRequestDto()));
  }

  @Test
  void removeItem_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header("If-Match", "etag")).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.removeItem("basketRef", "1234", "etag"));
  }

  @Test
  void removeItems_ThrowsException() {
    // Arrange
    var list = List.of("1234");
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header("If-Match", "etag")).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.removeItems("basketRef", list, "etag"));
  }

  @Test
  void sendGetBasket_ThrowsException() {
    // Arrange
    BasketNotFoundException ex = mock(BasketNotFoundException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketNotFoundException.class,
        () -> basketClient.sendGetBasket("basketRef"));
  }

  @Test
  void sendGetBasket_ThrowsException2() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendGetBasket("basketRef"));
  }

  @Test
  void sendGetBasket_ThrowsException3() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    var responseEntity =
        new ResponseEntity<BasketDto>(new BasketDto(), (org.springframework.http.HttpHeaders) null, HttpStatusCode.valueOf(200));
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));

    // Act & Assert
    assertThrows(BasketDigitalException.class,
        () -> basketClient.sendGetBasket("basketRef"));
  }

  @Test
  void sendCancelBasket_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendCancelBasket("basketRef", new CancelBasketDto()));
  }

  @Test
  void sendSetErroredBooking_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    var erroredBookingDto = new ErroredBookingDto();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.setErroredBooking("basketRef", erroredBookingDto));
  }

  @Test
  void sendSetErroredBooking_success() {

    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenReturn(customResponseSpec);
    when(customResponseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
   basketClient.setErroredBooking("basketRef", new ErroredBookingDto());

    // Assert
   verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateAllowances_ThrowsException() {
    // Arrange
    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<Void>> ent = Mono.error(ex);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.toBodilessEntity()).thenReturn(ent);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    var updateAllowancesRequestDto = new UpdateAllowancesRequestDto();

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.updateAllowances("basketRef", updateAllowancesRequestDto, "eTag"));
  }

  @Test
  void updateAllowances_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenReturn(customResponseSpec);
    var headers = new HttpHeaders();
    headers.setETag("\"123\"");
    var responseEntity = new ResponseEntity<Void>(headers, HttpStatusCode.valueOf(200));
    when(customResponseSpec.toBodilessEntity()).thenReturn(Mono.just(responseEntity));

    // Act
    basketClient.updateAllowances("basketRef", new UpdateAllowancesRequestDto(), "eTag");

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void triggerEmailConfirmation_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getEmailConfirmationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toEntity(Void.class)).thenReturn(Mono.empty());

    // Act
    basketClient.triggerEmailConfirmation(new EmailRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void initiatePayment_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(InitiatePaymentResponseDto.class)).thenReturn(
        Mono.just(new InitiatePaymentResponseDto()));

    // Act
    basketClient.initiatePayment("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77",
        new PaymentRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void processAmend_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    basketClient.processAmend("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77",
        new ProcessAmendRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void changeBasketStatus_success() {
    // Arrange
    String bookingRef = "bookingRef123";
    ChangeBasketStatusDto changeDto = new ChangeBasketStatusDto();
    BasketDto expectedResponse = new BasketDto();

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(ChangeBasketStatusDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(), any())).thenReturn(customResponseSpec);

    when(customResponseSpec.bodyToMono(BasketDto.class)).thenReturn(Mono.just(expectedResponse));

    // Act
    BasketDto actualResponse = basketClient.changeStatus(bookingRef, changeDto);

    // Assert
    assertEquals(expectedResponse, actualResponse);
    verify(webClient).put();
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).body(any(), eq(ChangeBasketStatusDto.class));
    verify(requestHeadersSpec).retrieve();
    verify(customResponseSpec).onStatus(any(), any());
    verify(customResponseSpec).bodyToMono(BasketDto.class);
  }

  @Test
  void changeBasketStatus_onStatusError_throwsException() {
    // Arrange
    String bookingRef = "bookingRef123";
    ChangeBasketStatusDto changeDto = new ChangeBasketStatusDto();

    BasketInternalException expectedException = new BasketInternalException(
        "Error occurred",
        "Debug info for error",
        null,
        1001
    );

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(ChangeBasketStatusDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(), any()))
        .thenAnswer(invocation -> customResponseSpec);

    when(customResponseSpec.bodyToMono(BasketDto.class))
        .thenReturn(Mono.error(expectedException));

    // Act & Assert
    BasketInternalException actualException = assertThrows(BasketInternalException.class, () ->
        basketClient.changeStatus(bookingRef, changeDto)
    );

    assertEquals("Debug info for error", actualException.getMessage());
    assertEquals("Debug info for error", actualException.getDebugMessage());
    assertEquals(1001, actualException.getErrorCode());
  }

  @Test
  void sendAddPromotionToBasket_success() {
    // Arrange
    String basketReference = "basket-123";
    String lastModifiedETag = "etag";
    PromotionsInformationRequestDto promotionsRequest = mock(PromotionsInformationRequestDto.class);

    HttpHeaders header = new HttpHeaders();
    header.set("ETag", "123");
    ResponseEntity<BasketDto> responseEntity = new ResponseEntity<>(
        new BasketDto(),
        header,
        HttpStatus.OK);

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(PromotionsInformationRequestDto.class))).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header("If-Match", lastModifiedETag)).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var result = basketClient.addPromotionToBasket(basketReference, promotionsRequest,
        lastModifiedETag);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getT1());
    assertThat(result.getT2(), is("123"));

    verify(webClient).put();
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).body(any(), eq(PromotionsInformationRequestDto.class));
    verify(requestHeadersSpec).header("If-Match", lastModifiedETag);
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).toEntity(BasketDto.class);
  }

  @Test
  void addPromotionToBasket_throwsException() {
    // Arrange
    var promotionRequest = new PromotionsInformationRequestDto();
    promotionRequest.setPromotionCode("TEST");
    promotionRequest.setPromoKind(PromoKind.SITE_WIDE);

    BasketInternalException ex = mock(BasketInternalException.class);
    Mono<ResponseEntity<BasketDto>> rsp = Mono.error(ex);

    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(rsp);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class),
        eq(PromotionsInformationRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header("If-Match", "etag")).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.addPromotionToBasket("basketRef", promotionRequest, "etag"));
  }

  @Test
  void changeBasketIdContext_success() {
    // Arrange
    String bookingRef = "bookingRef123";
    ChangeBasketIdContextDto changeDto = new ChangeBasketIdContextDto();
    BasketDto expectedResponse = new BasketDto();

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(ChangeBasketIdContextDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(), any())).thenReturn(customResponseSpec);

    when(customResponseSpec.bodyToMono(BasketDto.class)).thenReturn(Mono.just(expectedResponse));

    // Act
    BasketDto actualResponse = basketClient.changeIdContext(bookingRef, changeDto);

    // Assert
    assertEquals(expectedResponse, actualResponse);
    verify(webClient).put();
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).body(any(), eq(ChangeBasketIdContextDto.class));
    verify(requestHeadersSpec).retrieve();
    verify(customResponseSpec).onStatus(any(), any());
    verify(customResponseSpec).bodyToMono(BasketDto.class);
  }

  @Test
  void changeBasketIdContext_onStatusError_throwsException() {
    // Arrange
    String bookingRef = "bookingRef123";
    ChangeBasketIdContextDto changeDto = new ChangeBasketIdContextDto();

    BasketInternalException expectedException = new BasketInternalException(
        "Error occurred",
        "Debug info for error",
        null,
        1001
    );

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(ChangeBasketIdContextDto.class))).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(), any()))
        .thenAnswer(invocation -> customResponseSpec);

    when(customResponseSpec.bodyToMono(BasketDto.class))
        .thenReturn(Mono.error(expectedException));

    // Act & Assert
    BasketInternalException actualException = assertThrows(BasketInternalException.class, () ->
        basketClient.changeIdContext(bookingRef, changeDto)
    );

    assertEquals("Debug info for error", actualException.getMessage());
    assertEquals("Debug info for error", actualException.getDebugMessage());
    assertEquals(1001, actualException.getErrorCode());
  }

  @Test
  void sendCreateReservationBasket_success() {
    // Arrange
    HttpHeaders header = new HttpHeaders();
    ResponseEntity<BasketDto> responseEntity = new ResponseEntity<>(
        new BasketDto(),
        header,
        HttpStatus.OK);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toEntity(BasketDto.class)).thenReturn(Mono.just(responseEntity));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = basketClient.createBasketReservation(mock(CreateBasketRequestReservationDto.class));

    // Assert
    assertNotNull(response);
  }

  @Test
  void createReservationBasket_ThrowsException2() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.OK);
    when(customResponseSpec.toEntity(BasketDto.class)).thenReturn(
        Mono.just(ResponseEntity.ok(null)));
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    CreateBasketRequestReservationDto createBasketRequestReservationDto = new CreateBasketRequestReservationDto();

    // Act & Assert
    assertThrows(BasketDigitalException.class,
        () -> basketClient.createBasketReservation(createBasketRequestReservationDto));
  }

  @Test
  void createReservationBasket_throwsInternalException() {
    // Arrange
    BasketInternalException ex =
        new BasketInternalException("msg", "debug", null, 1001);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(basketProperties.getCreateEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(), any()))
        .thenReturn(customResponseSpec);

    when(customResponseSpec.toEntity(BasketDto.class))
        .thenReturn(Mono.error(ex));

    // Provide valid DTO so logger doesn't NPE
    CreateBasketRequestReservationDto dto = new CreateBasketRequestReservationDto();
    UserInfoDto user = new UserInfoDto();
    user.setUserId("abc");
    dto.setUserInfoDto(user);
    dto.setHotelId("hotel");

    // Act & Assert
    assertThrows(BasketInternalException.class,
        () -> basketClient.createBasketReservation(dto));
  }


}
