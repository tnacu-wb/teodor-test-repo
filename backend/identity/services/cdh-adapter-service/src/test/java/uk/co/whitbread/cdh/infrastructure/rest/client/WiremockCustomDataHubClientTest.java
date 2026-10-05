package uk.co.whitbread.cdh.infrastructure.rest.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;

import java.lang.reflect.Method;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception.OauthClientException;
import uk.co.whitbread.cdh.utils.CustomStatusCodeException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockCustomDataHubClientTest {

  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final Body testBody = Body.none();
  private final Body sizeTestBodyOK = Body.fromOneOf(null, generateResponse(1024 * 1024), null, null);
  private final Body sizeTestBodyOverflow = Body.fromOneOf(null, generateResponse(1024 * 1024 + 1), null, null);
  private final ReservationSearchCriteria requestBody = ReservationSearchCriteria.builder().build();
  @Mock
  private OAuthProvider oauthProvider;
  @Mock
  private CdhApiOauthProperties cdhOauthProperties;
  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private WebClientProperties webClientProperties;

  @InjectMocks
  CustomerDataHubClient client;

  private static final String TEST_ACCESSED_BY = "tester@example.com";
  private static final String TEST_ACCESS_CONTEXT = "PI";

  @SneakyThrows
  @BeforeEach
  void setUp(){
    wm = new WireMockServer(8080);

    wm.stubFor(any(urlMatching("^.*test.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(testBody)));

    wm.stubFor(any(urlMatching("^.*exc.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(testBody)));

    wm.stubFor(any(urlMatching("^.*buffer-size-test-ok.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200)
            .withResponseBody(sizeTestBodyOK)));

    wm.stubFor(any(urlMatching("^.*buffer-size-test-overflow.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200)
            .withResponseBody(sizeTestBodyOverflow)));

    wm.start();

    when(webClientProperties.getSize()).thenReturn(1);

    Method privateMethod = CustomerDataHubClient.class.getDeclaredMethod("init");
    privateMethod.setAccessible(true);
    privateMethod.invoke(client);
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testPostCdh_ShouldReturnNoResult() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act
    ReservationSearch response = client.postCdh(path + "/test", requestBody);
    //Asserts
    assertNull(response);
  }

  @Test
  void testPostCdh_ShouldNotThrowDataBufferLimitException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act
    ReservationSearch response = client.postCdh(path + "/buffer-size-test-ok", requestBody);
    //Asserts
    assertEquals(ReservationSearch.class, response.getClass());
  }

  @Test
  void testPostCdh_ShouldThrowDataBufferLimitException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act & Assert
    var exception = assertThrows(WebClientResponseException.class,
        () -> client.postCdh(path + "/buffer-size-test-overflow", requestBody));
    assertEquals(DataBufferLimitException.class, exception.getCause().getClass());
  }

  @Test
  void testPostCdh_ShouldReturn5xxCdhException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act & Assert
    var exception = assertThrows(CDHException.class,
        () -> client.postCdh(path + "/exc1", requestBody));
    assertEquals(ErrorCode.CDH_SEARCH_COMPANY_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void testPostCdh_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    //Act & Assert
    assertThrows(OauthClientException.class, () -> client.postCdh(path + "/exc1", requestBody));
  }

  @Test
  void testPostCdh_StatusCodeExceptionException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(new CustomStatusCodeException());
    //Act & Assert
    assertThrows(CustomStatusCodeException.class,
        () -> client.getCdh(path + "/exc1", ReservationSearch.class));
  }

  @Test
  void testGetCdh_ShouldReturnNoResult() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act
    ReservationSearch response = client.getCdh(path + "/test", ReservationSearch.class);
    //Asserts
    assertNull(response);
  }

  @Test
  void testGetCdh_ShouldReturn5xxCdhException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act & Assert
    var exception = assertThrows(CDHException.class,
        () -> client.getCdh(path + "/exc1", ReservationSearch.class));
    assertEquals(ErrorCode.CDH_GET_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void testGetCdh_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    //Act & Assert
    assertThrows(OauthClientException.class, () -> client.getCdh(path + "/exc1", ReservationSearch.class));
  }

  @Test
  void testPostCdhGeneric_ShouldReturnResult() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act
    ReservationSearch response = client.postCdh(path + "/buffer-size-test-ok", requestBody,
        ReservationSearch.class, TEST_ACCESSED_BY, TEST_ACCESS_CONTEXT);
    //Asserts
    assertEquals(ReservationSearch.class, response.getClass());
  }

  @Test
  void testPostCdhGeneric_ShouldThrowDataBufferLimitException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act & Assert
    var exception = assertThrows(WebClientResponseException.class,
        () -> client.postCdh(path + "/buffer-size-test-overflow", requestBody,
            ReservationSearch.class, TEST_ACCESSED_BY, TEST_ACCESS_CONTEXT));
    assertEquals(DataBufferLimitException.class, exception.getCause().getClass());
  }

  @Test
  void testPostCdhGeneric_TokenException() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenThrow(
        new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, "debug"));
    //Act & Assert
    assertThrows(OauthClientException.class,
        () -> client.postCdh(path + "/exc1", requestBody, ReservationSearch.class,
            TEST_ACCESSED_BY, TEST_ACCESS_CONTEXT));
  }

  @Test
  void testPostCdhGeneric_ShouldReturnCdhExceptionForEmptyErrorBody() {
    //Arrange
    arrangeProperties();
    when(oauthProvider.getBearerToken()).thenReturn("token");
    //Act & Assert
    var exception = assertThrows(CDHException.class,
        () -> client.postCdh(path + "/exc1", requestBody, ReservationSearch.class,
            TEST_ACCESSED_BY, TEST_ACCESS_CONTEXT));
    assertEquals(ErrorCode.CDH_POST_EXCEPTION.getCode(), exception.getErrorCode());
  }

  private String generateResponse(final int length) {
    return "{" + "\"a\":\"" + "a".repeat(Math.max(0, length-8)) + "\"}";
  }

  private void arrangeProperties() {
    when(cdhOauthProperties.getSubscriptionKeyHeaderName()).thenReturn("key");
    when(cdhOauthProperties.getBookingSubscriptionKey()).thenReturn("SubscriptionKey");
    when(cdhApiProperties.getRequestHeaderName()).thenReturn("name");
    when(cdhApiProperties.getRequestHeaderValue()).thenReturn("name");
  }
}
