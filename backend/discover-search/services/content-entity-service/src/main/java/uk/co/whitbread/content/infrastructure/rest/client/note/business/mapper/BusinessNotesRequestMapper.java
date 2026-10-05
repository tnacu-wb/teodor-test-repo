package uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.out.BusinessNotesRequestAemDto;

@Mapper(componentModel = "spring")
public interface BusinessNotesRequestMapper {

  BusinessNotesRequest toModel(BusinessNotesRequestAemDto businessNotesRequestAemDto);

  BusinessNotesRequestAemDto toDto(BusinessNotesRequest businessNotesRequest);
}
