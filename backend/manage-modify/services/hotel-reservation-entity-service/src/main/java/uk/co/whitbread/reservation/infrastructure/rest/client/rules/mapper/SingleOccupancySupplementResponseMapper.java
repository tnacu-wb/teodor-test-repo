package uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.SingleOccupancySupplementResponse;
import uk.co.whitbread.rules.entity.service.generated.models.agent.SingleOccupancySupplementResponseDto;

@Mapper(componentModel = "spring")
public interface SingleOccupancySupplementResponseMapper {

  SingleOccupancySupplementResponse toModel(SingleOccupancySupplementResponseDto singleOccupancySupplementResponseDto);
}
