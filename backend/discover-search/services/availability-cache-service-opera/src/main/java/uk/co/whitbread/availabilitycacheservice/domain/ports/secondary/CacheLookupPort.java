package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

public interface CacheLookupPort {

  HotelInformationExtendedDto getHotelInformationFromCache(String hotelId, String country,
      String language);
}
