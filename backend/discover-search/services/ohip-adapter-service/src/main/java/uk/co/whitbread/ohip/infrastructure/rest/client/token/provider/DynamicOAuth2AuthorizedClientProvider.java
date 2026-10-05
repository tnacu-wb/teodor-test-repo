package uk.co.whitbread.ohip.infrastructure.rest.client.token.provider;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TOKEN_SERVICE_REGISTRATION_ID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import reactor.core.publisher.Mono;

/**
 * Dynamic OAuth2 client provider that routes requests to appropriate provider based on registration
 * ID. This allows Spring OAuth2 to handle caching automatically for both token sources.
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicOAuth2AuthorizedClientProvider implements
    ReactiveOAuth2AuthorizedClientProvider {

  private final ReactiveOAuth2AuthorizedClientProvider tokenServiceProvider;
  private final ReactiveOAuth2AuthorizedClientProvider operaProvider;

  @Override
  public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
    // Check which registration ID is being used
    String registrationId = context.getClientRegistration().getRegistrationId();

    if (TOKEN_SERVICE_REGISTRATION_ID.equals(registrationId)) {
      log.debug("Using token service provider for registration: {}", registrationId);
      return tokenServiceProvider.authorize(context);
    } else {
      log.debug("Using Opera provider for registration: {}", registrationId);
      return operaProvider.authorize(context);
    }
  }
}
