package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;

@Mapper(componentModel = "spring")
public interface ConfirmReservationResponseOhipMapper {

  ConfirmReservationResponse toModel(
      ConfirmReservationResponseDto confirmReservationResponseDto);
}
