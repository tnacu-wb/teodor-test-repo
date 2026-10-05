package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsResponseDto;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;

@Mapper(componentModel = "spring")
public interface DepositsResponseOhipMapper {

  DepositsResponse toModel(DepositsResponseDto depositsOhipResponseDto);

}