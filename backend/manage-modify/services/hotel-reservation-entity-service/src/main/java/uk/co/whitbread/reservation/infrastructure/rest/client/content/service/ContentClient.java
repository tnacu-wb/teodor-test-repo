package uk.co.whitbread.reservation.infrastructure.rest.client.content.service;

import static java.util.Objects.nonNull;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.IndexHeaderDataDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RateInformationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SearchRulesDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoHeaderDataException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoSearchRulesDataException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
public class ContentClient {

  private static final String BRAND = "pi";
  private final ContentProperties contentProperties;
  private final WebClient contentWebClient;

  public ContentClient(@Qualifier("contentWebClient") WebClient contentWebClient,
      ContentProperties contentProperties) {
    this.contentProperties = contentProperties;
    this.contentWebClient = contentWebClient;
  }

  public IndexHeaderDataDto getIndexHeaderData(String country, String language) {
    return contentWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getContentIndexHeaderEndpoint())
            .queryParam("country", country)
            .queryParam("language", language)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoHeaderDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(IndexHeaderDataDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to fetch index header data from content service."))
        .block();
  }

  public BusinessNotesResponseDto getBusinessNotes(String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getBusinessNotesEndpoint())
            .queryParam("lang", language)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(BusinessNotesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get business notes "
                + "from content service with lang=%s", language)))
        .block();
  }

  public HotelPaymentInformationDto getHotelPaymentInformation(String hotelId,
      String language, String country) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getHotelPaymentInformation())
            .queryParam("language", language)
            .queryParam("country", country)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoHeaderDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(HotelPaymentInformationDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(
                "Error while trying to get business notes from content service with lang=%s",
                hotelId)))
        .block();
  }

  public HotelInformationExtendedDto getHotelInformation(String hotelId, String country, String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getHotelInformationEndpoint())
            .queryParam("country", country)
            .queryParam("language", language)
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
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get hotelInformation for hotelId=%s", hotelId)))
        .block();
  }

  public RateInformationDto getHotelRateInformation(String hotelId, String country,
      String language) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getHotelRateInformationEndpoint())
            .queryParam("country", nonNull(country) ? country : "gb")
            .queryParam("language", nonNull(language) ? language : "en")
            .queryParam("brand", BRAND)
            .queryParam("hotelId", hotelId)
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
        .bodyToMono(RateInformationDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get hotelRateInformation for hotelId=%s",
                hotelId)))
        .block();
  }

  public SearchRulesDto getSearchRules(String channel, Optional<String> brand) {
    return contentWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentProperties.getSearchRulesEndpoint())
            .queryParam("channelId", channel)
            .queryParamIfPresent("brand", brand)
            .build())
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(NoSearchRulesDataException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(ContentException.class);
        })
        .bodyToMono(SearchRulesDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get searchRules for channel=%s, brand=%s",
                channel, brand.orElse(null))))
        .block();
  }
}
