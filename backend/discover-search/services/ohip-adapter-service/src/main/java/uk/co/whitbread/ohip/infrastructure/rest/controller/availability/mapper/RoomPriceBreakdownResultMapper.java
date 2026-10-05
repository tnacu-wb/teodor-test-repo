package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RoomPriceBreakdownResultDto;

@Mapper(componentModel = "spring")
public interface RoomPriceBreakdownResultMapper {

  RoomPriceBreakdownResultDto toDto(RoomPriceBreakdownResult roomPriceBreakdownResult);
}
