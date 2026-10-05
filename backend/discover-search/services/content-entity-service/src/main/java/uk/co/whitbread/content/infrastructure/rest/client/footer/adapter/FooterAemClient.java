package uk.co.whitbread.content.infrastructure.rest.client.footer.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_FOOTER_INFO_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.FooterResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.out.FooterRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class FooterAemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  public FooterResponseAemDto getFooterInformation(FooterRequestAemDto footerRequestAemDto) {
    log.debug("Entered getFooterInformation with country={}, language={}, brand={}",
        footerRequestAemDto.getCountry(), footerRequestAemDto.getLanguage(),
        footerRequestAemDto.getSite());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getFooterEndpoint())
            .build(footerRequestAemDto.getCountry(),
                footerRequestAemDto.getLanguage(),
                footerRequestAemDto.getSite()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_FOOTER_INFO_EXCEPTION,
                  "Unable to get footer information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(FooterResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }
}
