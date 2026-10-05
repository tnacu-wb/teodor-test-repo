package uk.co.whitbread.ohip.infrastructure.rest.client.token.provider;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TOKEN_SERVICE_REGISTRATION_ID;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.TokenServiceClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto.TokenResponse;

/**
 * Custom OAuth2 client provider for token service integration. This provider handles the
 * non-standard token service flow and integrates with Spring Security's OAuth2 caching
 * infrastructure.
 */
@Slf4j
public class TokenServiceOAuth2AuthorizedClientProvider implements
    ReactiveOAuth2AuthorizedClientProvider {

  public static final String TOKEN_TYPE_BEARER = "BEARER";
  private final TokenServiceClient tokenServiceClient;
  private final ReactiveClientRegistrationRepository clientRegistrationRepository;

  public TokenServiceOAuth2AuthorizedClientProvider(
      TokenServiceClient tokenServiceClient,
      ReactiveClientRegistrationRepository clientRegistrationRepository) {
    this.tokenServiceClient = tokenServiceClient;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Override
  public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
    log.debug("Authorizing with token service for client: {}",
        context.getClientRegistration().getRegistrationId());

    // Check if there's an existing authorized client and if it's still valid
    OAuth2AuthorizedClient authorizedClient = context.getAuthorizedClient();
    if (authorizedClient != null && !shouldRefresh(authorizedClient.getAccessToken())) {
      log.debug("Using cached token from token service (expires at: {})",
          authorizedClient.getAccessToken().getExpiresAt());
      return Mono.just(authorizedClient);
    }

    if (authorizedClient != null) {
      log.debug("Refreshing token from token service (expired at: {})",
          authorizedClient.getAccessToken().getExpiresAt());
    } else {
      log.debug("Fetching fresh token from token service");
    }

    return tokenServiceClient.getOperaAccessTokenResponse()
        .flatMap(this::createOauth2AuthorizedClient)
        .doOnSuccess(client -> log.debug("Successfully authorized with token service"))
        .doOnError(exception -> log.error("Failed to authorize with token service", exception));
  }

  private boolean shouldRefresh(OAuth2AccessToken accessToken) {
    if (accessToken.getExpiresAt() == null) {
      log.debug("Token has no expiration time, refreshing");
      return true;
    }

    Instant now = Instant.now();
    boolean isExpired = now.isAfter(accessToken.getExpiresAt());

    if (isExpired) {
      log.debug("Token expired at {}, current time is {} - refreshing",
          accessToken.getExpiresAt(), now);
    } else {
      log.debug("Token expires at {}, current time is {} - using cached token",
          accessToken.getExpiresAt(), now);
    }

    return isExpired;
  }

  private Mono<OAuth2AuthorizedClient> createOauth2AuthorizedClient(TokenResponse tokenResponse) {
    // Get the token service client registration
    return clientRegistrationRepository
        .findByRegistrationId(TOKEN_SERVICE_REGISTRATION_ID)
        .map(clientRegistration -> {
          validateTokenType(tokenResponse.getTokenType());
          // Create OAuth2AccessToken with proper expiration and token type from response
          OAuth2AccessToken accessToken = new OAuth2AccessToken(
              OAuth2AccessToken.TokenType.BEARER,
              tokenResponse.getAccessToken(),
              Instant.parse(tokenResponse.getIssuedAt()),
              tokenResponse.getExpirationTime()
          );

          // Create OAuth2AuthorizedClient with valid client registration
          return new OAuth2AuthorizedClient(
              clientRegistration, // valid registration
              "token-service", // principal name
              accessToken,
              null // refreshToken - not provided by token service
          );
        });
  }

  private void validateTokenType(String tokenType) {
    if (tokenType == null || tokenType.trim().isEmpty()) {
      log.debug("No token type provided, defaulting to BEARER");
      return;
    }

    String normalizedType = tokenType.trim().toUpperCase();
    if (!TOKEN_TYPE_BEARER.equals(normalizedType)) {
      // Spring OAuth2 only supports BEARER tokens
      log.error(
          "Token service returned unsupported token type. Spring OAuth2 only supports BEARER tokens.");
      throw new IllegalArgumentException(
          "Unsupported token type: " + tokenType + ". Only BEARER tokens are supported.");
    }
  }
}
