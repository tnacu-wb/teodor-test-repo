package uk.co.whitbread.token.infrastructure.rest.client.token.ohip;

import static uk.co.whitbread.token.infrastructure.util.LoggingUtils.sanitizeLogging;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import uk.co.whitbread.token.ErrorCode;
import uk.co.whitbread.token.domain.model.out.TokenResponse;
import uk.co.whitbread.token.infrastructure.rest.client.token.exception.TokenServiceException;

@Slf4j
@Component
public class OhipTokenClient {

  public static final String PRINCIPAL_NAME_SUFFIX = "-service-principal";
  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final Duration refreshAhead;

  // Prevent thundering-herd refresh within a single pod
  private final Map<String, ReentrantLock> refreshLocks = new ConcurrentHashMap<>();

  public OhipTokenClient(
      OAuth2AuthorizedClientManager authorizedClientManager,
      OAuth2AuthorizedClientService authorizedClientService,
      @Value("${app.token.refreshAheadMinutes}") Duration refreshAhead) {
    this.authorizedClientManager = authorizedClientManager;
    this.authorizedClientService = authorizedClientService;
    this.refreshAhead = refreshAhead;
  }

  public TokenResponse getAccessToken(String providerId) {
    String principalName = getPrincipalName(providerId);

    OAuth2AuthorizedClient existing = authorizedClientService.loadAuthorizedClient(providerId,
        principalName);
    if (existing == null) {
      log.debug("No cached token for provider {}, authorizing...", sanitizeLogging(providerId));
      return authorizeAndReturn(providerId, principalName);
    }

    if (isExpiringSoon(existing.getAccessToken())) {
      // serialize refresh to avoid multiple simultaneous refresh attempts
      ReentrantLock lock = refreshLocks.computeIfAbsent(providerId, k -> new ReentrantLock());
      lock.lock();
      try {
        // double-check after acquiring the lock
        OAuth2AuthorizedClient latest = authorizedClientService.loadAuthorizedClient(providerId,
            principalName);
        if (latest == null || isExpiringSoon(latest.getAccessToken())) {
          log.info("Refreshing token for {} (expires at {})", sanitizeLogging(providerId),
              latest != null ? latest.getAccessToken().getExpiresAt() : null);
          // Force Spring Security to fetch a new token by clearing the cached one.
          authorizedClientService.removeAuthorizedClient(providerId, principalName);
          return authorizeAndReturn(providerId, principalName);
        } else {
          return toResponse(Objects.requireNonNull(latest.getAccessToken()));
        }
      } finally {
        lock.unlock();
      }
    }

    return toResponse(Objects.requireNonNull(existing.getAccessToken()));
  }

  private static String getPrincipalName(String providerId) {
    return providerId + PRINCIPAL_NAME_SUFFIX; // fixed principal per client
  }

  private TokenResponse toResponse(OAuth2AccessToken token) {
    return TokenResponse.builder()
        .accessToken(token.getTokenValue())
        .tokenType(token.getTokenType().getValue())
        .expiresIn((int) Duration.between(
            Instant.now(),
            Objects.requireNonNull(token.getExpiresAt())
        ).getSeconds())
        .issuedAt(String.valueOf(token.getIssuedAt()))
        .build();
  }

  private boolean isExpiringSoon(OAuth2AccessToken token) {
    Instant expiresAt = token.getExpiresAt();
    if (expiresAt == null) {
      // defensive: treat unknown expiry as expiring
      return true;
    }
    Instant now = Instant.now();
    return now.plus(refreshAhead).isAfter(expiresAt);
  }

  private TokenResponse authorizeAndReturn(String registrationId, String principalName) {
    OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
        .withClientRegistrationId(registrationId)
        .principal(principalName)
        .build();

    OAuth2AuthorizedClient authorized = authorizedClientManager.authorize(request);

    if (authorized == null || authorized.getAccessToken() == null) {
      throw new TokenServiceException(ErrorCode.TOKEN_UNABLE_TO_ACQUIRE_TOKEN_EXCEPTION,
          "Unable to acquire access token for " + registrationId);
    }

    return toResponse(authorized.getAccessToken());
  }
}
