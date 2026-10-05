package uk.co.whitbread.shared.auth.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.auth.PasswordlessEmailType;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.PasswordlessEmailResponse;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.BaseRequest;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.properties.PasswordlessProperties;

import java.sql.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordlessServiceTest {

    private PasswordlessService service;

    @Mock
    private AuthAPI authAPI;

    @Mock
    private PasswordlessEmailResponse passwordlessEmailResponse;

    @Mock
    private BaseRequest<PasswordlessEmailResponse> passwordlessEmailRequest;

    @Mock
    private Response<PasswordlessEmailResponse> passwordlessEmailResponseWrapper;

    @Mock
    private TokenRequest tokenRequest;

    @Mock
    private Response<TokenHolder> tokenResponse;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private final String EMAIL = "test@whitbread.com";
    private final String TOKEN_TYPE = "Bearer";
    private final String REALM_TYPE = "email";
    private final String SCOPE = "openid profile email address phone";
    private final String ACCESS_TOKEN = "9s7-PmTlIoB4mq27WD90evxE39Mi6cNQ";
    private final String ID_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUUkJSVVF4TURaRE1USTFPVEk0TkR" +
            "nME0wUTNSRFl3TlRoQ1FqUkVOVVpGTWtJeU9EUXdOdyJ9.eyJuaWNrbmFtZSI6InNhbnRpYWdvLmVsaWFyZCIsIm5hbWUiOiJzY" +
            "W50aWFnby5lbGlhcmRAd2hpdGJyZWFkLmNvbSIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci85ZWNi" +
            "ZDk4ZDQ4YjAwMzlhMWIwMDE3MGRkZTk1ZGI5YT9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZ" +
            "hdGFycyUyRnNhLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDIyLTAxLTIzVDE2OjI0OjM5LjM4N1oiLCJlbWFpbCI6InNhbnRpYWdvLm" +
            "VsaWFyZEB3aGl0YnJlYWQuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlzcyI6Imh0dHBzOi8vd2JvZGV0ZXN0LmV1LmF1d" +
            "GgwLmNvbS8iLCJzdWIiOiJlbWFpbHw2MWRmNGMyMDcwYWQzZDMxNDAxZjlhMWUiLCJhdWQiOiJoTHhIZEtxOWRLVnJQbjFYNUZ0" +
            "clY2dllWbWhmUzJqVSIsImlhdCI6MTY0Mjk1NTA3OSwiZXhwIjoxNjQzNTU5ODc5fQ.FCktNKkpHlxfiAOmJWnJV4oBSoaA02M1" +
            "vvaZWgVzUtul6aoBT8veHLwXm2QAGkA4cQTUqdfN1mqPFICvzwz1XaVlqVrMfhQbtsQo0d21Ob_BN-zfSV6WDrXYmb6DFRV-Jbj" +
            "n0Hf53bXgIQ55B303gZQHMkmi2r1UnAZeiHN-Z5RSLIPwNShr0K1j-WDOJBqSDIt87YweefVKU1xWOW-VUmwOy67VFp-fW-1XD7" +
            "DFhJDF5-ZgX-Q6oM-8p-L-TbH2sBAZLR693r1ySQlOlEyvT-4ITN_kjA04wUCiqUJXT7ant5LE_0uQTPaJukO5p42sym6pJR9HN" +
            "6WPxI18l8sPhQ";
    private final String REFRESH_TOKEN = null;
    private final long EXPIRES_IN = 86400L;

    @Test
    public void expectDomainFormatException() {
        var wrongDomain = new PasswordlessProperties();
        wrongDomain.setDomain("wrong domain with spaces");
        wrongDomain.setClientId("correct clientId");
        wrongDomain.setClientSecret("correct clientSecret");
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The domain had an invalid format and couldn't be parsed as an URL.");
        service = new PasswordlessService(wrongDomain);
    }

    @Test
    public void sendOTP_expectSuccessfulResponse() throws Auth0Exception {
        service = new PasswordlessService(getCorrectProperties());
        service.setAuth(authAPI);
        when(authAPI.startPasswordlessEmailFlow(EMAIL, PasswordlessEmailType.CODE)).thenReturn(passwordlessEmailRequest);
        when(passwordlessEmailRequest.execute()).thenReturn(passwordlessEmailResponseWrapper);
        when(passwordlessEmailResponseWrapper.getBody()).thenReturn(passwordlessEmailResponse);
        when(passwordlessEmailResponse.getEmail()).thenReturn(EMAIL);
        when(passwordlessEmailResponse.isEmailVerified()).thenReturn(false);
        var response = service.sendOTP(EMAIL);
        assertEquals(EMAIL, response.getEmail());
        assertFalse(response.isEmailVerified());
    }

    @Test
    public void send0TP_expectDomainToFail() throws Auth0Exception {
        var wrongDomain = new PasswordlessProperties();
        wrongDomain.setDomain("wrong.domain");
        wrongDomain.setClientId("correct clientId");
        wrongDomain.setClientSecret("correct clientSecret");
        service = new PasswordlessService(wrongDomain);
        service.setAuth(authAPI);
        when(authAPI.startPasswordlessEmailFlow(EMAIL, PasswordlessEmailType.CODE)).thenReturn(passwordlessEmailRequest);
        when(passwordlessEmailRequest.execute()).thenThrow(new Auth0Exception("Failed to execute request."));
        exceptionRule.expect(AuthServiceException.class);
        exceptionRule.expectMessage("Could not trigger sending of OTP.");
        service.sendOTP(EMAIL);
    }

    @Test
    public void send0TP_expectCredentialsToFail() throws Auth0Exception {
        var wrongCredentials = new PasswordlessProperties();
        wrongCredentials.setDomain("correct.domain");
        wrongCredentials.setClientId("wrong clientId");
        wrongCredentials.setClientSecret("wrong clientSecret");
        service = new PasswordlessService(wrongCredentials);
        service.setAuth(authAPI);
        when(authAPI.startPasswordlessEmailFlow(EMAIL, PasswordlessEmailType.CODE)).thenReturn(passwordlessEmailRequest);
        when(passwordlessEmailRequest.execute()).thenThrow(throwUnauthorizedException());
        exceptionRule.expect(AuthServiceException.class);
        exceptionRule.expectMessage("Could not trigger sending of OTP.");
        service.sendOTP(EMAIL);
    }

    @Test
    public void sendOTP_expectEmailToFail() throws Auth0Exception {
        var badEmail = "notanemail";
        service = new PasswordlessService(getCorrectProperties());
        service.setAuth(authAPI);
        when(authAPI.startPasswordlessEmailFlow(badEmail, PasswordlessEmailType.CODE)).thenReturn(passwordlessEmailRequest);
        when(passwordlessEmailRequest.execute()).thenThrow(throwBadEmailException());
        exceptionRule.expect(AuthServiceException.class);
        exceptionRule.expectMessage("Could not trigger sending of OTP.");
        service.sendOTP(badEmail);
    }

    @Test
    public void exchangeOTP_expectSuccessfulResponse() throws Auth0Exception {
        service = new PasswordlessService(getCorrectProperties());
        service.setAuth(authAPI);
        when(authAPI.exchangePasswordlessOtp(EMAIL, REALM_TYPE, new char[]{'1','2','3','4','5','6'})).thenReturn(tokenRequest);
        when(tokenRequest.execute()).thenReturn(tokenResponse);
        when(tokenResponse.getBody()).thenReturn(getTokenResponse());
        var response = service.exchangeOTP(EMAIL, "123456");
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(ID_TOKEN, response.getIdToken());
        assertEquals(TOKEN_TYPE, response.getTokenType());
        assertEquals(SCOPE, response.getScope());
        assertEquals(EXPIRES_IN, response.getExpiresIn());
    }

    @Test
    public void exchangeOTP_expectWrongEmailOrCodeToFail() throws Auth0Exception {
        service = new PasswordlessService(getCorrectProperties());
        service.setAuth(authAPI);
        when(authAPI.exchangePasswordlessOtp(EMAIL, REALM_TYPE, new char[]{'1','2','3','4','5','6'})).thenReturn(tokenRequest);
        when(tokenRequest.execute()).thenThrow(throwWrongEmailOrCodeException());
        exceptionRule.expect(AuthServiceException.class);
        exceptionRule.expectMessage("Could not exchange OTP for token.");
        service.exchangeOTP(EMAIL, "123456");
    }

    private PasswordlessProperties getCorrectProperties() {
        var properties = new PasswordlessProperties();
        properties.setDomain("correct.domain");
        properties.setClientId("correct clientId");
        properties.setClientSecret("correct clientSecret");
        return properties;
    }

    private APIException throwBadEmailException() {
        var values = new HashMap<String, Object>();
        values.put("error", "bad.email");
        values.put("error_description", "error in email - email format validation failed: notanemail");
        return new APIException(values, 400);
    }

    private APIException throwUnauthorizedException() {
        var values = new HashMap<String, Object>();
        values.put("error", "unauthorized_client");
        values.put("error_description", "Client authentication is required");
        return new APIException(values, 403);
    }

    private APIException throwWrongEmailOrCodeException() {
        var values = new HashMap<String, Object>();
        values.put("error", "invalid_grant");
        values.put("error_description", "Wrong email or verification code.");
        return new APIException(values, 403);
    }

    private TokenHolder getTokenResponse() {
        return new TokenHolder(ACCESS_TOKEN,
                ID_TOKEN,
                REFRESH_TOKEN,
                TOKEN_TYPE,
                EXPIRES_IN,
                SCOPE,
                Date.valueOf("2022-01-23"));
    }

}
