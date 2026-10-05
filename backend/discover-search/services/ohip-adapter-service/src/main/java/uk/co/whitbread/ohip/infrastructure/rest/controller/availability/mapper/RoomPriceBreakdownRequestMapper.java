package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RoomPriceBreakdownRequestDto;

@Mapper(componentModel = "spring")
public interface RoomPriceBreakdownRequestMapper {

  RoomPriceBreakdownRequest toDomainModel(RoomPriceBreakdownRequestDto roomPriceBreakdownRequestDto);
}
