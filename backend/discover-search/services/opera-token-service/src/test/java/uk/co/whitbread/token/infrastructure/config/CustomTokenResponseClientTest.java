package uk.co.whitbread.token.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

class CustomTokenResponseClientTest {

  private static final String ENTERPRISE_ID = "testEnterpriseId";
  private static final String APP_KEY = "testAppKey";

  @Test
  void getTokenResponse_shouldSendOperaHeadersAndReturnAccessToken() throws Exception {
    CapturedRequest capturedRequest = new CapturedRequest();
    String responseBody = "{\"access_token\":\"opera-token\",\"token_type\":\"bearer\",\"expires_in\":3600}";

    try (TokenServer tokenServer = new TokenServer(responseBody, capturedRequest)) {
      CustomTokenResponseClient tokenResponseClient = new CustomTokenResponseClient(ENTERPRISE_ID, APP_KEY);

      OAuth2AccessTokenResponse response = tokenResponseClient.getTokenResponse(
          new OAuth2ClientCredentialsGrantRequest(clientRegistration(tokenServer.tokenUri())));

      assertNotNull(response);
      assertEquals("opera-token", response.getAccessToken().getTokenValue());
      assertEquals(3600L, response.getAccessToken().getExpiresAt().getEpochSecond()
          - response.getAccessToken().getIssuedAt().getEpochSecond());
      assertEquals("POST", capturedRequest.method.get());
      assertEquals(ENTERPRISE_ID, capturedRequest.firstHeader(CustomTokenResponseClient.HEADER_ENTERPRISE_ID));
      assertEquals(APP_KEY, capturedRequest.firstHeader(CustomTokenResponseClient.HEADER_X_APP_KEY));
      assertTrue(capturedRequest.body.get().contains("grant_type=client_credentials"));
    }
  }

  @Test
  void getTokenResponse_shouldRejectUnexpectedTokenType() throws Exception {
    CapturedRequest capturedRequest = new CapturedRequest();
    String responseBody = "{\"access_token\":\"opera-token\",\"token_type\":\"mac\",\"expires_in\":3600}";

    try (TokenServer tokenServer = new TokenServer(responseBody, capturedRequest)) {
      CustomTokenResponseClient tokenResponseClient = new CustomTokenResponseClient(ENTERPRISE_ID, APP_KEY);
      OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(
          clientRegistration(tokenServer.tokenUri()));

      RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenResponseClient.getTokenResponse(request));

      assertTrue(hasMessageContaining(exception, "Unexpected token_type in Opera response: mac"));
    }
  }

  private static ClientRegistration clientRegistration(String tokenUri) {
    return ClientRegistration
        .withRegistrationId("ohip")
        .clientId("client-id")
        .clientSecret("client-secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .tokenUri(tokenUri)
        .build();
  }

  private static boolean hasMessageContaining(Throwable throwable, String expectedMessage) {
    Throwable current = throwable;
    while (current != null) {
      if (current.getMessage() != null && current.getMessage().contains(expectedMessage)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private static final class CapturedRequest {

    private final AtomicReference<String> method = new AtomicReference<>();
    private final AtomicReference<com.sun.net.httpserver.Headers> headers = new AtomicReference<>();
    private final AtomicReference<String> body = new AtomicReference<>();

    private String firstHeader(String name) {
      List<String> values = headers.get().get(name);
      return values == null || values.isEmpty() ? null : values.get(0);
    }
  }

  private static final class TokenServer implements AutoCloseable {

    private final HttpServer server;

    private TokenServer(String responseBody, CapturedRequest capturedRequest) throws IOException {
      server = HttpServer.create(new InetSocketAddress(0), 0);
      server.createContext("/oauth/token", exchange -> {
        capturedRequest.method.set(exchange.getRequestMethod());
        capturedRequest.headers.set(exchange.getRequestHeaders());
        capturedRequest.body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
      });
      server.start();
    }

    private String tokenUri() {
      return URI.create("http://localhost:" + server.getAddress().getPort() + "/oauth/token").toString();
    }

    @Override
    public void close() {
      server.stop(0);
    }
  }
}
