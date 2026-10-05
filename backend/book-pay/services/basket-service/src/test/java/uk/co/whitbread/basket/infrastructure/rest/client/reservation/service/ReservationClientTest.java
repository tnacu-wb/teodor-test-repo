package uk.co.whitbread.basket.infrastructure.rest.client.reservation.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.DiscountInvalidAmountException;
import uk.co.whitbread.basket.generated.models.reservation.AttachReservationProfileRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmReservationResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmationCustomerDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmationRoomStayDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.CustomerDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositsDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositsResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.MarketingPreferencesResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationGuestRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.SpecialRequestsDto;
import uk.co.whitbread.basket.generated.models.reservation.UniqueIDTypeDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.config.ReservationConstants;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.properties.ReservationClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.ReservationProfilesDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDto;
import uk.co.whitbread.commons.exceptions.exception.ExceptionInterface;

@ExtendWith(MockitoExtension.class)
class ReservationClientTest {

  @InjectMocks
  private ReservationClient reservationClient;
  @Mock
  private WebClient webClient;
  @Mock
  private ReservationClientProperties reservationClientProperties;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  private Throwable clause = new Throwable();
  private HotelReservationException hotelReservationException = new HotelReservationException("a.b.c", "debugMessage",
      clause, 100);

  private DiscountInvalidAmountException discountInvalidAmountException = new DiscountInvalidAmountException("a.b.c", "debugMessage",
      clause, 100);


  @Test
  void testGetReservationsByBasketReference_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
        mockReservationsDetailsResponse());

    // Act
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    var response = reservationClient.getReservationsByBasketReference("123", requestDto);

    // Assert
    assertNotNull(response);
  }

  @Test
  void testGetReservationsByBasketReference__ShouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    var thrownException = assertThrows(HotelReservationException.class,
        () -> reservationClient.getReservationsByBasketReference("123", requestDto));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetReservationsByBasketReference__ShouldMap4xxToHotelReservationException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      Predicate<HttpStatusCode> statusPredicate = invocation.getArgument(0);
      @SuppressWarnings("unchecked")
      Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction = invocation.getArgument(1);
      if (statusPredicate.test(HttpStatus.NOT_FOUND)) {
        exceptionFunction.apply(ClientResponse.create(HttpStatus.NOT_FOUND).build()).block();
      }
      return responseSpec;
    });

    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.getReservationsByBasketReference("123", requestDto));

    // Assert
    Assertions.assertEquals("Reservation not found for basketReference=123",
        thrownException.getGlobalErrTextTemplate());
    Assertions.assertEquals("HTTP 404 from Reservation Entity Service",
        thrownException.getDebugMessage());
    Assertions.assertEquals(404, thrownException.getErrorCode());
  }

  @Test
  void testSendUpdateCompanyQuestionAndAnswerDetailsRequests_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient
        .sendUpdateCompanyQuestionAndAnswerDetailsRequests(
        mockCompanyQuestionAndAnswerDetailsRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testSendUpdateCompanyQuestionAndAnswerDetailsRequests__ShouldReturnException() {
    // Arrange
    var reqDto = mockCompanyQuestionAndAnswerDetailsRequestDto();
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrows(HotelReservationException.class,
        () -> reservationClient
            .sendUpdateCompanyQuestionAndAnswerDetailsRequests(reqDto));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void testPutUpdateBusinessItems_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient.sendPutUpdateBusinessItems(mockBusinessItemsRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testPutUpdateSpecialRequests__success(){
    // Arrange
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.empty());

    // Act

    reservationClient.sendPutReservationsSpecialRequests(mockSpecialRequestsDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteRoutingInstructions__success(){
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient.deleteRoutingInstructions("HOTEL_ID", new HashSet<>(List.of("12345")));

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  private SpecialRequestsDto mockSpecialRequestsDto() {
    var specialReq = new SpecialRequestsDto();
    specialReq.setSpecialRequests(List.of("SING"));
    specialReq.setReservationIds(List.of("12345"));
    specialReq.setHotelId("MANOLD");
    return specialReq;
  }


  @Test
  void testPutUpdateSpecialRequests_error_5xx() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.sendPutReservationsSpecialRequests(mockSpecialRequestsDto()));

    // Assert
    assertHotelReservationException(thrownException);
  }


  @Test
  void testGetDepositsForReservationId__ShouldReturnOk(){
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DepositsResponseDto.class)).thenReturn(
        mockDepositsResponseDto());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = reservationClient.getDepositsForReservationId("hotelId", "reservationId");

    // Assert
    assertNotNull(response);

  }

  private Mono<DepositsResponseDto> mockDepositsResponseDto() {
    var deposits = new DepositsResponseDto();
    deposits.setDeposits(Collections.singletonList(new DepositsDto()));
    return Mono.just(deposits);
  }

  @Test
  void testGetMarketingPreferences__ShouldReturnOk(){

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MarketingPreferencesResponseDto.class)).thenReturn(
        mockMarketingPrefenrecesResponseDto());

    // Act
    var response =  reservationClient.getMarketingPreferences("hotelId", "reservationId");

    // Assert
    assertNotNull(response);

  }

  private Mono<MarketingPreferencesResponseDto> mockMarketingPrefenrecesResponseDto() {
    var marketingPreferences = new MarketingPreferencesResponseDto();
    marketingPreferences.setContactValue("CONTACT");
    marketingPreferences.setOptIn(Boolean.TRUE);
    marketingPreferences.setCustomer(new CustomerDto());
    return Mono.just(marketingPreferences);
  }


  @Test
  void testPutUpdateBusinessItems_error_5xx() {
    // Arrange

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));
    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.sendPutUpdateBusinessItems(mockBusinessItemsRequestDto()));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void testPutUpdateDiscount_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    var discountRequest = mockUpdateDiscountRequest();

    // Act
    reservationClient.sendPutUpdateDiscount(discountRequest);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testPutUpdateDiscount_error_4xx() {
    // Arrange
    var discountRequest = mockUpdateDiscountRequest();
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(discountInvalidAmountException));

    // Act
    var thrownException = assertThrowsExactly(DiscountInvalidAmountException.class,
        () -> reservationClient.sendPutUpdateDiscount(discountRequest));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testPutUpdateDiscount_error_5xx() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.sendPutUpdateDiscount(mockUpdateDiscountRequest()));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testAttachProfileToReservations_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient.attachProfileToReservations(new AttachReservationProfileRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetDepositsForReservationId__ShouldReturnException(){
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(DepositsResponseDto.class)).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.getDepositsForReservationId("hotelId", "reservationId"));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void testGetMarketingPreferences__ShouldReturnException(){
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(MarketingPreferencesResponseDto.class)).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.getMarketingPreferences("hotelId", "reservationId"));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void testAttachProfileToReservations_error_5xx() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.attachProfileToReservations(new AttachReservationProfileRequestDto()));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testDeleteRoutingInstructions_error_5xx() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.deleteRoutingInstructions("HOTEL_ID", new HashSet<>(List.of("12345"))));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testSendPutUpdateReservation_error_5xx() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ConfirmReservationResponseDto.class)).
            thenReturn(Mono.error(hotelReservationException));
    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.sendPutUpdateReservation(new UpdateReservationSingleCallRequestDto()));
    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void testCreateProfileIds_success() {
    // Arrange
    ReservationGuestRequestDto  guestRequestDto =  new ReservationGuestRequestDto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationProfilesDto.class)).thenReturn(Mono.empty());

    // Act
    reservationClient.createProfileIds(guestRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testCreateProfileIds_error_5xx() {
    // Arrange
    ReservationGuestRequestDto  guestRequestDto =  new ReservationGuestRequestDto();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(ReservationProfilesDto.class)).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.createProfileIds(guestRequestDto));

    //Assert
    assertHotelReservationException(thrownException);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateReservationAlerts__success(){
    // Arrange
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.empty());

    // Act

    reservationClient.updateReservationAlerts(mockReservationUserDefinedFieldsRequest());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateReservationAlerts_error_5Xx() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.updateReservationAlerts(mockReservationUserDefinedFieldsRequest()));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void getPreviewDepositsForReservationId_returnsDepositsWhenReservationServiceRespondsSuccessfully() {
    // Arrange
    var hotelId = "hotelId";
    var reservationIds = Set.of("reservationId");
    var previewDepositsEndpoint = "/deposits/preview";
    var uriBuilder = mock(UriBuilder.class);

    when(reservationClientProperties.getPreviewDepositsEndpoint()).thenReturn(previewDepositsEndpoint);
    when(uriBuilder.path(previewDepositsEndpoint)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam(ReservationConstants.HOTEL_ID, hotelId)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam(ReservationConstants.RESERVATION_IDS, reservationIds)).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("/deposits/preview"));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      var uriFunction = (Function<UriBuilder, URI>) invocation.getArgument(0);
      uriFunction.apply(uriBuilder);
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DepositFoliosResponseDto.class)).thenReturn(mockDepositFoliosResponseDto());

    // Act
    var response = reservationClient.getPreviewDepositsForReservationId(hotelId, reservationIds);

    // Assert
    assertNotNull(response);
    verify(uriBuilder).path(previewDepositsEndpoint);
    verify(uriBuilder).queryParam(ReservationConstants.HOTEL_ID, hotelId);
    verify(uriBuilder).queryParam(ReservationConstants.RESERVATION_IDS, reservationIds);
    verify(uriBuilder).build();
  }

  @Test
  void getPreviewDepositsForReservationId_returnsNullWhenReservationServiceRespondsWithEmptyBody() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DepositFoliosResponseDto.class)).thenReturn(Mono.empty());

    // Act
    var response = reservationClient.getPreviewDepositsForReservationId("hotelId", Set.of("reservationId"));

    // Assert
    assertNull(response);
  }

  @Test
  void getPreviewDepositsForReservationId_throwsHotelReservationExceptionWhenReservationServiceReturnsError() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.bodyToMono(DepositFoliosResponseDto.class)).thenReturn(Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.getPreviewDepositsForReservationId("hotelId", Set.of("reservationId")));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void saveDepositsFolios_completesWhenReservationServiceAcceptsRequest() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient.saveDepositsFolios(new DepositFoliosRequestDto());

    // Assert
    verify(webClient).post();
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void saveDepositsFolios_throwsHotelReservationExceptionWhenReservationServiceReturnsError() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.saveDepositsFolios(new DepositFoliosRequestDto()));

    // Assert
    assertHotelReservationException(thrownException);
  }

  @Test
  void saveDepositsFolios_withValidDepositFoliosRequestDto_shouldPostToCorrectEndpoint() {
    // Arrange
    var depositFoliosRequestDto = new DepositFoliosRequestDto();
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(depositFoliosRequestDto)).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    reservationClient.saveDepositsFolios(depositFoliosRequestDto);

    // Assert
    verify(webClient).post();
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).bodyValue(depositFoliosRequestDto);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void saveDepositsFolios_with4xxError_shouldThrowHotelReservationException() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.error(hotelReservationException));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> reservationClient.saveDepositsFolios(new DepositFoliosRequestDto()));

    // Assert
    assertHotelReservationException(thrownException);
  }

  private UpdateReservationAlertsRequestDto mockReservationUserDefinedFieldsRequest() {
    var request = new UpdateReservationAlertsRequestDto();
    request.setHotelId("123");
    request.setReservationIds(Set.of());
    request.setAlerts(List.of());
    return request;
  }

  private Mono<ReservationByBasketRefResponseDto> mockReservationsDetailsResponse() {
    return Mono.just(new ReservationByBasketRefResponseDto());
  }

  private Mono<DepositFoliosResponseDto> mockDepositFoliosResponseDto() {
    return Mono.just(new DepositFoliosResponseDto());
  }

  private UpdateDiscountRequestDto mockUpdateDiscountRequest() {
    return new UpdateDiscountRequestDto();
  }

  private BusinessItemsRequestDto mockBusinessItemsRequestDto() {
    return new BusinessItemsRequestDto();
  }

  private CompanyQuestionAndAnswerDetailsRequestDto mockCompanyQuestionAndAnswerDetailsRequestDto() {
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto =
        new CompanyQuestionAndAnswerDetailsRequestDto();
    companyQuestionAndAnswerDetailsRequestDto.setCompanyQuestionAndAnswerDetails(mockCompanyQuestionAndAnswerDetailsDto());
    return companyQuestionAndAnswerDetailsRequestDto;
  }


  private CompanyQuestionAndAnswerDetailsDto mockCompanyQuestionAndAnswerDetailsDto() {
    CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto = new CompanyQuestionAndAnswerDetailsDto();
    List<CompanyQuestionAndAnswerDto> companyQuestionAndAnswers = List.of(createCompanyQuestionAndAnswerDto());
    companyQuestionAndAnswerDetailsDto.setPurchaseOrderQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    companyQuestionAndAnswerDetailsDto.setUserDefinedQuestionAndAnswers(companyQuestionAndAnswers);
    companyQuestionAndAnswerDetailsDto.setCustomerReferenceQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    return companyQuestionAndAnswerDetailsDto;
  }

  private CompanyQuestionAndAnswerDto createCompanyQuestionAndAnswerDto() {
    var companyQuestionAndAnswerDto = new CompanyQuestionAndAnswerDto();
    companyQuestionAndAnswerDto.setQuestion("Who am I?");
    companyQuestionAndAnswerDto.setAnswer("Test");
    return companyQuestionAndAnswerDto;
  }

  @Test
  void sendPutUpdateReservationTest() {

    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ConfirmReservationResponseDto.class)).
            thenReturn(mockConfirmReservationResponseDTO());
    // Act
    var response = reservationClient.sendPutUpdateReservation(new UpdateReservationSingleCallRequestDto());
    // Assert
    assertNotNull(response);
  }
   private Mono<ConfirmReservationResponseDto> mockConfirmReservationResponseDTO() {
    var response = new ConfirmReservationResponseDto();
    response.setReservationIdList(
            List.of(mockIdType("101", "Reservation"), mockIdType("264873", "Confirmation")));
    response.setReservationStatus("Reserved");
    ConfirmationCustomerDto customer = new ConfirmationCustomerDto();
    customer.setGivenName("Tester");
    customer.setSurName("Testerson");
    response.setReservationGuest(customer);
    ConfirmationRoomStayDto roomStay = new ConfirmationRoomStayDto();
    roomStay.arrivalDate(LocalDate.now());
    roomStay.departureDate(LocalDate.now());
    response.setRoomStay(roomStay);
    return Mono.just(response);
  }
  private UniqueIDTypeDto mockIdType(String id, String type) {
    UniqueIDTypeDto idType = new UniqueIDTypeDto();
    idType.setType(type);
    idType.setId(id);
    return idType;
  }

  private void assertHotelReservationException(ExceptionInterface thrownException) {
    Assertions.assertEquals("debugMessage", thrownException.getDebugMessage());
    Assertions.assertEquals("a.b.c", thrownException.getGlobalErrTextTemplate());
    Assertions.assertEquals(100, thrownException.getErrorCode());
  }

}