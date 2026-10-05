package uk.co.whitbread.piba.registration.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_CODE;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_ROLE;
import static uk.co.whitbread.piba.registration.util.TestUtil.TETHERED_USER_GUID;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.service.PibaRegistrationService;
import uk.co.whitbread.piba.registration.util.TestUtil;
import uk.co.whitbread.shared.auth.service.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PibaRegistrationControllerTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig()
                    .dynamicPort()
                    .usingFilesUnderClasspath("wiremock"))
            .build();

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", wireMock::getPort);
    }

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String VALID_SESSION_ID = "validSessionId";

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @MockitoBean
    private PibaRegistrationService mockPibaRegistrationService;
    @MockitoBean
    private TokenService mockAuthTokenService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final TestUtil testUtil = new TestUtil();

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.of(VALID_SESSION_ID));
        Jwt mockJwt = Jwt.withTokenValue("random_token")
                .header("alg", "none")
                .claim("sub", "test-user")
                .issuer("http://localhost/")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
    }

    @Test
    void getInfo_ShouldReturnHttpStatusCode200() {
        when(mockPibaRegistrationService.getInfo(REGISTRATION_CODE))
                .thenReturn(testUtil.buildRegistrationInfoResponse());

        webTestClient.get()
                .uri("/piba/registration/info/" + REGISTRATION_CODE)
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> org.assertj.core.api.Assertions.assertThat(body).contains(REGISTRATION_ROLE));
    }

    @Test
    void getInfo_ShouldReturnHttpStatusCode500WhenServiceFails() {
        when(mockPibaRegistrationService.getInfo(REGISTRATION_CODE)).thenThrow(PibaRegistrationException.class);

        webTestClient.get()
                .uri("/piba/registration/info/" + REGISTRATION_CODE)
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void authenticate_ShouldReturnHttpStatusCode201() {
        RegistrationAuthenticationRequest registrationAuthenticationRequest = testUtil.buildAuthenticationRequest(REGISTRATION_CODE);

        when(mockPibaRegistrationService.authenticate(registrationAuthenticationRequest))
                .thenReturn(testUtil.buildAuthenticationResponse());

        webTestClient.post()
                .uri("/piba/registration/authenticate")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationAuthenticationRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .value(body -> org.assertj.core.api.Assertions.assertThat(body).contains("Forename"));
    }

    @Test
    void authenticate_ShouldReturnHttpStatusCode500WhenServiceFails() {
        RegistrationAuthenticationRequest registrationAuthenticationRequest = testUtil.buildAuthenticationRequest(REGISTRATION_CODE);
        when(mockPibaRegistrationService.authenticate(registrationAuthenticationRequest)).thenThrow(PibaRegistrationException.class);

        webTestClient.post()
                .uri("/piba/registration/authenticate")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationAuthenticationRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void submit_ShouldReturnHttpStatusCode201() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE);

        when(mockPibaRegistrationService.submit(registrationSubmitRequest, "Bearer random_token"))
                .thenReturn(testUtil.buildRegistrationSubmitResp());

        webTestClient.post()
                .uri("/piba/registration/submit")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationSubmitRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .value(body -> org.assertj.core.api.Assertions.assertThat(body).contains(TETHERED_USER_GUID));
    }

    @Test
    void submit_ShouldReturnHttpStatusCode500WhenServiceFails() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE);
        when(mockPibaRegistrationService.submit(registrationSubmitRequest, "Bearer random_token")).thenThrow(PibaRegistrationException.class);

        webTestClient.post()
                .uri("/piba/registration/submit")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationSubmitRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void submit_ShouldReturnHttpStatusCode401WhenAuthorizationHeaderMissing() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE);

        webTestClient.post()
                .uri("/piba/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationSubmitRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
