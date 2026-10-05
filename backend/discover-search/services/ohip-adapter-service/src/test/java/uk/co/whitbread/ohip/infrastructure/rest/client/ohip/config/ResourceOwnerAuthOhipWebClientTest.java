package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;


@Disabled
@SpringBootTest(webEnvironment = RANDOM_PORT, properties =
    {"config.service.ohip.is-client-credentials-enabled=false",
        "config.service.ohip.wire-tap-access-token=true",
        "config.service.ohip.auth-endpoint=/auth",
        "config.service.ohip.clientId=test-client-id",
        "config.service.ohip.clientSecret=test-secret",
        "config.service.ohip.username=test-username",
        "config.service.ohip.password=test-password",
        "config.service.ohip.app-key=app-key-test"})
class ResourceOwnerAuthOhipWebClientTest {

  private static WireMockServer wireMockServer;
  @Autowired
  private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

  @BeforeAll
  public static void setup() {
    wireMockServer = new WireMockServer(options().port(8080));
    // Stub the wiremock server to return access token for specific request format
    wireMockServer.stubFor(post("/auth")
        .withHeader("Accept", equalTo("application/json"))
        .withHeader("x-app-key", equalTo("app-key-test"))
        .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
        .withHeader("Authorization", equalTo("Basic dGVzdC1jbGllbnQtaWQ6dGVzdC1zZWNyZXQ="))
        .withRequestBody(equalTo("grant_type=password&username=test-username&password=test-password"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("{\"access_token\":\"test-token\",\"token_type\":\"bearer\",\"expires_in\":3600}")));
    wireMockServer.start();
  }

  @AfterAll
  public static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void test_ResourceOwner_Authorization() {
    // Create OAuth2AuthorizeRequest
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId(OhipConstants.REGISTRATION_ID)
            .principal("test-principal")
            .attributes(attrs -> attrs.put(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME, "test-scope"))
            .build();

    // Call authorize method with OAuth2AuthorizeRequest
    OAuth2AuthorizedClient result = authorizedClientManager.authorize(authorizeRequest).block();

    Assertions.assertNotNull(result);
    Assertions.assertEquals("test-token", result.getAccessToken().getTokenValue());
  }

}