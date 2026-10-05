package uk.co.whitbread.infrastructure.rest.client.content;


import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.HotelAvailabilityBadReqException;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.content.generated.models.MealsInfoResponseDto;
import uk.co.whitbread.hotel.content.generated.models.SearchRulesDto;
import uk.co.whitbread.infrastructure.config.ContentServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.cache.exceptions.ContentServiceException;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class ContentServiceClient {

  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";

  private static final String HOTEL_ID = "hotelId";
  private static final String BRAND = "pi";
  private static final String CHANNEL = "PI";
  private static final String SUB_CHANNEL = "WEB";
  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private final WebClient contentServiceWebClient;
  private final ContentServiceProperties contentServiceProperties;


  @Async
  public void triggerHotelFacilitiesFilterUpdate() {
    log.info("Entered triggerHotelFacilitiesFilterUpdate");

    contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(contentServiceProperties.getHotelFacilitiesFilterUpdateEndpoint())
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to triggerHotelFacilitiesFilterUpdate"))
        .block();
  }

  @Async
  public void triggerHotelsOpeningSoonCacheUpdate() {
    log.info("Entered triggerHotelsOpeningSoonCacheUpdate");

    contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(contentServiceProperties.getHotelsOpeningSoonCacheUpdateEndpoint())
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to triggerHotelsOpeningSoonCacheUpdate"))
        .block();
  }

  public ExtrasLabelDto getExtrasLabels(String country, String language) {
    log.info("Entered getExtrasLabels");

    return contentServiceWebClient.get()
              .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getExtrasLabels())
                      .queryParam(COUNTRY, country)
                      .queryParam(LANGUAGE, language)
                      .build())
              .retrieve()
              .onStatus(HttpStatusCode::isError, response -> {
                WebClientUtils.logErrorHeader(log, response);
                return response.bodyToMono(ContentServiceException.class);
              })
              .bodyToMono(ExtrasLabelDto.class)
              .doOnError(exception -> ExceptionLogger.log(log, exception,
                      "Error while trying to get extrasLabels"))
              .block();
  }

  public String getHotelBrand(String hotelId) {
    var hotelInformation = contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getHotelInformationEndpoint())
            .queryParam(COUNTRY, COUNTRY_GB)
            .queryParam(LANGUAGE, LANGUAGE_EN)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityBadReqException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .bodyToMono(HotelInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to get hotelInformation Details"))
        .block();
    return hotelInformation != null ? hotelInformation.getBrand() : "";
  }

  public MealsInfoResponseDto getUpsellItemsAndSoftBundles(String hotelId, String country, String language) {
    return contentServiceWebClient.get()
            .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getUpsellItemsEndpoint())
                    .queryParam(COUNTRY, country)
                    .queryParam(LANGUAGE, language)
                    .queryParam(HOTEL_ID, hotelId)
                    .build())
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
              WebClientUtils.logErrorHeader(log, response);
              return response.bodyToMono(ContentServiceException.class);
            })
            .bodyToMono(MealsInfoResponseDto.class)
            .doOnError(exception -> ExceptionLogger.log(log, exception,
                    "Error while trying to get upsellItems for hotel " + hotelId))
            .block();
  }

  public SearchRulesDto getSearchRules(String channel, Optional<String> brand) {
    return contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getSearchRulesEndpoint())
            .queryParam("channelId", channel)
            .queryParamIfPresent("brand", brand)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .bodyToMono(SearchRulesDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get searchRules for channel=%s, brand=%s",
                channel, brand.orElse(null))))
        .block();
  }

  public Map<String, String> getPreferencesLabels(String country, String language, String category, String label) {
    log.info("Entered getCategoryLabels");

    return contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getCategoryLabelsEndpoint())
            .queryParam(COUNTRY, country)
            .queryParam(LANGUAGE, language)
            .queryParam("labels", label)
            .build(category))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get preference labels"))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "GlobalConfigCache")
  public GlobalConfigDto getGlobalConfig(String country, String language) {
    return contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getGlobalConfigEndpoint())
            .queryParam(COUNTRY, nonNull(country) ? country : COUNTRY_GB)
            .queryParam(LANGUAGE, nonNull(language) ? language : LANGUAGE_EN)
            .queryParam("brand", BRAND)
            .queryParam("channelId", CHANNEL)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .bodyToMono(GlobalConfigDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Error while trying to get global config"))
        .block();
  }

  public HotelInformationExtendedDto getHotelInformation(String country, String language, String hotelId) {
    return contentServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(contentServiceProperties.getHotelInformationEndpoint())
            .queryParam(COUNTRY, nonNull(country) ? country : COUNTRY_GB)
            .queryParam(LANGUAGE, nonNull(language) ? language : LANGUAGE_EN)
            .queryParam("channel", CHANNEL)
            .queryParam("subchannel", SUB_CHANNEL)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(ContentServiceException.class);
        })
        .bodyToMono(HotelInformationExtendedDto.class)
        .doOnError(ex ->
            ExceptionLogger.log(log, ex,
                String.format("Error while trying to get hotel information for hotel: %s", hotelId)))
        .block();
  }
}
