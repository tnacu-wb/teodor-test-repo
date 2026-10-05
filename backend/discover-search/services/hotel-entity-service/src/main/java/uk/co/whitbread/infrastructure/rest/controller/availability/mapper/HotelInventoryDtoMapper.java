package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelInventoryRoomTypeDto;


@Mapper(componentModel = "spring")
public interface HotelInventoryDtoMapper {

  HotelInventoryRequest toDomainModel(String hotelId,
      HotelInventoryRequestDto hotelInventoryRequest);

  HotelInventoryRoomTypeDto toDto(HotelInventoryRoomType hotelInventory);
}
