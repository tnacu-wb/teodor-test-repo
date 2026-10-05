package uk.co.whitbread.ohip.infrastructure.rest.client.token.provider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.REGISTRATION_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TOKEN_SERVICE_REGISTRATION_ID;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DynamicOAuth2AuthorizedClientProviderTest {

  @Mock
  private ReactiveOAuth2AuthorizedClientProvider tokenServiceProvider;

  @Mock
  private ReactiveOAuth2AuthorizedClientProvider operaProvider;

  @Mock
  private OAuth2AuthorizationContext context;

  private DynamicOAuth2AuthorizedClientProvider dynamicProvider;

  @BeforeEach
  void setUp() {
    dynamicProvider = new DynamicOAuth2AuthorizedClientProvider(
        tokenServiceProvider,
        operaProvider
    );
  }

  @Test
  void shouldRouteToTokenServiceProvider_WhenRegistrationIdIsTokenService() {
    // Given
    ClientRegistration tokenServiceRegistration = ClientRegistration
        .withRegistrationId(TOKEN_SERVICE_REGISTRATION_ID)
        .tokenUri("http://token-service/token")
        .clientId("token-service-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    OAuth2AccessToken accessToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "token",
        Instant.now().minusSeconds(60),
        Instant.now().plusSeconds(3600)
    );
    OAuth2AuthorizedClient expectedClient = new OAuth2AuthorizedClient(
        tokenServiceRegistration,
        "test-principal",
        accessToken,
        null
    );

    // When
    when(context.getClientRegistration()).thenReturn(tokenServiceRegistration);
    when(tokenServiceProvider.authorize(context)).thenReturn(Mono.just(expectedClient));

    Mono<OAuth2AuthorizedClient> result = dynamicProvider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectNext(expectedClient)
        .verifyComplete();

    verify(tokenServiceProvider).authorize(context);
    verify(operaProvider, never()).authorize(any());
  }

  @Test
  void shouldRouteToOperaProvider_WhenRegistrationIdIsOpera() {
    // Given
    ClientRegistration operaRegistration = ClientRegistration
        .withRegistrationId(REGISTRATION_ID)
        .tokenUri("http://opera/token")
        .clientId("opera-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    OAuth2AccessToken accessToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "token",
        Instant.now().minusSeconds(60),
        Instant.now().plusSeconds(3600)
    );
    OAuth2AuthorizedClient expectedClient = new OAuth2AuthorizedClient(
        operaRegistration,
        "test-principal",
        accessToken,
        null
    );

    // When
    when(context.getClientRegistration()).thenReturn(operaRegistration);
    when(operaProvider.authorize(context)).thenReturn(Mono.just(expectedClient));

    Mono<OAuth2AuthorizedClient> result = dynamicProvider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectNext(expectedClient)
        .verifyComplete();

    verify(operaProvider).authorize(context);
    verify(tokenServiceProvider, never()).authorize(any());
  }

  @Test
  void shouldRouteToOperaProvider_WhenRegistrationIdIsUnknown() {
    // Given
    ClientRegistration unknownRegistration = ClientRegistration
        .withRegistrationId("unknown-registration")
        .tokenUri("http://unknown/token")
        .clientId("unknown-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    OAuth2AccessToken accessToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "token",
        Instant.now().minusSeconds(60),
        Instant.now().plusSeconds(3600)
    );
    OAuth2AuthorizedClient expectedClient = new OAuth2AuthorizedClient(
        unknownRegistration,
        "test-principal",
        accessToken,
        null
    );

    // When
    when(context.getClientRegistration()).thenReturn(unknownRegistration);
    when(operaProvider.authorize(context)).thenReturn(Mono.just(expectedClient));

    Mono<OAuth2AuthorizedClient> result = dynamicProvider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectNext(expectedClient)
        .verifyComplete();

    verify(operaProvider).authorize(context);
    verify(tokenServiceProvider, never()).authorize(any());
  }

  @Test
  void shouldPropagateError_WhenTokenServiceProviderFails() {
    // Given
    ClientRegistration tokenServiceRegistration = ClientRegistration
        .withRegistrationId(TOKEN_SERVICE_REGISTRATION_ID)
        .tokenUri("http://token-service/token")
        .clientId("token-service-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    RuntimeException error = new RuntimeException("Token service error");

    // When
    when(context.getClientRegistration()).thenReturn(tokenServiceRegistration);
    when(tokenServiceProvider.authorize(context)).thenReturn(Mono.error(error));

    Mono<OAuth2AuthorizedClient> result = dynamicProvider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(tokenServiceProvider).authorize(context);
  }

  @Test
  void shouldPropagateError_WhenOperaProviderFails() {
    // Given
    ClientRegistration operaRegistration = ClientRegistration
        .withRegistrationId(REGISTRATION_ID)
        .tokenUri("http://opera/token")
        .clientId("opera-client")
        .clientSecret("secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();

    RuntimeException error = new RuntimeException("Opera error");

    // When
    when(context.getClientRegistration()).thenReturn(operaRegistration);
    when(operaProvider.authorize(context)).thenReturn(Mono.error(error));

    Mono<OAuth2AuthorizedClient> result = dynamicProvider.authorize(context);

    // Then
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(operaProvider).authorize(context);
  }
}
