package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OauthClientException;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhOauthResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhToken;

@ExtendWith(MockitoExtension.class)
class OAuthProviderTest {

  @InjectMocks
  private OAuthProvider oAuthProvider;

  @Mock
  private CdhOauthFeignClient cdhOAuthFeignClient;

  @Mock
  private OAuthProperties oAuthProperties;

  @Mock
  private CdhToken cdhToken;

  public void setoAuthProperties() {
    when(oAuthProperties.getTokenUrl()).thenReturn("https://tokenUrl");
    when(oAuthProperties.getClientId()).thenReturn("mockclientid");
    when((oAuthProperties.getClientSecret())).thenReturn("mockclientsecret");
    when(oAuthProperties.getGrantType()).thenReturn("mockgranttype");
    when((oAuthProperties.getScope())).thenReturn("mockscope");
  }

  @Test
  void verifyReturnSuccessAccessToken() {
    setoAuthProperties();
    when(cdhOAuthFeignClient.getAccessToken(getRequestMap(), "https://tokenUrl"))
        .thenReturn(CdhOauthResponseDto.builder().accessToken("12345").expiresIn(123).extExpiresIn(321).build());
    final String token = oAuthProvider.getNewBearerToken();
    Assert.assertEquals("12345", token);
  }

  @Test
  void shouldReturnBearerTokenWithError() {
    setoAuthProperties();
    final OauthClientException oauthClientException = new OauthClientException(
        ErrorCode.FEIGN_DECODER_EXCEPTION, "invalid_grant_type");

    when(cdhOAuthFeignClient.getAccessToken(getRequestMap(), "https://tokenUrl")).thenThrow(oauthClientException);
    assertThrows(OauthClientException.class, () -> oAuthProvider.getNewBearerToken());
  }

  @Test
  void testGetToken_WhenTokenValueIsNotNull() {
    // Arrange
    var expectedValue = "existingToken";
    when(cdhToken.getValue()).thenReturn(expectedValue);

    // Act
    var actualValue = oAuthProvider.getBearerToken();

    // Assert
    assertEquals(expectedValue, actualValue);
    verify(cdhToken, times(2)).getValue();
  }

  @Test
  void testGetToken_WhenTokenValueIsNull() {
    // Arrange
    when(cdhToken.getValue()).thenReturn(null);
    var spyOAuthProvider = spy(oAuthProvider);
    var newToken = "newToken";
    doReturn(newToken).when(spyOAuthProvider).getNewBearerToken();

    // Act
    var actualValue = spyOAuthProvider.getBearerToken();

    // Assert
    assertEquals(newToken, actualValue);
    verify(cdhToken).getValue();
    verify(spyOAuthProvider).getNewBearerToken();
  }

  public MultiValueMap<String, String> getRequestMap() {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add("client_id", oAuthProperties.getClientId());
    request.add("grant_type", oAuthProperties.getGrantType());
    request.add("client_secret", oAuthProperties.getClientSecret());
    request.add("scope", oAuthProperties.getScope());
    return request;
  }
}
