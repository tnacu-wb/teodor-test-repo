package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.ItemInventoryResponseDto;

@Mapper(componentModel = "spring")
public interface ItemInventoryMapper {

  ItemInventoryRequest toDomainModel(String hotelId, ItemInventoryRequestDto itemInventoryRequest);

  ItemInventoryResponseDto toDto(ItemInventoryResponse hotelInventory);

}
