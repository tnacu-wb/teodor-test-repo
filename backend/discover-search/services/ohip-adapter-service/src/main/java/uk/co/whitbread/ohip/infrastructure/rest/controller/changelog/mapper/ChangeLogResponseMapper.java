package uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;

@Mapper(componentModel = "spring")
public interface ChangeLogResponseMapper {

  ChangeLogResponseDto toDto(ChangeLogResponse changeLogResponse);

}
