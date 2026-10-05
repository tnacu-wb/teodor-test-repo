package uk.co.whitbread.payments.infrastructure.rest.client.hotels;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.mapper.HotelInfoMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.HotelInfoClient;

@Slf4j
public record HotelInfoPortImpl(
    HotelInfoClient hotelInfoClientClient,
    HotelInfoMapper hotelInfoMapper)
    implements HotelInfoPort {

  @Override
  public HotelInfo findHotelPaymentDetails(final String hotelCode,
      final String country,
      final String language) {
    log.debug("Entered find hotel payments details for hotelCode={} with country={} and language={}",
        hotelCode, country, language);
    final var hotelInfoDto = hotelInfoClientClient
        .findHotelPaymentDetails(hotelCode, country, language);
    return hotelInfoMapper.toHotelInfoModel(hotelInfoDto);
  }
}