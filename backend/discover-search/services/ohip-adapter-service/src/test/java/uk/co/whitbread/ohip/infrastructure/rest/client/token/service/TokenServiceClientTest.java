package uk.co.whitbread.ohip.infrastructure.rest.client.token.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto.TokenResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.properties.TokenServiceProperties;

class TokenServiceClientTest {

  private MockWebServer mockWebServer;
  private TokenServiceClient tokenServiceClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    TokenServiceProperties properties = new TokenServiceProperties();
    properties.setTokenEndpoint("/v1/tokens/opera/access-token");

    tokenServiceClient = new TokenServiceClient(webClient, properties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getOperaAccessTokenResponse_Success() {
    // Given
    String body = """
        {
          "accessToken": "abc123",
          "tokenType": "Bearer",
          "expiresIn": 3600,
          "issuedAt": "2025-01-01T12:00:00Z"
        }
        """;

    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(body));

    // When
    Mono<TokenResponse> resultMono = tokenServiceClient.getOperaAccessTokenResponse();
    TokenResponse response = resultMono.block();

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("abc123");
    assertThat(response.getTokenType()).isEqualTo("Bearer");
    assertThat(response.getExpiresIn()).isEqualTo(3600);
    assertThat(response.getIssuedAt()).isEqualTo("2025-01-01T12:00:00Z");
  }

  @Test
  void getOperaAccessTokenResponse_ServerError_ThrowsOhipInternalException() {
    // Given
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(500)
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"error\":\"server error\"}"));

    // When
    Mono<TokenResponse> resultMono = tokenServiceClient.getOperaAccessTokenResponse();

    // Then
    assertThatThrownBy(resultMono::block)
        .isInstanceOf(OhipInternalException.class)
        .hasMessageContaining(ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION.getMessage());
  }
}
