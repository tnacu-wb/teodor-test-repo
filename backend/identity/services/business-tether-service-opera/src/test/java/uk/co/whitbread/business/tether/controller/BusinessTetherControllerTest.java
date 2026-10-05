package uk.co.whitbread.business.tether.controller;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.business.tether.model.LoginCriteria;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.business.tether.model.TetheredLoginResponse;
import uk.co.whitbread.business.tether.service.BusinessTetherLoginService;
import uk.co.whitbread.business.tether.service.BusinessTetherService;
import uk.co.whitbread.shared.auth.service.TokenService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class BusinessTetherControllerTest extends MockedJwtDecoderBase {

    private static final String VALID_SESSION_ID = "validSessionId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String WORLDLINE_GUID = "guid";
    private static final String WORLDLINE_HASH = "Ad5ojjeTZWe7yxcLDCmay6LD8Ea9A9LMAsxHMHXFgvE=";
    private static final String WORLDLINE_NONCE = "NkQ4NTA2NjUtOTgyRC00QzgxLTlFMDEtMkQyRDg2MkI0RkY0";
    private static final String WORLDLINE_SESSION_ID = "MzgwMjQyMjktM0U0MC00QzA1LTgyRkYtMUQ1N0UzQkFDNjlD";
    private static final String WORLDLINE_SECRET = "MTZEOUI0NDItRDgxOS00NzMxLUEzMjMtMEZGMTZDQjc4MEVD";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private BusinessTetherService mockBusinessTetherService;

    @MockitoBean
    private BusinessTetherLoginService mockBusinessTetherLoginService;

    @MockitoBean
    private TokenService mockAuthTokenService;

    @Test
    void tetherLink__ValidRequest_ShouldReturn201Success() throws Exception {
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.of(VALID_SESSION_ID));
        when(mockBusinessTetherService.tetherByAccountOrCardRequest(any(TetherLinkRequest.class), any(String.class)))
                .thenReturn(WORLDLINE_GUID);

        HttpResponse<String> response = HTTP_CLIENT.send(
                buildRequest("/business/tether", """
                        {"linkId":"11111","linkCode":"11111","memorableWord":"word"}
                        """),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode());
        assertTrue(response.body().contains("\"guid\":\"" + WORLDLINE_GUID + "\""));
    }

    @Test
    void tetherLogin__ValidRequest_ShouldReturn201Success() throws Exception {
        when(mockBusinessTetherLoginService.login(buildLoginCriteria()))
                .thenReturn(buildTetheredLoginResponse());

        HttpResponse<String> response = HTTP_CLIENT.send(
                buildRequest("/business/tether/login", """
                        {"guid":"guid"}
                        """),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode());
        assertTrue(response.body().contains("\"sharedSecret\":\"" + WORLDLINE_SECRET + "\""));
    }

    private HttpRequest buildRequest(String path, String body) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + serverPort + path))
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private LoginCriteria buildLoginCriteria() {
        LoginCriteria loginCriteriaRequest = new LoginCriteria();
        loginCriteriaRequest.setGuid(WORLDLINE_GUID);
        return loginCriteriaRequest;
    }

    private TetheredLoginResponse buildTetheredLoginResponse() {
        TetheredLoginResponse tetheredLoginResponse = new TetheredLoginResponse();
        tetheredLoginResponse.setHash(WORLDLINE_HASH);
        tetheredLoginResponse.setSessionId(WORLDLINE_SESSION_ID);
        tetheredLoginResponse.setSharedSecret(WORLDLINE_SECRET);
        tetheredLoginResponse.setNonce(WORLDLINE_NONCE);
        return tetheredLoginResponse;
    }
}
