package uk.co.whitbread.company.employee.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.shared.cdh.CustomerDataHubClient;
import uk.co.whitbread.shared.cdh.model.CdhAccessContext;
import uk.co.whitbread.shared.cdh.model.CdhHeaders;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;

@ExtendWith(MockitoExtension.class)
class CdhClientOverrideConfigTest {

    private static WireMockServer wireMockServer;

    private final CdhClientOverrideConfig config = new CdhClientOverrideConfig();

    @Mock
    private OAuthProvider oauthProvider;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void cdhJsonMapperShouldDeserializeNullPrimitiveBooleans() throws Exception {
        JsonMapper mapper = config.cdhJsonMapper();

        GetEmployeeResponse response = mapper.readValue("""
            {
              "mainEmployee": null,
              "textConfirmation": null,
              "lockedForEditing": null,
              "awaitingApproval": null
            }
            """, GetEmployeeResponse.class);

        assertThat(response.isMainEmployee()).isFalse();
        assertThat(response.isTextConfirmation()).isFalse();
        assertThat(response.isLockedForEditing()).isFalse();
        assertThat(response.isAwaitingApproval()).isFalse();
    }

    @Test
    void companyEmployeeCdhWebClientShouldApplyDefaultHeaders() {
        wireMockServer.stubFor(get(urlPathEqualTo("/employee"))
            .willReturn(ok("success")));

        WebClient webClient = config.companyEmployeeCdhWebClient(
            HttpClient.create().baseUrl(wireMockServer.baseUrl()),
            oauthProvider,
            config.cdhJsonMapper()
        );

        String responseBody = webClient.get()
            .uri("/employee?source=test")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(responseBody).isEqualTo("success");
        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/employee"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
            .withHeader(CdhHeaders.ACCESS_CONTEXT.getHeader(), equalTo(CdhAccessContext.PI.name())));
    }

    @Test
    void companyEmployeeCdhWebClientShouldRetryWithFreshBearerTokenOnUnauthorized() {
        when(oauthProvider.getNewBearerToken()).thenReturn("fresh-token");

        wireMockServer.stubFor(get(urlEqualTo("/secured"))
            .inScenario("refresh-token")
            .whenScenarioStateIs("Started")
            .willReturn(aResponse().withStatus(401))
            .willSetStateTo("retried"));
        wireMockServer.stubFor(get(urlEqualTo("/secured"))
            .inScenario("refresh-token")
            .whenScenarioStateIs("retried")
            .willReturn(ok("retried-success")));

        WebClient webClient = config.companyEmployeeCdhWebClient(
            HttpClient.create().baseUrl(wireMockServer.baseUrl()),
            oauthProvider,
            config.cdhJsonMapper()
        );

        String responseBody = webClient.get()
            .uri("/secured")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(responseBody).isEqualTo("retried-success");
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/secured"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer fresh-token")));
    }

    @Test
    void customerDataHubClientShouldUseProvidedWebClient() {
        wireMockServer.stubFor(get(urlEqualTo("/employee"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody("""
                    {
                      "mainEmployee": null,
                      "textConfirmation": null,
                      "lockedForEditing": null,
                      "awaitingApproval": null
                    }
                    """)));

        WebClient webClient = config.companyEmployeeCdhWebClient(
            HttpClient.create().baseUrl(wireMockServer.baseUrl()),
            oauthProvider,
            config.cdhJsonMapper()
        );

        CustomerDataHubClient customerDataHubClient = config.customerDataHubClient(webClient, oauthProvider);

        assertThat(customerDataHubClient.getCDH("/employee", new HttpHeaders(), GetEmployeeResponse.class))
            .hasValueSatisfying(response -> {
                assertThat(response.isMainEmployee()).isFalse();
                assertThat(response.isTextConfirmation()).isFalse();
                assertThat(response.isLockedForEditing()).isFalse();
                assertThat(response.isAwaitingApproval()).isFalse();
            });
    }
}
