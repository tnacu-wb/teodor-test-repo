package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhOauthResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhToken;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthProvider {

  private final OAuthProperties properties;
  private final CdhOauthFeignClient cdhOAuthFeignClient;
  private final CdhToken cdhToken;

  public String getBearerToken() {
    return cdhToken.getValue() != null ? cdhToken.getValue() : getNewBearerToken();
  }

  public String getNewBearerToken() {

    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add("client_id", properties.getClientId());
    request.add("grant_type", properties.getGrantType());
    request.add("client_secret", properties.getClientSecret());
    request.add("scope", properties.getScope());

    CdhOauthResponseDto token = cdhOAuthFeignClient.getAccessToken(request, properties.getTokenUrl());
    log.info("New bearer token created");

    cdhToken.setValue(token.getAccessToken());

    return token.getAccessToken();
  }
}
