package uk.co.whitbread.ohip.infrastructure.rest.client.token.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TOKEN_SERVICE_REGISTRATION_ID;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.TokenServiceClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto.TokenResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenServiceOAuth2AuthorizedClientProviderTest {

  @Mock
  private TokenServiceClient tokenServiceClient;

  @Mock
  private ReactiveClientRegistrationRepository clientRegistrationRepository;

  @Mock
  private OAuth2AuthorizationContext context;

  private TokenServiceOAuth2AuthorizedClientProvider provider;
  private ClientRegistration tokenServiceRegistration;

  @BeforeEach
  void setUp() {
    provider = new TokenServiceOAuth2AuthorizedClientProvider(
        tokenServiceClient,
        clientRegistrationRepository
    );

    tokenServiceRegistration = ClientRegistration
        .withRegistrationId(TOKEN_SERVICE_REGISTRATION_ID)
        .tokenUri("http://token-service/token")
        .clientId("token-service-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    when(context.getClientRegistration()).thenReturn(tokenServiceRegistration);
    Mockito.lenient()
        .when(clientRegistrationRepository.findByRegistrationId(TOKEN_SERVICE_REGISTRATION_ID))
        .thenReturn(Mono.just(tokenServiceRegistration));
  }

  @Test
  void shouldFetchToken_WhenNoCachedTokenExists() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("new-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt(Instant.now().toString());

    // When
    when(context.getAuthorizedClient()).thenReturn(null);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(tokenResponse));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient).isNotNull();
          assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo("new-token");
          assertThat(authorizedClient.getClientRegistration()).isEqualTo(tokenServiceRegistration);
          assertThat(authorizedClient.getPrincipalName()).isEqualTo("token-service");
        })
        .verifyComplete();

    verify(tokenServiceClient).getOperaAccessTokenResponse();
  }

  @Test
  void shouldUseCachedToken_WhenTokenIsStillValid() {
    // Given
    Instant expiresAt = Instant.now().plusSeconds(3600); // Valid for 1 hour
    OAuth2AccessToken cachedToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "cached-token",
        Instant.now().minusSeconds(1800), // Issued 30 min ago
        expiresAt
    );

    OAuth2AuthorizedClient cachedClient = new OAuth2AuthorizedClient(
        tokenServiceRegistration,
        "token-service",
        cachedToken,
        null
    );

    // When
    when(context.getAuthorizedClient()).thenReturn(cachedClient);

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient).isSameAs(cachedClient);
          assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo("cached-token");
        })
        .verifyComplete();

    // Verify token service was NOT called (should use cached token)
    verify(tokenServiceClient, never()).getOperaAccessTokenResponse();
  }

  @Test
  void shouldRefreshToken_WhenTokenIsExpired() {
    // Given
    Instant expiredAt = Instant.now().minusSeconds(60); // Expired 1 min ago
    OAuth2AccessToken expiredToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "expired-token",
        Instant.now().minusSeconds(3700), // Issued 1 hour + 2 min ago
        expiredAt
    );

    OAuth2AuthorizedClient expiredClient = new OAuth2AuthorizedClient(
        tokenServiceRegistration,
        "token-service",
        expiredToken,
        null
    );

    TokenResponse newTokenResponse = new TokenResponse();
    newTokenResponse.setAccessToken("refreshed-token");
    newTokenResponse.setTokenType("Bearer");
    newTokenResponse.setExpiresIn(3600);
    newTokenResponse.setIssuedAt(Instant.now().toString());

    // When
    when(context.getAuthorizedClient()).thenReturn(expiredClient);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(newTokenResponse));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient).isNotNull();
          assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo(
              "refreshed-token");
          assertThat(authorizedClient.getAccessToken().getTokenValue()).isNotEqualTo(
              "expired-token");
        })
        .verifyComplete();

    verify(tokenServiceClient).getOperaAccessTokenResponse();
  }

  @Test
  void shouldRefreshToken_WhenTokenHasNoExpiration() {
    // Given
    OAuth2AccessToken tokenWithoutExpiration = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "token-no-expiration",
        Instant.now(),
        null // No expiration
    );

    OAuth2AuthorizedClient clientWithoutExpiration = new OAuth2AuthorizedClient(
        tokenServiceRegistration,
        "token-service",
        tokenWithoutExpiration,
        null
    );

    TokenResponse newTokenResponse = new TokenResponse();
    newTokenResponse.setAccessToken("refreshed-token");
    newTokenResponse.setTokenType("Bearer");
    newTokenResponse.setExpiresIn(3600);
    newTokenResponse.setIssuedAt(Instant.now().toString());

    // When
    when(context.getAuthorizedClient()).thenReturn(clientWithoutExpiration);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(newTokenResponse));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo(
              "refreshed-token");
        })
        .verifyComplete();

    verify(tokenServiceClient).getOperaAccessTokenResponse();
  }

  @Test
  void shouldHandleError_WhenTokenServiceClientFails() {
    // Given
    RuntimeException error = new RuntimeException("Token service error");

    // When
    when(context.getAuthorizedClient()).thenReturn(null);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.error(error));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(tokenServiceClient).getOperaAccessTokenResponse();
  }

  @Test
  void shouldParseBearerTokenType_WhenTokenTypeIsBearer() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("test-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt(Instant.now().toString());

    // When
    when(context.getAuthorizedClient()).thenReturn(null);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(tokenResponse));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient.getAccessToken().getTokenType())
              .isEqualTo(OAuth2AccessToken.TokenType.BEARER);
        })
        .verifyComplete();
  }

  @Test
  void shouldDefaultToBearer_WhenTokenTypeIsMissing() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("test-token");
    tokenResponse.setTokenType(null); // Missing token type
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt(Instant.now().toString());

    // When
    when(context.getAuthorizedClient()).thenReturn(null);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(tokenResponse));

    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    StepVerifier.create(result)
        .assertNext(authorizedClient -> {
          assertThat(authorizedClient.getAccessToken().getTokenType())
              .isEqualTo(OAuth2AccessToken.TokenType.BEARER);
        })
        .verifyComplete();
  }

  @Test
  void shouldThrowWhenUnsupportedTokenTypeProvided() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("test-token");
    tokenResponse.setTokenType("UNKNOWN_TYPE"); // unsupported
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt(Instant.now().toString());

    when(context.getAuthorizedClient()).thenReturn(null);
    when(tokenServiceClient.getOperaAccessTokenResponse())
        .thenReturn(Mono.just(tokenResponse));

    // When
    Mono<OAuth2AuthorizedClient> result = provider.authorize(context);

    // Then
    reactor.test.StepVerifier.create(result)
        .expectError(IllegalArgumentException.class)
        .verify();
  }
}
