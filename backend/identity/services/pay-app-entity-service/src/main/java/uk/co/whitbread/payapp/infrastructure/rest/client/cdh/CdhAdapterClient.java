package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.CDHException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.properties.CdhAdapterProperties;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;


@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private static final String ACCESS_CONTEXT = "InnBusiness";
  private static final String ERROR_MESSAGE =
      "Retrieved exception from CDH Adapter, response status = %s ";

  private final CdhAdapterProperties cdhAdapterProperties;
  private final WebClient cdhAdapterWebClient;

  public GetEmployeesResponse getEmployees(String email, String accessedBy) {
    return cdhAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(cdhAdapterProperties.getGetEmployeesEndpoint())
            .queryParam("emailAddress", email)
            .queryParam("accessContext", ACCESS_CONTEXT)
            .queryParam("accessedBy", accessedBy)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToMono(GetEmployeesResponse.class)
        .doOnError(exception -> log.error(
            "Error while trying to get employees from CDH Adapter with email={}, accessedBy={}",
            email, accessedBy, exception))
        .block();
  }

  private Mono<CDHException> throwCdhException(ClientResponse response) {
    log.error("CDH ADAPTER API Response --Status: {}; --Headers: {}",
        response.statusCode(), response.headers().asHttpHeaders());
    CDHException cdhException = new CDHException(
        ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION,
        String.format(ERROR_MESSAGE, response.statusCode()));
    return Mono.error(cdhException);
  }
}
