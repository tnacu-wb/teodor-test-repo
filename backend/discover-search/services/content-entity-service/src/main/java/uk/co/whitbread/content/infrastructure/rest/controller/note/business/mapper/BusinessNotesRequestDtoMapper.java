package uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.in.BusinessNotesRequestDto;

@Mapper(componentModel = "spring")
public interface BusinessNotesRequestDtoMapper {

  BusinessNotesRequest toDomainModel(BusinessNotesRequestDto mealsRequestAemDto);
}
