package uk.co.whitbread.cdh.infrastructure.rest.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.company.CompanyClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OauthClientException;
import uk.co.whitbread.cdh.infrastructure.rest.client.report.managementinformation.CompanyReportClient;
import uk.co.whitbread.cdh.utils.CustomStatusCodeException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockCompanyClientTest {

  private final String companyId = "id";
  private final String accessedBy = "accessedBy";
  private final String accessContext = "accessContext";
  private final EmergencyReport report = EmergencyReport.builder().build();
  private final ManagementInformation managementInformation = ManagementInformation.builder().build();
  private final CompanySearchCriteria request = CompanySearchCriteria.builder().build();
  @Mock
  private OAuthProvider oauthProvider;
  @Mock
  private CdhApiOauthProperties cdhOauthProperties;
  @Mock
  CdhApiProperties cdhApiProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);

  private final Body testBody = Body.none();


  @BeforeEach
  void setUp() {
    wm = new WireMockServer(8080);

    wm.stubFor(any(urlMatching("^.*test.*$")).willReturn(
        aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withStatus(404)
            .withResponseBody(testBody)));

    wm.stubFor(any(urlMatching("^.*badReq.*$")).willReturn(
        aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withStatus(400)
            .withResponseBody(testBody)));

    wm.stubFor(any(urlMatching("^.*exc.*$")).willReturn(
        aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withStatus(500)
            .withResponseBody(testBody)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testGetCompany_ShouldReturnNoResponse() {
    //Arrange
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompanyEndpoint()).thenReturn("/test/{companyAccountId}");

    //Act
    Company response = client.getCompany(companyId, accessContext, accessedBy);

    //Asserts
    assertNull(response);
  }

  @Test
  void testGetCompany_ShouldReturn5xxCdhException() {
    //Arrange
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompanyEndpoint()).thenReturn("/exc/{companyAccountId}");

    //Act
    assertThrows(CDHException.class, () -> client.getCompany(companyId, accessContext, accessedBy));
  }

  @Test
  void testGetCompany_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompanyEndpoint()).thenReturn("/exc/{companyAccountId}");

    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.getCompany(companyId, accessContext, accessedBy));
  }

  @Test
  void testGetCompany_StatusCodeExceptionException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompanyEndpoint()).thenReturn("/exc/{companyAccountId}");

    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getCompany(companyId, accessContext, accessedBy));
  }

  @Test
  void testGetCompanies_ShouldReturnNoResponse() {
    //Arrange
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompaniesEndpoint()).thenReturn("/test1");

    //Act
    CompanySearch response = client.getCompanies(request);

    //Asserts
    assertNull(response);
  }


  @Test
  void testGetCompanies_ShouldReturn5xxCdhException() {
    //Arrange
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompaniesEndpoint()).thenReturn("/exc1");

    //Act
    assertThrows(CDHException.class, () -> client.getCompanies(request));
  }

  @Test
  void testGetCompanies_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompaniesEndpoint()).thenReturn("/exc1");

    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.getCompanies(request));
  }

  @Test
  void testGetCompanies_StatusCodeExceptionException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    final CompanyClient client = new CompanyClient(cdhApiProperties, webClient, oauthProvider);
    when(cdhApiProperties.getGetCompaniesEndpoint()).thenReturn("/exc1");

    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getCompanies(request));
  }

  @Test
  void testGetManagementInformationReport_ShouldReturnNoResponse() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetManagementInformationEndpoint()).thenReturn("/test2");

    //Act
    CompanyReports response = client.getManagementInformation(managementInformation, companyId,
        accessedBy, accessContext);

    //Asserts
    assertNull(response);
  }


  @Test
  void testGetManagementInformation_ShouldReturn5xxCdhException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetManagementInformationEndpoint()).thenReturn("/exc3");

    //Act
    assertThrows(CDHException.class,
        () -> client.getManagementInformation(managementInformation, companyId, accessedBy,
            accessContext));
  }

  @Test
  void testGetManagementInformation_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetManagementInformationEndpoint()).thenReturn("/exc3");

    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.getManagementInformation(managementInformation, companyId, accessedBy,
            accessContext));
  }

  @Test
  void testGetManagementInformation_StatusCodeExceptionException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetManagementInformationEndpoint()).thenReturn("/exc3");

    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getManagementInformation(managementInformation, companyId, accessedBy,
            accessContext));
  }

  @Test
  void testGetEmergencyReport_ShouldReturnNoResult() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetEmergencyReportEndpoint()).thenReturn("/test3");

    //Act
    EmergencyReportResults response = client.getEmergencyReport(report, companyId, accessedBy,
        accessContext);

    //Asserts
    assertNull(response);
  }

  @Test
  void testEmergencyReport_ShouldReturn5xxCdhException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetEmergencyReportEndpoint()).thenReturn("/exc3");

    //Act
    assertThrows(CDHException.class,
        () -> client.getEmergencyReport(report, companyId, accessedBy, accessContext));
  }

  @Test
  void testEmergencyReport_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetEmergencyReportEndpoint()).thenReturn("/exc3");

    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.getEmergencyReport(report, companyId, accessedBy, accessContext));
  }

  @Test
  void testEmergencyReport_StatusCodeExceptionException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    final CompanyReportClient client = new CompanyReportClient(cdhApiProperties, webClient,
        oauthProvider, cdhOauthProperties);
    when(cdhApiProperties.getGetEmergencyReportEndpoint()).thenReturn("/exc3");

    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getEmergencyReport(report, companyId, accessedBy, accessContext));
  }

  private void arrangeProperties() {
    when(cdhOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("key");
    when(cdhOauthProperties.getAccountSubscriptionKey()).thenReturn("SubscriptionKey");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("name");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("name");
  }
}
