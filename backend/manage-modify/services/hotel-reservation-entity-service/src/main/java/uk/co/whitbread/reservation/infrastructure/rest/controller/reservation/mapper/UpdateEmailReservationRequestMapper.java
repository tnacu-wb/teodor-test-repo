package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.UpdateEmailReservationRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateEmailReservationRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateEmailReservationRequestMapper {

  @Mapping(source = "email", target = "email", defaultValue = "test@test.com")
  UpdateEmailReservationRequest toModel(UpdateEmailReservationRequestDto updateEmailReservationRequestDto);

}
