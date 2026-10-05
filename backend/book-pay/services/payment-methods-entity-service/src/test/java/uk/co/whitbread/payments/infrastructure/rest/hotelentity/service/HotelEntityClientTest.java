package uk.co.whitbread.payments.infrastructure.rest.hotelentity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.domain.exception.HotelInfoException;
import uk.co.whitbread.payments.infrastructure.rest.client.service.CustomTestResponseSpec;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.properties.HotelEntityClientProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class HotelEntityClientTest {

  @Mock
  private WebClient hotelEntityWebClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Mock
  private HotelEntityClientProperties hotelEntityClientProperties;

  private HotelEntityClient hotelEntityClient;

  private static final String HOTEL_ID = "HOTEL123";
  private static final String HOTEL_INFO_ENDPOINT = "/hotels/{hotelId}/info";
  private static final String THREE_LETTER_ID = "HTL";
  private static final String UK_COUNTRY_CODE = "GB";
  private static final String DE_COUNTRY_CODE = "DE";

  @BeforeEach
  void setUp() {
    when(hotelEntityClientProperties.getHotelInfoEndpoint()).thenReturn(HOTEL_INFO_ENDPOINT);
    hotelEntityClient = new HotelEntityClient(hotelEntityWebClient, hotelEntityClientProperties);
  }

  @Test
  void getHotelInfo_success_returnsHotelInfoDto() {
    // Arrange
    HotelInfoDto expectedDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.just(expectedDto));

    // Act
    HotelInfoDto result = hotelEntityClient.getHotelInfo(HOTEL_ID);

    // Assert
    assertNotNull(result);
    assertEquals(THREE_LETTER_ID, result.getThreeLetterId());
    assertEquals(UK_COUNTRY_CODE, result.getHotelCountryCode());
    verify(hotelEntityWebClient).get();
    verify(requestHeadersUriSpec).uri(HOTEL_INFO_ENDPOINT, HOTEL_ID);
  }

  @Test
  void getHotelInfo_successWithDECountry_returnsHotelInfoDto() {
    // Arrange
    HotelInfoDto expectedDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(DE_COUNTRY_CODE)
        .build();

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.just(expectedDto));

    // Act
    HotelInfoDto result = hotelEntityClient.getHotelInfo(HOTEL_ID);

    // Assert
    assertNotNull(result);
    assertEquals(DE_COUNTRY_CODE, result.getHotelCountryCode());
  }

  @Test
  void getHotelInfo_internalServerError_throwsException() {
    // Arrange
    String errorMessage = "Error while fetching hotel info";

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelInfoException("message", errorMessage,
            new Exception(), 500)));

    // Act & Assert
    Exception exception = assertThrows(HotelInfoException.class,
        () -> hotelEntityClient.getHotelInfo(HOTEL_ID));

    assertTrue(exception.getMessage().contains(errorMessage));
    verify(hotelEntityWebClient).get();
  }

  @Test
  void getHotelInfo_notFound_throwsException() {
    // Arrange
    String errorMessage = "Hotel not found";

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelInfoException("message", errorMessage,
            new Exception(), 404)));

    // Act & Assert
    Exception exception = assertThrows(HotelInfoException.class,
        () -> hotelEntityClient.getHotelInfo(HOTEL_ID));

    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void getHotelInfo_badRequest_throwsException() {
    // Arrange
    String errorMessage = "Invalid hotel ID";

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelInfoException("message", errorMessage,
            new Exception(), 400)));

    // Act & Assert
    Exception exception = assertThrows(HotelInfoException.class,
        () -> hotelEntityClient.getHotelInfo(HOTEL_ID));

    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void getHotelInfo_serviceUnavailable_throwsException() {
    // Arrange
    String errorMessage = "Service temporarily unavailable";

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelInfoException("message", errorMessage,
            new Exception(), 503)));

    // Act & Assert
    Exception exception = assertThrows(HotelInfoException.class,
        () -> hotelEntityClient.getHotelInfo(HOTEL_ID));

    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void getHotelInfo_emptyResponse_returnsNull() {
    // Arrange
    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.empty());

    // Act
    HotelInfoDto result = hotelEntityClient.getHotelInfo(HOTEL_ID);

    // Assert
    assertNull(result);
    verify(hotelEntityWebClient).get();
  }

  @Test
  void getHotelInfo_nullHotelCountryCode_returnsHotelInfoWithNullCountry() {
    // Arrange
    HotelInfoDto expectedDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(null)
        .build();

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, HOTEL_ID))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.just(expectedDto));

    // Act
    HotelInfoDto result = hotelEntityClient.getHotelInfo(HOTEL_ID);

    // Assert
    assertNotNull(result);
    assertEquals(THREE_LETTER_ID, result.getThreeLetterId());
    assertNull(result.getHotelCountryCode());
  }

  @Test
  void getHotelInfo_differentHotelIds_callsCorrectEndpoint() {
    // Arrange
    String differentHotelId = "HOTEL999";
    HotelInfoDto expectedDto = HotelInfoDto.builder()
        .threeLetterId("HTL")
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(HOTEL_INFO_ENDPOINT, differentHotelId))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.just(expectedDto));

    // Act
    HotelInfoDto result = hotelEntityClient.getHotelInfo(differentHotelId);

    // Assert
    assertNotNull(result);
    verify(requestHeadersUriSpec).uri(HOTEL_INFO_ENDPOINT, differentHotelId);
  }
}

