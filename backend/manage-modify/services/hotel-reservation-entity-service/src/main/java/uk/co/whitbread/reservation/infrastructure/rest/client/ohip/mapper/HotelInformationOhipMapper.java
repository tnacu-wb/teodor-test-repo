package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;

@Mapper(componentModel = "spring")
public interface HotelInformationOhipMapper {

  HotelInformationResponse toModel(HotelInfoDto cancelReservationRequestDto);
}