package uk.co.whitbread.account.infrastructure.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.account.infrastructure.exception.ErrorCode;
import uk.co.whitbread.account.infrastructure.exception.GlobalErrorHandler;
import uk.co.whitbread.account.infrastructure.exception.ServiceException;
import uk.co.whitbread.account.infrastructure.exception.ServiceRequestException;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestV2Dto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.MarketingPreferencesClient;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.properties.MarketingPreferencesClientProperties;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceChannelDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceLocaleDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UserJourneyDto;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;

@ExtendWith(MockitoExtension.class)
public class MarketingPreferencesClientTest {

  private static final String BRAND_CODES[] = {"PINN"};
  private static final String CUSTOMER_ID = "big.boi";
  private static final String MOCKED_RESPONSE_MESSAGE = "An unexpected error has occurred.";
  private static final String NEWSLETTER_PREFERENCES_ENDPOINT = "/marketing/newsletter/email/";
  private static final String INTERNAL_NEWSLETTER_PREFERENCES_ENDPOINT = "/internal/marketing/newsletter";
  private static final String AZURE_FDID = "test-fdid";
  private static final String APIM_SUBSCRIPTION_KEY = "test-apim-subscription-key";

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  private MarketingPreferencesClientProperties marketingPreferencesClientProperties;
  private MarketingPreferencesClient marketingPreferencesClient;

  @BeforeEach
  void setUp() {
    marketingPreferencesClientProperties = new MarketingPreferencesClientProperties();
    marketingPreferencesClientProperties.setNewsletterPreferencesEndpoint(NEWSLETTER_PREFERENCES_ENDPOINT);
    marketingPreferencesClientProperties.setInternalNewsletterPreferencesEndpoint(
        INTERNAL_NEWSLETTER_PREFERENCES_ENDPOINT);
    marketingPreferencesClientProperties.setAzureFDID(AZURE_FDID);
    marketingPreferencesClientProperties.setApimSubscriptionKey(APIM_SUBSCRIPTION_KEY);
    marketingPreferencesClient = new MarketingPreferencesClient(webClient, marketingPreferencesClientProperties);
  }


  @Test
  void updatePreferences_success() {
    // Arrange

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(NEWSLETTER_PREFERENCES_ENDPOINT + CUSTOMER_ID)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("X-Azure-FDID", AZURE_FDID)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Ocp-Apim-Subscription-Key", APIM_SUBSCRIPTION_KEY)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(""));

    //Act
    var resp = marketingPreferencesClient.updateMarketingPreferences(
        validPreferencesUpdateRequestDto());

    // Assert
    Assertions.assertThat(resp);
  }

  @Test
  void updatePreferences_WhenA4xxErrorOccurs_ThenServiceRequestExceptionWithDetailsIsLogged() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(NEWSLETTER_PREFERENCES_ENDPOINT + CUSTOMER_ID)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("X-Azure-FDID", AZURE_FDID)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Ocp-Apim-Subscription-Key", APIM_SUBSCRIPTION_KEY)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.NOT_FOUND)) {
            // Simulate a 400 error response
            return errorFunction.apply(createErrorResponse(HttpStatus.NOT_FOUND)).block();
          }
          return responseSpec;
        });

    MarketingPreferencesRequestDto request = validPreferencesUpdateRequestDto();
    ServiceRequestException exception = assertThrows(ServiceRequestException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION.getCode(),
        exception.getErrorCode());
    assertEquals(String.format(GlobalErrorHandler.WEBCLIENT_SERVICE_REQUEST_INCORRECT, HttpStatus.NOT_FOUND,
        List.of(MOCKED_RESPONSE_MESSAGE)), exception.getMessage());
  }

  @Test
  void updatePreferences_WhenDifferentErrorOccurs_ThenServiceExceptionWithDetailsIsLogged() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(NEWSLETTER_PREFERENCES_ENDPOINT + CUSTOMER_ID)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("X-Azure-FDID", AZURE_FDID)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Ocp-Apim-Subscription-Key", APIM_SUBSCRIPTION_KEY)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
            // Simulate a 500 error response
            return errorFunction.apply(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR)).block();
          }
          return responseSpec;
        });

    MarketingPreferencesRequestDto request = validPreferencesUpdateRequestDto();
    ServiceException exception = assertThrows(ServiceException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(String.format(GlobalErrorHandler.WEBCLIENT_SERVICE_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR,
        List.of(MOCKED_RESPONSE_MESSAGE)), exception.getMessage());
  }

  @Test
  void updatePreferences_When401ErrorOccurs_ThenServiceExceptionWithAuthenticationDetailsIsLogged() {
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(NEWSLETTER_PREFERENCES_ENDPOINT + CUSTOMER_ID)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("X-Azure-FDID", AZURE_FDID)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Ocp-Apim-Subscription-Key", APIM_SUBSCRIPTION_KEY)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestDto.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.UNAUTHORIZED)) {
            return errorFunction.apply(createErrorResponse(HttpStatus.UNAUTHORIZED)).block();
          }
          return responseSpec;
        });

    MarketingPreferencesRequestDto request = validPreferencesUpdateRequestDto();
    ServiceException e = assertThrows(ServiceException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION.getCode(), e.getErrorCode());
    assertEquals(MarketingPreferencesClient.WEBCLIENT_SERVICE_AUTHENTICATION_ERROR, e.getMessage());
  }

  @Test
  void updatePreferences_When403ErrorOccurs_ThenServiceExceptionWithAuthenticationDetailsIsLogged() {
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(NEWSLETTER_PREFERENCES_ENDPOINT + CUSTOMER_ID)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("X-Azure-FDID", AZURE_FDID)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Ocp-Apim-Subscription-Key", APIM_SUBSCRIPTION_KEY)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestDto.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.FORBIDDEN)) {
            return errorFunction.apply(createErrorResponse(HttpStatus.FORBIDDEN)).block();
          }
          return responseSpec;
        });

    MarketingPreferencesRequestDto request = validPreferencesUpdateRequestDto();
    ServiceException e = assertThrows(ServiceException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION.getCode(), e.getErrorCode());
    assertEquals(MarketingPreferencesClient.WEBCLIENT_SERVICE_AUTHENTICATION_ERROR, e.getMessage());
  }

  @Test
  void updatePreferencesV2_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(INTERNAL_NEWSLETTER_PREFERENCES_ENDPOINT))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestV2Dto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(""));

    // Act
    var resp = marketingPreferencesClient.updateMarketingPreferences(validPreferencesUpdateRequestV2Dto());

    // Assert
    Assertions.assertThat(resp).isNotNull().isEqualTo("");
  }

  @Test
  void updatePreferencesV2_WhenA4xxErrorOccurs_ThenServiceRequestExceptionWithDetailsIsLogged() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(INTERNAL_NEWSLETTER_PREFERENCES_ENDPOINT))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestV2Dto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.NOT_FOUND)) {
            return errorFunction.apply(createErrorResponse(HttpStatus.NOT_FOUND)).block();
          }
          return responseSpec;
        });

    // Act
    MarketingPreferencesRequestV2Dto request = validPreferencesUpdateRequestV2Dto();
    ServiceRequestException e = assertThrows(ServiceRequestException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    // Assert
    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION_V2.getCode(), e.getErrorCode());
    assertEquals(String.format(GlobalErrorHandler.WEBCLIENT_SERVICE_REQUEST_INCORRECT, HttpStatus.NOT_FOUND,
        List.of(MOCKED_RESPONSE_MESSAGE)), e.getMessage());
  }

  @Test
  void updatePreferencesV2_WhenDifferentErrorOccurs_ThenServiceExceptionWithDetailsIsLogged() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(INTERNAL_NEWSLETTER_PREFERENCES_ENDPOINT))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), Mockito.eq(MarketingPreferencesRequestV2Dto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(Mockito.any(), Mockito.any()))
        .thenAnswer(invocation -> {
          var statusPredicate = invocation.getArgument(0, java.util.function.Predicate.class);
          Function<ClientResponse, Mono<? extends Throwable>> errorFunction = invocation.getArgument(
              1, java.util.function.Function.class);
          if (statusPredicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
            return errorFunction.apply(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR)).block();
          }
          return responseSpec;
        });

    // Act
    MarketingPreferencesRequestV2Dto request = validPreferencesUpdateRequestV2Dto();
    ServiceException e = assertThrows(ServiceException.class,
        () -> marketingPreferencesClient.updateMarketingPreferences(request));

    // Assert
    assertEquals(ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION_V2.getCode(), e.getErrorCode());
    assertEquals(String.format(GlobalErrorHandler.WEBCLIENT_SERVICE_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR,
        List.of(MOCKED_RESPONSE_MESSAGE)), e.getMessage());
  }

  private MarketingPreferencesRequestDto validPreferencesUpdateRequestDto() {
    var customer = new CustomerDto("Mr", "Big", "Boi", "DE", "big.boi", "DE", "big.boi", "DE");
    return new MarketingPreferencesRequestDto(true, true, BRAND_CODES,
        customer, new SourceDetailsDto(SourceChannelDto.WEB.toString(),
        UserJourneyDto.NEWSLETTERSIGNUP.toString(), SourceLocaleDto.DE.toString()));
  }

  private MarketingPreferencesRequestV2Dto validPreferencesUpdateRequestV2Dto() {
    var customer = new CustomerDto("Mr", "Big", "Boi", "DE", "big.boi", "DE", "big.boi", "DE");
    return new MarketingPreferencesRequestV2Dto(null, true,
        true, false, false, BRAND_CODES,
        customer, new SourceDetailsDto(SourceChannelDto.WEB.toString(),
            UserJourneyDto.NEWSLETTERSIGNUP.toString(), SourceLocaleDto.DE.toString()), "big.boi"
    );
  }

  private ClientResponse createErrorResponse(HttpStatus status) {
    ObjectMapper objectMapper = new ObjectMapper();
    String json;
    try {
      json = objectMapper.writeValueAsString(new ErrorResponse(status.toString(), MOCKED_RESPONSE_MESSAGE));
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Unable to serialize error response.", exception);
    }
    DataBuffer buffer = new DefaultDataBufferFactory().wrap(json.getBytes());

    return ClientResponse
        .create(status)
        .header("Content-Type", "application/json")
        .body(Flux.just(buffer))
        .build();
  }
}
