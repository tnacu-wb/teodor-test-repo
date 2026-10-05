package uk.co.whitbread.basket.infrastructure.rest.client.rules.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.rules.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.basket.generated.models.rules.VatRuleResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.exception.RulesAgentException;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.properties.RulesAgentClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class RulesAgentClient {

  private final WebClient rulesAgentWebClient;
  private final RulesAgentClientProperties rulesAgentClientProperties;

  public RulesAgentClient(
      @Qualifier("rulesAgentWebClient") WebClient rulesAgentWebClient,
      RulesAgentClientProperties rulesAgentClientProperties) {
    this.rulesAgentWebClient = rulesAgentWebClient;
    this.rulesAgentClientProperties = rulesAgentClientProperties;
  }

  public BusinessAllowanceRuleResponseDto getBusinessAllowances() {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentClientProperties.getBusinessAllowanceEndpoint())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(BusinessAllowanceRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "An error was returned by Rules Agent when retrieving business allowances!"))
        .block();
  }

  public VatRuleResponseDto getVatCodes(String vatRegion, List<String> pkgCodeArr) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentClientProperties.getVatCodesEndpoint())
            .queryParam("vatRegion", vatRegion)
            .queryParam("pkgCodeArr", pkgCodeArr)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(VatRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "An error was returned by Rules Agent when retrieving VAT codes!"))
        .block();
  }
}
