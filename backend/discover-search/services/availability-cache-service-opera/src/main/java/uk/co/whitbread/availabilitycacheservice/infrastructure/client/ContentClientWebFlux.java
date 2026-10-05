package uk.co.whitbread.availabilitycacheservice.infrastructure.client;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.exception.ContentException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.exception.NoHeaderDataException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.content.ContentServiceProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

@Slf4j
@Component
public class ContentClientWebFlux {

  private static final String BRAND = "pi";
  private static final String CHANNEL = "PI";
  private static final String SUB_CHANNEL = "WEB";
  private final ContentServiceProperties contentProperties;
  private final WebClient contentWebClient;

  public ContentClientWebFlux(@Qualifier("contentServiceWebClient") WebClient contentWebClient,
      ContentServiceProperties contentProperties) {
    this.contentProperties = contentProperties;
    this.contentWebClient = contentWebClient;
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "GlobalConfigCache")
  public GlobalConfigDto getGlobalConfig(String country, String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getGlobalConfigEndpoint())
            .queryParam("country", isNotEmpty(country) ? country : "gb")
            .queryParam("language", isNotEmpty(language) ? language : "en")
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
        .doOnError(ex ->
            ExceptionLogger.log(log, ex, "Error while trying to get global config"))
        .block();
  }

  public HotelInformationExtendedDto getHotelInformation(String country, String language, String hotelId) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getHotelInformationEndpoint())
            .queryParam("country", isNotEmpty(country) ? country : "gb")
            .queryParam("language", isNotEmpty(language) ? language : "en")
            .queryParam("channel", CHANNEL)
            .queryParam("subchannel", SUB_CHANNEL)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoHeaderDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(HotelInformationExtendedDto.class)
        .doOnError(ex ->
            ExceptionLogger.log(log, ex,
                    String.format("Error while trying to get hotel information for hotel: %s", hotelId)))
        .block();
  }
}