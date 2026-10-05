package uk.co.whitbread.content.infrastructure.rest.client.note.business;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter.BusinessNotesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.out.BusinessNotesRequestAemDto;

@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmBusinessNotesAemServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockBusinessNotesAemClientTest {

  @Mock
  private AemProperties aemProperties;
  @InjectWireMock("wmBusinessNotesAemServer")
  WireMockServer wm;
  String path = "http://localhost:8080";

  BusinessNotesRequestAemDto request = new BusinessNotesRequestAemDto("en");

  @BeforeEach
  void setUp() {
    wm.stubFor(get(urlEqualTo("/etc/designs/global/dictionaries/allowances/i18n.jsondict.en"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterEach
  void cleanUp() {
    wm.resetAll();
  }

  @Test
  void testAemClient_ShouldReturnException() {
    WebClient webClient = WebClient.create(path);
    BusinessNotesAemClient aemClient = new BusinessNotesAemClient(webClient, aemProperties);
    when(aemProperties.getBusinessNotesEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/allowances/i18n.jsondict.{language}");

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getBusinessNotes(request));
  }

}
