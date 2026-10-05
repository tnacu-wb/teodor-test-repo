package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CopyReservationsRequestDto;

@Mapper(componentModel = "spring")
public interface CopyReservationsRequestMapper {

  CopyReservationsRequest toModel(CopyReservationsRequestDto copyReservationsRequestDto);
}
