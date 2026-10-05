package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_CARD_MANAGEMENT_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COMMON_ICONS_INFO_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CardManagementContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CommonIconsResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CardManagementRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CommonIconsRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class CardManagementAemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  /** CommonIcons Information.
   * AEM integration for INN-BUSINESS common icons.
   * return CommonIconsResponseAemDto.
   */
  public CommonIconsResponseAemDto getCommonIconsInformation(
      CommonIconsRequestAemDto commonIconsRequestAemDto) {
    log.debug("Entered getCommonIconsInformation with language={}",
        commonIconsRequestAemDto.getLanguage());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getInnbCommonIconsEndpoint())
            .build(commonIconsRequestAemDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_COMMON_ICONS_INFO_EXCEPTION,
                  "Unable to get InnBusiness common icons information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(CommonIconsResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  /** CardManagementContentInformation.
   * AEM integration for INN-BUSINESS card management content.
   * return CardManagementContentResponseAem.
   */
  public CardManagementContentResponseAemDto getCardManagementContentInformation(
      CardManagementRequestAemDto cardManagementRequestAemDto) {
    log.debug("Entered getCardManagementContentInformation with language={}",
        cardManagementRequestAemDto.getLanguage());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getInnbCardManagementEndpoint())
            .build(cardManagementRequestAemDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_CARD_MANAGEMENT_INFO_EXCEPTION,
                  "Unable to get InnBusiness card management content information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(CardManagementContentResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

}
