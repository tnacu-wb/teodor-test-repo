package uk.co.whitbread.payapp.infrastructure.rest.client.company;

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
import uk.co.whitbread.payapp.infrastructure.rest.client.company.exceptions.CompanyResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.properties.CompanyProperties;

@ExtendWith(MockitoExtension.class)
class WiremockCompanyClientTest {

  @Mock
  private CompanyProperties companyProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient companyWebClient = WebClient.create(path);

  private static final String COMPANY_ID = "COMP_12345678-1234-1234-1234-123456789876";
  private static final String AUTHORIZATION_TOKEN = "Bearer dummy";

  @BeforeEach
  void setUp() {
    when(companyProperties.getGetCompanyDetailsEndpoint()).thenReturn("/company/{companyId}");

    wm = new WireMockServer(options().port(8080));
    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testCompanyClient_getCompanyDetails_ShouldThrowException() {
    CompanyClient companyClient = new CompanyClient(companyWebClient, companyProperties);
    String errorResponseBody = """
        {
            "status": 404,
            "message": "No Company account found with this given company account id"
        }
        """;

    wm.stubFor(any(urlMatching("^.*company.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(CompanyResponseException.class,
        () -> companyClient.getCompanyDetails(COMPANY_ID, AUTHORIZATION_TOKEN));
  }

}
