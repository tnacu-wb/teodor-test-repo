package uk.co.whitbread.shared.cdh.oauth;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "oauthclient", url = "${cdh.oauth-client.host}",
        fallbackFactory = OAuthFeignClientFallbackFactory.class, configuration = OAuthServiceErrorDecoder.class)
@ConditionalOnProperty(value = "cdh.oauth-client.enabled", havingValue = "true")
public interface OAuthFeignClient {
    @PostMapping(produces = APPLICATION_FORM_URLENCODED_VALUE, value = "{token-url}")
    OAuthResponse getBearerToken(@RequestBody MultiValueMap<String, String> request, @PathVariable("token-url") String tokenUrl);
}