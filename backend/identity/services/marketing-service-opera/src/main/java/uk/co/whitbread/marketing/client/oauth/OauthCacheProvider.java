package uk.co.whitbread.marketing.client.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.marketing.properties.OauthProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class OauthCacheProvider {

    private final OauthFeignClient oauthFeignClient;
    private final OauthProperties oauthProperties;

    @Cacheable(cacheNames = "marketingToken", key = "'marketingToken'")
    public String getBearerToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oauthProperties.getClientId());
        requestBody.add("grant_type", oauthProperties.getGrantType());
        requestBody.add("client_secret", oauthProperties.getClientSecret());
        requestBody.add("scope", oauthProperties.getScope());
        final OauthResponse bearerToken = oauthFeignClient.getBearerToken(
                requestBody,
                oauthProperties.getTokenUrl()
        );
        log.info("New bearer token created");
        return bearerToken.getAccessToken();
    }

}
