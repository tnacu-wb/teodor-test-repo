package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import feign.Response;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;


@RunWith(MockitoJUnitRunner.class)
class OAuthServiceErrorDecoderTest {

  String methodKey = "methodKey";
  private ObjectMapper objectMapper = new ObjectMapper();
  private OAuthServiceErrorDecoder errorDecoder = new OAuthServiceErrorDecoder(objectMapper);

  @SneakyThrows
  @Test
  void testDecodeResponse_shouldReturnException() {
    //Arrange
    Map<String, Collection<String>> headers = new HashMap<>();
    Request req = Request.create(HttpMethod.POST, "url", headers, Body.empty(), null);
    OauthClientError error = new OauthClientError();
    error.setError("abc");
    error.setStatus(400);
    error.setErrorDescription("desc");
    String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error);
    Response response = Response.builder()
        .status(500)
        .body(jsonString, StandardCharsets.UTF_8)
        .request(req).build();

    //Act & Assert
    var exception = errorDecoder.decode(methodKey, response);
    assertNotNull(exception);
    assertTrue(exception instanceof OauthClientException);
    assertEquals(ErrorCode.FEIGN_DECODER_CONTENT_EXCEPTION.getCode(),
        ((OauthClientException) exception).getErrorCode());
    assertEquals(
        "Error while trying to connect to client via feign. Response: "
            + "OauthClientError(status=500, error=abc, errorDescription=desc, errorCodes=null, "
            + "timestamp=null, traceId=null, correlationId=null, errorUri=null), methodKey: methodKey",
        exception.getMessage());
  }

  @Test
  void testDecodeNullResponse_shouldThrowException() {
    //Arrange

    //Act & Assert
    var exception = assertThrows(OauthClientException.class,
        () -> errorDecoder.decode(methodKey, null));
    assertEquals("Error on feign error decoder. Unknown error methodKey",
        exception.getMessage());
    assertEquals(ErrorCode.FEIGN_DECODER_UNKNOWN_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @SneakyThrows
  @Test
  void testDecodeInvalidResponse_shouldThrowException() {
    //Arrange
    Map<String, Collection<String>> headers = new HashMap<>();
    Request req = Request.create(HttpMethod.POST, "url", headers, Body.empty(), null);
    OauthClientError error = new OauthClientError();
    String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error);
    Response response = Response.builder()
        .status(500)
        .body(jsonString, StandardCharsets.UTF_8)
        .request(req).build();

    //Act & Assert
    OauthClientException exception = (OauthClientException) errorDecoder.decode(methodKey, response);
    Assertions.assertThat(exception.getMessage()).contains("Error while trying to connect to client via feign.");
    assertEquals(ErrorCode.FEIGN_DECODER_CONTENT_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @SneakyThrows
  @Test
  void testDecodeParseException_shouldThrowException() {
    //Arrange
    Map<String, Collection<String>> headers = new HashMap<>();
    Request req = Request.create(HttpMethod.POST, "url", headers, Body.empty(), null);
    Exception error = new Exception("abc");
    String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error);
    Response response = Response.builder()
        .status(500)
        .body(jsonString, StandardCharsets.UTF_8)
        .request(req).build();

    //Act & Assert
    var exception = assertThrows(OauthClientException.class,
        () -> errorDecoder.decode(methodKey, response));
    assertEquals(
        "Error on feign error decoder. Response status= 500. "
            + "Error parsing Exception coming from service methodKey",
        exception.getMessage());
    assertEquals(ErrorCode.FEIGN_DECODER_PARSE_EXCEPTION.getCode(), exception.getErrorCode());
  }
}
