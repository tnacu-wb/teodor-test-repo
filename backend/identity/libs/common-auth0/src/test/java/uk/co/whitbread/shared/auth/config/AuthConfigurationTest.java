package uk.co.whitbread.shared.auth.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.security.model.RbacRuleHasAccessResponse;
import uk.co.whitbread.shared.auth.tenant.SecurityProperties;

class AuthConfigurationTest {

  private final AuthConfiguration authConfiguration = new AuthConfiguration();

  @Test
  void bearerTokenResolver_prefersAuthorizationHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer standard-token");
    request.addHeader("WB-Authorization", "Bearer legacy-token");

    String token = authConfiguration.bearerTokenResolver().resolve(request);

    assertEquals("standard-token", token);
  }

  @Test
  void bearerTokenResolver_fallsBackToWbAuthorizationHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("WB-Authorization", "Bearer legacy-token");

    String token = authConfiguration.bearerTokenResolver().resolve(request);

    assertEquals("legacy-token", token);
  }

  @Test
  void bearerTokenResolver_usesWbAuthorizationWhenAuthorizationIsBlank() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, " ");
    request.addHeader("WB-Authorization", "Bearer legacy-token");

    String token = authConfiguration.bearerTokenResolver().resolve(request);

    assertEquals("legacy-token", token);
  }

  @Test
  void bearerTokenResolver_returnsNullWhenNeitherHeaderContainsBearerToken() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    String token = authConfiguration.bearerTokenResolver().resolve(request);

    assertNull(token);
  }

  @Test
  void rulesServiceWebClient_usesConfiguredBaseUrlAndDefaultJsonCodec() throws Exception {
    try (MockWebServer mockServer = new MockWebServer()) {
      mockServer.start();
      mockServer.enqueue(new MockResponse()
          .setBody("{\"hasAccess\":true}")
          .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

      SecurityProperties securityProperties = new SecurityProperties();
      securityProperties.setRulesEngineHost(mockServer.url("/").toString());

      WebClient webClient = authConfiguration.rulesServiceWebClient(
          securityProperties,
          WebClient.builder()
      );

      RbacRuleHasAccessResponse response = webClient.get()
          .uri("/rules")
          .retrieve()
          .bodyToMono(RbacRuleHasAccessResponse.class)
          .block();

      RecordedRequest request = mockServer.takeRequest();
      assertEquals("/rules", request.getPath());
      assertEquals("application/json", request.getHeader(HttpHeaders.CONTENT_TYPE));
      assertNotNull(response);
      assertEquals(true, response.getHasAccess());
    }
  }
}
