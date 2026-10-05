package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

public interface CacheOutPort {

  HotelInformationExtendedDto getHotelInformationFromCache(String country, String language,
      String hotelId);
}
