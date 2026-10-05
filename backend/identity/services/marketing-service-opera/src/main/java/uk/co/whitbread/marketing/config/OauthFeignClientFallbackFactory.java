package uk.co.whitbread.marketing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import uk.co.whitbread.marketing.client.oauth.OauthFeignClient;
import uk.co.whitbread.marketing.client.oauth.OauthResponse;
import uk.co.whitbread.marketing.exception.OauthClientException;

@Slf4j
@Component
public class OauthFeignClientFallbackFactory implements FallbackFactory<OauthFeignClient> {

    @Override
    public OauthFeignClient create(Throwable throwable) {
        return (request, tokenUrl) -> {
            if (throwable instanceof HttpStatusCodeException) {
                HttpStatusCodeException e = (HttpStatusCodeException) throwable;
                log.error("Failed to call Azure Oauth2 {}", e.getResponseBodyAsString());
                throw e;
            } else if (throwable instanceof OauthClientException) {
                throw (OauthClientException) throwable;
            }
            log.error("Circuit breaker is probably tripped {}", throwable.getMessage(), throwable);
            return new OauthResponse();
        };

    }
}

