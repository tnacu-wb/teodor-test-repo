package uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ALL_HOTELS_DETAILS_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_APPS_HOMEPAGE_CONFIG_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_DLP_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_GET_LABELS_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_GLOBAL_CONFIG_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_HOTELS_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_INDEX_HEADER_DATA_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_LABELS_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PRICE_FINDER_CONFIG_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ROOM_CLASS_CONFIG_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_SEARCH_RESULT_DATA_EXCEPTION;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getAppsHomepageEndpoint;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getGlobalConfigEndpoint;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getLabelsEndpoint;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getPageDlpUriPath;

import java.util.List;
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
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsHomepageResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelShortInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.AemIndexHeaderDataDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderGlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data.AemSearchResultsDataDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemDlpProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemHomepageProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryLabelsDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LocalizationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;


@RequiredArgsConstructor
@Component
@Slf4j
public class AemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;
  private final AemDlpProperties aemDlpProperties;
  private final AemHomepageProperties aemAppsHomepageProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "LabelsCache", key = "{#labelsRequestDto.language,#labelsRequestDto.category}")
  public Map<String, String> getLabels(LabelsRequestDto labelsRequestDto) {
    log.debug("Entered getLabels with country={}, language={}, category={}",
        labelsRequestDto.getCountry(), labelsRequestDto.getLanguage(),
        labelsRequestDto.getCategory());
    var categoryEndpointUrl = getLabelsEndpoint(labelsRequestDto.getCategory(), aemProperties);
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(categoryEndpointUrl)
            .build(labelsRequestDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_GET_LABELS_EXCEPTION,
                  "Unable to get labels",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
        })
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "AllHotelDetailsCache", key = "{#hotelInformationDto.country,#hotelInformationDto.language}")
  public List<HotelShortInformationDto> getAllHotelDetails(
      HotelInformationDto hotelInformationDto) {
    log.debug("Entered getAllHotelDetails with country={}, language={}, hotelId={}",
        hotelInformationDto.getCountry(), hotelInformationDto.getLanguage(),
        hotelInformationDto.getHotelId());
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getAllHotelDetailsEndpoint())
            .build(hotelInformationDto.getCountry(), hotelInformationDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_ALL_HOTELS_DETAILS_EXCEPTION,
                  String.format("Hotels not found for country: %s and language: %s.",
                      hotelInformationDto.getCountry(), hotelInformationDto.getLanguage()),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(new ParameterizedTypeReference<List<HotelShortInformationDto>>() {
        })
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public AemIndexHeaderDataDto getIndexHeaderData(
      IndexHeaderDataRequestDto indexHeaderDataRequestDto) {
    log.debug("Entered getIndexHeaderData with country={}, language={}, businessBooker={}",
        indexHeaderDataRequestDto.getCountry(), indexHeaderDataRequestDto.getLanguage(),
        indexHeaderDataRequestDto.getBusinessBooker());
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                (indexHeaderDataRequestDto.getBusinessBooker() != null
                    && indexHeaderDataRequestDto.getBusinessBooker()
                    .equals(Boolean.TRUE))
                    ? aemProperties.getBbIndexHeaderDataEndpoint()
                    : aemProperties.getIndexHeaderDataEndpoint())
            .build(indexHeaderDataRequestDto.getCountry(),
                indexHeaderDataRequestDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_INDEX_HEADER_DATA_EXCEPTION,
                  "Unable to get index header data.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemIndexHeaderDataDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RoomClassConfigDto getRoomClassConfig(GlobalConfigRequestDto globalConfigRequestDto) {
    log.debug("Entered getRoomClassConfig with country={}, language={}, channelId={}, brand={}",
        globalConfigRequestDto.getCountry(), globalConfigRequestDto.getLanguage(),
        globalConfigRequestDto.getChannelId(), globalConfigRequestDto.getBrand());
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(getGlobalConfigEndpoint(globalConfigRequestDto.getChannelId(), aemProperties))
            .build(globalConfigRequestDto.getCountry(),
                globalConfigRequestDto.getLanguage(),
                globalConfigRequestDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_ROOM_CLASS_CONFIG_EXCEPTION,
                  "Unable to get room class config.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(RoomClassConfigDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public GlobalConfigDto getGlobalConfig(GlobalConfigRequestDto globalConfigRequestDto) {
    log.debug("Entered getGlobalConfig with country={}, language={}, channelId={}, brand={}",
        globalConfigRequestDto.getCountry(), globalConfigRequestDto.getLanguage(),
        globalConfigRequestDto.getChannelId(), globalConfigRequestDto.getBrand());
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(getGlobalConfigEndpoint(globalConfigRequestDto.getChannelId(), aemProperties))
            .build(globalConfigRequestDto.getCountry(),
                globalConfigRequestDto.getLanguage(),
                globalConfigRequestDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_GLOBAL_CONFIG_EXCEPTION,
                  "Unable to get global config data.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(GlobalConfigDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public AemSearchResultsDataDto getSearchResultsData(
      LocalizationRequestDto localizationRequestDto) {
    log.debug("Entered getSearchResultsData with country={}, language={}",
        localizationRequestDto.getCountry(), localizationRequestDto.getLanguage());
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getSearchResultsDataEndpoint())
            .build(localizationRequestDto.getCountry(),
                localizationRequestDto.getLanguage()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_SEARCH_RESULT_DATA_EXCEPTION,
                  "Unable to get search results data.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemSearchResultsDataDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "HotelInformationCache")
  public AemHotelInformationDto getSingleHotelInformation(String country, String language,
      String hotelId) {
    log.debug("Entered getHotelInformation with country={}, language={}, hotelIds={}",
        country, language, hotelId);
    String message = "Unable to get hotel information";
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getHotelInformationEndpoint())
            .build(country, language, hotelId.charAt(0), hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemBadRequestException(AEM_HOTELS_INFORMATION_EXCEPTION,
                  message, exception))
              .flatMap(Mono::error);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(AEM_HOTELS_INFORMATION_EXCEPTION,
                  message, exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemHotelInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "MultipleLabelsCache", key = "{#request.language, #request.categories}")
  public Map<String, Map<String, String>> getMultipleLabels(MultipleLabelsRequestDto request) {
    log.debug("Entered getMultipleLabels with country={}, language={}, categories={}",
        request.getCountry(), request.getLanguage(), request.getCategories().size());
    return Flux.fromIterable(request.getCategories())
        .flatMap(category -> {
          String labelsEndpoint = getLabelsEndpoint(category, aemProperties);
          return aemWebClient
              .get()
              .uri(uriBuilder -> uriBuilder.path(labelsEndpoint)
                  .build(request.getLanguage()))
              .retrieve()
              .onStatus(HttpStatusCode::isError, response -> {
                WebClientUtils.logErrorResponse(log, response);
                return response.createException()
                    .map(exception -> new AemResponseException(
                        AEM_LABELS_EXCEPTION, "Unable to get labels.",
                        exception))
                    .flatMap(Mono::error);
              })
              .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
              })
              .map(result -> new CategoryLabelsDto(category.getField(), result));
        })
        .collectMap(CategoryLabelsDto::getCategory, CategoryLabelsDto::getLabels)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "DlpInformationCache",
      key = "{#dlpInformationRequest.country, #dlpInformationRequest.language, #dlpInformationRequest.dlpPath}")
  public DlpInformationDto getDlpInformation(DlpInformationRequestDto dlpInformationRequest) {
    var country = dlpInformationRequest.getCountry();
    var language = dlpInformationRequest.getLanguage();
    var dlpPath = dlpInformationRequest.getDlpPath();

    log.debug("Entered getDlpInformation for {}/{}/{}", country.substring(0, 2),
        language.substring(0, 2), dlpPath.substring(0, Math.min(dlpPath.length(), 100)));

    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
            getPageDlpUriPath(dlpPath, country, language, aemProperties,
                aemDlpProperties)).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_DLP_INFORMATION_EXCEPTION,
                  "Unable to get DLP information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(DlpInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "AppsHomepageInformationCache",
      key = "{#homepageAppsRequest.country, #homepageAppsRequest.language, "
          + "#homepageAppsRequest.channel, #homepageAppsRequest.subchannel}")
  public AppsHomepageResponseDto getAppsHomepage(AppsHomepageRequestDto homepageAppsRequest) {
    var country = homepageAppsRequest.getCountry();
    var language = homepageAppsRequest.getLanguage();
    var channel = homepageAppsRequest.getChannel();
    var subchannel = homepageAppsRequest.getSubchannel();
    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
            getAppsHomepageEndpoint(channel, subchannel, country, language, aemProperties,
                aemAppsHomepageProperties)).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_APPS_HOMEPAGE_CONFIG_EXCEPTION,
                  "Unable to get homepage apps information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AppsHomepageResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception)).block();
  }

  public PriceFinderGlobalConfigDto getPriceFinderGlobalConfig(PriceFinderGlobalConfigRequestDto
                                                                       priceFinderGlobalConfigRequestDto) {
    log.debug("Entered getPriceFinderGlobalConfig with country={}, language={}, channelId={}, brand={}, path={}",
            priceFinderGlobalConfigRequestDto.getCountry(), priceFinderGlobalConfigRequestDto.getLanguage(),
            priceFinderGlobalConfigRequestDto.getChannelId(), priceFinderGlobalConfigRequestDto.getBrand(),
            priceFinderGlobalConfigRequestDto.getPath());
    return aemWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(getGlobalConfigEndpoint(priceFinderGlobalConfigRequestDto.getChannelId(), aemProperties))
                        .build(priceFinderGlobalConfigRequestDto.getCountry(),
                                priceFinderGlobalConfigRequestDto.getLanguage(),
                                priceFinderGlobalConfigRequestDto.getBrand()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                  WebClientUtils.logErrorResponse(log, response);
                  return response.createException()
                            .map(exception -> new AemResponseException(
                                    AEM_PRICE_FINDER_CONFIG_EXCEPTION,
                                    "Unable to get price finder global config data.",
                                    exception))
                            .flatMap(Mono::error);
                })
                .bodyToMono(PriceFinderGlobalConfigDto.class)
                .doOnError(exception -> ExceptionLogger.log(log, exception))
                .block();
  }
}
