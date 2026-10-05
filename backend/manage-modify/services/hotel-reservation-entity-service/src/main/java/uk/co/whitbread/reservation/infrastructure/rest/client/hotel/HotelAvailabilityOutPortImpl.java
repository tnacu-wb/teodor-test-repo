package uk.co.whitbread.reservation.infrastructure.rest.client.hotel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper.HotelAvailabilityResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper.HotelEntityAvailabilityRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityV2RequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.HotelAvailabilityClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelAvailabilityOutPortImpl implements HotelAvailabilityOutPort {
  private final HotelAvailabilityClient hotelAvailabilityClient;
  private final HotelEntityAvailabilityRequestMapper hotelAvailabilityRequestMapper;
  private final HotelAvailabilityResponseMapper hotelAvailabilityResponseMapper;

  public HotelAvailabilityByIdsV2 getHotelAvailabilitiesByIdsV2(HotelAvailabilityByIdsV2Request request) {
    HotelAvailabilityV2RequestDto requestDto = hotelAvailabilityRequestMapper.toDto(request);
    HotelAvailabilityByIdsV2Dto availDto = hotelAvailabilityClient.getHotelAvailabilityV2(requestDto);
    return hotelAvailabilityResponseMapper.toDomainModel(availDto);
  }
}
