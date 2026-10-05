package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInRequestDto;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;

@Mapper(componentModel = "spring")
public interface PreCheckInRequestOhipMapper {

  PreCheckInRequestDto toDto(PreCheckInRequest preCheckInRequest);
}
