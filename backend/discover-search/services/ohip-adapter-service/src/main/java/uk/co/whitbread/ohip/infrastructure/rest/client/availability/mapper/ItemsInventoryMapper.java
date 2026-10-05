package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;

@Mapper(componentModel = "spring")
public interface ItemsInventoryMapper {

  ItemInventoryRequestDto toDto(
      ItemInventoryRequest itemInventoryRequest);

  ItemInventoryResponse toDomainModel(
      ItemInventoryResponseDto itemsInventoryResponseDto);
}
