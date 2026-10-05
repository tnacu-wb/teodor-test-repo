package mocks;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ContentServiceMockResponse {

  public static void setupMockRateClassification(WireMockServer mockService) throws IOException {
    mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/content/rateclassifications?brand=pi&language=en"))
        .willReturn(WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody(copyToString(ContentServiceMockResponse.class.getClassLoader()
                    .getResourceAsStream("stubs/content-service/rateClassification.json"),
                defaultCharset()))));
  }
}
