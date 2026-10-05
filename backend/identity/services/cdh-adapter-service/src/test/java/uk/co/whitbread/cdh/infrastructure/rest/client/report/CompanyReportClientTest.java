package uk.co.whitbread.cdh.infrastructure.rest.client.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyResults;
import uk.co.whitbread.cdh.domain.model.report.out.Reports;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.report.managementinformation.CompanyReportClient;
import uk.co.whitbread.cdh.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class CompanyReportClientTest {

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

  @Mock
  private CdhApiProperties cdhApiProperties;

  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;

  @Mock
  private OAuthProvider oAuthProvider;

  @Mock
  private CustomTestResponseSpec customResponseSpec;


  @InjectMocks
  private CompanyReportClient companyReportClient;

  @Test
  void test_getMIReport_success() {

    // Arrange
    ManagementInformation managementInformation = ManagementInformation
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-10")
        .build();

    when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("jhfgh");
    when(cdhApiOauthProperties.getAccountSubscriptionKey()).thenReturn("87rtdcb");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("dwicgigdf");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("63nkdjvgu");
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyReports.class)).thenReturn(
        mockMIReportResponse());

    // Act
    var response = companyReportClient.getManagementInformation(managementInformation,
        "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui");

    // Assert
    verifyNoMoreInteractions(webClient);
    assertNotNull(response);
  }

  @Test
  void test_getMIReport_failed() {

    // Arrange
    ManagementInformation managementInformation = ManagementInformation
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-10")
        .build();

    when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("jhfgh");
    when(cdhApiOauthProperties.getAccountSubscriptionKey()).thenReturn("87rtdcb");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("dwicgigdf");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("63nkdjvgu");
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> companyReportClient.getManagementInformation(managementInformation,
            "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
            "cdh-adapter-service", "ccui"));
    assertEquals(ErrorCode.CDH_MANAGEMENT_INFO_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void test_getEmergencyReport_success() {

    // Arrange
    EmergencyReport emergencyReport = EmergencyReport
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-10")
        .build();

    when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("jhfgh");
    when(cdhApiOauthProperties.getAccountSubscriptionKey()).thenReturn("87rtdcb");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("dwicgigdf");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("63nkdjvgu");
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(EmergencyReportResults.class)).thenReturn(
        mockEmergencyReportResponse());

    // Act
    var response = companyReportClient.getEmergencyReport(emergencyReport,
        "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui");

    // Assert
    verifyNoMoreInteractions(webClient);
    assertNotNull(response);
  }

  @Test
  void test_getEmergencyReport_failed() {

    // Arrange
    EmergencyReport emergencyReport = EmergencyReport
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-10")
        .build();

    when(cdhApiOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("jhfgh");
    when(cdhApiOauthProperties.getAccountSubscriptionKey()).thenReturn("87rtdcb");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("dwicgigdf");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("63nkdjvgu");
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> companyReportClient.getEmergencyReport(emergencyReport,
            "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
            "cdh-adapter-service", "ccui"));
    assertEquals(ErrorCode.CDH_EMERGENCY_REPORT_EXCEPTION.getCode(), exception.getErrorCode());
  }

  private Mono<CompanyReports> mockMIReportResponse() {
    return Mono.just(CompanyReports.builder().results(createReportList())
        .build());

  }

  private Mono<EmergencyReportResults> mockEmergencyReportResponse() {
    return Mono.just(EmergencyReportResults.builder().results(createEmergencyReportList())
        .build());

  }

  private List<Reports> createReportList() {
    Reports report1 = Reports.builder().arrivalDate("2023-01-01").build();
    Reports report2 = Reports.builder().arrivalDate("2023-01-15").bookingReference("hijhd8530")
        .build();
    List<Reports> reportsList = new ArrayList<>();
    reportsList.add(report1);
    reportsList.add(report2);
    return reportsList;
  }

  private List<EmergencyResults> createEmergencyReportList() {
    EmergencyResults report1 = EmergencyResults.builder().arrivalDate("2023-01-01").build();
    EmergencyResults report2 = EmergencyResults.builder().arrivalDate("2023-01-15")
        .bookingReference("hijhd8530")
        .build();
    List<EmergencyResults> reportsList = new ArrayList<>();
    reportsList.add(report1);
    reportsList.add(report2);
    return reportsList;
  }
}
