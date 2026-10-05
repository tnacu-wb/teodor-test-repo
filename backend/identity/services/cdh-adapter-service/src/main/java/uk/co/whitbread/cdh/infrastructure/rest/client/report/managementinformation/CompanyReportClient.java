package uk.co.whitbread.cdh.infrastructure.rest.client.report.managementinformation;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyReportClient {
  private static final String EXCEPTION = "Retrieved exception from CDH, response status = %s on %s";
  private static final String BEARER_PREFIX = "Bearer";
  private static final String ACCESS_CONTEXT = "AccessContext";
  private static final String ACCESSED_BY = "AccessedBy";

  private final CdhApiProperties cdhApiProperties;

  private final WebClient cdhAccountServicesWebclient;

  private final OAuthProvider oAuthProvider;

  private final CdhApiOauthProperties cdhApiOauthProperties;

  public CompanyReports getManagementInformation(ManagementInformation managementInformation,
      String companyId, String accessedBy, String accessContext) {

    HttpHeaders headers = getHttpHeadersForCdh(accessedBy, accessContext);
    return cdhAccountServicesWebclient
        .post()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetManagementInformationEndpoint())
            .build(companyId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .body(Mono.just(managementInformation), ManagementInformation.class)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("Company data not found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError,
            response -> throwCdhException(response, ErrorCode.CDH_MANAGEMENT_INFO_EXCEPTION))
        .bodyToMono(CompanyReports.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public EmergencyReportResults getEmergencyReport(EmergencyReport emergencyReport,
      String companyId, String accessedBy, String accessContext) {

    HttpHeaders headers = getHttpHeadersForCdh(accessedBy, accessContext);
    return cdhAccountServicesWebclient
        .post()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetEmergencyReportEndpoint())
            .build(companyId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .body(Mono.just(emergencyReport), EmergencyReport.class)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("Company data not found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError,
            response -> throwCdhException(response, ErrorCode.CDH_EMERGENCY_REPORT_EXCEPTION))
        .bodyToMono(EmergencyReportResults.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  private HttpHeaders getHttpHeadersForCdh(String accessedBy, String accessContext) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
        cdhApiOauthProperties.getAccountSubscriptionKey());
    headers.add(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue());
    headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken());
    headers.add(ACCESS_CONTEXT, accessContext);
    headers.add(ACCESSED_BY, accessedBy);
    return headers;
  }

  private Mono<CDHException> throwCdhException(ClientResponse response, ErrorCode code) {
    log.error("CDH API Response --Status: {}; --Headers: {}",
        response.statusCode(), response.headers().asHttpHeaders());
    CDHException cdhException = new CDHException(
        code,
        String.format(EXCEPTION, response.statusCode(),
            cdhApiProperties.getGetCompaniesEndpoint()));
    return Mono.error(cdhException);
  }

}
