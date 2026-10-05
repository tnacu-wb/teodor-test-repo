package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OAuthFeignClientFallbackFactory;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OAuthServiceErrorDecoder;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhOauthResponseDto;

@FeignClient(name = "cdhaoauthclient", url = "${cdh.oauth-client.host}",
        fallbackFactory = OAuthFeignClientFallbackFactory.class, configuration = OAuthServiceErrorDecoder.class)
public interface CdhOauthFeignClient {

  @PostMapping(produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE, value = "{token-url}")
  CdhOauthResponseDto getAccessToken(@RequestBody MultiValueMap<String, String> request,
                                     @PathVariable("token-url") String token);
}
