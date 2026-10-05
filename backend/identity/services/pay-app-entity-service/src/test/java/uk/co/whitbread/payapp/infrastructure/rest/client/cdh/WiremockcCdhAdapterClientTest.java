package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.CDHException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.properties.CdhAdapterProperties;

@ExtendWith(MockitoExtension.class)
class WiremockcCdhAdapterClientTest {

  @Mock
  private CdhAdapterProperties cdhAdapterProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient cdhAdapterWebClient = WebClient.create(path);

  private static final String ACCESS_BY = "InnBusiness";
  private static final String COMPANY_ID = "123456";


  @BeforeEach
  void setUp() {
    when(cdhAdapterProperties.getGetEmployeesEndpoint()).thenReturn("/v1/cdh/account/employees");

    wm = new WireMockServer(options().port(8080));
    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testCdhAdapterClient_getEmployees_ShouldThrowException() {
    CdhAdapterClient cdhAdapterClient = new CdhAdapterClient(cdhAdapterProperties,
        cdhAdapterWebClient);
    String errorResponseBody = """
        {
            "status": 404,
            "message": "No Company account found with this given company account id"
        }
        """;

    wm.stubFor(any(urlMatching("^.*employees.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(CDHException.class,
        () -> cdhAdapterClient.getEmployees(COMPANY_ID, ACCESS_BY));
  }
}
