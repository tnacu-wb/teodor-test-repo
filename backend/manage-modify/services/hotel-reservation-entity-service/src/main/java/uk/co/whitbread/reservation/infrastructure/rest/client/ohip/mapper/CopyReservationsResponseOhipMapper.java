package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CopyReservationsResponseDto;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;

@Mapper(componentModel = "spring")
public interface CopyReservationsResponseOhipMapper {

  CopyReservationsResponse toModel(CopyReservationsResponseDto copyReservationsResponseDto);
}
