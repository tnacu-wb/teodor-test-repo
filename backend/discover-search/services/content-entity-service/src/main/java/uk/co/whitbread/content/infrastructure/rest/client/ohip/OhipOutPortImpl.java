package uk.co.whitbread.content.infrastructure.rest.client.ohip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.content.domain.ports.secondary.OhipOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInfoMapper;

@RequiredArgsConstructor
@Slf4j
public class OhipOutPortImpl implements OhipOutPort {

  private final OhipAdapterClient ohipAdapterClient;
  private final HotelInfoMapper hotelInfoMapper;

  @Override
  public HotelInfo getHotelInfo(String hotelId) {
    log.debug("Entered getHotelInfo with hotelId={}", hotelId);
    return hotelInfoMapper.toDomainModel(ohipAdapterClient.getHotelInfo(hotelId));
  }

}
