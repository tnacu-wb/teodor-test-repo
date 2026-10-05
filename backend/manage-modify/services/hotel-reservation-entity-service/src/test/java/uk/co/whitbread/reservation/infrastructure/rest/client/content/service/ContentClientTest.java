package uk.co.whitbread.reservation.infrastructure.rest.client.content.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.IndexHeaderDataDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RateInformationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SearchRulesDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoHeaderDataException;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ContentClientTest {

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
  private ContentClient contentClient;

  @Test
  void getIndexHeaderData__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(IndexHeaderDataDto.class)).thenReturn(mockGetIndexHeaderResponse());

    // Act
    var response = contentClient.getIndexHeaderData("gb", "en");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getHotelInformation__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(mockHotelInformationExtendedDto());

    // Act
    var response = contentClient.getHotelInformation("LONEUS", "gb", "en");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getHotelInformation_Exception() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<HotelInformationExtendedDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getHotelInformation("LONEUS", "gb", "en"));
  }

  @Test
  void getHotelRateInformation__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RateInformationDto.class)).thenReturn(mockRateInformationDto());

    // Act
    var response = contentClient.getHotelRateInformation("LONEUS", "gb", "en");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getHotelRateInformation_Exception() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<RateInformationDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(RateInformationDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
            () -> contentClient.getHotelRateInformation("LONEUS", "gb", "en"));
  }

  @Test
  void getHotelPaymentInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelPaymentInformationDto.class)).thenReturn(mockGetHotelPaymentResponse());

    // Act
    var response = contentClient.getHotelPaymentInformation("HOTELTEST", "en", "gb");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getHotelPaymentInformation_NoHeaderDataException() {
    // Arrange
    NoHeaderDataException ex = mock(NoHeaderDataException.class);
    Mono<HotelPaymentInformationDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(HotelPaymentInformationDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(NoHeaderDataException.class,
        () -> contentClient.getHotelPaymentInformation("HOTELTEST", "en", "gb"));
  }

  @Test
  void getHotelPaymentInformation_ContentException() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<HotelPaymentInformationDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(HotelPaymentInformationDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getHotelPaymentInformation("HOTELTEST", "en", "gb"));
  }

  @Test
  void getIndexHeaderData_NoHeaderDataException() {
    // Arrange
    NoHeaderDataException ex = mock(NoHeaderDataException.class);
    Mono<IndexHeaderDataDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(IndexHeaderDataDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(NoHeaderDataException.class, () -> contentClient.getIndexHeaderData("gb", "en"));
  }

  @Test
  void getIndexHeaderData_ContentException() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<IndexHeaderDataDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(IndexHeaderDataDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class, () -> contentClient.getIndexHeaderData("gb", "en"));
  }


  @Test
  void getSearchRules__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SearchRulesDto.class)).thenReturn(mockSearchRulesDto());

    // Act
    var response = contentClient.getSearchRules("DISTR", Optional.of("PI"));

    // Assert
    assertNotNull(response);
  }

  @Test
  void getSearchRules_NoHeaderDataException() {
    // Arrange
    NoHeaderDataException ex = mock(NoHeaderDataException.class);
    Mono<SearchRulesDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(SearchRulesDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(NoHeaderDataException.class,
        () -> contentClient.getSearchRules("DISTR", Optional.of("PI")));
  }

  @Test
  void getSearchRules_ContentException() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<SearchRulesDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(SearchRulesDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getSearchRules("DISTR", Optional.of("PI")));
  }

  private Mono<IndexHeaderDataDto> mockGetIndexHeaderResponse() {
    return Mono.just(new IndexHeaderDataDto());
  }

  private Mono<HotelInformationExtendedDto> mockHotelInformationExtendedDto() {
    return Mono.just(new HotelInformationExtendedDto());
  }

  private Mono<RateInformationDto> mockRateInformationDto() {
    return Mono.just(new RateInformationDto());
  }

  private Mono<HotelPaymentInformationDto> mockGetHotelPaymentResponse() {
    return Mono.just(new HotelPaymentInformationDto());
  }

  private Mono<SearchRulesDto> mockSearchRulesDto() {
    return Mono.just(new SearchRulesDto());
  }
}