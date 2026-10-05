package uk.co.whitbread.content.infrastructure.rest.client.countries.aem.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COUNTIES_INFO_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.in.CountriesResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.out.CountriesRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class CountriesAemClient {

  private final AemProperties aemProperties;
  private final WebClient aemWebClient;

  public CountriesResponseAemDto getCountries(
      CountriesRequestAemDto countriesRequestAemDto) {

    log.debug("Entered getCountries with country={}, language={}, site={}",
        countriesRequestAemDto.getCountry(), countriesRequestAemDto.getLanguage(),
        countriesRequestAemDto.getSite());

    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(aemProperties.getCountriesEndpoint())
                .build(countriesRequestAemDto.getCountry(),
                    countriesRequestAemDto.getLanguage(),
                    countriesRequestAemDto.getSite()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_COUNTIES_INFO_EXCEPTION, "Unable to counties.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(CountriesResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
