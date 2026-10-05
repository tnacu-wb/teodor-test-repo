package uk.co.whitbread.digitalkey.infrastructure.rest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.digitalkey.ErrorCode;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpConstants;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.service.AxpClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.AxpErrorResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.AxpServiceException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.ResourceNotFoundException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.AxpGenerateOtpRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.GoogleWalletProvisioningRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.RegisterMobileDeviceRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.VerfiyOtpProvisionRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.AxpGenerateOtpResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.GoogleWalletProvisioningResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.RegisterMobileDeviceResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.VerifyOtpProvisionResponseDto;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AxpClientTest {

  @Mock
  private WebClient axpWebClient;

  @Mock
  private AxpProperties axpProperties;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;


  @InjectMocks
  private AxpClient axpClient;

  private VerfiyOtpProvisionRequestDto requestDto;
  private VerifyOtpProvisionResponseDto responseDto;

  @BeforeEach
  void setUp() {
    requestDto = new VerfiyOtpProvisionRequestDto();
    requestDto.setOtpCode("123456");
    requestDto.setOtpType("EMAIL");
    requestDto.setOtpDestination("test@example.com");
    requestDto.setShortPropertyCode("PROP123");
    requestDto.setFirstName("John");
    requestDto.setLastName("Doe");
    requestDto.setBookingReference("BOOK123");

    responseDto = new VerifyOtpProvisionResponseDto();
    responseDto.setProvisioningCredentialIdentifier("cred123");

    MockitoAnnotations.openMocks(this);
    when(axpProperties.getGenerateOtpEndPoint()).thenReturn("/otp/generate");
    when(axpProperties.getApiKey()).thenReturn("test-api-key");
    when(axpProperties.getBrandId()).thenReturn("brand123");
  }

  @Test
  void testGenerateOtpSuccess() {
    AxpGenerateOtpRequestDto axpGenerateOtpRequestDto = new AxpGenerateOtpRequestDto();
    axpGenerateOtpRequestDto.setDestination("test@example.com");
    AxpGenerateOtpResponseDto axpGenerateOtpResponseDto = new AxpGenerateOtpResponseDto();
    axpGenerateOtpResponseDto.setSuccess(true);

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(), eq(AxpGenerateOtpRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AxpGenerateOtpResponseDto.class)).thenReturn(Mono.just(axpGenerateOtpResponseDto));

    AxpGenerateOtpResponseDto result = axpClient.generateOtp(axpGenerateOtpRequestDto);
    assertNotNull(result);
    assertTrue(result.isSuccess());
  }

  @Test
  void testGenerateOtp_axpServiceError() {
    AxpGenerateOtpRequestDto axpGenerateOtpRequestDto = new AxpGenerateOtpRequestDto();
    axpGenerateOtpRequestDto.setDestination("test@example.com");
    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(axpProperties.getApiKey()).thenReturn("api-key");
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(AxpGenerateOtpRequestDto.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocation -> {
          // Get the error handler function
          Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);

          // Create a mock ClientResponse
          ClientResponse mockResponse = mock(ClientResponse.class);

          // Mock headers to avoid NullPointerException
          ClientResponse.Headers mockHeaders = mock(ClientResponse.Headers.class);
          when(mockHeaders.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY);
          when(mockResponse.headers()).thenReturn(mockHeaders);
          // Simulate the error by applying the handler
          Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
          // Return a ResponseSpec that will throw the error when bodyToMono is called
          when(responseSpec.bodyToMono(AxpGenerateOtpResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
          return responseSpec;
        });

    assertThrows(AxpServiceException.class, () -> axpClient.generateOtp(axpGenerateOtpRequestDto));
  }

  @Test
  void testPassProvisioningWithOtpSuccess() {
    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(), eq(VerfiyOtpProvisionRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VerifyOtpProvisionResponseDto.class)).thenReturn(Mono.just(responseDto));

    VerifyOtpProvisionResponseDto result = axpClient.passProvisioningWithOtp(requestDto);

    assertNotNull(result);
    assertEquals("cred123", result.getProvisioningCredentialIdentifier());
  }

  @Test
  void testPassProvisioningWithOtp_invalidOrExpiredOtp() {
    VerfiyOtpProvisionRequestDto verfiyOtpProvisionRequestDto = new VerfiyOtpProvisionRequestDto();
    verfiyOtpProvisionRequestDto.setOtpDestination("test@example.com");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(axpProperties.getApiKey()).thenReturn("api-key");
    when(axpProperties.getPassProvisioningWithOtp()).thenReturn("/provisioning/otp");
    when(axpProperties.getBrandId()).thenReturn("brand-id");

    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(VerfiyOtpProvisionRequestDto.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);
    when(mockResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    ClientResponse.Headers mockHeaders = mock(ClientResponse.Headers.class);
    when(mockHeaders.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY); // or a custom HttpHeaders if needed
    when(mockResponse.headers()).thenReturn(mockHeaders);

    AxpErrorResponse axpErrorResponse = new AxpErrorResponse();
    axpErrorResponse.setError(AxpConstants.INVALID_OR_EXPIRED_OTP);
    when(mockResponse.bodyToMono(AxpErrorResponse.class)).thenReturn(Mono.just(axpErrorResponse));

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocation -> {
          Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
          Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);

          if (predicate.test(HttpStatus.BAD_REQUEST)) {
            Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
            when(responseSpec.bodyToMono(VerifyOtpProvisionResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
          }
          return responseSpec;
        });

    AxpServiceException exception = assertThrows(AxpServiceException.class, () -> {
      axpClient.passProvisioningWithOtp(verfiyOtpProvisionRequestDto);
    });

    assertEquals(ErrorCode.INVALID_CODE.getCode(), exception.getErrorCode());
    assertEquals(AxpConstants.INVALID_OR_EXPIRED_OTP, exception.getMessage());
  }

  @Test
  void testPassProvisioningWithOtp_LocationNotFound() {
    VerfiyOtpProvisionRequestDto verfiyOtpProvisionRequestDto = new VerfiyOtpProvisionRequestDto();
    verfiyOtpProvisionRequestDto.setOtpDestination("test@example.com");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(axpProperties.getApiKey()).thenReturn("api-key");
    when(axpProperties.getPassProvisioningWithOtp()).thenReturn("/provisioning/otp");
    when(axpProperties.getBrandId()).thenReturn("brand-id");

    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(VerfiyOtpProvisionRequestDto.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);

    when(mockResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

    ClientResponse.Headers mockHeaders = mock(ClientResponse.Headers.class);
    when(mockHeaders.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY); // or a custom HttpHeaders if needed
    when(mockResponse.headers()).thenReturn(mockHeaders);

    AxpErrorResponse axpErrorResponse = new AxpErrorResponse();
    axpErrorResponse.setError(AxpConstants.LOCATION_NOT_FOUND);
    when(mockResponse.bodyToMono(AxpErrorResponse.class)).thenReturn(Mono.just(axpErrorResponse));

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocation -> {
          Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
          Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);

          if (predicate.test(HttpStatus.NOT_FOUND)) {
            Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
            when(responseSpec.bodyToMono(VerifyOtpProvisionResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
          }

          return responseSpec;
        });

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      axpClient.passProvisioningWithOtp(verfiyOtpProvisionRequestDto);
    });

    assertEquals(ErrorCode.LOCATION_NOT_FOUND.getCode(), exception.getErrorCode());
    assertEquals(AxpConstants.LOCATION_NOT_FOUND, exception.getMessage());
  }

  @Test
  void testPassProvisioningWithOtp_BookingNotFound() {
    VerfiyOtpProvisionRequestDto verfiyOtpProvisionRequestDto = new VerfiyOtpProvisionRequestDto();
    verfiyOtpProvisionRequestDto.setOtpDestination("test@example.com");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(axpProperties.getApiKey()).thenReturn("api-key");
    when(axpProperties.getPassProvisioningWithOtp()).thenReturn("/provisioning/otp");
    when(axpProperties.getBrandId()).thenReturn("brand-id");

    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(VerfiyOtpProvisionRequestDto.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);

    when(mockResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

    ClientResponse.Headers mockHeaders = mock(ClientResponse.Headers.class);
    when(mockHeaders.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY); // or a custom HttpHeaders if needed
    when(mockResponse.headers()).thenReturn(mockHeaders);

    AxpErrorResponse axpErrorResponse = new AxpErrorResponse();
    axpErrorResponse.setError(AxpConstants.BOOKING_NOT_FOUND);
    when(mockResponse.bodyToMono(AxpErrorResponse.class)).thenReturn(Mono.just(axpErrorResponse));

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocation -> {
          Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
          Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);

          if (predicate.test(HttpStatus.NOT_FOUND)) {
            Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
            when(responseSpec.bodyToMono(VerifyOtpProvisionResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
          }

          return responseSpec;
        });

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      axpClient.passProvisioningWithOtp(verfiyOtpProvisionRequestDto);
    });

    assertEquals(ErrorCode.BOOKING_NOT_FOUND.getCode(), exception.getErrorCode());
    assertEquals(AxpConstants.BOOKING_NOT_FOUND, exception.getMessage());
  }

  @Test
  void testPassProvisioningWithOtp_ProfileNotFound() {
    VerfiyOtpProvisionRequestDto verfiyOtpProvisionRequestDto = new VerfiyOtpProvisionRequestDto();
    verfiyOtpProvisionRequestDto.setOtpDestination("test@example.com");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(axpProperties.getApiKey()).thenReturn("api-key");
    when(axpProperties.getPassProvisioningWithOtp()).thenReturn("/provisioning/otp");
    when(axpProperties.getBrandId()).thenReturn("brand-id");

    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(VerfiyOtpProvisionRequestDto.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);

    when(mockResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

    ClientResponse.Headers mockHeaders = mock(ClientResponse.Headers.class);
    when(mockHeaders.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY); // or a custom HttpHeaders if needed
    when(mockResponse.headers()).thenReturn(mockHeaders);

    AxpErrorResponse axpErrorResponse = new AxpErrorResponse();
    axpErrorResponse.setError(AxpConstants.PROFILE_NOT_FOUND);
    when(mockResponse.bodyToMono(AxpErrorResponse.class)).thenReturn(Mono.just(axpErrorResponse));

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocation -> {
          Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
          Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);

          if (predicate.test(HttpStatus.NOT_FOUND)) {
            Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
            when(responseSpec.bodyToMono(VerifyOtpProvisionResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
          }

          return responseSpec;
        });

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      axpClient.passProvisioningWithOtp(verfiyOtpProvisionRequestDto);
    });

    assertEquals(ErrorCode.PROFILE_NOT_FOUND.getCode(), exception.getErrorCode());
    assertEquals(AxpConstants.PROFILE_NOT_FOUND, exception.getMessage());
  }

  @Test
  void testRegisterMobileDevice_success() {
    RegisterMobileDeviceRequestDto registerMobileDeviceRequestDto = new RegisterMobileDeviceRequestDto();
    registerMobileDeviceRequestDto.setBookingReference("BOOK123");

    RegisterMobileDeviceResponseDto registerMobileDeviceResponseDto = new RegisterMobileDeviceResponseDto();
    registerMobileDeviceResponseDto.setMobileDeviceId("uuid-123");

    when(axpProperties.getRegisterMobileDeviceEndpoint()).thenReturn("/mobile-devices");
    when(axpProperties.getBrandId()).thenReturn("brand123");
    when(axpProperties.getApiKey()).thenReturn("test-api-key");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RegisterMobileDeviceResponseDto.class))
            .thenReturn(Mono.just(registerMobileDeviceResponseDto));

    RegisterMobileDeviceResponseDto result = axpClient.registerMobileDevice(registerMobileDeviceRequestDto);

    assertNotNull(result);
    assertEquals("uuid-123", result.getMobileDeviceId());
  }

  @ParameterizedTest
  @MethodSource("errorScenarios")
  void testRegisterMobileDevice_errorScenarios(HttpStatus status, ErrorCode expectedErrorCode) {
      RegisterMobileDeviceRequestDto registerMobileDeviceRequestDto = new RegisterMobileDeviceRequestDto();

    when(axpProperties.getRegisterMobileDeviceEndpoint()).thenReturn("/mobile-devices");
    when(axpProperties.getBrandId()).thenReturn("brand123");
    when(axpProperties.getApiKey()).thenReturn("test-api-key");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);
    when(mockResponse.statusCode()).thenReturn(status);

    ClientResponse.Headers headers = mock(ClientResponse.Headers.class);
    when(headers.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY);
    when(mockResponse.headers()).thenReturn(headers);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenAnswer(invocation -> {
         Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
         Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
          if (predicate.test(status)) {
              Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
              when(responseSpec.bodyToMono(RegisterMobileDeviceResponseDto.class))
                      .thenReturn(errorMono.flatMap(Mono::error));
          }
          return responseSpec;
    });
      AxpServiceException ex = assertThrows(AxpServiceException.class, () -> axpClient.registerMobileDevice(registerMobileDeviceRequestDto));
    assertEquals(expectedErrorCode.getCode(), ex.getErrorCode());
  }

  private static Stream<Arguments> errorScenarios() {
    return Stream.of(
            Arguments.of(HttpStatus.BAD_REQUEST, ErrorCode.ALLIANTS_BAD_REQUEST),
            Arguments.of(HttpStatus.FORBIDDEN, ErrorCode.ALLIANTS_BAD_REQUEST),
            Arguments.of(HttpStatus.UNAUTHORIZED, ErrorCode.ALLIANTS_BAD_REQUEST),
            Arguments.of(HttpStatus.NOT_FOUND, ErrorCode.ALLIANTS_BAD_REQUEST),
            Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ALLIANTS_INTERNAL_ERROR)
    );
  }

  @Test
  void testGoogleWalletProvisioningWithOtp_success() {
    GoogleWalletProvisioningRequestDto googleWalletProvisioningRequestDto = new GoogleWalletProvisioningRequestDto();
    GoogleWalletProvisioningResponseDto googleWalletProvisioningResponseDto = new GoogleWalletProvisioningResponseDto();
    googleWalletProvisioningResponseDto.setCredentialToken("WL4TMR33T7E4RFMR");

    when(axpProperties.getGoogleWalletProvisioningWithOtp()).thenReturn("/google-wallet-provisioning");
    when(axpProperties.getBrandId()).thenReturn("brand123");
    when(axpProperties.getApiKey()).thenReturn("test-api-key");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GoogleWalletProvisioningResponseDto.class)).thenReturn(Mono.just(googleWalletProvisioningResponseDto));
    GoogleWalletProvisioningResponseDto result = axpClient.googleWalletProvisioningWithOtp(googleWalletProvisioningRequestDto);

    assertNotNull(result);
    assertEquals("WL4TMR33T7E4RFMR", result.getCredentialToken());
  }

  @ParameterizedTest
  @MethodSource("googleWalletErrorScenarios")
  void testGoogleWalletProvisioningWithOtp_errorScenarios(HttpStatus status, ErrorCode expectedErrorCode) {
    GoogleWalletProvisioningRequestDto googleWalletProvisioningRequestDto = new GoogleWalletProvisioningRequestDto();
    when(axpProperties.getGoogleWalletProvisioningWithOtp()).thenReturn("/google-wallet-provisioning");
    when(axpProperties.getBrandId()).thenReturn("brand123");
    when(axpProperties.getApiKey()).thenReturn("test-api-key");

    when(axpWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    ClientResponse mockResponse = mock(ClientResponse.class);
    when(mockResponse.statusCode()).thenReturn(status);

    ClientResponse.Headers headers = mock(ClientResponse.Headers.class);
    when(headers.asHttpHeaders()).thenReturn(HttpHeaders.EMPTY);
    when(mockResponse.headers()).thenReturn(headers);

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenAnswer(invocation -> {
      Predicate<HttpStatusCode> predicate = invocation.getArgument(0);
      Function<ClientResponse, Mono<? extends Throwable>> handler = invocation.getArgument(1);
      if (predicate.test(status)) {
        Mono<? extends Throwable> errorMono = handler.apply(mockResponse);
        when(responseSpec.bodyToMono(GoogleWalletProvisioningResponseDto.class)).thenReturn(errorMono.flatMap(Mono::error));
      }
      return responseSpec;
    });

    AxpServiceException ex = assertThrows(AxpServiceException.class,
            () -> axpClient.googleWalletProvisioningWithOtp(googleWalletProvisioningRequestDto));
    assertEquals(expectedErrorCode.getCode(), ex.getErrorCode());
  }

  private static Stream<Arguments> googleWalletErrorScenarios() {
    return Stream.of(Arguments.of(HttpStatus.BAD_REQUEST, ErrorCode.ALLIANTS_BAD_REQUEST),
      Arguments.of(HttpStatus.FORBIDDEN, ErrorCode.ALLIANTS_BAD_REQUEST),
      Arguments.of(HttpStatus.UNAUTHORIZED, ErrorCode.ALLIANTS_BAD_REQUEST),
      Arguments.of(HttpStatus.NOT_FOUND, ErrorCode.ALLIANTS_BAD_REQUEST),
      Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ALLIANTS_INTERNAL_ERROR)
    );
  }
}
