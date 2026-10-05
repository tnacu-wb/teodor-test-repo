package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;

@ExtendWith(MockitoExtension.class)
class WebClientConfigFeatureFlagTest {

  @Mock
  private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag featureFlag;

  @Mock
  private FeatureFlag.Feature useTokenServiceFeature;

  @Mock
  private OhipProperties ohipProperties;

  private WebClientConfig webClientConfig;
  private ServerOAuth2AuthorizedClientExchangeFilterFunction spyOAuth2Filter;

  @BeforeEach
  void setUp() {
    webClientConfig = new WebClientConfig(ohipProperties, JsonMapper.builder().build());
    when(ohipProperties.getHost()).thenReturn("http://localhost:8080");
    when(ohipProperties.getAppKey()).thenReturn("test-app-key");

    // Create spy for OAuth2 filter to verify registration ID setting
    spyOAuth2Filter = spy(
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager));

    // Avoid NPE inside OAuth2 filter: authorize must return a non-null Mono
    when(authorizedClientManager.authorize(any())).thenReturn(Mono.empty());
  }

  @Nested
  class FeatureFlagEnabledTests {

    @BeforeEach
    void setUpFeatureFlagEnabled() {
      // No stubbing needed; test only verifies that filters are registered
    }

    @Test
    void shouldSetTokenServiceRegistrationId_WhenFeatureFlagIsEnabled() {
      // Given
      when(featureFlag.getUseTokenService()).thenReturn(useTokenServiceFeature);
      when(unleashWrapper.isEnabled(useTokenServiceFeature)).thenReturn(true);

      // When
      WebClient wc = webClientConfig.ohipWebClient(
          authorizedClientManager,
          unleashWrapper,
          featureFlag,
          WebClient.builder(),
          reactor.netty.http.client.HttpClient.create()
      ).mutate().filter((req, next) ->
          // short-circuit network, we only need the OAuth filter to run
          reactor.core.publisher.Mono.just(ClientResponse.create(HttpStatus.OK).build())
      ).build();

      wc.get().uri("/test").retrieve().toBodilessEntity().block();

      // Then - capture authorize request and assert registration id
      ArgumentCaptor<OAuth2AuthorizeRequest> authCaptor = ArgumentCaptor.forClass(
          OAuth2AuthorizeRequest.class);
      verify(authorizedClientManager).authorize(authCaptor.capture());
      assertThat(authCaptor.getValue().getClientRegistrationId()).isEqualTo(
          OhipConstants.TOKEN_SERVICE_REGISTRATION_ID);
    }
  }

  @Nested
  class FeatureFlagDisabledTests {

    @BeforeEach
    void setUpFeatureFlagDisabled() {
      // No stubbing needed; test only verifies that filters are registered
    }

    @Test
    void shouldSetOperaRegistrationId_WhenFeatureFlagIsNull() {
      // Given - feature flag is null
      when(featureFlag.getUseTokenService()).thenReturn(null);

      // When
      WebClient wc = webClientConfig.ohipWebClient(
          authorizedClientManager,
          unleashWrapper,
          featureFlag,
          WebClient.builder(),
          reactor.netty.http.client.HttpClient.create()
      ).mutate().filter((req, next) ->
          reactor.core.publisher.Mono.just(ClientResponse.create(HttpStatus.OK).build())
      ).build();

      wc.get().uri("/test").retrieve().toBodilessEntity().block();

      // Then
      ArgumentCaptor<OAuth2AuthorizeRequest> authCaptor = ArgumentCaptor.forClass(
          OAuth2AuthorizeRequest.class);
      verify(authorizedClientManager).authorize(authCaptor.capture());
      assertThat(authCaptor.getValue().getClientRegistrationId()).isEqualTo(
          OhipConstants.REGISTRATION_ID);
    }

    @Test
    void shouldSetOperaRegistrationId_WhenUnleashWrapperReturnsFalse() {
      // Given - feature flag exists but Unleash returns false (flag disabled in Unleash)
      when(featureFlag.getUseTokenService()).thenReturn(useTokenServiceFeature);
      when(unleashWrapper.isEnabled(useTokenServiceFeature)).thenReturn(false);

      // When
      WebClient wc = webClientConfig.ohipWebClient(
          authorizedClientManager,
          unleashWrapper,
          featureFlag,
          WebClient.builder(),
          reactor.netty.http.client.HttpClient.create()
      ).mutate().filter((req, next) ->
          reactor.core.publisher.Mono.just(ClientResponse.create(HttpStatus.OK).build())
      ).build();

      wc.get().uri("/test").retrieve().toBodilessEntity().block();

      // Then
      ArgumentCaptor<OAuth2AuthorizeRequest> authCaptor = ArgumentCaptor.forClass(
          OAuth2AuthorizeRequest.class);
      verify(authorizedClientManager).authorize(authCaptor.capture());
      assertThat(authCaptor.getValue().getClientRegistrationId()).isEqualTo(
          OhipConstants.REGISTRATION_ID);
    }
  }
}





