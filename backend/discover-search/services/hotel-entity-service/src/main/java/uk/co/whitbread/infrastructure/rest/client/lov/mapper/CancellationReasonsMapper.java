package uk.co.whitbread.infrastructure.rest.client.lov.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonsResponseDto;

@Mapper(componentModel = "spring")
public interface CancellationReasonsMapper {

  CancellationReasonsResponse toDomainModel(CancellationReasonsResponseDto cancellationReasonDto);

}
