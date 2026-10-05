package uk.co.whitbread.infrastructure.rest.client.microsoftoauth;

import static uk.co.whitbread.infrastructure.config.WebClientBeanConfig.logRequest;
import static uk.co.whitbread.infrastructure.config.WebClientBeanConfig.logResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.infrastructure.config.Dynamics365Properties;
import uk.co.whitbread.infrastructure.config.OAuthProperties;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.model.MicrosoftOauthResponseDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthProvider {

  private WebClient microsoftWebClient;
  private final OAuthProperties properties;
  private final Dynamics365Properties dynamics365Properties;

  @PostConstruct
  private void init() {
    microsoftWebClient = WebClient.builder()
        .filter(logRequest())
        .filter(logResponse())
        .baseUrl(properties.getHost())
        .build();
  }

  @Cacheable(cacheNames = "microsoftToken")
  public String getBearerTokenNonCached() {
    return getBearerToken();
  }

  public String getBearerToken() {

    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
    request.add("client_id", properties.getClientId());
    request.add("grant_type", properties.getGrantType());
    request.add("client_secret", properties.getClientSecret());
    request.add("scope", properties.getScope());
    request.add("resource", dynamics365Properties.getHost());

    MicrosoftOauthResponseDto token = microsoftWebClient.post()
        .uri(properties.getTokenUrl())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(request))
        .retrieve()
        .bodyToMono(MicrosoftOauthResponseDto.class)
        .block();

    if (token != null) {
      log.info("New bearer token created successfully.");
      return token.getAccessToken();
    }
    return null;
  }

  @CacheEvict(value = "microsoftToken", allEntries = true, beforeInvocation = true)
  public void evictBearerTokenCache() {
    log.debug("Bearer token has been deleted from cache");
  }
}
