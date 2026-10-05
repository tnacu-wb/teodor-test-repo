package uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COOKIE_POLICIES_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in.CookiePoliciesInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.out.CookiePoliciesRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@RequiredArgsConstructor
@Component
@Slf4j
public class CookiePoliciesAemClient {

  private final AemProperties aemProperties;
  private final WebClient aemWebClient;

  public CookiePoliciesInformationDto getCookiePolicies(
      CookiePoliciesRequestAemDto cookiePoliciesRequestAemDto) {
    return aemWebClient.get().uri(
            uriBuilder -> uriBuilder.path(aemProperties.getCookiePoliciesEndpoint())
                .build(cookiePoliciesRequestAemDto.getCountry(),
                    cookiePoliciesRequestAemDto.getLanguage(),
                    cookiePoliciesRequestAemDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_COOKIE_POLICIES_EXCEPTION,
                  "Unable to get cookie policies.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(CookiePoliciesInformationDto.class)
        .doOnError(
            exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
