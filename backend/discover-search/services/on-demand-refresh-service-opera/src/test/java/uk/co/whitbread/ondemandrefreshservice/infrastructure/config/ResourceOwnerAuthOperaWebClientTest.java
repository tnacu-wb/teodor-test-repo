package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

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
import org.springframework.test.context.ContextConfiguration;


@Disabled
@SpringBootTest(webEnvironment = RANDOM_PORT, properties =
    {"config.service.opera.is-client-credentials-enabled=false",
        "config.service.opera.wire-tap-access-token=true",
        "config.service.opera.auth-endpoint=/auth",
        "config.service.opera.clientId=test-client-id",
        "config.service.opera.clientSecret=test-secret",
        "config.service.opera.username=test-username",
        "config.service.opera.password=test-password",
        "config.service.opera.app-key=app-key-test"})
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
class ResourceOwnerAuthOperaWebClientTest {

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
        .withHeader("Authorization", equalTo("Basic dummyToken"))
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
        OAuth2AuthorizeRequest.withClientRegistrationId(OperaConstants.REGISTRATION_ID)
            .principal("test-principal")
            .attributes(attrs -> attrs.put(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME, "test-scope"))
            .build();

    // Call authorize method with OAuth2AuthorizeRequest
    OAuth2AuthorizedClient result = authorizedClientManager.authorize(authorizeRequest).block();

    Assertions.assertNotNull(result);
    Assertions.assertEquals("test-token", result.getAccessToken().getTokenValue());
  }

}