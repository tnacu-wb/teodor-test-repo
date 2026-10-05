package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsResponseDto;

@Mapper(componentModel = "spring")
public interface OccasionResponseMapper {

  @Mapping(target = "occasions", expression = "java(toResponseToDto(occasionsResponse))")
  OccasionsResponseDto toDto(OccasionsResponse occasionsResponse);

  default List<OccasionsDto> toResponseToDto(OccasionsResponse occasionsResponse) {
    if (occasionsResponse == null || occasionsResponse.getOccasions() == null) {
      return Collections.emptyList();
    }
    return occasionsResponse.getOccasions().stream()
        .map(occasions -> OccasionsDto.builder()
            .id(occasions.getId())
            .name(occasions.getName())
            .available(occasions.getAvailable())
            .build())
        .toList();
  }

}
