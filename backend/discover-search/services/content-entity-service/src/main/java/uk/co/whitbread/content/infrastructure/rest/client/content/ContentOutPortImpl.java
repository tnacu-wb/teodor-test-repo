package uk.co.whitbread.content.infrastructure.rest.client.content;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_DLP_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_EXTRAS_LABEL_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_HOTEL_SLUG_NOT_FOUND_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_LABEL_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_MULTIPLE_LABELS_EXCEPTION;

import io.getunleash.UnleashContext;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.domain.model.feature.FeatureFlag;
import uk.co.whitbread.content.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseoutItem;
import uk.co.whitbread.content.domain.model.hotel.out.FacilityCloseoutItem;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelShortInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsOpeningSoonResult;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsWithFacilityFilterResult;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.domain.model.labels.out.Extras;
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.domain.model.promoconfig.evaluator.PromoEvaluationStrategy;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.content.infrastructure.config.SrpFiltersConfig;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.ResourceNotFoundException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.AemClientAppsHomepageMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.AemClientDlpInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.GlobalConfigRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.IndexHeaderDataMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.IndexHeaderDataRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.PriceFinderGlobalConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.PromotionsConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.RoomClassConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.RoomUpgradeOptionsMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.SearchResultsDataMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.SearchRulesMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.UpsellItemsExtrasMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionItemsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.CityTax;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelShortInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.HotelReviewClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LocalizationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.MultipleLabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.PromoBoxResolutionResult;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.resolver.PromoBoxResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoFlowTypeResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoStrategyRegistry;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.utils.ParsedDates;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.SnowdropHotelsRetriever;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TripAdvisorReviewsDto;

@RequiredArgsConstructor
@Slf4j
public class ContentOutPortImpl implements ContentOutPort {

  public static final String NO_UPSELLING_CODES_FOUND = "No Upselling Codes Found";
  public static final List<String> EXTRAS_IDS = List.of("HSCKIN", "HSCOU2", "DBPROS");
  public static final String ANCILLARIES_EXTRA = "ancillaries.extras.";
  public static final String UNLEASH_CONTEXT_HOTEL_CODE = "hotelCode";
  private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter FACILITY_CLOSEOUT_INPUT_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private final LabelsRequestMapper labelsRequestMapper;
  private final LocalizationRequestMapper localizationRequestMapper;
  private final MultipleLabelsRequestMapper multipleLabelsRequestMapper;
  private final HotelInformationRequestMapper hotelInformationRequestMapper;
  private final HotelInformationMapper hotelInformationMapper;
  private final IndexHeaderDataMapper indexHeaderDataMapper;
  private final SearchResultsDataMapper searchResultsDataMapper;
  private final SearchRulesMapper searchRulesMapper;
  private final IndexHeaderDataRequestMapper indexHeaderDataRequestMapper;
  private final GlobalConfigRequestMapper globalConfigRequestMapper;
  private final RoomClassConfigMapper roomClassConfigMapper;
  private final AemClientDlpInformationMapper dlpInformationMapper;
  private final AemClientAppsHomepageMapper homepageMapper;
  private final RoomUpgradeOptionsMapper roomUpgradeOptionsMapper;
  private final AemClient aemClient;
  private final HotelReviewClient hotelReviewClient;
  private final SrpFiltersConfig srpFiltersConfig;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final SnowdropHotelsRetriever snowdropHotelsRetriever;
  private final PromotionsConfigMapper promotionsConfigMapper;
  private final PriceFinderGlobalConfigMapper priceFinderGlobalConfigMapper;
  private final PromoFlowTypeResolver flowTypeResolver;
  private final PromoStrategyRegistry strategyRegistry;
  private final PromoBoxResolver promoBoxResolver;
  private final UpsellItemsExtrasMapper upsellItemsExtrasMapper;

  @Override
  public Map<String, String> getLabels(LabelsRequest labelsRequest) {
    log.debug("Entered getLabels with country={}, language={}, category={}, labels filter: {}",
        labelsRequest.getCountry(), labelsRequest.getLanguage(), labelsRequest.getCategory(),
        labelsRequest.getLabels());
    final var labelsResponse = aemClient.getLabels(labelsRequestMapper.toDtoModel(labelsRequest));
    if (MapUtils.isEmpty(labelsResponse)) {
      var message = String.format(
          "No labels for this getLabels request for country=%s, language=%s, category=%s",
          labelsRequest.getCountry(), labelsRequest.getLanguage(), labelsRequest.getCategory());
      var exception = new ContentException(DIGITAL_LABEL_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return labelsResponse;
  }

  @Override
  public Map<String, Map<String, String>> getMultipleLabels(
      MultipleLabelsRequest multipleLabelsRequest) {
    log.debug("Entered getMultipleLabels with country={}, language={}, categories={}",
        multipleLabelsRequest.getCountry(), multipleLabelsRequest.getLanguage(),
        multipleLabelsRequest.getCategories().size());

    MultipleLabelsRequestDto multipleLabelsRequestDto = multipleLabelsRequestMapper.toDtoModel(multipleLabelsRequest);
    if (multipleLabelsRequestDto.getCategories() != null) {
      Collections.sort(multipleLabelsRequestDto.getCategories());
    }
    final Map<String, Map<String, String>> labels = aemClient.getMultipleLabels(multipleLabelsRequestDto);

    if (labels != null && !labels.isEmpty()) {
      return labels;
    }

    var message = String.format(
        "No labels for this getMultipleLabels request for country=%s, language=%s,"
            + " categories=%s", multipleLabelsRequest.getCountry(),
        multipleLabelsRequest.getLanguage(), multipleLabelsRequest.getCategories().size());
    var exception = new ContentException(DIGITAL_MULTIPLE_LABELS_EXCEPTION, message);
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "IndexHeaderDataCache")
  public IndexHeaderData getIndexHeaderData(IndexHeaderDataRequest indexHeaderDataRequest) {
    log.debug("Entered getIndexHeaderData with country={}, language={}, businessBooker={}",
        indexHeaderDataRequest.getCountry(), indexHeaderDataRequest.getLanguage(),
        indexHeaderDataRequest.getBusinessBooker());
    var aemIndexHeaderDataResponse = aemClient.getIndexHeaderData(
        indexHeaderDataRequestMapper.toDto(indexHeaderDataRequest));
    return indexHeaderDataMapper.toDomainModel(aemIndexHeaderDataResponse);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "SearchRulesCache")
  public SearchRules getSearchRules(GlobalConfigRequest globalConfigRequest) {
    log.debug("Entered getSearchRules with country={}, language={}, channelId={}, brand={}",
        globalConfigRequest.getCountry(), globalConfigRequest.getLanguage(),
        globalConfigRequest.getChannelId(), globalConfigRequest.getBrand());
    var globalConfigDto = aemClient.getGlobalConfig(globalConfigRequestMapper.toDto(
        globalConfigRequest));
    return searchRulesMapper.toDomainModel(globalConfigDto, globalConfigRequest);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "RoomClassConfigCache")
  public RoomClassConfig getRoomClassConfig(GlobalConfigRequest globalConfigRequest) {
    log.debug("Entered getRoomClassConfig with country={}, language={}, channelId={}, brand={}",
        globalConfigRequest.getCountry(), globalConfigRequest.getLanguage(),
        globalConfigRequest.getChannelId(), globalConfigRequest.getBrand());
    var roomClassConfigDto = aemClient
        .getRoomClassConfig(globalConfigRequestMapper.toDto(globalConfigRequest));
    return roomClassConfigMapper.toDomainModel(roomClassConfigDto);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "GlobalConfigCache")
  public GlobalConfig getGlobalConfig(GlobalConfigRequest globalConfigRequest) {

    log.debug("Entered getGlobalConfig with country={}, language={}, channelId={}, brand={}",
        globalConfigRequest.getCountry(), globalConfigRequest.getLanguage(),
        globalConfigRequest.getChannelId(), globalConfigRequest.getBrand());

    var aemGlobalConfigResponse = aemClient.getGlobalConfig(globalConfigRequestMapper.toDto(
        globalConfigRequest));

    return GlobalConfig.builder()
        .maxRoomsLim(searchRulesMapper
            .toDomainModel(aemGlobalConfigResponse, globalConfigRequest))
        .roomClassConfig(roomClassConfigMapper.toDomainModel(aemGlobalConfigResponse).getRoomClassConfig())
        .roomUpgradeOptions(roomUpgradeOptionsMapper.toDomainModel(aemGlobalConfigResponse.getRoomUpgradeOptions()))
        .promotionsConfig(promotionsConfigMapper.toDomainModel(aemGlobalConfigResponse.getPromotionsConfig()))
        .priceFinderConfig(priceFinderGlobalConfigMapper
             .toDomainModel(aemGlobalConfigResponse.getPriceFinderConfig()))
        .hotelsWithCityTax(aemGlobalConfigResponse.getHotelsWithCityTax())
        .upsellItemsExtras(upsellItemsExtrasMapper.toDomainModel(aemGlobalConfigResponse.getUpsellItemsExtras()))
        .build();
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "SearchResultsDataCache")
  public SearchResultsData getSearchResultsData(LocalizationRequest localizationRequest) {
    log.debug("Entered getSearchResultsData with country={}, language={}",
        localizationRequest.getCountry(), localizationRequest.getLanguage());
    var aemSearchResultsDataResponse = aemClient.getSearchResultsData(
        localizationRequestMapper.toDtoModel(localizationRequest));
    return searchResultsDataMapper.toDomainModel(aemSearchResultsDataResponse);
  }

  @Override
  public HotelInformation getHotelInformation(HotelInformationRequest hotelInformationRequest,
      boolean tripAdvisorDataRequired) {
    log.debug("Entered getHotelInformation with country={}, language={}, hotelId={}, slug={}",
        hotelInformationRequest.getCountry(), hotelInformationRequest.getLanguage(),
        hotelInformationRequest.getHotelId(), hotelInformationRequest.getSlug());

    var hotelInformationDto = getHotelInformation(hotelInformationRequest.getHotelId(),
        hotelInformationRequest.getCountry(), hotelInformationRequest.getLanguage(), false, tripAdvisorDataRequired);

    final HotelInformation hotelInformation = hotelInformationMapper.toDomainModel(
        hotelInformationDto);
    populateUpsellCodes(hotelInformation, hotelInformationDto);
    return hotelInformation;

  }

  private AemHotelInformationDto getHotelInformation(@NotEmpty String hotelId, @NotEmpty String country,
      @NotEmpty String language, boolean ignoreAemError, boolean tripAdvisorDataRequired) {

    var hotelInfoFuture = CompletableFuture.supplyAsync(() -> aemClient
        .getSingleHotelInformation(country, language, hotelId));

    CompletableFuture<TripAdvisorReviewsDto> tripAdvisorFuture = null;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFetchTripadvisorFeedback(),
        UnleashContext.builder().addProperty(UNLEASH_CONTEXT_HOTEL_CODE, hotelId).build())
        && tripAdvisorDataRequired) {
      tripAdvisorFuture = CompletableFuture
          .supplyAsync(() -> hotelReviewClient.getTripAdvisorReviews(hotelId, language));
    }

    AemHotelInformationDto hotelInfo;
    try {
      hotelInfo = hotelInfoFuture.join();
    } catch (RuntimeException e) {
      if (ignoreAemError) {
        log.warn("Could not fetch hotel information for hotelId={} from AEM", hotelId, e);
        return null;
      }
      var cause = e.getCause();
      if (Objects.nonNull(cause) && AemResponseException.class.isAssignableFrom(cause.getClass())) {
        throw (AemResponseException) cause;
      }
      throw e;
    }
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFetchTripadvisorFeedback(),
        UnleashContext.builder().addProperty(UNLEASH_CONTEXT_HOTEL_CODE, hotelId).build())
        && Objects.nonNull(tripAdvisorFuture)) {
      try {
        var tripAdvisorReviews = tripAdvisorFuture.join();
        hotelInfo.setTripAdvisorReviews(hotelInformationMapper.toModel(tripAdvisorReviews));
      } catch (RuntimeException e) {
        log.warn("Could not fetch Tripadvisor reviews for hotelId={}", hotelId, e);
      }
    }
    formatDateForCityTax(hotelInfo);
    return hotelInfo;
  }

  private void formatDateForCityTax(AemHotelInformationDto aemHotelInformationDto) {
    if (Objects.isNull(aemHotelInformationDto) || Objects.isNull(aemHotelInformationDto.getCityTax())) {
      return;
    }
    CityTax cityTax = aemHotelInformationDto.getCityTax();
    cityTax.setBookingDateFrom(formatDate(cityTax.getBookingDateFrom()));
    cityTax.setEffectiveFrom(formatDate(cityTax.getEffectiveFrom()));
    aemHotelInformationDto.setCityTax(cityTax);
  }

  private String formatDate(String dateStr) {
    if (StringUtils.isBlank(dateStr)) {
      return dateStr;
    }
    try {
      LocalDate date = LocalDate.parse(dateStr, INPUT_FORMAT);
      return date.format(OUTPUT_FORMAT);
    } catch (DateTimeParseException ex) {
      throw new DateTimeParseException(
          "Dates must be in right format: " + ex.getParsedString(),
          ex.getParsedString(),
          ex.getErrorIndex(),
          ex
      );
    }
  }

  private static void populateUpsellCodes(final HotelInformation hotelInformation,
      final AemHotelInformationDto aemHotelInformationDto) {
    if (hotelInformation != null && hotelInformation.getAncillaryCloseout() != null
        && CollectionUtils.isNotEmpty(hotelInformation.getAncillaryCloseout().getItems())) {
      for (AncillaryCloseoutItem ancillaryCloseoutItem : hotelInformation.getAncillaryCloseout()
          .getItems()) {
        if (MapUtils.isNotEmpty(aemHotelInformationDto.getServiceCodeAndUpsellCodeMapping())) {
          final String upsellCodes = aemHotelInformationDto.getServiceCodeAndUpsellCodeMapping()
              .get(ancillaryCloseoutItem.getServiceCode());
          if (StringUtils.isNotEmpty(upsellCodes)) {
            ancillaryCloseoutItem.setUpsellCodes(upsellCodes);
          } else {
            ancillaryCloseoutItem.setUpsellCodes(NO_UPSELLING_CODES_FOUND);
          }
        }
      }
    }
  }

  @Override
  public List<HotelInformation> getHotelsInformation(
      HotelsInformationRequest hotelsInformationRequest, boolean tripAdvisorDataRequired) {
    log.debug("Entered getHotelsInformation with country={}, language={}, hotelIds={}",
        hotelsInformationRequest.getCountry(), hotelsInformationRequest.getLanguage(),
        hotelsInformationRequest.getHotelIds().size());

    boolean useMiles = "gb".equalsIgnoreCase(hotelsInformationRequest.getCountry())
        && "en".equalsIgnoreCase(hotelsInformationRequest.getLanguage());

    return hotelsInformationRequest.getHotelIds()
        .parallelStream()
        .map(hotelId -> getHotelInformation(hotelId,
            hotelsInformationRequest.getCountry(), hotelsInformationRequest.getLanguage(), true,
            tripAdvisorDataRequired))
        .filter(Objects::nonNull)
        .map(aemHotelInformationDto -> {
          var hotelInformation = hotelInformationMapper.toDomainModel(
              aemHotelInformationDto,
              hotelsInformationRequest.getLatitudeRef(),
              hotelsInformationRequest.getLongitudeRef(),
              useMiles);
          filterHotelFacilitiesByOverlappingCloseouts(
              hotelInformation,
              hotelsInformationRequest.getStayStartDate(),
              hotelsInformationRequest.getStayEndDate());
          return hotelInformation;
        })
        .toList();
  }

  @Override
  public HotelInformation getHotelInformationBySlug(
      HotelInformationRequest hotelInformationRequest) {
    log.debug("Entered getHotelInformationBySlug with country={}, language={}, hotelId={}, slug={}",
        hotelInformationRequest.getCountry(), hotelInformationRequest.getLanguage(),
        hotelInformationRequest.getHotelId(), hotelInformationRequest.getSlug());

    var hotelId = getHotelDetailsFromAem(aemClient.getAllHotelDetails(
            hotelInformationRequestMapper.toDtoModel(hotelInformationRequest)),
        hotelInformationRequest.getSlug()).getCode();

    var hotelInformation = getHotelInformation(hotelId, hotelInformationRequest.getCountry(),
        hotelInformationRequest.getLanguage(), false, true);
    var hotelInformationModel = hotelInformationMapper.toDomainModel(hotelInformation);
    filterHotelFacilitiesByOverlappingCloseouts(
        hotelInformationModel,
        hotelInformationRequest.getStayStartDate(),
        hotelInformationRequest.getStayEndDate());
    return hotelInformationModel;
  }

  public List<HotelShortInformation> getAllHotelsShortInformation(
      String country, String language) {
    String sanitizedCountry = country.replaceAll("[^a-zA-Z0-9-]", "").trim();
    String sanitizedLanguage = language.replaceAll("[^a-zA-Z0-9-]", "").trim();
    log.debug("Entered getAllHotelsShortInformation with country={}, language={}",
        sanitizedCountry, sanitizedLanguage);

    List<HotelShortInformationDto> hotelShortInformationDtoList =
        aemClient.getAllHotelDetails(
            hotelInformationRequestMapper.toDtoModel(
                HotelInformationRequest.builder()
                    .country(country)
                    .language(language)
                    .hotelId("")
                    .build()));

    if (CollectionUtils.isEmpty(hotelShortInformationDtoList)) {
      return Collections.emptyList();
    }

    return hotelShortInformationDtoList.stream().map(hotelShortInformationDto ->
        hotelInformationMapper.toShortHotelDomainModel(hotelShortInformationDto))
        .toList();
  }

  private HotelShortInformationDto getHotelDetailsFromAem(
      List<HotelShortInformationDto> hotelShortInformationDtoList, String slug) {
    log.debug("Entered getHotelDetailsFromAem with hotelShortInformationDtoList={}, slug={}",
        hotelShortInformationDtoList.size(), slug);
    return hotelShortInformationDtoList
        .stream()
        .filter(hotelShortInformationDto -> StringUtils.equals(
            hotelShortInformationDto.getHotelPagePath(), slug))
        .findFirst()
        .orElseThrow(() -> {
          String message = String.format(
              "HotelDetails From Aem with hotelShortInformationDtoList=%s, slug=%s not found",
              hotelShortInformationDtoList.size(), slug);
          var exception = new ResourceNotFoundException(DIGITAL_HOTEL_SLUG_NOT_FOUND_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  @Override
  public HotelPaymentInformation getHotelPaymentInformation(
      HotelInformationRequest hotelInformationRequest) {
    log.debug(
        "Entered getHotelPaymentInformation with country={}, language={}, hotelId={}, slug={}",
        hotelInformationRequest.getCountry(), hotelInformationRequest.getLanguage(),
        hotelInformationRequest.getHotelId(), hotelInformationRequest.getSlug());

    var aemHotelInformation =
        aemClient.getSingleHotelInformation(hotelInformationRequest.getCountry(),
            hotelInformationRequest.getLanguage(), hotelInformationRequest.getHotelId());
    return hotelInformationMapper.toHotelPaymentInformationModel(aemHotelInformation);
  }

  @Override
  public List<HotelInformation> getAllHotelsInformation(String country, String language) {

    var allHotelIds = getAllHotelCodes(country, language);

    var hotelsInfoRequest = HotelsInformationRequest.builder()
        .country(country)
        .language(language)
        .hotelIds(allHotelIds)
        .build();

    return getHotelsInformation(hotelsInfoRequest, false);
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager7Days",
      value = "HotelFacilitiesFilterCache", key = "#facilityFilter")
  public HotelsWithFacilityFilterResult getHotelsWithFacilityFilter(String facilityFilter,
      List<HotelInformation> hotelsInformation) {

    var hotelIds = hotelsInformation.parallelStream()
        .filter(hi ->
            hi.getHotelFacilities().stream().anyMatch(hf -> hf.getCode().equals(facilityFilter)))
        .map(HotelInformation::getHotelId)
        .toList();

    return new HotelsWithFacilityFilterResult(hotelIds);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "HotelsOpeningSoonCache", key = "'OpeningSoon'")
  public HotelsOpeningSoonResult getHotelsOpeningSoon(List<HotelInformation> hotelsInformation) {

    var hotelIds = hotelsInformation.stream()
        .filter(Objects::nonNull)
        .filter(this::isHotelOpeningSoon)
        .map(HotelInformation::getHotelId)
        .toList();

    return new HotelsOpeningSoonResult(hotelIds);
  }

  private boolean isHotelOpeningSoon(HotelInformation hotelInformation) {
    if (StringUtils.isBlank(hotelInformation.getHotelOpeningDate())) {
      return false;
    }

    try {
      var parsedDate = LocalDate.parse(hotelInformation.getHotelOpeningDate(),
          DateTimeFormatter.ISO_DATE_TIME);
      return !LocalDate.now().isAfter(parsedDate);
    } catch (DateTimeParseException dateTimeParseException) {
      log.error("Unable to parse hotelOpeningDate={}", hotelInformation.getHotelOpeningDate());
    }
    return false;
  }

  @Override
  public List<String> getAllHotelFacilityFilters() {
    return srpFiltersConfig.getFacilityCodes();
  }

  private List<String> getAllHotelCodes(String country, String language) {

    List<HotelShortInformationDto> shortInformationDtoList = aemClient.getAllHotelDetails(
        hotelInformationRequestMapper.toDtoModel(
            HotelInformationRequest.builder()
                .country(country)
                .language(language)
                .hotelId("")
                .slug(null)
                .build()));

    return shortInformationDtoList
        .stream()
        .map(HotelShortInformationDto::getCode)
        .toList();
  }

  @Override
  public ExtrasLabel getExtras(String country, String language) {
    log.debug("Entered getExtras");
    final var extrasLabels = aemClient.getLabels(
        labelsRequestMapper.toDtoModel(country, language, CategoryEnumDto.EXTRAS));
    if (MapUtils.isEmpty(extrasLabels)) {
      var message = String.format(
          "No labels for this getExtras request for country=%s, language=%s, category=%s",
          country, language, CategoryEnumDto.EXTRAS);
      var exception = new ContentException(DIGITAL_EXTRAS_LABEL_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    String extrasListStr = extrasLabels.get("ancillaries.extras.list");

    List<String> packageCodes = extrasListStr != null
            ? Arrays.stream(extrasListStr.split(","))
            .map(String::trim)
            .toList()
            : Collections.emptyList();

    List<Extras> extrasResponseList = new ArrayList<>();

    EXTRAS_IDS.forEach(extrasId -> {
      if (extrasLabels.containsKey(
              ANCILLARIES_EXTRA + extrasId + ".name")) {
        extrasResponseList.add(
                buildExtras(extrasLabels, extrasId));
      }
    });

    return ExtrasLabel.builder()
            .extrasLabels(extrasResponseList)
            .extrasList(packageCodes)
            .build();
  }

  @Override
  public DlpInformation getDlpInformation(DlpInformationRequest dlpInformationRequest) {
    var dlpPath = dlpInformationRequest.getDlpPath();
    log.debug("Entered getDlpInformation for {}/{}/{}",
        dlpInformationRequest.getCountry().substring(0, 2),
        dlpInformationRequest.getLanguage().substring(0, 2),
        dlpPath.substring(0, Math.min(dlpPath.length(), 100)));
    var dlpInformationRequestDto = dlpInformationMapper.toDto(dlpInformationRequest);
    var dlpInformationDto = aemClient.getDlpInformation(dlpInformationRequestDto);

    if (dlpInformationDto == null) {
      var exception =  new ContentException(AEM_DLP_INFORMATION_EXCEPTION,
              "Unable to get DLP information.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (dlpInformationDto.getHotels() == null || dlpInformationDto.getHotels().isEmpty()) {
      log.info("No hotels received from AEM for dlpPath {}", dlpInformationRequest.getDlpPath());
      dlpInformationDto.setHotels(
              snowdropHotelsRetriever.mapHotelsFromSnowdrop(dlpInformationRequest, dlpInformationDto));
    }
    return dlpInformationMapper.toDomainModel(dlpInformationDto);
  }

  @Override
  public AppsHomepageResponse getAppsHomepage(AppsHomepageRequest homepageRequest) {
    var homepageAppsRequestDto = homepageMapper.toDto(homepageRequest);
    var response = aemClient.getAppsHomepage(homepageAppsRequestDto);
    return homepageMapper.toDomainModel(response);
  }

  @Override
  public PromotionsInformationResponse getPromoConfig(PromoConfigRequest req) {
    log.debug("Entered getPromoConfig {}", req);

    final var gcReq = GlobalConfigRequest.builder()
        .brand(req.getBrand())
        .country(req.getCountry())
        .language(req.getLanguage())
        .channelId(req.getChannelId())
        .build();

    var globalConfigResponse =
        aemClient.getGlobalConfig(globalConfigRequestMapper.toDto(gcReq));

    final var promoBox = Optional.ofNullable(globalConfigResponse)
        .map(GlobalConfigDto::getPromotionsConfig)
        .map(PromotionsConfigDto::getPromoBox)
        .orElse(null);

    PromoBoxResolutionResult boxResult =
        promoBoxResolver.resolvePreEvaluation(req);

    PromotionsInformationResponse promoResponse;

    if (boxResult.isTerminal()) {
      promoResponse = noPromo();
    } else {

      PromotionsInformationResponse validationResponse =
              validateRateAndRoom(req, globalConfigResponse);

      if (validationResponse != null) {
        return promotionsConfigMapper.toPromotionsInformationResponseModel(
                        validationResponse, promoBox);
      }

      promoResponse =
          Optional.ofNullable(globalConfigResponse)
              .map(resp -> promotionsConfigMapper.toDomainModel(resp.getPromotionsConfig()))
              .filter(pc -> pc.getPromoItems() != null && !pc.getPromoItems().isEmpty())
              .map(pc -> getPromotionInfo(req, pc))
              .orElseGet(this::noPromo);

      boxResult =
          promoBoxResolver.resolvePostEvaluation(req, promoResponse);
    }
    promoResponse.setPromoBoxStatus(boxResult.getStatus());
    promoResponse.setPromoBoxMessageKey(
            resolvePromoBoxMessageKey(boxResult.getStatus())
    );

    log.debug(
        "PromoBox resolved: status={}, messageKey={}, promoCode={}, promoKind={}",
        boxResult.getStatus(),
        promoResponse.getPromoBoxMessageKey(),
        req.getPromotionCode(),
        req.getPromoKind()
    );

    if (req.getPromoKind() == PromoKind.UNIQUE) {
      promoResponse.setPromotionCode(req.getPromotionCode());
    }

    return Optional.ofNullable(
            promotionsConfigMapper.toPromotionsInformationResponseModel(
                promoResponse, promoBox))
        .orElse(promoResponse);
  }

  private String resolvePromoBoxMessageKey(PromoBoxStatus status) {
    if (status == null) {
      return null;
    }

    return switch (status) {
      case EMPTY -> "whenEmpty";
      case INVALID -> "whenInvalid";
      case CODE_ALREADY_APPLIED -> "whenCodeAlreadyApplied";
      case CODE_EXPIRED -> "whenCodeExpired";
      case UNAVAILABLE -> "whenUnavailable";
      case SUCCESS -> "whenSuccess";
      case MAX_ROOMS_EXCEEDED -> "whenMaxRoomsExceeded";
      case MIN_ROOMS_NOT_MET -> "whenMinRoomsNotMet";
    };
  }

  private PromotionsInformationResponse getPromotionInfo(PromoConfigRequest request,
      PromotionsConfig promotionsConfig) {

    ParsedDates dates = parseDates(request);

    String promoCodeForEvaluation =
        request.getPromoKind() == PromoKind.UNIQUE
            ? request.getOperaPromoCode()
            : request.getPromotionCode();

    PromoContext promoContext = PromoContext.builder()
        .bookingDate(dates.bookingDate())
        .stayStartDate(dates.stayStartDate())
        .stayEndDate(dates.stayEndDate())
        .promoCode(promoCodeForEvaluation)
        .promoKind(request.getPromoKind())
        .promoBox(Boolean.TRUE.equals(request.getIsPromoBox()))
        .amendRequest(Boolean.TRUE.equals(request.getIsAmendRequest()))
        .build();

    PromoFlowType flowType = flowTypeResolver.resolve(promoContext);

    log.debug("Resolved promo flowType={} for promoCode={}, promoBox={}, amendRequest={}",
        flowType, promoContext.getPromoCode(), promoContext.isPromoBox(),
        promoContext.isAmendRequest()
    );

    PromoEvaluationStrategy strategy = strategyRegistry.get(flowType);

    return strategy.evaluate(promoContext, promotionsConfig);
  }

  private ParsedDates parseDates(PromoConfigRequest req) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    try {
      LocalDate bookingDate =
          LocalDate.parse(req.getBookingDate(), formatter);
      LocalDate stayStart =
          LocalDate.parse(req.getStayStartDate(), formatter);
      LocalDate stayEnd =
          LocalDate.parse(req.getStayEndDate(), formatter);

      if (stayStart.isAfter(stayEnd)) {
        throw new IllegalArgumentException(
            String.format(
                "stayStartDate=%s must not be after stayEndDate=%s",
                req.getStayStartDate(),
                req.getStayEndDate()
            )
        );
      }

      return new ParsedDates(bookingDate, stayStart, stayEnd);

    } catch (DateTimeParseException ex) {
      throw new DateTimeParseException(
          "Dates must be in yyyy-MM-dd format: " + ex.getParsedString(),
          ex.getParsedString(),
          ex.getErrorIndex(),
          ex
      );
    }
  }

  private PromotionsInformationResponse noPromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(false)
        .isWithinPromoWindow(false)
        .promoInvalidMessage("No applicable promotions")
        .build();
  }

  private PromotionsInformationResponse validateRateAndRoom(
          PromoConfigRequest req,
          GlobalConfigDto globalConfigResponse
  ) {
    if (req.getRateName() == null || req.getRoomClass() == null) {
      return null;
    }

    PromotionItemsDto promoItem = Optional.ofNullable(globalConfigResponse)
            .map(GlobalConfigDto::getPromotionsConfig)
            .filter(pc -> pc.getPromoItems() != null && !pc.getPromoItems().isEmpty())
            .map(PromotionsConfigDto::getPromoItems)
            .flatMap(items -> items.stream()
                    .filter(item -> item.getPromoCode() != null
                            && item.getPromoCode().equalsIgnoreCase(req.getOperaPromoCode()))
                    .findFirst())
            .orElse(null);

    if (promoItem == null) {
      return null;
    }

    if (isMinRoomsNotMet(req, promoItem)) {
      PromotionsInformationResponse response = noPromo();
      response.setPromoBoxStatus(PromoBoxStatus.MIN_ROOMS_NOT_MET);
      response.setPromoBoxMessageKey(resolvePromoBoxMessageKey(PromoBoxStatus.MIN_ROOMS_NOT_MET));
      return response;
    }

    if (isMaxRoomsExceeded(req, promoItem)) {
      PromotionsInformationResponse response = noPromo();
      response.setPromoBoxStatus(PromoBoxStatus.MAX_ROOMS_EXCEEDED);
      response.setPromoBoxMessageKey(resolvePromoBoxMessageKey(PromoBoxStatus.MAX_ROOMS_EXCEEDED));
      return response;
    }

    boolean isRateValid = isRateValid(promoItem, req);
    boolean isRoomValid = isRoomValid(promoItem, req);

    String errorMessage = resolveErrorMessage(promoItem, isRateValid, isRoomValid);

    if (errorMessage != null) {
      PromotionsInformationResponse resp = noPromo();
      resp.setErrorRateAndRoomMessage(errorMessage);
      return resp;
    }

    return null;
  }

  private boolean isMaxRoomsExceeded(
          PromoConfigRequest request,
          PromotionItemsDto promoItem) {

    return request.getNoOfRooms() != null && promoItem.getMaxRooms() != null
            && request.getNoOfRooms() > promoItem.getMaxRooms();
  }

  private boolean isMinRoomsNotMet(
          PromoConfigRequest request,
          PromotionItemsDto promoItem) {

    return request.getNoOfRooms() != null && promoItem.getMaxRooms() != null
            && request.getNoOfRooms() < promoItem.getMinRooms();
  }

  private boolean isRateValid(PromotionItemsDto item, PromoConfigRequest req) {
    return item.getRatePlanCode() != null
            && item.getRatePlanCode().equalsIgnoreCase(req.getRateName());
  }

  private boolean isRoomValid(PromotionItemsDto item, PromoConfigRequest req) {
    return item.getRoomClass() != null
            && item.getRoomClass().stream()
            .anyMatch(rc -> rc.equalsIgnoreCase(req.getRoomClass()));
  }

  private String resolveErrorMessage(PromotionItemsDto item,
                                     boolean isRateValid,
                                     boolean isRoomValid) {

    if (!isRateValid && !isRoomValid) {
      return item.getPromoWrongRoomClassAndRatePlanMessage();
    } else if (!isRateValid) {
      return item.getPromoWrongRatePlanMessage();
    } else if (!isRoomValid) {
      return item.getPromoWrongRoomClassMessage();
    }

    return null;
  }

  private Extras buildExtras(Map<String, String> extrasLabels, String extrasId) {
    return Extras.builder()
        .id(extrasId)
        .name(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".name"))
        .description(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".description"))
        .imageSrc(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".image"))
        .order(Integer.valueOf(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".order")))
        .referenceDateType(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".referenceDateType"))
        .requiresInventory(Boolean.valueOf(extrasLabels.get(ANCILLARIES_EXTRA + extrasId + ".requiresInventory")))
        .build();
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "PriceFinderGlobalConfigCache")
  public PriceFinderGlobalConfig getPriceFinderGlobalConfig(PriceFinderGlobalConfigRequest
                                                                      priceFinderGlobalConfigRequest) {
    log.debug("Entered getPriceFinderGlobalConfig with country={}, language={}, channelId={}, brand={}, path={}",
            priceFinderGlobalConfigRequest.getCountry(), priceFinderGlobalConfigRequest.getLanguage(),
            priceFinderGlobalConfigRequest.getChannelId(), priceFinderGlobalConfigRequest.getBrand(),
            priceFinderGlobalConfigRequest.getPath());
    var priceFinderConfigDto = aemClient
            .getPriceFinderGlobalConfig(priceFinderGlobalConfigMapper.toDto(priceFinderGlobalConfigRequest));
    return priceFinderGlobalConfigMapper.toDomainModel(priceFinderConfigDto);
  }

  private void filterHotelFacilitiesByOverlappingCloseouts(HotelInformation hotelInformation,
      LocalDate stayStartDate, LocalDate stayEndDate) {
    if (hotelInformation == null || CollectionUtils.isEmpty(hotelInformation.getHotelFacilities())) {
      return;
    }

    Set<String> closeoutFacilityCodes = getOverlappingFacilityCodes(
        hotelInformation, stayStartDate, stayEndDate);
    if (closeoutFacilityCodes.isEmpty()) {
      return;
    }

    hotelInformation.setHotelFacilities(hotelInformation.getHotelFacilities().stream()
        .filter(Objects::nonNull)
        .filter(hotelFacility -> !closeoutFacilityCodes.contains(hotelFacility.getCode()))
        .toList());

    log.debug("HotelId={} removed closeout facilities={} for stayStartDate={} stayEndDate={}",
        hotelInformation.getHotelId(), closeoutFacilityCodes, stayStartDate, stayEndDate);
  }

  private Set<String> getOverlappingFacilityCodes(HotelInformation hotelInformation,
      LocalDate stayStartDate, LocalDate stayEndDate) {
    if (hotelInformation == null || stayStartDate == null || stayEndDate == null
        || stayStartDate.isAfter(stayEndDate)
        || hotelInformation.getFacilityCloseout() == null
        || CollectionUtils.isEmpty(hotelInformation.getFacilityCloseout().getItems())) {
      return Collections.emptySet();
    }

    Set<String> closeoutFacilityCodes = new HashSet<>();
    for (FacilityCloseoutItem item : hotelInformation.getFacilityCloseout().getItems()) {
      if (item == null || CollectionUtils.isEmpty(item.getFacilityCodes())) {
        continue;
      }
      if (overlapsWithStayDates(item, stayStartDate, stayEndDate)) {
        for (String facilityCode : item.getFacilityCodes()) {
          if (StringUtils.isNotBlank(facilityCode)) {
            closeoutFacilityCodes.add(facilityCode);
          }
        }
      }
    }
    return closeoutFacilityCodes;
  }

  private boolean overlapsWithStayDates(FacilityCloseoutItem closeoutItem,
      LocalDate stayStartDate, LocalDate stayEndDate) {
    LocalDate closeoutStartDate = parseFacilityCloseoutDate(closeoutItem.getStartDate());
    LocalDate closeoutEndDate = parseFacilityCloseoutDate(closeoutItem.getEndDate());

    if (closeoutStartDate == null || closeoutEndDate == null || closeoutStartDate.isAfter(closeoutEndDate)) {
      return false;
    }

    // Overlap exists when there is at least one common day between both ranges.
    return !stayStartDate.isAfter(closeoutEndDate) && !stayEndDate.isBefore(closeoutStartDate);
  }

  private LocalDate parseFacilityCloseoutDate(String date) {
    if (StringUtils.isBlank(date)) {
      return null;
    }
    try {
      return LocalDate.parse(date, FACILITY_CLOSEOUT_INPUT_FORMAT);
    } catch (DateTimeParseException ex) {
      log.warn("Unable to parse facility closeout date={}", date);
      return null;
    }
  }

}
