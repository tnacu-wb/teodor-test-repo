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
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.EmployeeClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OauthClientException;
import uk.co.whitbread.cdh.utils.CustomStatusCodeException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockEmployeeClientTest {

  @Mock
  private OAuthProvider oauthProvider;
  @Mock
  private CdhApiOauthProperties cdhOauthProperties;
  @Mock
  CdhApiProperties cdhApiProperties;
  @Mock
  WebClientProperties webClientProperties;

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
  void testGetCompanies_ShouldReturnNoResponse() {
    //Arrange
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    final EmployeeClient client = new EmployeeClient(cdhApiProperties, webClient, oauthProvider,
        webClientProperties);
    when(cdhApiProperties.getGetEmployeesEndpoint()).thenReturn("/test1");

    //Act
    var response = client.getEmployees(new EmployeeSearchCriteria());

    //Asserts
    assertNull(response);
  }

  @Test
  void testGetEmployees_ShouldReturn5xxCdhException() {
    //Arrange
    var employeeSearchCriteria =  new EmployeeSearchCriteria();
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    final EmployeeClient client = new EmployeeClient(cdhApiProperties, webClient, oauthProvider,
        webClientProperties);
    when(cdhApiProperties.getGetEmployeesEndpoint()).thenReturn("/exc1");

    //Act
    assertThrows(CDHException.class, () -> client.getEmployees(employeeSearchCriteria));
  }


  @Test
  void testGetEmployees_TokenException() {
    //Arrange
    var employeeSearchCriteria =  new EmployeeSearchCriteria();
    arrangeProperties();
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    final EmployeeClient client = new EmployeeClient(cdhApiProperties, webClient, oauthProvider,
        webClientProperties);
    when(cdhApiProperties.getGetEmployeesEndpoint()).thenReturn("/exc1");

    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.getEmployees(employeeSearchCriteria));
  }

  @Test
  void testGetCompanies_StatusCodeExceptionException() {
    //Arrange
    var employeeSearchCriteria =  new EmployeeSearchCriteria();
    arrangeProperties();
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    final EmployeeClient client = new EmployeeClient(cdhApiProperties, webClient, oauthProvider,
        webClientProperties);
    when(cdhApiProperties.getGetEmployeesEndpoint()).thenReturn("/exc1");

    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getEmployees(employeeSearchCriteria));
  }

  private void arrangeProperties() {
    when(cdhOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("key");
    when(cdhOauthProperties.getAccountSubscriptionKey()).thenReturn("SubscriptionKey");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("name");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("name");
  }
}
