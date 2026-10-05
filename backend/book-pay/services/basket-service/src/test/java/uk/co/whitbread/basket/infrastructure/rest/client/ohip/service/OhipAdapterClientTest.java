package uk.co.whitbread.basket.infrastructure.rest.client.ohip.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationPaymentCardType;
import uk.co.whitbread.basket.generated.models.ohip.BillingAddressCaptRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.NegotiatedRatesResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInResponse;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateCustomReferenceNumberRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.exceptions.OhipHotelReservationException;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

@ExtendWith(MockitoExtension.class)
class OhipAdapterClientTest {

  private static final String EXPECTED_MESSAGE = "An error was returned calling the Ohip service";
  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;
  @Mock
  private WebClient webClient;
  @Mock
  private OhipAdapterProperties ohipAdapterProperties;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  private final UpdateCustomReferenceNumberRequestDto requestDto = new UpdateCustomReferenceNumberRequestDto();
  private final BillingAddressCaptRequestDto billingAddressCaptRequestDto = new BillingAddressCaptRequestDto();
  private final UpdateReservationCcAgentIdRequestDto ccAgentRequestDto = new UpdateReservationCcAgentIdRequestDto();
  private final String profileId = "123456";

  @Test
  void billingAddressTest() {
    when(ohipAdapterProperties.getReservationBillingAddressEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    ohipAdapterClient
        .sendReservationBillingAddress(billingAddressCaptRequestDto);

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void customReferenceNumberTest() {
    when(ohipAdapterProperties.getCustomReferenceNumberEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    ohipAdapterClient.sendUpdateCustomReferenceNumber(requestDto);

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void customReferenceNumberTest_shouldReturn() {
    when(ohipAdapterProperties.getCustomReferenceNumberEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(new OhipHotelReservationException("message",
            EXPECTED_MESSAGE, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(OhipHotelReservationException.class,
        () -> ohipAdapterClient
            .sendUpdateCustomReferenceNumber(requestDto));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE));

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendReservationBillingAddress_shouldReturnPaymentExceptionException() {

    //Arrange
    when(ohipAdapterProperties.getReservationBillingAddressEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(new OhipHotelReservationException("message",
            EXPECTED_MESSAGE, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(OhipHotelReservationException.class,
        () -> ohipAdapterClient.sendReservationBillingAddress(billingAddressCaptRequestDto));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
  }

  @Test
  void sendUpdateReservationCcAgentId_shouldHandleSuccessfulResponse() {
    // Arrange
    when(ohipAdapterProperties.getUpdateReservationCcAgentIdEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ResponseEntity.class)).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    ohipAdapterClient.sendUpdateReservationCcAgentId(ccAgentRequestDto);

    // Assert
    verify(webClient).put();
    verify(requestBodyUriSpec).uri(anyString());
    verify(requestBodySpec).contentType(any());
    verify(requestBodySpec).body(any(), (Class<Object>) any());
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).bodyToMono(ResponseEntity.class);
    verifyNoMoreInteractions(webClient, requestBodyUriSpec, requestBodySpec, requestHeadersSpec,
        responseSpec);
  }

  @Test
  void getNegotiatedRates_shouldHandleSuccessfulResponse() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(NegotiatedRatesResponseDto.class)).thenReturn(
        Mono.just(new NegotiatedRatesResponseDto()));

    // Act
    var response = ohipAdapterClient.getNegotiatedRates(profileId);

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateCharacterUdfs_shouldHandleSuccessfulResponse() {
    // Arrange
    var udfsDto = new UdfsRequestDto();
    when(ohipAdapterProperties.getUpdateCharacterUdfsEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipAdapterClient.updateCharacterUdfs(udfsDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateCharacterUdfs_shouldReturnOhipExceptionException() {

    //Arrange
    var udfsDto = new UdfsRequestDto();
    when(ohipAdapterProperties.getUpdateCharacterUdfsEndpoint()).thenReturn("Some url");
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.toBodilessEntity()).thenReturn(
        Mono.error(new OhipHotelReservationException("message",
            EXPECTED_MESSAGE, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(OhipHotelReservationException.class,
        () -> ohipAdapterClient.updateCharacterUdfs(udfsDto));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
  }

  @Test
  void getPaymentType_shouldReturnListSuccessfully() {
    String hotelId = "LINMIL";
    Set<String> reservationIds = Set.of("4443204");
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    ArgumentCaptor<Function> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
    when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    List<ReservationInfoPaymentType> mockResponse = List.of(new ReservationInfoPaymentType(
                        new ReservationPaymentCardType("BU", 2)));
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));
    List<ReservationInfoPaymentType> result = ohipAdapterClient.getPaymentType(hotelId, reservationIds);

    Function<UriBuilder, URI> uriFunction = uriFunctionCaptor.getValue();
    UriBuilder builder = new DefaultUriBuilderFactory().builder();
    URI builtUri = uriFunction.apply(builder);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("BU", result.get(0).getPaymentCardType().getPaymentMethod());
    assertTrue(builtUri.toString().contains("LINMIL"));
    assertTrue(builtUri.toString().contains("443204"));

    verify(webClient).get();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getPaymentType_shouldReturnOhipException_onErrorStatus() {
    String hotelId = "LINMIL";
    Set<String> reservationIds = Set.of("4443204");
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(
            Mono.error(new OhipHotelReservationException("message", EXPECTED_MESSAGE, new Exception(), 1)));
    Exception ex = assertThrows(OhipHotelReservationException.class, () ->
            ohipAdapterClient.getPaymentType(hotelId, reservationIds));
    assertTrue(ex.getMessage().contains(EXPECTED_MESSAGE));
    }

  @Test
  void saveReservationPreRegister_success() {
    PreCheckInRequestDto request = new PreCheckInRequestDto();
    PreCheckInResponse response = new PreCheckInResponse();
    ResponseEntity<PreCheckInResponse> responseEntity = ResponseEntity.ok(response);

    when(ohipAdapterProperties.getSaveReservationPreRegister()).thenReturn("some-url");
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(PreCheckInRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toEntity(PreCheckInResponse.class)).thenReturn(Mono.just(responseEntity));

    ResponseEntity<PreCheckInResponse> result =
        ohipAdapterClient.saveReservationPreRegister(request);

    assertNotNull(result);
    assertEquals(responseEntity, result);

    verify(webClient).post();
  }

  @Test
  void saveReservationPreRegister_shouldThrowException() {
    PreCheckInRequestDto request = new PreCheckInRequestDto();

    when(ohipAdapterProperties.getSaveReservationPreRegister()).thenReturn("some-url");
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), eq(PreCheckInRequestDto.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.toEntity(PreCheckInResponse.class))
            .thenReturn(Mono.error(new OhipHotelReservationException(
                    "message", "Error calling service", new Exception(), 1)));

    Exception ex = assertThrows(OhipHotelReservationException.class,
        () -> ohipAdapterClient.saveReservationPreRegister(request));

    assertTrue(ex.getMessage().contains("Error calling service"));
  }

  @Test
  void getReservationDetails_shouldHandleSuccessfulResponse() {

    // Arrange
    String hotelId = "HOTEL_1";
    String reservationId = "RES_123";

    ReservationByBasketRefResponseDto expectedResponse =
        new ReservationByBasketRefResponseDto();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);

    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);

    when(responseSpec.onStatus(
        any(Predicate.class),
        any(Function.class)))
        .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(
        ReservationByBasketRefResponseDto.class))
        .thenReturn(Mono.just(expectedResponse));

    // Act
    ReservationByBasketRefResponseDto response =
        ohipAdapterClient.getReservationDetails(
            hotelId,
            reservationId);

    // Assert
    assertNotNull(response);
    assertEquals(expectedResponse, response);

    verify(webClient).get();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(
        any(Predicate.class),
        any(Function.class));
    verify(responseSpec)
        .bodyToMono(ReservationByBasketRefResponseDto.class);

    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getReservationDetails_shouldHandleExceptionResponse() {

    // Arrange
    String hotelId = "HOTEL_1";
    String reservationId = "RES_123";

    RuntimeException exception =
        new RuntimeException("Test Exception");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);

    ArgumentCaptor<Function> uriFunctionCaptor =
        ArgumentCaptor.forClass(Function.class);

    when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture()))
        .thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve())
        .thenReturn(responseSpec);

    when(responseSpec.onStatus(
        any(Predicate.class),
        any(Function.class)))
        .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(
        ReservationByBasketRefResponseDto.class))
        .thenReturn(Mono.error(exception));

    // Act & Assert
    RuntimeException thrown =
        assertThrows(
            RuntimeException.class,
            () -> ohipAdapterClient.getReservationDetails(
                hotelId,
                reservationId));

    assertEquals("Test Exception", thrown.getMessage());

    // Verify URI Builder lambda execution
    @SuppressWarnings("unchecked")
    Function<UriBuilder, URI> uriFunction =
        uriFunctionCaptor.getValue();

    URI builtUri =
        uriFunction.apply(
            new DefaultUriBuilderFactory().builder());

    assertNotNull(builtUri);
    assertTrue(builtUri.toString().contains(hotelId));
    assertTrue(builtUri.toString().contains(reservationId));

    // Verify interactions
    verify(webClient).get();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(
        any(Predicate.class),
        any(Function.class));
    verify(responseSpec)
        .bodyToMono(ReservationByBasketRefResponseDto.class);

    verifyNoMoreInteractions(webClient);
  }


}