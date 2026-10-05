package uk.co.whitbread.infrastructure.rest.client.content;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.infrastructure.rest.client.content.mapper.SearchRulesResponseMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContentServiceOutPortImpl implements ContentServiceOutPort {

  private final ContentServiceClient contentServiceClient;
  private final SearchRulesResponseMapper searchRulesResponseMapper;

  private final CacheSearchOutPort cacheSearchOutPort;

  @Override
  public void triggerHotelFacilityFilterUpdate() {
    contentServiceClient.triggerHotelFacilitiesFilterUpdate();
  }

  @Override
  public void triggerHotelsOpeningSoonCacheUpdate() {
    contentServiceClient.triggerHotelsOpeningSoonCacheUpdate();
  }

  @Override
  public String getHotelBrand(String hotelId) {
    return contentServiceClient.getHotelBrand(hotelId);
  }

  @Override
  public SearchRules getSearchRules(String channel, Optional<String> brand) {
    var searchRules = contentServiceClient.getSearchRules(channel, brand);
    return searchRulesResponseMapper.toModel(searchRules);
  }

  @Override
  public Map<String, String> getPreferencesLabels(String country, String language, String category, String label) {
    return contentServiceClient.getPreferencesLabels(country, language, category, label);
  }

  @Override
  public GlobalConfigDto getGlobalConfig(String country, String language) {
    return contentServiceClient.getGlobalConfig(country, language);
  }

  @Override
  public HotelInformationExtendedDto getHotelInformation(String country, String language, String hotelId) {
    var hotelInformation = cacheSearchOutPort.getHotelInformationFromCache(country, language, hotelId);
    return hotelInformation != null
        ? hotelInformation : contentServiceClient.getHotelInformation(country, language, hotelId);
  }

  @Override
  public ExtrasLabelDto getExtrasLabels(String country, String language) {
    return contentServiceClient.getExtrasLabels(country, language);
  }
}
