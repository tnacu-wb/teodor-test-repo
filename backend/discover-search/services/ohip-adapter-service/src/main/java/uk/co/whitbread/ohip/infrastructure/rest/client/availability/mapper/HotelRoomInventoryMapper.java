package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.HotelInventoryRequestDto;

@Mapper(componentModel = "spring")
public interface HotelRoomInventoryMapper {

  HotelInventoryRequestDto toDto(HotelInventoryRequest hotelInventoryRequest);

}
