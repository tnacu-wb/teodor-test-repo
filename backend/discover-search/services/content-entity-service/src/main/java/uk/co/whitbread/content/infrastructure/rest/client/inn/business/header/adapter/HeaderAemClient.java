package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_HEADER_CONTENT_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_LAYOUT_INFO_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.HeaderContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.HeaderRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.LayoutRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class HeaderAemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  /** LayoutInformation.
   * AEM integration for INN-BUSINESS new layout.
   * return LayoutResponseAem.
   */
  public LayoutResponseAemDto getLayoutInformation(LayoutRequestAemDto layoutRequestAemDto) {
    log.debug("Entered getLayoutInformation with dictionary={}, language={}",
        layoutRequestAemDto.getDictionary(),
        layoutRequestAemDto.getLanguage());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getInnbLayoutEndpoint())
            .build(layoutRequestAemDto.getDictionary(),
                layoutRequestAemDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_LAYOUT_INFO_EXCEPTION,
                  "Unable to get InnBusiness layout information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(LayoutResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  /** HeaderContentInformation.
   * AEM integration for INN-BUSINESS new header content.
   * return HeaderContentResponseAem.
   */
  public HeaderContentResponseAemDto getHeaderContentInformation(
      HeaderRequestAemDto headerRequestAemDto) {
    log.debug("Entered getHeaderContentInformation with country={}, language={}",
        headerRequestAemDto.getCountry(),
        headerRequestAemDto.getLanguage());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getInnbContentEndpoint())
            .build(headerRequestAemDto.getCountry(),
                headerRequestAemDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_HEADER_CONTENT_INFO_EXCEPTION,
                  "Unable to get InnBusiness header content information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(HeaderContentResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }


}
