package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import java.util.Set;
import org.mapstruct.Mapper;
import uk.co.whitbread.basket.generated.models.reservation.AttachReservationProfileRequestDto;

@Mapper(componentModel = "spring")
public interface AttachReservationProfileRequestMapper {

  AttachReservationProfileRequestDto toDto(String hotelId, String profileId,
      Set<String> reservationIds);

}
