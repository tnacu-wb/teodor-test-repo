package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomOccupancyRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccuRuleResp;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomOccupancyRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyResponseDto;

@Mapper(componentModel = "spring")
public interface MaxRoomOccupancyDtoMapper {

  static final String DEFAULT_BRAND = "PI";

  @Mapping(target = "brand", expression = "java(toBrandModel(roomOccupancyRequestDto))")
  MaxRoomOccupancyRequest toModel(MaxRoomOccupancyRequestDto roomOccupancyRequestDto);

  @Mapping(target = "roomOccupancies", source = "maxOccupancyData")
  MaxRoomOccupancyResponseDto toDto(MaxRoomOccuRuleResp roomOccupancyRuleResponse);

  default String toBrandModel(MaxRoomOccupancyRequestDto roomOccupancyRequestDto) {
    if (roomOccupancyRequestDto.getBrand() == null || roomOccupancyRequestDto.getBrand()
        .isBlank()) {
      return DEFAULT_BRAND;
    }
    return roomOccupancyRequestDto.getBrand();
  }
}
