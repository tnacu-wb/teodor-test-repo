package uk.co.whitbread.basket.infrastructure.rest.client.content.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import uk.co.whitbread.basket.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.basket.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.exceptions.ContentException;

@ExtendWith(MockitoExtension.class)
class ContentClientTest {

  private static final String HOTEL_CODE = "DUNGOU";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private static final String MESSAGE = "An error was returned calling the Content service";

  @InjectMocks
  private ContentClient contentClient;

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
  void testGetHotelPaymentDetails_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelPaymentInformationDto.class)).thenReturn(
        mockHotelPaymentInformationResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    // Act
    var response = contentClient.getHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void testBusinessNotes_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(BusinessNotesResponseDto.class)).thenReturn(
        mockBusinessNotesResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = contentClient.getBusinessNotes(LANGUAGE);

    // Assert
    assertNotNull(response);
  }

  @Test
  void geGetHotelPaymentDetails_shouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(HotelPaymentInformationDto.class))
        .thenReturn(Mono.error(new ContentException("message",
            MESSAGE, new Exception(), 1)));

    //Act
    var exception = assertThrows(ContentException.class,
        () -> contentClient.getHotelPaymentDetails("hotel","de","de"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(MESSAGE));
  }

  @Test
  void getBusinessNotes_shouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BusinessNotesResponseDto.class))
        .thenReturn(Mono.error(new ContentException("message",
            MESSAGE, new Exception(), 1)));

    //Act
    var exception = assertThrows(ContentException.class,
        () -> contentClient.getBusinessNotes("de"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(MESSAGE));
  }

  private Mono<BusinessNotesResponseDto> mockBusinessNotesResponse() {
    return Mono.just(new BusinessNotesResponseDto());
  }

  private Mono<HotelPaymentInformationDto> mockHotelPaymentInformationResponse() {
    return Mono.just(new HotelPaymentInformationDto());
  }
}
