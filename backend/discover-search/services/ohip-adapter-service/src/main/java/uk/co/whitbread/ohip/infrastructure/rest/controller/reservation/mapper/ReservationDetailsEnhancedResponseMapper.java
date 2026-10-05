package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationDetailsEnhancedDto;

@Mapper(componentModel = "spring")
public interface ReservationDetailsEnhancedResponseMapper {

  ReservationDetailsEnhancedDto toDto(ReservationDetailsEnhancedResponse reservationDetailsEnhanced);
}
