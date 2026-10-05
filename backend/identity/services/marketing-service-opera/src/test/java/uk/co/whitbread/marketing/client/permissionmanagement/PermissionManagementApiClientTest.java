package uk.co.whitbread.marketing.client.permissionmanagement;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.nio.charset.StandardCharsets;

import uk.co.whitbread.marketing.client.oauth.OauthCacheProvider;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementConfirmDoubleOptIn;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetResponse;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUnsubscribeRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUpdateRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.RequestAcceptedResponse;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.properties.CustomerHubProperties;
import uk.co.whitbread.marketing.properties.PermissionManagementApiProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.contract.spec.internal.MediaTypes.APPLICATION_JSON;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionManagementApiClientTest {

  static final String UPDATE_PREFERENCES_SERVICE_URL = "https://whitbread-stg.azure-api.net/PermissionManagement/V1/UpdatePermissions";
  static final String CONFIRM_DOUBLE_OPT_IN_SERVICE_URL = "https://whitbread-stg.azure-api.net/PermissionManagement/V1/ConfirmDoubleOptIn";
  static final String UNSUBSCRIBE_SERVICE_URL = "https://whitbread-stg.azure-api.net/PermissionManagement/V1/Unsubscribe";
  static final String SUBSCRIPTION_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
  static final String GET_PREFERENCES_SERVICE_URL = "https://whitbread-stg.azure-api.net/PermissionManagement/V1/GetPermissions";
  static final String GET_PREFERENCES_SERVICE_V2_URL = "https://whitbread-stg.azure-api.net/PermissionManagement/V2/GetPermissions";
  static final String SUBSCRIPTION_KEY = "5d26557005f7476aba7ccfb12bdb9a4b";
  static final String HEADER_AUTH = "Authorization";
  static final String HEADER_AUTH_VALUE = "Bearer token_123";

  @Mock
  private RestTemplate restTemplateMock;
  @Mock
  private PermissionManagementApiProperties permissionManagementApiPropertiesMock;
  @Mock
  private CustomerHubProperties customerHubPropertiesMock;
  @Mock
  private OauthCacheProvider oauthCacheProvider;
  @Mock
  private PermissionManagementGetRequest permissionManagementGetRequestMock;
  @Mock
  private PermissionManagementGetResponse permissionManagementGetResponseMock;
  @Mock
  private PermissionManagementUpdateRequest permissionManagementUpdateRequestMock;
  @Mock
  private PermissionManagementConfirmDoubleOptIn permissionManagementConfirmDoubleOptInMock;
  @Mock
  private PermissionManagementUnsubscribeRequest permissionManagementUnsubscribeRequestMock;
  @Mock
  private RequestAcceptedResponse requestAcceptedResponseMock;

  private PermissionManagementApiClient target;

  @BeforeEach
  void setUp() {
    JsonMapper objectMapper = JsonMapper.builder().build();
    target = spy(
        new PermissionManagementApiClient(restTemplateMock, objectMapper, oauthCacheProvider,
            permissionManagementApiPropertiesMock, customerHubPropertiesMock));

    when(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName()).thenReturn(
        SUBSCRIPTION_KEY);
    when(permissionManagementApiPropertiesMock.getSubscriptionKey()).thenReturn(
        SUBSCRIPTION_KEY_HEADER_NAME);
    when(permissionManagementApiPropertiesMock.getUpdateMarketingPreferencesUrl()).thenReturn(
        UPDATE_PREFERENCES_SERVICE_URL);
    when(permissionManagementApiPropertiesMock.getGetMarketingPreferencesUrl()).thenReturn(
        GET_PREFERENCES_SERVICE_URL);
    when(permissionManagementApiPropertiesMock.getGetMarketingPreferencesV2Url()).thenReturn(
        GET_PREFERENCES_SERVICE_V2_URL);
    when(permissionManagementApiPropertiesMock.getConfirmDoubleOptInUrl()).thenReturn(
        CONFIRM_DOUBLE_OPT_IN_SERVICE_URL);
    when(permissionManagementApiPropertiesMock.getUnsubscribeUrl()).thenReturn(
        UNSUBSCRIBE_SERVICE_URL);
  }

  @Test
  void shouldBuildHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    var entityExpected = new HttpEntity<>(
        permissionManagementGetRequestMock,
        headers
    );

    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    var result = target.buildHttpEntity(permissionManagementGetRequestMock);
    Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(entityExpected);
  }

  @Test
  void shouldGetNewsletterPreferences() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        customerHubPropertiesMock.getSubscriptionKeyV2());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementGetRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(GET_PREFERENCES_SERVICE_V2_URL, POST, entityExpected,
        PermissionManagementGetResponse.class))
        .thenReturn(new ResponseEntity<>(permissionManagementGetResponseMock, OK));

    target.getNewsletterPreferences(permissionManagementGetRequestMock);

    verify(restTemplateMock).exchange(GET_PREFERENCES_SERVICE_V2_URL, POST, entityExpected,
        PermissionManagementGetResponse.class);
  }

  @Test
  void getNewsletterPreferencesThrows401CDHException() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        customerHubPropertiesMock.getSubscriptionKeyV2());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementGetRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(GET_PREFERENCES_SERVICE_V2_URL, POST, entityExpected,
        PermissionManagementGetResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.getNewsletterPreferences(permissionManagementGetRequestMock));

    assertEquals(HttpStatus.UNAUTHORIZED.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("401 Unauthorized", cdhException.getMessage());
  }

  @Test
  void getNewsletterPreferencesThrows400CDHException() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        customerHubPropertiesMock.getSubscriptionKeyV2());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementGetRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(GET_PREFERENCES_SERVICE_V2_URL, POST, entityExpected,
        PermissionManagementGetResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.getNewsletterPreferences(permissionManagementGetRequestMock));

    assertEquals(HttpStatus.BAD_REQUEST.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("400 Bad Request", cdhException.getMessage());
  }

  @Test
  void shouldUpdateNewsletterPreferences() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementUpdateRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(UPDATE_PREFERENCES_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenReturn(new ResponseEntity<>(requestAcceptedResponseMock, OK));

    target.updateNewsletterPreferences(permissionManagementUpdateRequestMock);

    verify(restTemplateMock).exchange(UPDATE_PREFERENCES_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class);
  }

  @Test
  void shouldUpdateNewsletterPreferencesThrowsCDHException() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementUpdateRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(UPDATE_PREFERENCES_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.updateNewsletterPreferences(permissionManagementUpdateRequestMock));

    assertEquals(HttpStatus.UNAUTHORIZED.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("401 Unauthorized", cdhException.getMessage());

  }

  @Test
  void shouldConfirmDoubleOptIn() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementConfirmDoubleOptInMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(CONFIRM_DOUBLE_OPT_IN_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenReturn(new ResponseEntity<>(requestAcceptedResponseMock, OK));

    target.confirmDoubleOptIn(permissionManagementConfirmDoubleOptInMock);

    verify(restTemplateMock).exchange(CONFIRM_DOUBLE_OPT_IN_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class);
  }

  @Test
  void shouldConfirmDoubleOptInThrowsCDHException() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementConfirmDoubleOptInMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(CONFIRM_DOUBLE_OPT_IN_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.confirmDoubleOptIn(permissionManagementConfirmDoubleOptInMock));

    assertEquals(HttpStatus.UNAUTHORIZED.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("401 Unauthorized", cdhException.getMessage());
  }

  @Test
  void shouldConfirmDoubleOptInThrowsCDHExceptionForNonJsonBody() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementConfirmDoubleOptInMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    HttpHeaders errorHeaders = new HttpHeaders();
    errorHeaders.setContentType(MediaType.TEXT_HTML);
    HttpClientErrorException nonJson401 = HttpClientErrorException.create(
        HttpStatus.UNAUTHORIZED,
        "Unauthorized",
        errorHeaders,
        "You are not authorized".getBytes(StandardCharsets.UTF_8),
        StandardCharsets.UTF_8);

    when(restTemplateMock.exchange(CONFIRM_DOUBLE_OPT_IN_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class)).thenThrow(nonJson401);

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.confirmDoubleOptIn(permissionManagementConfirmDoubleOptInMock));

    assertEquals(HttpStatus.UNAUTHORIZED.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("You are not authorized", cdhException.getMessage());
  }

  @Test
  void shouldUnsubscribeFromNewsletters() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementUnsubscribeRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(UNSUBSCRIBE_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenReturn(new ResponseEntity<>(requestAcceptedResponseMock, OK));

    target.unsubscribe(permissionManagementUnsubscribeRequestMock);

    verify(restTemplateMock).exchange(UNSUBSCRIBE_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class);
  }

  @Test
  void shouldUnsubscribeFromNewslettersThrowsCDHException() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
    headers.add(HEADER_AUTH, HEADER_AUTH_VALUE);
    headers.add(permissionManagementApiPropertiesMock.getSubscriptionKeyHeaderName(),
        permissionManagementApiPropertiesMock.getSubscriptionKey());
    HttpEntity entityExpected = new HttpEntity(
        permissionManagementUnsubscribeRequestMock,
        headers
    );
    doReturn("token_123").when(oauthCacheProvider).getBearerToken();

    when(restTemplateMock.exchange(UNSUBSCRIBE_SERVICE_URL, POST, entityExpected,
        RequestAcceptedResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    CDHException cdhException = assertThrows(CDHException.class,
        () -> target.unsubscribe(permissionManagementUnsubscribeRequestMock));

    assertEquals(HttpStatus.UNAUTHORIZED.value(), cdhException.getStatus());
    assertEquals("3001", cdhException.getErrorCode());
    assertEquals("401 Unauthorized", cdhException.getMessage());

  }
}
