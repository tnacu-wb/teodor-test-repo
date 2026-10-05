package uk.co.whitbread.account.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;

@Slf4j
public class GlobalErrorHandler {

  /**
   * Global Error Handler to capture error messages from WebClient error responses from downstream client.
   */
  public static final String WEBCLIENT_SERVICE_ERROR_MESSAGE =
      "An error was returned by Webclient Service, Response (code: %s, details: %s).";
  public static final String WEBCLIENT_SERVICE_REQUEST_INCORRECT =
      "Request to Webclient Service incorrect, Response (code: %s, details: %s).";

  public static Mono<ResponseStatusException> handleError(ClientResponse clientResponse, ErrorCode errorCodeRequest,
                                                          ErrorCode errorCode) {
    return clientResponse
        .bodyToMono(ErrorResponse.class)
        .flatMap(p -> {
          log.error("Response status: {}", clientResponse.statusCode());
          log.error("Response headers: {}", clientResponse.headers().asHttpHeaders());
          log.error("Response body: {}", p);

          if (clientResponse.statusCode().is4xxClientError()) {
            return Mono.error(new ServiceRequestException(errorCodeRequest,
                String.format(WEBCLIENT_SERVICE_REQUEST_INCORRECT, p.getCode(), p.getDetails())));
          } else {
            return Mono.error(new ServiceException(errorCode,
                String.format(WEBCLIENT_SERVICE_ERROR_MESSAGE, p.getCode(), p.getDetails())));
          }
        });
  }
}