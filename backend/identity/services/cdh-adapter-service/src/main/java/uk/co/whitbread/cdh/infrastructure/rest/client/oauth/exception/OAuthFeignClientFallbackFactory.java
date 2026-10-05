package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.CdhOauthFeignClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhOauthResponseDto;

@Slf4j
@Component
public class OAuthFeignClientFallbackFactory implements FallbackFactory<CdhOauthFeignClient> {

  @Override
  public CdhOauthFeignClient create(Throwable throwable) {
    return (request, tokenUrl) -> {
      if (throwable instanceof HttpStatusCodeException) {
        HttpStatusCodeException e = (HttpStatusCodeException) throwable;
        log.error("Failed to call Oauth {}", e.getResponseBodyAsString());
        throw e;
      } else if (throwable instanceof OauthClientException) {
        throw (OauthClientException) throwable;
      }
      log.error("Circuit breaker is probably tripped {}", throwable.getMessage(), throwable);
      return new CdhOauthResponseDto();
    };

  }
}
