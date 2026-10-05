package uk.co.whitbread.avail.business.events.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
public class OperaAuthenticationServiceTest {

  private static final String DESERIALIZING_ERROR = "Could not deserialize token";
  private static final String GET_TOKEN_FAILED_ERROR =
      "unable to fetch Authorization oAuthToken data from the Opera, "
          + "so returning null as the accessToken";
  private static final String USERNAME = "test_username";
  private static final String PASSWORD = "test_password";
  private static final String GRANT_TYPE = "test_grant_type";
  private static final String OCIM_SCOPE = "test_scope";
  private static final String DUMMY_API_KEY = "488c82b8-50fe-4cc1-ae82-4b7aa470bf58";
  private static final String EXPECTED_TOKEN_FROM_OPERA
      = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tll3TGZQOCIsImtpZC"
      + "I6Im1zLW9hdXRoa2V5In0.eyJzdWIiOiJXSEJPQzAwMV9BV1MtTVMtU0EtQ1JFRDI0MCIsImlzcyI6Ind3dy5vcmF"
      + "jbGUuY29tIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJPQXV0aFNlcnZpY2VQcm9maWxlIiwiaWF0IjoxNjYxND"
      + "IxODY2LCJvcmFjbGUub2F1dGgucHJuLmlkX3R5cGUiOiJMREFQX1VJRCIsImV4cCI6MTY2MTQyNTQ2Niwib3JhY2xl"
      + "Lm9hdXRoLnRrX2NvbnRleHQiOiJ1c2VyX2Fzc2VydGlvbiIsImF1ZCI6WyJodHRwczovLypvcmFjbGUqLmNvbSIsI"
      + "mh0dHBzOi8vKi5pbnQgIiwiaHR0cHM6Ly8qb2NzLm9jLXRlc3QuY29tLyJdLCJwcm4iOiJXSEJPQzAwMV9BV1MtT"
      + "VMtU0EtQ1JFRDI0MCIsImp0aSI6IjU3ZWEwZjVhLWFmZmYtNGQ1Yy1hNjRhLWMwOWEyMWI3MDg0YyIsIm9yYWNs"
      + "ZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiV0hCT0MwMDFfQ2xpZW50IiwidXNlci50ZW5hbnQubmFtZSI6IkR"
      + "lZmF1bHREb21haW4iLCJvcmFjbGUub2F1dGguaWRfZF9pZCI6IjEyMzQ1Njc4LTEyMzQtMTIzNC0xMjM0LTEyMzQ"
      + "1Njc4OTAxMiJ9.c0jY45QMe7YTk6R3pMAivNXL_-xcSigxHyS8-3bwxiwZHVvmyYyqQvPc8WmtQVpoB0qdQ5WYEP"
      + "xf8JB8hEM7erjgoC_kew3wojoWteu-9WSdkUK-FPyJT8qkDDw9a3cAmOXsob2HZR1Tt3Be9OICNnvha8oodFeIY4S"
      + "r9DHh2eFqJ1tvYUZwC5-znubEU87Fy_sl2BdQlXQvCkpfxugh_SuPirH3OriN52___HzZ4Xu7AburhRXpYipaKyk"
      + "7xNQ8_Qr6PxoclYo3TgCQSccty-g-DZ8yOuN5cSh22bBL9SAhezUxas3lhqSiP-EwQFtkjAe1aF8C"
      + "F1BCtKqII8OQUA";

  private static final String INVALID_TOKEN_FROM_OPERA
      = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tll3TGZQOCIsImtpZC"
      + "I6Im1zLW9hdXRoa2V5In0.";

  @Mock
  private OperaProperties operaProperties;

  private ObjectMapper objectMapper;

  @Mock
  private OperaRestClient operaRestClient;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag featureFlag;

  private OperaAuthenticationService operaAuthenticationService;


  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    operaAuthenticationService =
        new OperaAuthenticationService(operaProperties, objectMapper, operaRestClient,
            unleashWrapper, featureFlag);
  }

  @Test
  public void fetchOauthTokenSuccessfulWhenCacheHasExpired() {
    
    Mockito.when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(false);

    Mockito.when(operaProperties.getUsername()).thenReturn(USERNAME);

    Mockito.when(operaProperties.getPassword()).thenReturn(PASSWORD);

    Mockito.when(operaProperties.getGrantType()).thenReturn(GRANT_TYPE);

    Mockito.when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);

    final ServiceUrl serviceUrl = getServiceUrl();

    Mockito.when(operaProperties.getServiceUrl()).thenReturn(serviceUrl);

    Mockito.when(operaRestClient.getOauthToken(Mockito.eq(serviceUrl.getOauth()),
        Mockito.any(HttpEntity.class)))
        .thenReturn(buildOauhCallResponse(EXPECTED_TOKEN_FROM_OPERA, HttpStatus.OK));

    String actualTokenFromOpera = operaAuthenticationService.fetchOauthToken(false);

    assertEquals(EXPECTED_TOKEN_FROM_OPERA, actualTokenFromOpera);

  }

  @Test
  public void fetchOauthTokenShouldLogErrorWhenNotValidToken() {
    
    Mockito.when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(false);

    Mockito.when(operaProperties.getUsername()).thenReturn(USERNAME);

    Mockito.when(operaProperties.getPassword()).thenReturn(PASSWORD);

    Mockito.when(operaProperties.getGrantType()).thenReturn(GRANT_TYPE);

    Mockito.when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);

    final ServiceUrl serviceUrl = getServiceUrl();

    Mockito.when(operaProperties.getServiceUrl()).thenReturn(serviceUrl);

    Mockito.when(operaRestClient.getOauthToken(Mockito.eq(serviceUrl.getOauth()),
        Mockito.any(HttpEntity.class)))
        .thenReturn(buildOauhCallResponse(INVALID_TOKEN_FROM_OPERA, HttpStatus.OK));

    try {
      operaAuthenticationService.fetchOauthToken(false);
      fail("Expected an IndexOutOfBoundsException to be thrown");
    } catch (IndexOutOfBoundsException e) {
    }

  }

  @Test
  public void shouldLogErrorAndReturnNullWhenOperaIsNotSendingSuccessfulRes() {
    
    Mockito.when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(false);

    final String nullAuthToken = null;

    Mockito.when(operaProperties.getUsername()).thenReturn(USERNAME);

    Mockito.when(operaProperties.getPassword()).thenReturn(PASSWORD);

    Mockito.when(operaProperties.getGrantType()).thenReturn(GRANT_TYPE);

    Mockito.when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);

    final ServiceUrl serviceUrl = getServiceUrl();

    Mockito.when(operaProperties.getServiceUrl()).thenReturn(serviceUrl);

    Mockito.when(operaRestClient.getOauthToken(Mockito.eq(serviceUrl.getOauth()),
        Mockito.any(HttpEntity.class)))
        .thenReturn(
            buildOauhCallResponse(INVALID_TOKEN_FROM_OPERA, HttpStatus.INTERNAL_SERVER_ERROR));

    String actualTokenFromOpera = operaAuthenticationService.fetchOauthToken(false);

    assertEquals(nullAuthToken, actualTokenFromOpera);
  }
  
  @Test
  void fetchOcimOauthTokenSuccessfulWhenCacheHasExpired() {
    Mockito.when(operaProperties.getIsClientCredentialsEnabled()).thenReturn(true);
    
    Mockito.when(operaProperties.getScope()).thenReturn(OCIM_SCOPE);
    
    Mockito.when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
    
    final ServiceUrl serviceUrl = getServiceUrl();
    
    Mockito.when(operaProperties.getServiceUrl()).thenReturn(serviceUrl);
    
    Mockito.when(operaRestClient.getOauthToken(Mockito.eq(serviceUrl.getOauth()),
            Mockito.any(HttpEntity.class)))
        .thenReturn(buildOauhCallResponse(EXPECTED_TOKEN_FROM_OPERA, HttpStatus.OK));
    
    String actualTokenFromOpera = operaAuthenticationService.fetchOauthToken(false);
    
    assertEquals(EXPECTED_TOKEN_FROM_OPERA, actualTokenFromOpera);
  }

  private ServiceUrl getServiceUrl() {

    ServiceUrl serviceUrl = new ServiceUrl();
    serviceUrl.setSubscriptionUrl("wss://test4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com");
    serviceUrl
        .setOauth("https://test4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com/oauth/v1/tokens");
    return serviceUrl;
  }

  private ResponseEntity buildOauhCallResponse(final String token, final HttpStatus httpStatus) {
    String responseString = "";
    final OauthTokenResponse oAuhResponse = buildOauthTokenResponse(token);
    try {
      responseString = objectMapper.writeValueAsString(oAuhResponse);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return ResponseEntity.status(httpStatus).body(responseString);
  }

  /*private HttpEntity buildHttEntity() {

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(USERNAME, USERNAME);
    map.add(PASSWORD, PASSWORD);
    map.add(GRANT_TYPE, GRANT_TYPE);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add(APP_KEY_HEADER, DUMMY_API_KEY);
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
    return entity;

  }*/

  private OauthTokenResponse buildOauthTokenResponse(final String token) {
    return OauthTokenResponse.builder()
        .expiresAt("3600")
        .tokenType("Bearer")
        .oracleTokenContext("user_assertion")
        .refreshToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImNMQXpEelN1dDc2VFZwYWxnMUw3Tl")
        .oracleGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer")
        .accessToken(token)
        .build();
  }

}
