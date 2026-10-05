package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CopyReservationsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.CopyReservationsRequest;

@Mapper(componentModel = "spring")
public interface CopyReservationsRequestOhipMapper {

  CopyReservationsRequestDto toDto(CopyReservationsRequest copyReservationsRequest);
}
