package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;

@Mapper(componentModel = "spring", uses = {
    ReservationMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationRequestMapper {

  ReservationRequest toModel(ReservationRequestDto reservationRequestDto);

  UpdateReservationAlertsRequest toAlertModel(
      UpdateReservationAlertsRequestDto updateReservationRequestDto);

}
