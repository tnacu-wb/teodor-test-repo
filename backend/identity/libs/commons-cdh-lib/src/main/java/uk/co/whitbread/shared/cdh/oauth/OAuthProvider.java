package uk.co.whitbread.shared.cdh.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.shared.cdh.oauth.token.CdhToken;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthProvider {
    private final OAuthFeignClient oauthFeignClient;
    private final OAuthProperties oauthProperties;
    private final CdhToken cdhToken;

    public String getBearerToken() {
        return cdhToken.getValue() != null ? cdhToken.getValue() : getNewBearerToken();
    }

    public String getNewBearerToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oauthProperties.getClientId());
        requestBody.add("grant_type", oauthProperties.getGrantType());
        requestBody.add("client_secret", oauthProperties.getClientSecret());
        requestBody.add("scope", oauthProperties.getScope());

        final OAuthResponse bearerToken = oauthFeignClient.getBearerToken(requestBody, oauthProperties.getTokenUrl());
        log.info("New bearer token created");

        cdhToken.setValue(bearerToken.getAccessToken());

        return bearerToken.getAccessToken();
    }
}
