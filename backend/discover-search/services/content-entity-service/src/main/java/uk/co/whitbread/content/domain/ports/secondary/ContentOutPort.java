package uk.co.whitbread.content.domain.ports.secondary;

import java.util.List;
import java.util.Map;
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
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;

public interface ContentOutPort {

  Map<String, String> getLabels(LabelsRequest labelsRequest);

  IndexHeaderData getIndexHeaderData(IndexHeaderDataRequest indexHeaderDataRequest);

  GlobalConfig getGlobalConfig(GlobalConfigRequest globalConfigRequest);

  SearchResultsData getSearchResultsData(LocalizationRequest localizationRequest);

  SearchRules getSearchRules(GlobalConfigRequest globalConfigRequest);

  RoomClassConfig getRoomClassConfig(GlobalConfigRequest globalConfigRequest);

  Map<String, Map<String, String>> getMultipleLabels(MultipleLabelsRequest multipleLabelsRequest);

  HotelInformation getHotelInformation(HotelInformationRequest hotelInformationRequest,
      boolean tripAdvisorDataRequired);

  List<HotelInformation> getHotelsInformation(HotelsInformationRequest hotelsInformationRequest,
      boolean tripAdvisorDataRequired);

  HotelInformation getHotelInformationBySlug(HotelInformationRequest hotelInformationRequest);

  HotelPaymentInformation getHotelPaymentInformation(
      HotelInformationRequest hotelInformationRequest);

  HotelsWithFacilityFilterResult getHotelsWithFacilityFilter(String facilityFilter,
                                                             List<HotelInformation> hotelsInformation);

  HotelsOpeningSoonResult getHotelsOpeningSoon(List<HotelInformation> hotelsInformation);

  List<String> getAllHotelFacilityFilters();

  List<HotelInformation> getAllHotelsInformation(String country, String language);

  List<HotelShortInformation> getAllHotelsShortInformation(String country, String language);

  ExtrasLabel getExtras(String country, String language);

  DlpInformation getDlpInformation(DlpInformationRequest dlpInformationRequest);

  AppsHomepageResponse getAppsHomepage(AppsHomepageRequest homepageRequest);

  PromotionsInformationResponse getPromoConfig(PromoConfigRequest promoConfigRequest);

  PriceFinderGlobalConfig getPriceFinderGlobalConfig(PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest);

}
