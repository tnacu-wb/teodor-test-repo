package uk.co.whitbread.cdh.infrastructure.rest.client.spending;

import java.util.List;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.mapper.EmployeeSpendClientMapper;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.model.EmployeeSpendResponse;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class EmployeeSpendReportClient {

  private static final String BEARER_PREFIX = "Bearer";
  private static final String ACCESS_CONTEXT = "AccessContext";
  private static final String ACCESSED_BY = "AccessedBy";
  private static final String EXCEPTION = "Retrieved exception from CDH, response status = %s on %s";

  private final CdhApiProperties cdhApiProperties;
  private final WebClient cdhAccountServicesWebclient;
  private final OAuthProvider oAuthProvider;
  private final CdhApiOauthProperties cdhApiOauthProperties;
  private final EmployeeSpendClientMapper employeeSpendClientMapper;

  public EmployeeSpendReportClient(CdhApiProperties cdhApiProperties,
                                   @Qualifier("cdhAccountServicesWebclient") WebClient cdhAccountServicesWebclient,
                                   OAuthProvider oauthProvider,
                                   CdhApiOauthProperties cdhApiOauthProperties,
                                   EmployeeSpendClientMapper employeeSpendClientMapper) {
    this.cdhApiProperties = cdhApiProperties;
    this.cdhAccountServicesWebclient = cdhAccountServicesWebclient;
    this.oAuthProvider = oauthProvider;
    this.cdhApiOauthProperties = cdhApiOauthProperties;
    this.employeeSpendClientMapper = employeeSpendClientMapper;
  }

  public List<EmployeeSpendReport> getEmployeeSpendReport(EmployeeSpendRequest request) {
    log.info("Calling CDH API to get employee spend report for companyAccountId={}, employeeAccountId={}",
        request.getCompanyAccountId(), request.getEmployeeAccountId());

    HttpHeaders headers = getHttpHeadersForCdh(request.getAccessedBy(), request.getAccessContext());
    MultiValueMap<String, String> params = buildQueryParams(request);

    List<EmployeeSpendResponse> responseList = cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(cdhApiProperties.getGetEmployeeSpendEndpoint())
            .queryParams(params)
            .build(request.getCompanyAccountId(), request.getEmployeeAccountId()))
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("Employee spend data not found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToFlux(EmployeeSpendResponse.class)
        .collectList()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();

    return responseList.stream()
        .map(employeeSpendClientMapper::toModel)
        .toList();
  }

  private MultiValueMap<String, String> buildQueryParams(EmployeeSpendRequest request) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("fromMonthYear", request.getFromMonthYear());
    params.add("toMonthYear", request.getToMonthYear());
    return params;
  }

  private HttpHeaders getHttpHeadersForCdh(String accessedBy, String accessContext) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
        cdhApiOauthProperties.getInnBusinessSubscriptionKey());
    headers.add(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue());
    headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken());
    headers.add(ACCESS_CONTEXT, accessContext);
    headers.add(ACCESSED_BY, accessedBy);
    return headers;
  }

  private Mono<CDHException> throwCdhException(ClientResponse response) {
    log.error("CDH API Response --Status: {}; --Headers: {}",
        response.statusCode(), response.headers().asHttpHeaders());
    CDHException cdhException = new CDHException(
        ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION,
        String.format(EXCEPTION, response.statusCode(),
            cdhApiProperties.getGetEmployeeSpendEndpoint()));
    return Mono.error(cdhException);
  }
}
