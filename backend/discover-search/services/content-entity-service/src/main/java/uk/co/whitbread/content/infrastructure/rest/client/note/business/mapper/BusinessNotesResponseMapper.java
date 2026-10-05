package uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.in.BusinessNotesResponseAemDto;

@Mapper(componentModel = "spring")
public interface BusinessNotesResponseMapper {

  BusinessNotesResponse toModel(BusinessNotesResponseAemDto businessNotesResponseAemDto);

  BusinessNotesResponseAemDto toDto(BusinessNotesResponse businessNotesResponse);
}
