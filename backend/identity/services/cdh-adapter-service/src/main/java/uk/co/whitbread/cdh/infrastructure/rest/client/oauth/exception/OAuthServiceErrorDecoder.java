package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@RequiredArgsConstructor
public class OAuthServiceErrorDecoder implements ErrorDecoder {

  private static final String CONNECT_TO_CLIENT_VIA_FEIGN =
       "Error while trying to connect to client via feign. Response: %s, methodKey: %s";
  private static final String PARSING_EXCEPTION_COMING_FROM_FEIGN =
      "%sResponse status= %s. Error parsing Exception coming from service %s";
  private static final String TRYING_TO_EXTRACT_FEIGN_CLIENT_RESPONSE =
      "%sResponse status= %s. Error while trying to extract feign client response content %s";
  private static final String UNKNOWN_ERROR = "%sUnknown error %s";
  private static final String DECODER_EXCEPTION_MESSAGE = "Error on feign error decoder. ";

  private final ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {
    return extractContent(response, methodKey);
  }

  private OauthClientException extractContent(Response response, String methodKey) {
    if (response != null) {
      int status = response.status();
      try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
        StreamUtils.copy(response.body().asInputStream(), output);
        final OauthClientError oauthClientError = objectMapper.readValue(output.toByteArray(), OauthClientError.class);
        oauthClientError.setStatus(status);
        String message = String.format(CONNECT_TO_CLIENT_VIA_FEIGN, oauthClientError, methodKey);
        var exception = new OauthClientException(ErrorCode.FEIGN_DECODER_CONTENT_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        return exception;
      } catch (JsonParseException | JsonMappingException ex) {
        String errorMessage = String.format(PARSING_EXCEPTION_COMING_FROM_FEIGN,
            DECODER_EXCEPTION_MESSAGE, status, methodKey);
        log.error(DECODER_EXCEPTION_MESSAGE, ex);
        var exception = new OauthClientException(ErrorCode.FEIGN_DECODER_PARSE_EXCEPTION,
            errorMessage);
        ExceptionLogger.log(log, exception);
        throw exception;
      } catch (Exception ex) {
        log.error(DECODER_EXCEPTION_MESSAGE, ex);
        String errorMessage = String.format(
            TRYING_TO_EXTRACT_FEIGN_CLIENT_RESPONSE, DECODER_EXCEPTION_MESSAGE, status, methodKey);
        var exception = new OauthClientException(ErrorCode.FEIGN_DECODER_EXCEPTION, errorMessage);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    String errorMessage = String.format(UNKNOWN_ERROR, DECODER_EXCEPTION_MESSAGE, methodKey);
    var exception = new OauthClientException(ErrorCode.FEIGN_DECODER_UNKNOWN_EXCEPTION,
        errorMessage);
    ExceptionLogger.log(log, exception);
    throw exception;
  }
}
