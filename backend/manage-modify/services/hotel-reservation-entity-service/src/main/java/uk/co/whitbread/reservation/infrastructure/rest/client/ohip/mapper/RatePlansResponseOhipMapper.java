package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;

@Mapper(componentModel = "spring")
public interface RatePlansResponseOhipMapper {

  RatePlansResponse toModel(RatePlansResponseDto ratePlansResponseDto);
}
