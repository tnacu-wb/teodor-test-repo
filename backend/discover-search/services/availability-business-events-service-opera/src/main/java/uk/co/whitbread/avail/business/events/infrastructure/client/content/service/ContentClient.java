package uk.co.whitbread.avail.business.events.infrastructure.client.content.service;

import static java.util.Objects.nonNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.exception.ContentException;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.exception.NoHeaderDataException;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.service.properties.ContentProperties;
import uk.co.whitbread.avail.business.events.infrastructure.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;

@Slf4j
@Component
public class ContentClient {

  private static final String BRAND = "pi";
  private static final String CHANNEL = "PI";
  private final ContentProperties contentProperties;
  private final WebClient contentWebClient;

  public ContentClient(@Qualifier("contentWebClient") WebClient contentWebClient,
      ContentProperties contentProperties) {
    this.contentProperties = contentProperties;
    this.contentWebClient = contentWebClient;
  }

  public GlobalConfigDto getGlobalConfig(String country, String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getGlobalConfigEndpoint())
            .queryParam("country", nonNull(country) ? country : "gb")
            .queryParam("language", nonNull(language) ? language : "en")
            .queryParam("brand", BRAND)
            .queryParam("channelId", CHANNEL)
            .build())
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoHeaderDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(GlobalConfigDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Error while trying to get global config"))
        .block();
  }
}