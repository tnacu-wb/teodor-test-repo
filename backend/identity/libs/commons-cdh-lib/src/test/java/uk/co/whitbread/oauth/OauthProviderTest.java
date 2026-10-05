package uk.co.whitbread.oauth;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.shared.cdh.exception.OauthClientException;
import uk.co.whitbread.shared.cdh.oauth.OAuthFeignClient;
import uk.co.whitbread.shared.cdh.oauth.OAuthProperties;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;
import uk.co.whitbread.shared.cdh.oauth.OAuthResponse;
import uk.co.whitbread.shared.cdh.oauth.token.CdhToken;

@ExtendWith(MockitoExtension.class)
public class OauthProviderTest {

    @Mock
    private OAuthFeignClient oauthFeignClient;

    @Mock
    private OAuthProperties oauthProperties;

    @Mock
    private CdhToken cdhToken;

    @InjectMocks
    private OAuthProvider target;

    @BeforeEach
    public void setup() {
        when(oauthProperties.getTokenUrl()).thenReturn("http://someurl");
        when(oauthProperties.getClientId()).thenReturn("any_client_id");
        when(oauthProperties.getClientSecret()).thenReturn("any_client_secret");
        when(oauthProperties.getGrantType()).thenReturn("any_grant_type");
        when(oauthProperties.getScope()).thenReturn("any_scope");
    }

    @Test
    public void shouldReturnBearerTokenSuccessfully() {
        OAuthResponse response = new OAuthResponse();
        response.setAccessToken("12345");
        when(oauthFeignClient.getBearerToken(getMap(), "http://someurl"))
                .thenReturn(response);
        final String bearerToken = target.getBearerToken();
        assertEquals("12345", bearerToken);
    }

    @Test
    public void shouldReturnBearerTokenWithError() {
        final OauthClientException azureOauthException = new OauthClientException();
        azureOauthException.setErrorCode("3002");
        azureOauthException.setStatus(400);
        azureOauthException.setMessage("invalid_grant_type");
        when(oauthFeignClient.getBearerToken(getMap(), "http://someurl"))
                .thenThrow(azureOauthException);
        assertThrows(OauthClientException.class,
            () -> target.getBearerToken());
    }

    public MultiValueMap<String, String> getMap() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oauthProperties.getClientId());
        requestBody.add("grant_type", oauthProperties.getGrantType());
        requestBody.add("client_secret", oauthProperties.getClientSecret());
        requestBody.add("scope", oauthProperties.getScope());
        return requestBody;
    }

}