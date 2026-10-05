package uk.co.whitbread.marketing.client.oauth;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.marketing.exception.OauthClientException;
import uk.co.whitbread.marketing.properties.OauthProperties;

@ExtendWith(MockitoExtension.class)
public class OauthCacheProviderTest {

    @Mock
    private OauthFeignClient oauthFeignClient;

    @Mock
    private OauthProperties oauthProperties;

    @InjectMocks
    private OauthCacheProvider target;

    @BeforeEach
    public void setup() {
        Mockito.when(oauthProperties.getTokenUrl()).thenReturn("http://someurl");
        Mockito.when(oauthProperties.getClientId()).thenReturn("any_client_id");
        Mockito.when(oauthProperties.getClientSecret()).thenReturn("any_client_secret");
        Mockito.when(oauthProperties.getGrantType()).thenReturn("any_grant_type");
        Mockito.when(oauthProperties.getScope()).thenReturn("any_scope");
    }

    @Test
    public void shouldReturnBearerTokenSuccessfully() {
        OauthResponse response = new OauthResponse();
        response.setAccessToken("12345");
        Mockito.when(oauthFeignClient.getBearerToken(getMap(),"http://someurl"))
            .thenReturn(response);
        final String bearerToken = target.getBearerToken();
        Assertions.assertThat(bearerToken).isEqualTo("12345");
    }

    @Test
    public void shouldReturnBearerTokenWithError() {
        final var azureOauthException = new OauthClientException();
        azureOauthException.setErrorCode("3002");
        azureOauthException.setStatus(400);
        azureOauthException.setMessage("invalid_grant_type");
        Mockito.when(oauthFeignClient.getBearerToken(getMap(),"http://someurl"))
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