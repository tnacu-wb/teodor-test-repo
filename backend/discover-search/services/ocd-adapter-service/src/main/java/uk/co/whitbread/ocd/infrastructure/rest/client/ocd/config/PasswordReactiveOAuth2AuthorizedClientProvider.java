package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class PasswordReactiveOAuth2AuthorizedClientProvider
    implements ReactiveOAuth2AuthorizedClientProvider {

  private static final String USERNAME_ATTRIBUTE = "username";
  private static final String PASSWORD_ATTRIBUTE = "password";

  @Setter
  private WebClient webClient = WebClient.builder().build();
  @Setter
  private Duration clockSkew = Duration.ofSeconds(60);
  private final Clock clock = Clock.systemUTC();

  @Override
  public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
    ClientRegistration registration = context.getClientRegistration();
    OAuth2AuthorizedClient existing = context.getAuthorizedClient();

    if (existing != null && !hasTokenExpired(existing.getAccessToken())) {
      return Mono.empty();
    }

    String username = context.getAttribute(USERNAME_ATTRIBUTE);
    String password = context.getAttribute(PASSWORD_ATTRIBUTE);

    return Mono.defer(() -> requestAccessToken(registration, username, password))
        .map(tokenResponse -> new OAuth2AuthorizedClient(
            registration,
            context.getPrincipal().getName(),
            tokenResponse.getAccessToken()));
  }

  private boolean hasTokenExpired(OAuth2AccessToken token) {
    Instant expiresAt = token.getExpiresAt();
    if (expiresAt == null) {
      return true;
    }
    return clock.instant().isAfter(expiresAt.minus(clockSkew));
  }

  @SuppressWarnings("unchecked")
  private Mono<OAuth2AccessTokenResponse> requestAccessToken(
      ClientRegistration registration, String username, String password) {

    return webClient.post()
        .uri(registration.getProviderDetails().getTokenUri())
        .headers(headers -> {
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
          headers.setBasicAuth(registration.getClientId(), registration.getClientSecret());
        })
        .body(BodyInserters
            .fromFormData("grant_type", PASSWORD_ATTRIBUTE)
            .with(USERNAME_ATTRIBUTE, username)
            .with(PASSWORD_ATTRIBUTE, password))
        .exchangeToMono(response -> response.bodyToMono(Map.class))
        .map(body -> {
          String accessToken = (String) body.get("access_token");
          Number expiresIn = (Number) body.getOrDefault("expires_in", 3600);
          Set<String> scopes = body.containsKey("scope")
              ? Set.of(((String) body.get("scope")).split(" "))
              : Collections.emptySet();

          return OAuth2AccessTokenResponse.withToken(accessToken)
              .tokenType(OAuth2AccessToken.TokenType.BEARER)
              .expiresIn(expiresIn.longValue())
              .scopes(scopes)
              .build();
        });
  }
}
