package uk.co.whitbread.basket.infrastructure.rest.client.hotel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.basket.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.HotelInfoClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelInfoOutPortImpl implements HotelInfoOutPort {

  private final HotelInfoClient hotelInfoClient;
  private final HotelInfoMapper hotelInfoMapper;

  @Override
  public HotelInfo getHotelInfo(String hotelId) {
    return hotelInfoMapper.toDomainModel(hotelInfoClient.getHotelInfo(hotelId));
  }

}
