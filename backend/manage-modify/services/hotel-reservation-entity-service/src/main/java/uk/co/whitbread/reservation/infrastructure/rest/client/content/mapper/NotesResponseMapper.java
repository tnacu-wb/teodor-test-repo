package uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;

@Mapper(componentModel = "spring")
public interface NotesResponseMapper {

  BusinessNotesResponse toModel(BusinessNotesResponseDto notes);

}
