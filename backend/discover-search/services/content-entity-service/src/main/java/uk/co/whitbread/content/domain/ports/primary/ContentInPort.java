package uk.co.whitbread.content.domain.ports.primary;

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
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;

public interface ContentInPort {

  Map<String, String> getLabels(LabelsRequest labelsRequest);

  IndexHeaderData getIndexHeaderData(IndexHeaderDataRequest indexHeaderDataRequest);

  SearchResultsData getSearchResultsData(LocalizationRequest localizationRequest);

  SearchRules getSearchRules(GlobalConfigRequest globalConfigRequest);

  RoomClassConfig getRoomClassConfig(GlobalConfigRequest globalConfigRequest);

  GlobalConfig getGlobalConfig(GlobalConfigRequest globalConfigRequest);

  Map<String, Map<String, String>> getMultipleLabels(MultipleLabelsRequest multipleLabelsRequest);

  HotelInformationExtended getHotelInformation(HotelInformationRequest hotelInformationRequest);

  List<HotelInformation> getHotelsInformation(HotelsInformationRequest hotelsInformationRequest);

  List<HotelShortInformation> getAllHotelsShortInformation(String country, String language);

  HotelInformation getHotelInformationBySlug(HotelInformationRequest hotelInformationRequest);

  HotelPaymentInformation getHotelPaymentInformation(
      HotelInformationRequest hotelInformationRequest);

  void updateHotelsFacilitiesCache();

  void updateHotelsOpeningSoonCache();

  ExtrasLabel getExtras(String country, String language);

  DlpInformation getDlpInformation(DlpInformationRequest dlpInformationRequest);

  AppsHomepageResponse getAppsHomepage(AppsHomepageRequest homepageRequest);

  PromotionsInformationResponse getPromoConfig(PromoConfigRequest promoConfigRequest);

  PriceFinderGlobalConfig getPriceFinderConfig(PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest);
}
