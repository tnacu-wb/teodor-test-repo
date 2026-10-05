package uk.co.whitbread.marketing.client.customerhub;

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
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesResponse;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.properties.CustomerHubPropertiesLegacy;
import uk.co.whitbread.marketing.utils.TestObjectMapperFactory;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerHubClientLegacyTest {

    static final String UPDATE_PREFERENCES_SERVICE_URL = "service-url-update-test";
    static final String UPDATE_PREFERENCES_AUTH_KEY = "auth-key-update-test";
    static final String AUTH_KEY_PARAM_NAME = "auth-key-param-name-test";
    static final String HEADER_NAME_1 = "header-name-1";
    static final String HEADER_VALUE_1 = "header-value-1";
    static final String HEADER_NAME_2 = "header-name-2";
    static final String HEADER_VALUE_2 = "header-value-2";
    @Mock private RestTemplate restTemplateMock;
    @Mock private CustomerHubPropertiesLegacy customerHubPropertiesLegacyMock;
    @Mock private CustomerHubNewsletterPreferencesUpdateRequest customerHubNewsletterPreferencesUpdateRequestMock;
    @Mock private CustomerHubNewsletterPreferencesResponse customerHubNewsletterPreferencesResponseMock;
    @Mock private HttpEntity httpEntityMock;

    private ResponseEntity responseEntity;

    private CustomerHubClientLegacy testObj;

  @BeforeEach
    void setUp() {
    JsonMapper objectMapper = TestObjectMapperFactory.create();
        testObj = spy(new CustomerHubClientLegacy(restTemplateMock, objectMapper, customerHubPropertiesLegacyMock));

        when(customerHubPropertiesLegacyMock.getUpdateMarketingPreferencesServiceUrl()).thenReturn(UPDATE_PREFERENCES_SERVICE_URL);
        when(customerHubPropertiesLegacyMock.getUpdateMarketingPreferencesAuthKey()).thenReturn(UPDATE_PREFERENCES_AUTH_KEY);
        when(customerHubPropertiesLegacyMock.getAuthKeyHeaderName()).thenReturn(AUTH_KEY_PARAM_NAME);

        responseEntity = new ResponseEntity(customerHubNewsletterPreferencesResponseMock,OK);
    }

    @Test
    void shouldBuildHttpEntity() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_NAME_1, HEADER_VALUE_1);
        httpHeaders.set(HEADER_NAME_2, HEADER_VALUE_2);
        doReturn(httpHeaders).when(testObj).buildHeaders(CustomerHubClientLegacy.RequestType.UPDATE);

        HttpEntity result = testObj.buildHttpEntity(customerHubNewsletterPreferencesUpdateRequestMock, CustomerHubClientLegacy.RequestType.UPDATE);
        assertNotNull(result);
        HttpHeaders resultHttpHeaders = result.getHeaders();
        assertTrue(resultHttpHeaders.containsHeader(HEADER_NAME_1));
        assertTrue(resultHttpHeaders.containsHeader(HEADER_NAME_2));
        assertEquals(HEADER_VALUE_1, resultHttpHeaders.getFirst(HEADER_NAME_1));
        assertEquals(HEADER_VALUE_2, resultHttpHeaders.getFirst(HEADER_NAME_2));

        assertEquals(result.getBody(), customerHubNewsletterPreferencesUpdateRequestMock);
    }

    @Test
    void shouldUpdateNewsletterPreferences() {

        doReturn(httpEntityMock).when(testObj).buildHttpEntity(customerHubNewsletterPreferencesUpdateRequestMock, CustomerHubClientLegacy.RequestType.UPDATE);

        when(restTemplateMock.exchange(UPDATE_PREFERENCES_SERVICE_URL, POST, httpEntityMock, CustomerHubNewsletterPreferencesResponse.class)).thenReturn(responseEntity);

        testObj.updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequestMock);

        verify(restTemplateMock).exchange(UPDATE_PREFERENCES_SERVICE_URL, POST, httpEntityMock, CustomerHubNewsletterPreferencesResponse.class);

    }

    @Test
    void shouldBuildHeaders_updatePreferences() {

        HttpHeaders result = testObj.buildHeaders(CustomerHubClientLegacy.RequestType.UPDATE);
        assertNotNull(result);
        assertEquals(MediaType.APPLICATION_JSON, result.getContentType());
        assertEquals(UPDATE_PREFERENCES_AUTH_KEY, result.getFirst(AUTH_KEY_PARAM_NAME));
    }

    @Test
    void shouldBuildHttpEntity_forEditPermissionsRequest() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HEADER_NAME_1, HEADER_VALUE_1);
        httpHeaders.set(HEADER_NAME_2, HEADER_VALUE_2);
        doReturn(httpHeaders).when(testObj).buildHeaders(CustomerHubClientLegacy.RequestType.UPDATE);

        HttpEntity result = testObj.buildHttpEntity(customerHubNewsletterPreferencesUpdateRequestMock, CustomerHubClientLegacy.RequestType.UPDATE);
        assertNotNull(result);
        HttpHeaders resultHttpHeaders = result.getHeaders();
        assertTrue(resultHttpHeaders.containsHeader(HEADER_NAME_1));
        assertTrue(resultHttpHeaders.containsHeader(HEADER_NAME_2));
        assertEquals(HEADER_VALUE_1, resultHttpHeaders.getFirst(HEADER_NAME_1));
        assertEquals(HEADER_VALUE_2, resultHttpHeaders.getFirst(HEADER_NAME_2));

        assertEquals(result.getBody(), customerHubNewsletterPreferencesUpdateRequestMock);

    }

    @Test
    void shouldReturn401Exception() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED",
                        "You do not have permission to view this directory or page.".getBytes(),
                        Charset.defaultCharset());

        when(restTemplateMock.exchange(eq(UPDATE_PREFERENCES_SERVICE_URL), eq(POST), any(),
                eq(CustomerHubNewsletterPreferencesResponse.class)))
                .thenThrow(httpClientErrorException);

        assertThrows(CDHException.class,
            () -> testObj.updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequestMock),
            "You do not have permission to view this directory or page.");
    }

    @Test
    void shouldReturn404Exception() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.NOT_FOUND,"NOT_FOUND",
                        "{\"status\": 404,\"message\": \"Contact channel not found with requested criteria.\"}".getBytes(),
                        Charset.defaultCharset());

        when(restTemplateMock.exchange(eq(UPDATE_PREFERENCES_SERVICE_URL), eq(POST), any(),
                eq(CustomerHubNewsletterPreferencesResponse.class)))
                .thenThrow(httpClientErrorException);
        assertThrows(CDHException.class,
            () -> testObj.updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequestMock),
            "Contact channel not found with requested criteria.");
    }

    @Test
    void shouldReturn500Exception() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"500_INTERNAL_SERVER_ERROR",
                        "Unknown error.".getBytes(),
                        Charset.defaultCharset());

        when(restTemplateMock.exchange(eq(UPDATE_PREFERENCES_SERVICE_URL), eq(POST), any(),
                eq(CustomerHubNewsletterPreferencesResponse.class)))
                .thenThrow(httpClientErrorException);
        assertThrows(CDHException.class,
            () -> testObj.updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequestMock),
            "Unknown error.");
    }

}
