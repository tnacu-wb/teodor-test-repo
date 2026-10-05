package uk.co.whitbread.infrastructure.rest.client.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.rest.client.accounts.exceptions.AccountServiceException;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetailsResponse;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.properties.AccountServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class AccountServiceClient {

  private final WebClient accountServiceWebClient;
  private final AccountServiceProperties accountServiceProperties;
  private static final String AUTHORIZATION_HEADER = "Authorization";

  public CompanyDetailsResponse getCompanyDetails(
      String authorization, String companyId) {
    log.debug("Entered getCompanyDetails for companyId={}", companyId);

    return accountServiceWebClient.get()
        .uri(uriBuilder -> {
          var requestBuilder = uriBuilder
              .path(accountServiceProperties.getCompanyEndpoint())
              .build(companyId);
          log.info("Request builds {}", requestBuilder);
          return requestBuilder;
        })
        .header(AUTHORIZATION_HEADER, "Bearer " + authorization)
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(AccountServiceException.class);
        })
        .bodyToMono(CompanyDetailsResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                String.format("Error trying to get the detailed info for companyId=%s", companyId)))
        .block();
  }
}
