package uk.co.whitbread.marketing.client.oauth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.marketing.config.OauthFeignClientFallbackFactory;
import uk.co.whitbread.marketing.config.OauthServiceErrorDecoder;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(name = "oauthclient", url = "${azure.oauth-client.host}",
        fallbackFactory = OauthFeignClientFallbackFactory.class, configuration = OauthServiceErrorDecoder.class)
public interface OauthFeignClient {
    @PostMapping(produces = APPLICATION_FORM_URLENCODED_VALUE, value = "{token-url}")
    OauthResponse getBearerToken(@RequestBody MultiValueMap<String, String> request,
                                 @PathVariable("token-url") String tokenUrl);
}