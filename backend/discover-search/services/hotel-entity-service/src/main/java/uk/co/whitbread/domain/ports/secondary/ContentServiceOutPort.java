package uk.co.whitbread.domain.ports.secondary;

import java.util.Map;
import java.util.Optional;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;

public interface ContentServiceOutPort {

  void triggerHotelFacilityFilterUpdate();

  void triggerHotelsOpeningSoonCacheUpdate();

  String getHotelBrand(String hotelId);

  SearchRules getSearchRules(String channel, Optional<String> brand);

  Map<String, String> getPreferencesLabels(String country, String language, String category,
      String label);

  GlobalConfigDto getGlobalConfig(String country, String language);

  HotelInformationExtendedDto getHotelInformation(String country, String language, String hotelId);

  ExtrasLabelDto getExtrasLabels(String country, String language);
}
