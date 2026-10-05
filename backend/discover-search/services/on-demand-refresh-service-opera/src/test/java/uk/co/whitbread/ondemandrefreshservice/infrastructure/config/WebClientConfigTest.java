package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static com.github.tomakehurst.wiremock.http.Fault.MALFORMED_RESPONSE_CHUNK;
import static com.github.tomakehurst.wiremock.http.Fault.RANDOM_DATA_THEN_CLOSE;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Disabled
@SpringBootTest(properties = {
    "config.service.opera.is-client-credentials-enabled=true",
    "config.service.opera.wire-tap-access-token=true",
    "config.service.opera.auth-endpoint=/oauth/v1/tokens",
    "config.service.opera.clientId=test-client-id",
    "config.service.opera.clientSecret=test-secret",
    "config.service.opera.app-key=app-key-test",
    "config.service.opera.scope=urn:ocp:hgbu:ws:_myscopes_",
    "config.service.opera.enterprise-id=eid-test"
})
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
class WebClientConfigTest {

  static WireMockServer wireMockServer;
  @Autowired
  private WebClient ohipWebClient;

  @BeforeAll
  static void startWireMock() {
    wireMockServer = new WireMockServer(0);
    wireMockServer.start();
  }

  @AfterAll
  static void stopWireMock() {
    wireMockServer.stop();
  }

  @DynamicPropertySource
  static void overrideOhipHost(DynamicPropertyRegistry registry) {
    registry.add("config.service.opera.host", () -> wireMockServer.baseUrl());
  }

  @BeforeEach
  void stubOAuthTokenEndpoint() {
    wireMockServer.stubFor(post("/oauth/v1/tokens")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"access_token\":\"test-token\",\"token_type\":\"bearer\",\"expires_in\":3600}")
        ));
  }

  @Test
  void testRetriesGetOnPrematureClose() {
    wireMockServer.stubFor(get("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("Started")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE))
        .willSetStateTo("SecondAttempt"));

    wireMockServer.stubFor(get("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("SecondAttempt")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE))
        .willSetStateTo("ThirdAttempt"));

    wireMockServer.stubFor(get("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("ThirdAttempt")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("success")
        ));

    Mono<String> response = ohipWebClient.get()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectNext("success")
        .verifyComplete();
  }

  @Test
  void testRetriesPutOnPrematureClose() {
    wireMockServer.stubFor(put("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("Started")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE))
        .willSetStateTo("SecondAttempt"));

    wireMockServer.stubFor(put("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("SecondAttempt")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE))
        .willSetStateTo("ThirdAttempt"));

    wireMockServer.stubFor(put("/test")
        .inScenario("Retry")
        .whenScenarioStateIs("ThirdAttempt")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("success")
        ));

    Mono<String> response = ohipWebClient.put()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectNext("success")
        .verifyComplete();
  }

  @Test
  void testNoRetryOnConnectionResetByPeer() {
    wireMockServer.stubFor(get("/test")
        .willReturn(aResponse().withFault(CONNECTION_RESET_BY_PEER)));

    Mono<String> response = ohipWebClient.get()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }

  @Test
  void testNoRetryOnRandomDataThenClose() {
    wireMockServer.stubFor(get("/test")
        .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

    Mono<String> response = ohipWebClient.get()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }

  @Test
  void testNoRetryOnMalformedResponseChunk() {
    wireMockServer.stubFor(get("/test")
        .willReturn(aResponse().withFault(MALFORMED_RESPONSE_CHUNK)));

    Mono<String> response = ohipWebClient.get()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }

  @Test
  void testNoRetryOnPost() {
    wireMockServer.stubFor(post("/test")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

    Mono<String> response = ohipWebClient.post()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }

  @Test
  void testNoRetryOnDelete() {
    wireMockServer.stubFor(delete("/test")
        .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

    Mono<String> response = ohipWebClient.delete()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }

  @Test
  void testNoRetryOnPatch() {
    wireMockServer.stubFor(patch(urlEqualTo("/test"))
        .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

    Mono<String> response = ohipWebClient.patch()
        .uri("/test")
        .retrieve()
        .bodyToMono(String.class);

    StepVerifier.create(response)
        .expectError()
        .verify();
  }
}