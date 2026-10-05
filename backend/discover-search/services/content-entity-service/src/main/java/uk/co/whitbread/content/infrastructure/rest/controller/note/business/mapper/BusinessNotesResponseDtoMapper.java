package uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNotesResponseDto;

@Mapper(componentModel = "spring")
public interface BusinessNotesResponseDtoMapper {

  BusinessNotesResponseDto toDto(BusinessNotesResponse mealsInfoResponse);
}
