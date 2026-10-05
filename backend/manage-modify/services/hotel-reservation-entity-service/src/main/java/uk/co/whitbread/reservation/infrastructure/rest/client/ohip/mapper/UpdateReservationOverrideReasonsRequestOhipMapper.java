package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;


@Mapper(componentModel = "spring")
public interface UpdateReservationOverrideReasonsRequestOhipMapper {

  UpdateReservationOverrideReasonsRequestDto toDto(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest);

}
