package uk.co.whitbread.content.domain.logic;

import static reactor.core.publisher.Mono.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.domain.logic.mapper.HotelInformationExtendedMapper;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformationExtended;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelShortInformation;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderViews;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.content.domain.ports.secondary.OhipOutPort;

@Slf4j
@RequiredArgsConstructor
public class ContentInPortImpl implements ContentInPort {

  public static final String DISTR = "DISTR";
  private final ContentOutPort contentOutPort;
  private final OhipOutPort ohipOutPort;
  private final HotelInformationExtendedMapper hotelInformationExtendedMapper;
  private final CacheManager cacheManager;

  @Override
  public Map<String, String> getLabels(LabelsRequest labelsRequest) {
    Map<String, String> allLabels = contentOutPort.getLabels(labelsRequest);
    List<String> filterLables = labelsRequest.getLabels();

    return Optional.ofNullable(filterLables)
        .map(filter -> allLabels.entrySet().stream()
            .filter(entry -> filter.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .orElse(allLabels);
  }

  @Override
  public IndexHeaderData getIndexHeaderData(IndexHeaderDataRequest indexHeaderDataRequest) {
    var indexHeaderData = contentOutPort.getIndexHeaderData(indexHeaderDataRequest);
    if (indexHeaderData.getContent() != null && indexHeaderData.getContent().getAnnouncement() != null
        && Boolean.FALSE.equals(indexHeaderData.getConfig().getFeatures().getAnnouncement())) {
      indexHeaderData.getContent().getAnnouncement().setText(null);
    }
    return indexHeaderData;
  }

  @Override
  public SearchResultsData getSearchResultsData(LocalizationRequest localizationRequest) {
    return contentOutPort.getSearchResultsData(localizationRequest);
  }

  @Override
  public SearchRules getSearchRules(GlobalConfigRequest globalConfigRequest) {
    return contentOutPort.getSearchRules(globalConfigRequest);
  }

  @Override
  public RoomClassConfig getRoomClassConfig(GlobalConfigRequest globalConfigRequest) {
    return contentOutPort.getRoomClassConfig(globalConfigRequest);
  }

  @Override
  public GlobalConfig getGlobalConfig(GlobalConfigRequest globalConfigRequest) {
    return contentOutPort.getGlobalConfig(globalConfigRequest);
  }

  @Override
  public Map<String, Map<String, String>> getMultipleLabels(
      MultipleLabelsRequest multipleLabelsRequest) {
    return contentOutPort.getMultipleLabels(multipleLabelsRequest);
  }

  @Override
  public HotelInformationExtended getHotelInformation(
      HotelInformationRequest hotelInformationRequest) {
    if (StringUtils.equals(DISTR, hotelInformationRequest.getChannel())) {
      return getHotelInformationExtended(hotelInformationRequest);
    } else {
      return hotelInformationExtendedMapper.toDomainModel(
          contentOutPort.getHotelInformation(hotelInformationRequest, false));
    }
  }

  private HotelInformationExtended getHotelInformationExtended(
      HotelInformationRequest hotelInformationRequest) {
    Mono<HotelInformation> hotelInformation =
        Mono.just(contentOutPort.getHotelInformation(hotelInformationRequest, false));
    Mono<HotelInfo> ohipHotelInfo =
        Mono.just(ohipOutPort.getHotelInfo(hotelInformationRequest.getHotelId()));

    return zip(hotelInformation, ohipHotelInfo)
        .map(tuple -> hotelInformationExtendedMapper.toDomainModel(tuple.getT1(), tuple.getT2()))
        .block();
  }

  @Override
  public List<HotelInformation> getHotelsInformation(
      HotelsInformationRequest hotelsInformationRequest) {
    return contentOutPort.getHotelsInformation(hotelsInformationRequest,
        Boolean.TRUE.equals(hotelsInformationRequest.getTripAdvisorDataRequired()));
  }

  @Override
  public HotelInformation getHotelInformationBySlug(
      HotelInformationRequest hotelInformationRequest) {
    return contentOutPort.getHotelInformationBySlug(hotelInformationRequest);
  }

  @Override
  public List<HotelShortInformation> getAllHotelsShortInformation(
      String country, String language) {
    return contentOutPort.getAllHotelsShortInformation(country, language);
  }

  @Override
  public HotelPaymentInformation getHotelPaymentInformation(
      HotelInformationRequest hotelInformationRequest) {
    return contentOutPort.getHotelPaymentInformation(hotelInformationRequest);
  }

  @Override
  public void updateHotelsFacilitiesCache() {

    var allHotelFacilityFilters = contentOutPort.getAllHotelFacilityFilters();
    log.debug("Retrieved the FacilityFilters={}", StringUtils.join(allHotelFacilityFilters, ", "));

    var allHotelsInformation = contentOutPort.getAllHotelsInformation("gb", "en");

    allHotelFacilityFilters.forEach(hf -> {
          var hotelIdsWithFacility =
              contentOutPort.getHotelsWithFacilityFilter(hf, allHotelsInformation);
          log.debug("Updated cache for facilityFilter={} and hotelIds={}", hf,
              StringUtils.join(hotelIdsWithFacility.getHotelIds(), ", "));
        }
    );
  }

  @Override
  public void updateHotelsOpeningSoonCache() {
    var allHotelsInformation = contentOutPort.getAllHotelsInformation("gb", "en");
    Optional.ofNullable(cacheManager.getCache("HotelsOpeningSoonCache"))
        .ifPresent(cache -> cache.evict("OpeningSoon"));
    var hotelIdsOpeningSoon = contentOutPort.getHotelsOpeningSoon(allHotelsInformation);
    log.debug("Updated Opening Soon cache for hotelIds={}",
        StringUtils.join(hotelIdsOpeningSoon.getHotelIds(), ", "));
  }

  @Override
  public ExtrasLabel getExtras(String country, String language) {
    return contentOutPort.getExtras(country, language);
  }

  @Override
  public DlpInformation getDlpInformation(DlpInformationRequest dlpInformationRequest) {
    return contentOutPort.getDlpInformation(dlpInformationRequest);
  }

  @Override
  public AppsHomepageResponse getAppsHomepage(AppsHomepageRequest homepageRequest) {
    return contentOutPort.getAppsHomepage(homepageRequest);
  }

  @Override
  public PromotionsInformationResponse getPromoConfig(PromoConfigRequest promoConfigRequest) {
    return contentOutPort.getPromoConfig(promoConfigRequest);
  }

  @Override
  public PriceFinderGlobalConfig getPriceFinderConfig(PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest) {
    var actualPriceFinderGlobalConfig = contentOutPort.getPriceFinderGlobalConfig(priceFinderGlobalConfigRequest);
    var requestedPath = priceFinderGlobalConfigRequest.getPath();
    if (actualPriceFinderGlobalConfig.getPriceFinderConfig().getPriceFinderViews() == null
            || "/".equals(requestedPath)) {
      actualPriceFinderGlobalConfig.getPriceFinderConfig().setPriceFinderViews(new ArrayList<PriceFinderViews>());
      return actualPriceFinderGlobalConfig;
    } else {
      var matchedPriceFinderViews = actualPriceFinderGlobalConfig.getPriceFinderConfig()
                .getPriceFinderViews().stream()
                .filter(view -> view.getPath() != null && view.getPath().equalsIgnoreCase(requestedPath))
                .toList();
      actualPriceFinderGlobalConfig.getPriceFinderConfig().setPriceFinderViews(matchedPriceFinderViews);
    }

    return actualPriceFinderGlobalConfig;
  }

}
