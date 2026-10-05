package uk.co.whitbread.ohip.infrastructure.rest.controller.lov.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.model.out.CancellationReasonsResponseDto;

@Mapper(componentModel = "spring")
public interface CancellationReasonsDtoMapper {
  CancellationReasonsResponseDto toDto(CancellationReasonsResponse cancellationReasonsResponse);

}
