package uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;

@Mapper(componentModel = "spring")
public interface ChangeLogResponseMapper {

  ChangeLogResponseDto toDto(ChangeLogResponse changeLogResponse);
}
