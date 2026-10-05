package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.HotelsOpeningSoonModel;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;

public interface CacheSearchOutPort {

  boolean checkCacheFacilitiesFilterAvailability(String key);

  boolean checkCacheOpeningSoonAvailability();

  HotelsWithFilterModel getHotelIdsByFilter(String filter);

  HotelsOpeningSoonModel getHotelIdsOpeningSoon();

  HotelInformationExtendedDto getHotelInformationFromCache(String country, String language, String hotelId);

  void saveOperaHotels(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                       List<HotelAvailabilityResponse> hotelAvailabilityList);

  List<HotelStatusDto> getOnsaleFlagFromCache(List<String> hotelIds);

  List<HotelAvailabilityResponse> getOperaAvailabilityFromCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);
}
