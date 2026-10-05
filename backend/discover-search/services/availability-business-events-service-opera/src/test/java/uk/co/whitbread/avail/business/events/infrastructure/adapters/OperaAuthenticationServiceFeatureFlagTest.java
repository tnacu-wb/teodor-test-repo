package uk.co.whitbread.avail.business.events.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.avail.business.events.infrastructure.client.OperaRestClient;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties.ServiceUrl;
import uk.co.whitbread.avail.business.events.infrastructure.model.OauthTokenResponse;

@ExtendWith(MockitoExtension.class)
class OperaAuthenticationServiceFeatureFlagTest {

  private static final String USERNAME = "test_username";
  private static final String PASSWORD = "test_password";
  private static final String GRANT_TYPE = "test_grant_type";
  private static final String OCIM_GRANT_TYPE = "client_credentials";
  private static final String OCIM_SCOPE = "test_scope";
  private static final String DUMMY_API_KEY = "488c82b8-50fe-4cc1-ae82-4b7aa470bf58";
  private static final String ENTERPRISE_ID = "WHBOC001";
  private static final String EXPECTED_TOKEN_FROM_OPERA
      = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0"
      + ".eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNjYxNDI1NDY2fQ"
      + ".dGVzdC1zaWduYXR1cmU";

  @Mock
  private OperaProperties operaProperties;

  @Mock
  private OperaRestClient operaRestClient;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag featureFlag;

  @Mock
  private FeatureFlag.Feature useTokenRefreshSkewFeature;

  private ObjectMapper objectMapper;
  private OperaAuthenticationService operaAuthenticationService;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    operaAuthenticationService =
        new OperaAuthenticationService(operaProperties, objectMapper, operaRestClient,
            unleashWrapper, featureFlag);
  }

  @Nested
  class PasswordGrant {

    @BeforeEach
    void setUpGrant() {
      when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(false);
      when(operaProperties.getUsername()).thenReturn(USERNAME);
      when(operaProperties.getPassword()).thenReturn(PASSWORD);
      when(operaProperties.getGrantType()).thenReturn(GRANT_TYPE);
      when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
      when(operaProperties.getServiceUrl()).thenReturn(getServiceUrl());
      when(operaRestClient.getOauthToken(eq(getServiceUrl().getOauth()), any(HttpEntity.class)))
          .thenReturn(buildOauthCallResponse(EXPECTED_TOKEN_FROM_OPERA, HttpStatus.OK));
    }

    @Test
    void shouldUseDefaultTokenExpiryTimeout_WhenFeatureFlagDisabled() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(null);
      when(operaProperties.getTokenExpiryTimeout()).thenReturn(5);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(operaProperties, atLeastOnce()).getTokenExpiryTimeout();
      verify(operaProperties, never()).getTokenRefreshClockSkew();
    }

    @Test
    void shouldUseTokenRefreshClockSkew_WhenFeatureFlagEnabled() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(useTokenRefreshSkewFeature);
      when(unleashWrapper.isEnabled(useTokenRefreshSkewFeature)).thenReturn(true);
      when(operaProperties.getTokenRefreshClockSkew()).thenReturn(15L);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(unleashWrapper).isEnabled(useTokenRefreshSkewFeature);
      verify(operaProperties).getTokenRefreshClockSkew();
      verify(operaProperties, never()).getTokenExpiryTimeout();
    }

    @Test
    void shouldFallbackToDefaultTimeout_WhenFeatureFlagEnabledButClockSkewIsNull() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(useTokenRefreshSkewFeature);
      when(unleashWrapper.isEnabled(useTokenRefreshSkewFeature)).thenReturn(true);
      when(operaProperties.getTokenRefreshClockSkew()).thenReturn(null);
      when(operaProperties.getTokenExpiryTimeout()).thenReturn(5);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(operaProperties).getTokenRefreshClockSkew();
      verify(operaProperties, atLeastOnce()).getTokenExpiryTimeout();
    }
  }

  @Nested
  class ClientCredentialsGrant {

    @BeforeEach
    void setUpGrant() {
      when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(true);
      when(operaProperties.getOcimGrantType()).thenReturn(OCIM_GRANT_TYPE);
      when(operaProperties.getScope()).thenReturn(OCIM_SCOPE);
      when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
      when(operaProperties.getEnterpriseId()).thenReturn(ENTERPRISE_ID);
      when(operaProperties.getServiceUrl()).thenReturn(getServiceUrl());
      when(operaRestClient.getOauthToken(eq(getServiceUrl().getOauth()), any(HttpEntity.class)))
          .thenReturn(buildOauthCallResponse(EXPECTED_TOKEN_FROM_OPERA, HttpStatus.OK));
    }

    @Test
    void shouldUseDefaultTokenExpiryTimeout_WhenFeatureFlagDisabled() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(null);
      when(operaProperties.getTokenExpiryTimeout()).thenReturn(5);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(operaProperties, atLeastOnce()).getTokenExpiryTimeout();
      verify(operaProperties, never()).getTokenRefreshClockSkew();
    }

    @Test
    void shouldUseTokenRefreshClockSkew_WhenFeatureFlagEnabled() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(useTokenRefreshSkewFeature);
      when(unleashWrapper.isEnabled(useTokenRefreshSkewFeature)).thenReturn(true);
      when(operaProperties.getTokenRefreshClockSkew()).thenReturn(15L);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(unleashWrapper).isEnabled(useTokenRefreshSkewFeature);
      verify(operaProperties).getTokenRefreshClockSkew();
      verify(operaProperties, never()).getTokenExpiryTimeout();
    }

    @Test
    void shouldFallbackToDefaultTimeout_WhenFeatureFlagEnabledButClockSkewIsNull() {
      // Given
      when(featureFlag.getUseTokenRefreshSkew()).thenReturn(useTokenRefreshSkewFeature);
      when(unleashWrapper.isEnabled(useTokenRefreshSkewFeature)).thenReturn(true);
      when(operaProperties.getTokenRefreshClockSkew()).thenReturn(null);
      when(operaProperties.getTokenExpiryTimeout()).thenReturn(5);

      // When - first call populates the token cache
      operaAuthenticationService.fetchOauthToken(false);
      // Second call enters the renewal check and exercises getTokenRefreshSkewInMinutes()
      String token = operaAuthenticationService.fetchOauthToken(false);

      // Then
      assertNotNull(token);
      assertEquals(EXPECTED_TOKEN_FROM_OPERA, token);
      verify(operaProperties).getTokenRefreshClockSkew();
      verify(operaProperties, atLeastOnce()).getTokenExpiryTimeout();
    }
  }

  private ServiceUrl getServiceUrl() {
    ServiceUrl serviceUrl = new ServiceUrl();
    serviceUrl.setSubscriptionUrl("wss://dummy-host.example.com");
    serviceUrl.setOauth("https://dummy-host.example.com/oauth/v1/tokens");
    return serviceUrl;
  }

  private ResponseEntity<String> buildOauthCallResponse(final String token, final HttpStatus httpStatus) {
    String responseString = "";
    final OauthTokenResponse oAuthResponse = buildOauthTokenResponse(token);
    try {
      responseString = objectMapper.writeValueAsString(oAuthResponse);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return ResponseEntity.status(httpStatus).body(responseString);
  }

  private OauthTokenResponse buildOauthTokenResponse(final String token) {
    return OauthTokenResponse.builder()
        .expiresAt("3600")
        .tokenType("Bearer")
        .oracleTokenContext("user_assertion")
        .refreshToken("dummy-refresh-token")
        .oracleGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer")
        .accessToken(token)
        .build();
  }
}
