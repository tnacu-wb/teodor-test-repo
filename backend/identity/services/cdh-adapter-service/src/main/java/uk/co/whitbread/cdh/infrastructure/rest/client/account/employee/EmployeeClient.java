package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee;

import static java.util.Collections.singletonList;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.CompanyEmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;

@Slf4j
@Component
public class EmployeeClient {

  private static final String BEARER_PREFIX = "Bearer";
  private static final String ACCESS_CONTEXT = "AccessContext";
  private static final String ACCESSED_BY = "AccessedBy";
  public static final String AWAITING_APPROVAL = "AwaitingApproval";
  public static final String PAGE_TOKEN = "PageToken";
  public static final String PAGE_SIZE = "PageSize";
  public static final String ACCESS_LEVEL = "AccessLevel";
  private static final String EXCEPTION = "Retrieved exception from CDH, response status = %s on %s";
  private static final int RESPONSE_RETRY_ATTEMPTS = 3;
  private static final Duration RESPONSE_RETRY_BACKOFF = Duration.ofSeconds(2);
  private final CdhApiProperties cdhApiProperties;
  private final WebClient cdhAccountServicesWebclient;
  private final OAuthProvider oAuthProvider;
  private final WebClientProperties webClientProperties;

  public EmployeeClient(CdhApiProperties cdhApiProperties,
                       @Qualifier("cdhAccountServicesWebclient") WebClient cdhAccountServicesWebclient,
                       OAuthProvider oauthProvider,
                       WebClientProperties webClientProperties) {
    this.cdhAccountServicesWebclient = cdhAccountServicesWebclient;
    this.cdhApiProperties = cdhApiProperties;
    this.oAuthProvider = oauthProvider;
    this.webClientProperties = webClientProperties;
    log.info("EmployeeClient initialized with blocking timeout: {} seconds",
        webClientProperties.getBlockingTimeout());
  }

  public GetEmployeeResponse getEmployee(GetEmployeeRequest getEmployeeRequest) {

    String companyAccountId = getEmployeeRequest.getCompanyAccountId();
    String employeeAccountId = getEmployeeRequest.getEmployeeAccountId();

    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetEmployeeEndpoint())
            .build(companyAccountId, employeeAccountId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, getEmployeeRequest.getAccessContext())
        .header(ACCESSED_BY, getEmployeeRequest.getAccessedBy())
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("Employee with companyAccountId {} and employeeAccountId {} not found in CDH",
              companyAccountId, employeeAccountId);
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToMono(GetEmployeeResponse.class)
        .block();
  }

  public GetEmployeeResponse getEmployeeV2(EmployeeRequestDto getEmployeeRequest) {

    String companyAccountId = getEmployeeRequest.getCompanyAccountId();
    String employeeAccountId = getEmployeeRequest.getEmployeeAccountId();

    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getEmployeeEndpointV2())
            .build(companyAccountId, employeeAccountId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, getEmployeeRequest.getAccessContext())
        .header(ACCESSED_BY, getEmployeeRequest.getAccessedBy())
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("GetEmployeeV2: Employee with companyAccountId {} and employeeAccountId {} not found in CDH",
              companyAccountId, employeeAccountId);
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToMono(GetEmployeeResponse.class)
        .block();
  }

  public GetEmployeesResponse getEmployees(EmployeeSearchCriteria employeeSearchCriteria) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (StringUtils.isNotEmpty(employeeSearchCriteria.getGlobalCompanyId())) {
      params.put("GlobalCompanyId", singletonList(employeeSearchCriteria.getGlobalCompanyId()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getBartEmployeeId())) {
      params.put("BartEmployeeId", singletonList(employeeSearchCriteria.getBartEmployeeId()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getBartGuestHistoryNumber())) {
      params.put("BartGuestHistoryNumber", singletonList(employeeSearchCriteria.getBartGuestHistoryNumber()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getActivationKey())) {
      params.put("BartGuestHistoryNumber", singletonList(employeeSearchCriteria.getBartGuestHistoryNumber()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getPageToken())) {
      params.put("PageToken", singletonList(employeeSearchCriteria.getPageToken()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getPageSize())) {
      params.put("PageSize", singletonList(employeeSearchCriteria.getPageSize()));
    }
    if (StringUtils.isNotEmpty(employeeSearchCriteria.getEmailAddress())) {
      params.put("EmailAddress", singletonList(employeeSearchCriteria.getEmailAddress()));
    }
    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetEmployeesEndpoint())
            .queryParams(params)
            .build())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, employeeSearchCriteria.getAccessContext())
        .header(ACCESSED_BY, employeeSearchCriteria.getAccessedBy())
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("Employee with email {} and employeeAccountId {} not found in CDH",
              employeeSearchCriteria.getEmailAddress(), employeeSearchCriteria.getBartEmployeeId());
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToMono(GetEmployeesResponse.class)
        .block();
  }

  public GetEmployeesResponse getEmployeesV2(EmployeeSearchCriteriaDto employeeSearchCriteria,
                                             String accessedBy, String accessContext) {

    return cdhAccountServicesWebclient
        .post()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getEmployeesEndpointV2())
            .build())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, accessContext)
        .header(ACCESSED_BY, accessedBy)
        .body(Mono.just(employeeSearchCriteria), EmployeeSearchCriteriaDto.class)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("GetEmployeesV2: Employee with email {} and employeeAccountId {} not found in CDH",
              employeeSearchCriteria.getEmailAddress(), employeeSearchCriteria.getBartEmployeeId());
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToMono(GetEmployeesResponse.class)
        .block();
  }
  
  /**
   * Retrieves all employees for a specific company with pagination support.
   * Uses configurable blocking timeout to handle large datasets that require multiple API calls.
   *
   * @param companyAccountId Company account identifier
   * @param accessedBy User email making the request
   * @param pageSize Number of results per page
   * @param pageToken Token for pagination continuation
   * @param accessContext Access context (e.g., "PI")
   * @param awaitingApproval Filter by approval status
   * @return GetEmployeesResponse containing employee data
   */
  public GetEmployeesResponse getCompanyEmployees(String companyAccountId, String accessedBy, Integer pageSize,
                                                  String pageToken, String accessContext, Boolean awaitingApproval) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    if (Objects.nonNull(pageSize) && pageSize > 0) {
      params.add(PAGE_SIZE, String.valueOf(pageSize));
    }
    if (StringUtils.isNotEmpty(pageToken)) {
      params.add(PAGE_TOKEN, pageToken);
    }
    if (Objects.nonNull(awaitingApproval)) {
      params.add(AWAITING_APPROVAL, String.valueOf(awaitingApproval));
    } else {
      params.add(AWAITING_APPROVAL, null);
    }
    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetCompanyEmployeeEndpoint())
            .queryParams(params)
            .build(companyAccountId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, accessContext)
        .header(ACCESSED_BY, accessedBy)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("Employees from company {} are not found in CDH", companyAccountId);
          CDHException cdhException = new CDHException(
              ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION, String.format(EXCEPTION, responseType.statusCode(),
                  responseType.request().getURI().getPath()));
          return Mono.error(cdhException);
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToFlux(GetEmployeesResponse.class)
        .retryWhen(Retry.backoff(RESPONSE_RETRY_ATTEMPTS, RESPONSE_RETRY_BACKOFF)
            .filter(EmployeeClient::isPrematureCloseException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())
            .doBeforeRetry(retrySignal -> log.warn(
                "Retrying company employees request after premature connection close. Attempt {} of {}",
                retrySignal.totalRetries() + 1, RESPONSE_RETRY_ATTEMPTS)))
        .onErrorMap(EmployeeClient::isPrematureCloseException,
            exception -> new CDHException(ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION,
                String.format("Connection closed before CDH response body completed on %s",
                    cdhApiProperties.getGetCompanyEmployeeEndpoint())))
        .blockLast(Duration.ofSeconds(webClientProperties.getBlockingTimeout()));
  }

  public GetEmployeesResponse getCompanyEmployeesV2(CompanyEmployeeSearchCriteriaDto companyEmployeeSearchCriteriaDto,
                                                    String companyAccountId, String accessedBy, String accessContext) {
    return cdhAccountServicesWebclient
        .post()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getCompanyEmployeesEndpointV2())
            .build(companyAccountId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, accessContext)
        .header(ACCESSED_BY, accessedBy)
        .body(Mono.just(companyEmployeeSearchCriteriaDto), CompanyEmployeeSearchCriteriaDto.class)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.error("GetCompanyEmployeesV2: Employees from company {} are not found in CDH", companyAccountId);
          CDHException cdhException = new CDHException(
              ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION, String.format(EXCEPTION, responseType.statusCode(),
              responseType.request().getURI().getPath()));
          return Mono.error(cdhException);
        })
        .onStatus(HttpStatusCode::isError, this::throwCdhException)
        .bodyToFlux(GetEmployeesResponse.class)
        .retryWhen(Retry.backoff(RESPONSE_RETRY_ATTEMPTS, RESPONSE_RETRY_BACKOFF)
            .filter(EmployeeClient::isPrematureCloseException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())
            .doBeforeRetry(retrySignal -> log.warn(
                "GetCompanyEmployeesV2: Retrying company employees request after premature connection close."
                    + " Attempt {} of {}",
                retrySignal.totalRetries() + 1, RESPONSE_RETRY_ATTEMPTS)))
        .onErrorMap(EmployeeClient::isPrematureCloseException,
            exception -> new CDHException(ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION,
                String.format("Connection closed before CDH response body completed on %s",
                    cdhApiProperties.getCompanyEmployeesEndpointV2())))
        .blockLast(Duration.ofSeconds(webClientProperties.getBlockingTimeout()));
  }

  private Mono<CDHException> throwCdhException(ClientResponse response) {

    log.error("CDH API Response --Status: {}; --Headers: {}",
        response.statusCode(), response.headers().asHttpHeaders());
    CDHException cdhException = new CDHException(
        ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION,
        String.format(EXCEPTION, response.statusCode(),
            response.request().getURI().getPath()));
    return Mono.error(cdhException);
  }

  private static boolean isPrematureCloseException(Throwable exception) {
    return exception instanceof PrematureCloseException
        || exception instanceof WebClientRequestException
        && exception.getCause() instanceof PrematureCloseException;
  }
}
