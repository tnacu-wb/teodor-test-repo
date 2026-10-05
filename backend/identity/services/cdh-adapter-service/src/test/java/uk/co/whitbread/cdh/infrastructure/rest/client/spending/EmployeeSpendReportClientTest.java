package uk.co.whitbread.cdh.infrastructure.rest.client.spending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.mapper.EmployeeSpendClientMapper;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.model.EmployeeSpendResponse;

@ExtendWith(MockitoExtension.class)
class EmployeeSpendReportClientTest {

  @Mock
  private CdhApiProperties cdhApiProperties;

  @Mock
  private WebClient cdhAccountServicesWebclient;

  @Mock
  private OAuthProvider oAuthProvider;

  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;

  @Mock
  private EmployeeSpendClientMapper employeeSpendClientMapper;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  private EmployeeSpendReportClient client;

  @BeforeEach
  void setUp() {
    client = new EmployeeSpendReportClient(
        cdhApiProperties,
        cdhAccountServicesWebclient,
        oAuthProvider,
        cdhApiOauthProperties,
        employeeSpendClientMapper
    );
  }

  @Test
  void testGetEmployeeSpendReport_Success() {
    // Given
    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    List<EmployeeSpendResponse> cdhResponses = Arrays.asList(
        EmployeeSpendResponse.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2025)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build(),
        EmployeeSpendResponse.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2026)
            .month(2)
            .noOfBookings(1)
            .bookingValue(71.00)
            .bookingCurrency("GBP")
            .build()
    );

    EmployeeSpendReport domainReport1 = EmployeeSpendReport.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .year(2025)
        .month(9)
        .noOfBookings(2)
        .bookingValue(96.00)
        .bookingCurrency("EUR")
        .build();

    EmployeeSpendReport domainReport2 = EmployeeSpendReport.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .year(2026)
        .month(2)
        .noOfBookings(1)
        .bookingValue(71.00)
        .bookingCurrency("GBP")
        .build();

    // Mock WebClient chain
    lenient().when(cdhAccountServicesWebclient.get()).thenReturn(requestHeadersUriSpec);
    lenient().when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    lenient().when(responseSpec.bodyToFlux(eq(EmployeeSpendResponse.class)))
        .thenReturn(Flux.fromIterable(cdhResponses));

    // Mock properties
    lenient().when(cdhApiProperties.getGetEmployeeSpendEndpoint())
        .thenReturn("/IB/V1/Report/EmployeeSpend/{companyAccountId}/{employeeAccountId}");
    lenient().when(oAuthProvider.getBearerToken()).thenReturn("test-token");
    lenient().when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("Ocp-Apim-Subscription-Key");
    lenient().when(cdhApiOauthProperties.getAccountSubscriptionKey()).thenReturn("test-sub-key");
    lenient().when(cdhApiProperties.getRequestHeaderName()).thenReturn("X-Azure-FDID");
    lenient().when(cdhApiProperties.getRequestHeaderValue()).thenReturn("test-fdid");

    // Mock mapper
    when(employeeSpendClientMapper.toModel(cdhResponses.getFirst())).thenReturn(domainReport1);
    when(employeeSpendClientMapper.toModel(cdhResponses.get(1))).thenReturn(domainReport2);

    // When
    var result = client.getEmployeeSpendReport(request);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("COMP123", result.getFirst().getCompanyAccountId());
    assertEquals("EMP456", result.getFirst().getEmployeeAccountId());
    assertEquals(2025, result.getFirst().getYear());
    assertEquals(9, result.getFirst().getMonth());
    assertEquals(2, result.getFirst().getNoOfBookings());
    assertEquals(96.00, result.getFirst().getBookingValue());
    assertEquals("EUR", result.getFirst().getBookingCurrency());

    verify(employeeSpendClientMapper).toModel(cdhResponses.getFirst());
    verify(employeeSpendClientMapper).toModel(cdhResponses.get(1));
  }

  @Test
  void testGetEmployeeSpendReport_NotFound_ReturnsEmptyList() {
    // Given
    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    // Mock WebClient chain
    lenient().when(cdhAccountServicesWebclient.get()).thenReturn(requestHeadersUriSpec);
    lenient().when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    lenient().when(responseSpec.bodyToFlux(eq(EmployeeSpendResponse.class)))
        .thenReturn(Flux.empty());

    // Mock properties
    lenient().when(cdhApiProperties.getGetEmployeeSpendEndpoint())
        .thenReturn("/IB/V1/Report/EmployeeSpend/{companyAccountId}/{employeeAccountId}");
    lenient().when(oAuthProvider.getBearerToken()).thenReturn("test-token");
    lenient().when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("Ocp-Apim-Subscription-Key");
    lenient().when(cdhApiOauthProperties.getInnBusinessSubscriptionKey()).thenReturn("test-sub-key");
    lenient().when(cdhApiProperties.getRequestHeaderName()).thenReturn("X-Azure-FDID");
    lenient().when(cdhApiProperties.getRequestHeaderValue()).thenReturn("test-fdid");

    // When
    var result = client.getEmployeeSpendReport(request);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetEmployeeSpendReport_ServerError_ThrowsCDHException() {
    // Given
    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    WebClientResponseException webClientException = WebClientResponseException.create(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        HttpHeaders.EMPTY,
        new byte[0],
        null
    );

    // Mock WebClient chain
    lenient().when(cdhAccountServicesWebclient.get()).thenReturn(requestHeadersUriSpec);
    lenient().when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    lenient().when(responseSpec.bodyToFlux(eq(EmployeeSpendResponse.class)))
        .thenReturn(Flux.error(webClientException));

    // Mock properties
    lenient().when(cdhApiProperties.getGetEmployeeSpendEndpoint())
        .thenReturn("/IB/V1/Report/EmployeeSpend/{companyAccountId}/{employeeAccountId}");
    lenient().when(oAuthProvider.getBearerToken()).thenReturn("test-token");
    lenient().when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("Ocp-Apim-Subscription-Key");
    lenient().when(cdhApiOauthProperties.getInnBusinessSubscriptionKey()).thenReturn("test-sub-key");
    lenient().when(cdhApiProperties.getRequestHeaderName()).thenReturn("X-Azure-FDID");
    lenient().when(cdhApiProperties.getRequestHeaderValue()).thenReturn("test-fdid");

    // When & Then
    assertThrows(WebClientResponseException.class, () -> client.getEmployeeSpendReport(request));
  }

  @Test
  void testThrowCdhException_CreatesProperException() {
    // Given
    String endpoint = "/IB/V1/Report/EmployeeSpend/{companyAccountId}/{employeeAccountId}";
    lenient().when(cdhApiProperties.getGetEmployeeSpendEndpoint()).thenReturn(endpoint);

    EmployeeSpendRequest request = EmployeeSpendRequest.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    // Mock WebClient chain to trigger onStatus error handler
    lenient().when(cdhAccountServicesWebclient.get()).thenReturn(requestHeadersUriSpec);
    lenient().when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    
    // Create a CDHException that will be thrown by the error handler
    CDHException expectedException = new CDHException(
        ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION,
        String.format("Retrieved exception from CDH, response status = %s on %s", 
            HttpStatus.INTERNAL_SERVER_ERROR, endpoint));
    
    lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    lenient().when(responseSpec.bodyToFlux(eq(EmployeeSpendResponse.class)))
        .thenReturn(Flux.error(expectedException));

    // Mock properties
    lenient().when(oAuthProvider.getBearerToken()).thenReturn("test-token");
    lenient().when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("Ocp-Apim-Subscription-Key");
    lenient().when(cdhApiOauthProperties.getInnBusinessSubscriptionKey()).thenReturn("test-sub-key");
    lenient().when(cdhApiProperties.getRequestHeaderName()).thenReturn("X-Azure-FDID");
    lenient().when(cdhApiProperties.getRequestHeaderValue()).thenReturn("test-fdid");

    // When & Then
    CDHException thrown = assertThrows(CDHException.class, () -> client.getEmployeeSpendReport(request));
    
    assertEquals(542, thrown.getErrorCode());
    assertTrue(thrown.getDebugMessage().contains("Retrieved exception from CDH"));
    assertTrue(thrown.getDebugMessage().contains("500 INTERNAL_SERVER_ERROR"));
  }
}
