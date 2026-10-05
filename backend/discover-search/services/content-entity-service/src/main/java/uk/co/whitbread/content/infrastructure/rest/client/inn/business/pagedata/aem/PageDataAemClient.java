package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PAGE_DATA_EXCEPTION;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.DictionaryDataDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageDataAemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "PageDataCache", key = "{#request.language, #request.dictionaries}")
  public Map<String, Map<String, String>> getPageData(PageDataRequestDto request) {
    log.debug("Entered getPageData with country={}, language={}, dictionaries={}",
        request.getCountry(), request.getLanguage(), request.getDictionaries().size());
    return Flux.fromIterable(request.getDictionaries())
        .flatMap(dictionary -> {
          String pageEndpoint = getAemEndpoint(dictionary);
          return aemWebClient
              .get()
              .uri(uriBuilder -> uriBuilder.path(pageEndpoint)
                  .build(request.getLanguage()))
              .retrieve()
              .onStatus(HttpStatusCode::isError, response -> {
                WebClientUtils.logErrorResponse(log, response);
                return response.createException()
                    .map(exception -> new AemResponseException(
                        AEM_PAGE_DATA_EXCEPTION, "Unable to get data.",
                        exception))
                    .flatMap(Mono::error);
              })
              .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
              })
              .map(result -> new DictionaryDataDto(dictionary.getField(), result));
        })
        .collectMap(DictionaryDataDto::getDictionary, DictionaryDataDto::getData)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  private String getAemEndpoint(DictionaryEnumDto dictionary) {
    return switch (dictionary) {
      case LAYOUT_DICTIONARY -> aemProperties.getInnbCommonLayoutEndpoint();
      case CARD_MANAGEMENT_DICTIONARY -> aemProperties.getInnbCardManagementEndpoint();
      case COMMON_ICONS_DICTIONARY -> aemProperties.getInnbCommonIconsEndpoint();
      case USER_MANAGEMENT_DICTIONARY -> aemProperties.getInnbUserManagementEndpoint();
      case PROFILE_MANAGEMENT_DICTIONARY -> aemProperties.getInnbProfileManagementEndpoint();
      case COMPANY_MANAGEMENT_DICTIONARY -> aemProperties.getInnbCompanyManagementEndpoint();
      case HOMEPAGE_DICTIONARY -> aemProperties.getHomePageEndpoint();
      case SPENDING_REPORTING_DICTIONARY -> aemProperties.getSpendingReportingEndpoint();
      case PAY_APPLICATION_DICTIONARY -> aemProperties.getPayApplicationEndpoint();
      case AUTH_DICTIONARY -> aemProperties.getAuthEndpoint();
      case NOTIFICATIONS_DICTIONARY -> aemProperties.getNotificationsEndpoint();
      case CONTACT_US_DICTIONARY -> aemProperties.getInnbContactUsEndpoint();
    };
  }

}
