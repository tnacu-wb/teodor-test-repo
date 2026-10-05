package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelInformationResponseDto;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;

@Mapper(componentModel = "spring")
public interface CancelResponseOhipMapper {

  CancelInformationResponse toModel(CancelInformationResponseDto cancelInformationResponse);
}
