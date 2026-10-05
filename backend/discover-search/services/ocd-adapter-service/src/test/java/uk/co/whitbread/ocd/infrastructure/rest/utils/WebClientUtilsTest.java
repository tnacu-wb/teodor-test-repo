package uk.co.whitbread.ocd.infrastructure.rest.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class WebClientUtilsTest {

  @Test
  void testLogErrorHeader() {
    Logger logger = mock(Logger.class);
    ClientResponse response = mock(ClientResponse.class);
    ClientResponse.Headers headers = mock(ClientResponse.Headers.class);

    when(response.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    when(response.headers()).thenReturn(headers);
    when(headers.asHttpHeaders()).thenReturn(new HttpHeaders());

    WebClientUtils.logErrorHeader(logger, response);

    verify(logger).error("Response status: {}", HttpStatus.BAD_REQUEST);
  }

  @Test
  void testLogErrorResponse() {
    Logger logger = mock(Logger.class);
    ClientResponse response = mock(ClientResponse.class);
    ClientResponse.Headers headers = mock(ClientResponse.Headers.class);

    when(response.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(response.headers()).thenReturn(headers);
    when(headers.asHttpHeaders()).thenReturn(new HttpHeaders());
    when(response.bodyToMono(Object.class)).thenReturn(Mono.just("TestBody"));

    WebClientUtils.logErrorResponse(logger, response);

    verify(logger).error("Response status: {}", HttpStatus.INTERNAL_SERVER_ERROR);
    verify(logger, timeout(100)).error("Response body: {}", "TestBody");
  }
}
