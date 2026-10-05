package uk.co.whitbread.payments.infrastructure.rest.client.customers.service;

import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.AUTHORIZATION;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.COMPANY_ID;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.EMPLOYEE_ID;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.SESSION_ID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.AccountsServiceException;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.CompanyDetailsResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.CompanyServiceClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
public class CompanyServiceClient {

  public static final String COMPANY_MICROSERVICE_ERROR_MESSAGE = "An error was returned by Company Microservice";
  private final WebClient companyServiceWebClient;
  private final CompanyServiceClientProperties companyServiceClientProperties;

  public CompanyServiceClient(@Qualifier("companyServiceWebClient") WebClient companyServiceWebClient,
                              CompanyServiceClientProperties companyServiceClientProperties) {
    this.companyServiceWebClient = companyServiceWebClient;
    this.companyServiceClientProperties = companyServiceClientProperties;
  }

  public CompanyDetailsResponseDto findCompany(String companyId, String sessionId,
                                               String employeeId, String token) {
    return companyServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(companyServiceClientProperties.getCompanyEndpoint())
            .build(companyId))
        .header(SESSION_ID, sessionId)
        .header(EMPLOYEE_ID, employeeId)
        .header(COMPANY_ID, companyId)
        .header(AUTHORIZATION, token)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AccountsServiceException(ErrorCode.FIND_COMPANY_BAD_REQUEST_EXCEPTION,
                COMPANY_MICROSERVICE_ERROR_MESSAGE));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new PaymentMethodsException(ErrorCode.FIND_COMPANY_EXCEPTION,
                COMPANY_MICROSERVICE_ERROR_MESSAGE));
        })
        .bodyToMono(CompanyDetailsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to find company details!"))
        .block();
  }
}
