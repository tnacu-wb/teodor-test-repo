package uk.co.whitbread.marketing.client.customerhub;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesResponse;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.client.oauth.OauthCacheProvider;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.properties.CustomerHubProperties;
import uk.co.whitbread.marketing.utils.TestObjectMapperFactory;

import java.nio.charset.Charset;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerHubClientTest {

  static final String EDIT_PREFERENCES_SERVICE_URL = "service-url-edit-test";
  static final String EDIT_PREFERENCES_AUTH_KEY = "auth-key-edit-test";
  static final String GET_PREFERENCES_SERVICE_URL = "service-url-get-test";
  static final String AUTH_KEY_PARAM_NAME = "auth-key-param-name-test";
  static final String HEADER_AUTH = "Authorization";
  static final String HEADER_AUTH_VALUE = "Bearer token_123";

  @Mock
  private RestTemplate restTemplateMock;
  @Mock
  private CustomerHubProperties customerHubPropertiesMock;
  @Mock
  private CustomerHubNewsletterPreferencesUpdateRequest customerHubNewsletterPreferencesUpdateRequestMock;
  @Mock
  private CustomerHubNewsletterPreferencesResponse customerHubNewsletterPreferencesResponseMock;
  @Mock
  private CustomerHubNewsletterPreferencesEditRequest customerHubNewsletterPreferencesEditRequestMock;
  @Mock
  private CustomerHubNewsletterPreferencesGetRequest customerHubNewsletterPreferencesGetRequest;
  @Mock
  private NewsletterPreferencesGetResponse newsletterPreferencesGetResponse;
  @Mock
  private OauthCacheProvider oauthCacheProvider;

  private ResponseEntity responseEntity;

  private CustomerHubClient target;

  @BeforeEach
  void setUp() {
    JsonMapper objectMapper = TestObjectMapperFactory.create();
    target = spy(new CustomerHubClient(restTemplateMock, objectMapper, oauthCacheProvider,
        customerHubPropertiesMock));

    when(customerHubPropertiesMock.getSubscriptionKeyHeaderName()).thenReturn(AUTH_KEY_PARAM_NAME);
    when(customerHubPropertiesMock.getSubscriptionKey()).thenReturn(EDIT_PREFERENCES_AUTH_KEY);
    when(customerHubPropertiesMock.getEditMarketingPreferencesUrl()).thenReturn(
        EDIT_PREFERENCES_SERVICE_URL);
    when(customerHubPropertiesMock.getGetMarketingPreferencesUrl()).thenReturn(
        GET_PREFERENCES_SERVICE_URL);

    responseEntity = new ResponseEntity(customerHubNewsletterPreferencesResponseMock, OK);
  }

  @Test
  void shouldBuildHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(customerHubPropertiesMock.getSubscriptionKeyHeaderName(),
        customerHubPropertiesMock.getSubscriptionKey());
    var entityExpected = new HttpEntity<>(
        customerHubNewsletterPreferencesGetRequest,
        headers
    );

    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    var result = target.buildHttpEntity(customerHubNewsletterPreferencesGetRequest);
    Assertions.assertThat(result).usingRecursiveComparison()
        .isEqualTo(entityExpected);
  }

  @Test
  void shouldEditNewsletterPreferences() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    headers.add(HEADER_AUTH,HEADER_AUTH_VALUE);
    headers.add(customerHubPropertiesMock.getSubscriptionKeyHeaderName(),customerHubPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        customerHubNewsletterPreferencesEditRequestMock,
        headers
    );

    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(EDIT_PREFERENCES_SERVICE_URL, POST, entityExpected, CustomerHubNewsletterPreferencesResponse.class)).thenReturn(responseEntity);

    target.editNewsletterPreferences(customerHubNewsletterPreferencesEditRequestMock);

    verify(restTemplateMock).exchange(EDIT_PREFERENCES_SERVICE_URL, POST, entityExpected, CustomerHubNewsletterPreferencesResponse.class);

  }

  @Test
  void shouldGetNewsletterPreferences() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(customerHubPropertiesMock.getSubscriptionKeyHeaderName(),
        customerHubPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        customerHubNewsletterPreferencesGetRequest,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(GET_PREFERENCES_SERVICE_URL, POST, entityExpected,
        NewsletterPreferencesGetResponse.class))
        .thenReturn(new ResponseEntity<>(newsletterPreferencesGetResponse, OK));

    target.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest);

    verify(restTemplateMock).exchange(GET_PREFERENCES_SERVICE_URL, POST, entityExpected,
        NewsletterPreferencesGetResponse.class);

  }

  @Test
  void shouldReturn401Exception() {
    HttpClientErrorException httpClientErrorException =
        new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
            "You do not have permission to view this directory or page.".getBytes(),
            Charset.defaultCharset());

    CDHException expectedException = new CDHException(401,
        "You do not have permission to view this directory or page.", "3001");

    when(restTemplateMock.exchange(eq(GET_PREFERENCES_SERVICE_URL), eq(POST), any(),
        eq(NewsletterPreferencesGetResponse.class)))
        .thenThrow(httpClientErrorException);
    try {
      target.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest);
      Assertions.fail("Test should have failed");
    } catch (Exception e) {
      CDHException cdhException = (CDHException) e;
      Assertions.assertThat(cdhException).usingRecursiveComparison().isEqualTo(expectedException);
    }
  }

  @Test
  void shouldReturn404Exception() {
    HttpClientErrorException httpClientErrorException =
        new HttpClientErrorException(HttpStatus.NOT_FOUND, "NOT_FOUND",
            "{\"status\": 404,\"message\": \"Contact channel not found with requested criteria.\"}".getBytes(),
            Charset.defaultCharset());

    CDHException expectedException = new CDHException(404,
        "Contact channel not found with requested criteria.", "3001");

    when(restTemplateMock.exchange(eq(GET_PREFERENCES_SERVICE_URL), eq(POST), any(),
        eq(NewsletterPreferencesGetResponse.class)))
        .thenThrow(httpClientErrorException);
    try {
      target.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest);
      Assertions.fail("Test should have failed");
    } catch (Exception e) {
      CDHException cdhException = (CDHException) e;
      Assertions.assertThat(cdhException).usingRecursiveComparison().isEqualTo(expectedException);
    }
  }

  @Test
  void shouldReturn500Exception() {
    HttpClientErrorException httpClientErrorException =
        new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
            "Unknown error.".getBytes(),
            Charset.defaultCharset());

    CDHException expectedException = new CDHException(500, "Unknown error.", "3001");

    when(restTemplateMock.exchange(eq(GET_PREFERENCES_SERVICE_URL), eq(POST), any(),
        eq(NewsletterPreferencesGetResponse.class)))
        .thenThrow(httpClientErrorException);

    try {
      target.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest);
      Assertions.fail("Test should have failed");
    } catch (Exception e) {
      CDHException cdhException = (CDHException) e;
      Assertions.assertThat(cdhException).usingRecursiveComparison().isEqualTo(expectedException);
    }
  }

}
