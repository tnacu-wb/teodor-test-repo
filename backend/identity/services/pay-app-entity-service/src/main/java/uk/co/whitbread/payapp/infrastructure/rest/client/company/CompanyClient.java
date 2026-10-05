package uk.co.whitbread.payapp.infrastructure.rest.client.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.exceptions.CompanyResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.properties.CompanyProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyClient {

  private final WebClient companyWebClient;
  private final CompanyProperties companyProperties;

  private static final String ERROR_MESSAGE =
      "Error while trying to get company details from company service. Response: ";
  private static final String BEARER = "Bearer ";

  public CompanyDetailsResponseDto getCompanyDetails(String companyId, String authorization) {
    return companyWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(companyProperties.getGetCompanyDetailsEndpoint())
            .build(companyId))
        .header(HttpHeaders.AUTHORIZATION, BEARER + authorization)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(Object.class)
                .flatMap(body -> {
                  log.error(ERROR_MESSAGE + "{}", body);
                  return Mono.error(new CompanyResponseException(
                      ErrorCode.COMPANY_GET_ERROR, ERROR_MESSAGE + body));
                }))
        .bodyToMono(CompanyDetailsResponseDto.class)
        .doOnError(exception -> log.error(
            "Error while trying to get company details from company service with companyId={}",
            companyId, exception))
        .block();
  }

}
