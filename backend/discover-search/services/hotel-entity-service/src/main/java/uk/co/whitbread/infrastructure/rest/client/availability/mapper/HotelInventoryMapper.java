package uk.co.whitbread.infrastructure.rest.client.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInventoryRoomTypeDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelInventoryRequestOhipDto;

@Mapper(componentModel = "spring")
public interface HotelInventoryMapper {

  HotelInventoryRequestOhipDto toOhipDto(HotelInventoryRequest hotelInventoryRequest);

  HotelInventoryRoomType toDomainModel(HotelInventoryRoomTypeDto hotelInventoryRoomTypeDto);
}
