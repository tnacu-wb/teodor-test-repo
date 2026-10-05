package uk.co.whitbread.token.infrastructure.rest.client.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import uk.co.whitbread.token.infrastructure.rest.client.token.exception.TokenServiceException;
import uk.co.whitbread.token.infrastructure.rest.client.token.ohip.OhipTokenClient;

@ExtendWith(MockitoExtension.class)
class OhipTokenClientTest {

  private static final String PROVIDER_ID = "ohip";
  private static final String PRINCIPAL = PROVIDER_ID + OhipTokenClient.PRINCIPAL_NAME_SUFFIX;

  @Mock
  private OAuth2AuthorizedClientManager authorizedClientManager;
  @Mock
  private OAuth2AuthorizedClientService authorizedClientService;

  @InjectMocks
  private OhipTokenClient ohipTokenClient;

  @BeforeEach
  void setUp() {
    // Reconstruct the client with a deterministic refreshAhead for tests
    ohipTokenClient = new OhipTokenClient(
        authorizedClientManager,
        authorizedClientService,
        Duration.ofMinutes(5)
    );
  }

  @Test
  void getAccessToken_shouldAuthorizeWhenNoCachedToken() {
    when(authorizedClientService.loadAuthorizedClient(PROVIDER_ID, PRINCIPAL)).thenReturn(null);

    OAuth2AccessToken token = createAccessToken("new-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(10)));
    OAuth2AuthorizedClient authorizedClient = createAuthorizedClient(PROVIDER_ID, token);
    when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);

    var response = ohipTokenClient.getAccessToken(PROVIDER_ID);

    assertNotNull(response);
    assertEquals("new-token", response.getAccessToken());
    assertEquals("Bearer", response.getTokenType());

    verify(authorizedClientService, times(1)).loadAuthorizedClient(PROVIDER_ID, PRINCIPAL);
    verify(authorizedClientManager, times(1)).authorize(any(OAuth2AuthorizeRequest.class));
  }

  @Test
  void getAccessToken_shouldReturnCachedWhenNotExpiringSoon() {
    OAuth2AccessToken token = createAccessToken("cached-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(10)));
    OAuth2AuthorizedClient existing = createAuthorizedClient(PROVIDER_ID, token);
    when(authorizedClientService.loadAuthorizedClient(PROVIDER_ID, PRINCIPAL)).thenReturn(existing);

    var response = ohipTokenClient.getAccessToken(PROVIDER_ID);

    assertNotNull(response);
    assertEquals("cached-token", response.getAccessToken());

    verify(authorizedClientManager, never()).authorize(any());
  }

  @Test
  void getAccessToken_shouldRefreshWhenExpiringSoon() {
    // existing token expires in 1 minute; refreshAhead is 5 minutes
    OAuth2AccessToken expiring = createAccessToken("expiring-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));
    OAuth2AuthorizedClient existing = createAuthorizedClient(PROVIDER_ID, expiring);
    when(authorizedClientService.loadAuthorizedClient(PROVIDER_ID, PRINCIPAL)).thenReturn(existing);

    OAuth2AccessToken refreshed = createAccessToken("refreshed-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(15)));
    OAuth2AuthorizedClient refreshedClient = createAuthorizedClient(PROVIDER_ID, refreshed);
    when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(refreshedClient);

    var response = ohipTokenClient.getAccessToken(PROVIDER_ID);

    assertNotNull(response);
    assertEquals("refreshed-token", response.getAccessToken());

    verify(authorizedClientManager, times(1)).authorize(any(OAuth2AuthorizeRequest.class));
    verify(authorizedClientService, times(1)).removeAuthorizedClient(PROVIDER_ID, PRINCIPAL);
  }

  @Test
  void getAccessToken_shouldThrowWhenAuthorizeReturnsNull() {
    when(authorizedClientService.loadAuthorizedClient(PROVIDER_ID, PRINCIPAL)).thenReturn(null);
    when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(null);

    assertThrows(TokenServiceException.class, () -> ohipTokenClient.getAccessToken(PROVIDER_ID));
  }

  @Test
  void getAccessToken_concurrentCalls_shouldOnlyAuthorizeOnceAndReturnRefreshedToken() throws Exception {
    // Arrange: initial expiring token forces refresh path
    OAuth2AccessToken expiring = createAccessToken("expiring-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(1)));
    OAuth2AuthorizedClient existing = createAuthorizedClient(PROVIDER_ID, expiring);

    OAuth2AccessToken refreshed = createAccessToken("refreshed-token", Instant.now(), Instant.now().plus(Duration.ofMinutes(30)));
    OAuth2AuthorizedClient refreshedClient = createAuthorizedClient(PROVIDER_ID, refreshed);
    AtomicBoolean tokenRefreshed = new AtomicBoolean(false);

    when(authorizedClientService.loadAuthorizedClient(PROVIDER_ID, PRINCIPAL))
      .thenAnswer(invocation -> tokenRefreshed.get() ? refreshedClient : existing);

    when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenAnswer(invocation -> {
      tokenRefreshed.set(true);
      return refreshedClient;
    });

    int threads = 8;
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<String>> futures = new ArrayList<>();

    for (int i = 0; i < threads; i++) {
      futures.add(pool.submit(() -> {
        start.await();
        return ohipTokenClient.getAccessToken(PROVIDER_ID).getAccessToken();
      }));
    }

    start.countDown();

    List<String> tokens = new ArrayList<>(threads);
    for (Future<String> f : futures) {
      tokens.add(f.get());
    }

    pool.shutdown();

    // Assert: authorization happened at most once, and all returned the refreshed token
    verify(authorizedClientManager, times(1)).authorize(any(OAuth2AuthorizeRequest.class));
    verify(authorizedClientService, times(1)).removeAuthorizedClient(PROVIDER_ID, PRINCIPAL);
    for (String t : tokens) {
      assertEquals("refreshed-token", t);
    }
  }

  private static OAuth2AccessToken createAccessToken(String value, Instant issuedAt, Instant expiresAt) {
    return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, value, issuedAt, expiresAt);
  }

  private static OAuth2AuthorizedClient createAuthorizedClient(String registrationId, OAuth2AccessToken token) {
    ClientRegistration registration = ClientRegistration
        .withRegistrationId(registrationId)
        .clientId("client-id")
        .clientSecret("client-secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .tokenUri("https://example.com/oauth/token")
        .build();
    return new OAuth2AuthorizedClient(registration, PRINCIPAL, token);
  }
}
