package uk.co.whitbread.infrastructure.rest.controller.lov.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.infrastructure.rest.controller.lov.model.out.CancellationReasonsResponseDto;

@Mapper(componentModel = "spring")
public interface CancellationReasonsDtoMapper {

  CancellationReasonsResponseDto toDto(CancellationReasonsResponse cancelInformationResponse);

}
